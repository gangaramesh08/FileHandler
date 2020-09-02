package com.handler.repository;

import com.handler.model.FileDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class FileDetailsRepositoryImpl implements FileDetailsRepository {
    static Map<Integer, FileDetails> fileDetailsMap= new HashMap<>();

    Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * This method fetches details of file from the mock DB fileDetailsMap using the ID.
     * @param id Integer
     * @return FileDetails
     */
    @Override
    public FileDetails fetchDetailsById(Integer id) {
        logger.debug("Entering method fetchDetailsById");
        FileDetails fileDetails = null;
        try{
            for(Integer field : fileDetailsMap.keySet()){
                if(field.equals(id)){
                    fileDetails = fileDetailsMap.get(field);
                    logger.debug("Fetched details of file from DB");
                    break;
                }
            }
        }catch (Exception exception){
            logger.error("Caught exception in fetchDetailsById : ", exception);
        }
        logger.debug("Exiting method fetchDetailsById");
        return fileDetails;
    }

    /**
     * This method deletes file from the mock DB fileDetailsMap for the given file id.
     * @param id Integer
     */
    @Override
    public void deleteFile(Integer id) {
        fileDetailsMap.remove(id);
        logger.debug("Removed file from the DB");
    }

    /**
     * This method inserts new file into the mock DB
     * @param newFileDetails FileDetails
     */
    @Override
    public void insert(FileDetails newFileDetails) {
        newFileDetails.setId(fileDetailsMap.size()+1);
        fileDetailsMap.put(fileDetailsMap.size()+1,newFileDetails);
        logger.debug("Inserted the file details into DB");
    }

    /**
     * Fetches the type of file Storage(Local, Remote, AWS etc.) from the DB for the given file ID
     * @param id Integer
     * @return String
     */
    @Override
    public String getStorageTypeById(Integer id) {
        String storageType = null;
        logger.debug("Entering method getStorageTypeById");
        for(Integer fileid : fileDetailsMap.keySet()){
            if(fileid.equals(id)){
                storageType = fileDetailsMap.get(fileid).getStorageType();
                logger.debug("Fetched storage Type from DB");
                break;
            }
        }
        logger.debug("Exiting method getStorageTypeById");
        return storageType;
    }
}
