package com.handler.model;

/**
 * This is a POJO for storing the details of a file.
 */
public class FileDetails {
    Integer id;
    String fileName;
    String storageType;
    String fileSize;
    String filePath;
    Boolean lockStatus;
    Boolean status;

    public FileDetails(String fileName, String storageType,String fileSize, String filePath, Boolean lockStatus, Boolean status) {
        this.fileName = fileName;
        this.storageType = storageType;
        this.fileSize = fileSize;
        this.filePath = filePath;
        this.lockStatus = lockStatus;
        this.status = status;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setLockStatus(Boolean lockStatus) {
        this.lockStatus = lockStatus;
    }

    public String getStorageType() {
        return storageType;
    }

    public void setStorageType(String storageType) {
        this.storageType = storageType;
    }

    public Integer getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileSize() {
        return fileSize;
    }

    public String getFilePath() {
        return filePath;
    }

    public Boolean getLockStatus() {
        return lockStatus;
    }

    public Boolean getStatus() {
        return status;
    }
}
