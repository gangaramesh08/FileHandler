package com.handler.repository;

import com.handler.model.FilePartDetails;

import java.util.Map;

public interface FilePartDetailsRepository {
    void insert(Integer id, String newFilePath,Integer partNumber);

    Map<Integer, FilePartDetails> getAllParts(Integer id);
}
