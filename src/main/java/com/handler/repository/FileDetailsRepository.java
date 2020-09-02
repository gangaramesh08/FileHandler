package com.handler.repository;

import com.handler.model.FileDetails;

public interface FileDetailsRepository {
    FileDetails fetchDetailsById(Integer id);

    void deleteFile(Integer id);

    void insert(FileDetails newFileDetails);

    String getStorageTypeById(Integer id);
}
