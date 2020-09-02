package com.handler.service;

import com.handler.common.FileConstants;
import com.handler.model.FileDetails;
import com.handler.repository.FilePartDetailsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileWriter;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class FileReadRunnable implements Runnable{
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    FilePartDetailsRepository filePartDetailsRepository;

    FileDetails fileDetails;
    File file;
    int start;
    int end;

    public FileReadRunnable(FileDetails fileDetails, File file, int start, int end) {
        this.fileDetails = fileDetails;
        this.file = file;
        this.start = start;
        this.end = end;
    }

    /**
     * This run method reads the file as chunks with each chunk size of start position to end position.
     * Each chunk of file is stored into location and the corresponding details are entered into the
     * FileParts mock DB.
     *
     */
    @Override
    public void run() {
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(fileDetails.getFilePath(), "r");
            MappedByteBuffer buffer = randomAccessFile.getChannel().map(FileChannel.MapMode.READ_ONLY, start, end);
            Integer part_Number = (start/ FileConstants.CHUNK_SIZE);
            String newFilePath = fileDetails.getFilePath()+part_Number;
            FileWriter fileWriter = new FileWriter(newFilePath);
            fileWriter.write(buffer.get());
            filePartDetailsRepository.insert(fileDetails.getId(),newFilePath,part_Number);


        } catch(Exception exception){
            logger.debug("Caught Exception inside CreateRunnable run method"+exception);
        }
    }
}
