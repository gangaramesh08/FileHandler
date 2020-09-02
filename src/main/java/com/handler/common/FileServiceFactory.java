package com.handler.common;

import com.handler.repository.FileDetailsRepository;
import com.handler.service.FileHandlerService;
import com.handler.service.FileHandlerServiceLocalImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FileServiceFactory {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    FileDetailsRepository fileDetailsRepository;

    /**
     * Factory method to get the instance of FileHandler service based on the Storage Type of file.
     * Storage type can be Local, Remote, AWS etc.
     *
     * @param id Integer
     * @return FileHandlerService
     */
    public FileHandlerService getInstance(Integer id) {
        try {
            logger.debug("Entering method getInstance : ");
            String storageType = fileDetailsRepository.getStorageTypeById(id);
            logger.debug("Storage type obtained : "+storageType);
            if (storageType.equalsIgnoreCase(FileConstants.STORAGE_LOCAL)) {
                logger.debug("Creating new instance for Local Storage");
                return new FileHandlerServiceLocalImpl();
            }
        } catch (Exception exception){
            logger.error("Caught Exception inside getInstance method ", exception);
        }
        logger.debug("Unable to find matching Storage type");
        return null;
    }
}
