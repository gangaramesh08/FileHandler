package com.handler.controller;

import com.handler.common.FileServiceFactory;
import com.handler.service.FileHandlerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/filehandler/")
public class FileHandlerController {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    FileHandlerService fileHandlerService;

    @Autowired
    FileServiceFactory fileServiceFactory;

    /**
     * To create a copy of the file
     * @param id Integer
     * @return String
     */
    @GetMapping(value = "/create")
    public String create(@RequestParam Integer id){
        String response;
        this.fileHandlerService = fileServiceFactory.getInstance(id);
        try {
            response = this.fileHandlerService.readFileAndCreate(id);
        } catch (Exception exception){
            logger.error("Caught exception inside create Controller ",exception);
            response = "Unable to locate file";
        }
        return response;
    }

    /**
     * Deletes a given file
     * @param id Integer
     * @return String
     */
    @GetMapping(value = "/delete")
    public String delete(@RequestParam Integer id){
        String status = "Failed to Delete";
        this.fileHandlerService = fileServiceFactory.getInstance(id);
        try {
            status = this.fileHandlerService.deleteFile(id);
        }catch (Exception exception){
            logger.error("Caught exception inside Delete API ",exception);
        }
        return status;
    }

    /**
     * Downloads file for a given id and returns the URL where the file can be obtained
     * @param id Integer
     * @return String
     */
    @GetMapping(value = "/download")
    public String download(@RequestParam Integer id){
        String url = "Unavailable";
        this.fileHandlerService = fileServiceFactory.getInstance(id);
        try {
            url = this.fileHandlerService.download(id);
        } catch (Exception exception) {
            logger.error("Exception caught inside download API ",exception);
        }
        return url;
    }
}

