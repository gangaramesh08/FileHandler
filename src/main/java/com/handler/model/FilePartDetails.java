package com.handler.model;

/**
 * This is a POJO for storing details of the chunks of a file
 */

public class FilePartDetails {
    Integer id;
    Integer fileId;
    String partLocation;
    Integer partNumber;
    String fileSize;

    public FilePartDetails(Integer id, Integer fileId, String partLocation, Integer partNumber, String fileSize) {
        this.id = id;
        this.fileId = fileId;
        this.partLocation = partLocation;
        this.partNumber = partNumber;
        this.fileSize = fileSize;
    }

    public Integer getId() {
        return id;
    }

    public Integer getFileId() {
        return fileId;
    }

    public String getPartLocation() {
        return partLocation;
    }

    public Integer getPartNumber() {
        return partNumber;
    }

    public String getFileSize() {
        return fileSize;
    }
}
