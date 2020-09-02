package com.handler.repository;

import com.handler.model.FilePartDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class FilePartDetailsRepositoryImpl implements FilePartDetailsRepository {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    static Map<Integer, FilePartDetails> partDetailsMap = new HashMap<>();

    /**
     * This method inserts chunks of file into the mock DB partDetailsMap
     * @param id Integer
     * @param newFilePath String
     * @param  partNumber Integer
     */
    @Override
    public void insert(Integer id, String newFilePath,Integer partNumber) {
        FilePartDetails filePartDetails = new FilePartDetails(1,id,newFilePath,partNumber,"");
        partDetailsMap.put(partDetailsMap.size()+1,filePartDetails);
    }


    /**
     * This method fetches all chunks of a single file stored in different locations
     * and returns a TreeMap with the part Numbers
     * @param id Integer
     * @return Map
     */
    @Override
    public Map<Integer, FilePartDetails> getAllParts(Integer id) {
        logger.debug("Entered getAllParts method");
        Map<Integer, FilePartDetails> partMap = new TreeMap<>();
        partDetailsMap.forEach((key,parts)->{
            logger.debug("Identified a chunk of the file");
            if(parts.getFileId().equals(id)){
                partMap.put(parts.getPartNumber(),parts);
            }
        });
        logger.debug("Exits from the method getAllParts");
        return partMap;
    }
}
