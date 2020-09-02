package com.handler.service;

import com.handler.common.FileConstants;
import com.handler.model.FileDetails;
import com.handler.model.FilePartDetails;
import com.handler.repository.FileDetailsRepository;
import com.handler.repository.FilePartDetailsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class FileHandlerServiceLocalImpl implements FileHandlerService {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    FilePartDetailsRepository filePartDetailsRepository;

    @Autowired
    FileDetailsRepository fileDetailsRepository;

    /**
     *      Reads file from the mock DB using the id of the file. The functions then divides the file into
     *      different chunks using a chunk size FileConstants.CHUNK_SIZE MB. These chunks are processed in parallel using Executors.
     *      After all the chunks are processed, they are merged and stored in the mock DB.
     * @param id Integer
     * @return String
     */

    @Override
    public String readFileAndCreate(Integer id) {
        String response = "OK";
        FileDetails fileDetails = fileDetailsRepository.fetchDetailsById(id);
        fileDetails.setLockStatus(true);
        File file = new File(fileDetails.getFilePath());
        try {
            int threadSize = Integer.parseInt(fileDetails.getFileSize())/ FileConstants.CHUNK_SIZE;
            if(Integer.parseInt(fileDetails.getFileSize())% FileConstants.CHUNK_SIZE!=0){
                threadSize +=1;
            }
            ExecutorService executorService = Executors.newFixedThreadPool(threadSize);
            for(int i=0;i<threadSize;i++){
                FileReadRunnable runnable = new FileReadRunnable(fileDetails,file,i* FileConstants.CHUNK_SIZE,(i* FileConstants.CHUNK_SIZE+threadSize));
                executorService.submit(runnable);
            }
            executorService.shutdown();
            while(filePartDetailsRepository.getAllParts(id).size()!=threadSize) {
                logger.debug("Waiting for all threads to complete");
            }
            FileDetails newFileDetails = mergeAll(fileDetails);
            newFileDetails.setLockStatus(false);
            fileDetailsRepository.insert(newFileDetails);

        } catch (Exception exception){
            response = "Unable to read file";
            logger.error("Exception caught inside readAndCreate ",exception);
        }
        finally {
            fileDetails.setLockStatus(false);
        }
        return response;
    }

    /**
     * This method identifies different parts of a file using part number and id and
     * merges all these chunks into a single file and writes it into a storage location
     * @param fileDetails FileDetails
     * @return FileDetails
     */
    private FileDetails mergeAll(FileDetails fileDetails) {
        FileDetails newFileDetails = null;
            String newFilePath = fileDetails.getFilePath()+"_copy";
            try {
                FileWriter fileWriter = new FileWriter(newFilePath);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                Map<Integer, FilePartDetails> partDetailsMap= filePartDetailsRepository.getAllParts(fileDetails.getId());
                logger.debug("All file chunks are fetched successfully");
                partDetailsMap.forEach((key,parts)->{
                    try {
                        FileReader reader= new FileReader(new File(parts.getPartLocation()));
                        BufferedReader bufferedReader = new BufferedReader(reader);
                        String line = bufferedReader.readLine();
                        while(line!=null){
                            bufferedWriter.append(line);
                            line = bufferedReader.readLine();
                        }
                        bufferedReader.close();

                    } catch (Exception exception){
                        logger.error("Caught exception inside mergeAll ",exception);
                    }finally {
                        try {
                            bufferedWriter.close();
                        } catch (IOException exception) {
                            logger.error("Error while closing stream inside mergeAll ",exception);
                        }
                    }
                });
                newFileDetails = new FileDetails(fileDetails.getFileName()+"_copy",fileDetails.getStorageType(),fileDetails.getFileSize(),newFilePath,false,true);
            } catch (IOException exception) {
                logger.error("Caught exception inside mergeAll ",exception);
            }
            return newFileDetails;
    }

    /**
     * This method deletes the file of a given id first from the storage location and then from the DB.
     * @param id Integer
     * @return String
     */
    @Override
    public String deleteFile(Integer id) {
        String status = "Failed";
        try {
            FileDetails fileDetails = fileDetailsRepository.fetchDetailsById(id);
            fileDetails.setLockStatus(true);
            logger.debug("Fetched file details from mock DB");
            File file = new File(fileDetails.getFilePath());
            if (checkFileExists(file) && !checkLockStatus(fileDetails)) {
                deleteFromStorage(file);
                fileDetailsRepository.deleteFile(id);
                logger.debug("Deleted successfully from storage and mock DB");
                status = "SUCCESS";
            }
        }catch (Exception exception){
            logger.error("Caught exception inside deleteFile method ",exception);
        }
        return status;
    }

    /**
     * This method first divides a large file into different chunks and then store it into a location.
     * All these chunks are then combined to form a single file and then copied to a storage location that
     * can be accessed externally. This method returns the url of this location.
     * @param id Integer
     * @return String
     */
    @Override
    public String download(Integer id) {
        String url;
        FileDetails fileDetails = fileDetailsRepository.fetchDetailsById(id);
        File file = new File(fileDetails.getFilePath());
        if(!checkkIfPartsAlreadyPresent(id)) {
            try {
                int threadSize = Integer.parseInt(fileDetails.getFileSize()) / FileConstants.CHUNK_SIZE;
                if (Integer.parseInt(fileDetails.getFileSize()) % FileConstants.CHUNK_SIZE != 0) {
                    threadSize += 1;
                }
                logger.debug("Thread size "+threadSize);
                ExecutorService executorService = Executors.newFixedThreadPool(threadSize);
                for (int i = 0; i < threadSize; i++) {
                    FileReadRunnable runnable = new FileReadRunnable(fileDetails, file, i * FileConstants.CHUNK_SIZE, (i * FileConstants.CHUNK_SIZE + threadSize));
                    executorService.submit(runnable);
                }
                executorService.shutdown();
                while (filePartDetailsRepository.getAllParts(id).size() != threadSize) {
                    logger.debug("Waiting for all threads to complete");
                }

            } catch (Exception exception) {
                url = "Unavailable";
                logger.error("Exception caught inside download " , exception);
                return url;
            }
        }
            FileDetails newFileDetails = mergeAll(fileDetails);
            url = newFileDetails.getFilePath();

        return url;
    }

    /**
     * This method checks if the file is already divided into parts and stored in DB.
     * @param id Integer
     * @return boolean
     */
    private boolean checkkIfPartsAlreadyPresent(Integer id) {
        boolean already_present =false;
        if(filePartDetailsRepository.getAllParts(id).size()!=0){
         already_present = true;
        }
        return already_present;
    }

    /**
     * This method deletes a file from its storage location
     * @param file File
     */
    private void deleteFromStorage(File file) {
        logger.debug("Deleting from storage ");
        try {
            if(file.delete()){
                logger.debug("Successfully deleted from storage");
            }
        }catch (Exception exception){
            logger.debug("Exception caught while deleting from storage",exception);
        }
    }

    /**
     * This method checks whether the file is locked for any activity
     * @param fileDetails FileDetails
     * @return boolean
     */
    private boolean checkLockStatus(FileDetails fileDetails) {
        return !fileDetails.getLockStatus();
    }

    /**
     * This method checks if the file exists in the given location
     * @param file File
     * @return boolean
     */
    private boolean checkFileExists(File file) {
        try {

            if(file.exists()){
                return true;
            }
        } catch(Exception exception){
            logger.error("Caught exception inside checkFileExists",exception);
        }
        return false;
    }
}
