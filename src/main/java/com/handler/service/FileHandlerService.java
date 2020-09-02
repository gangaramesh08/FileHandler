package com.handler.service;

import org.springframework.stereotype.Service;

@Service
public interface FileHandlerService {
    String readFileAndCreate(Integer id);

    String deleteFile(Integer id);

    String download(Integer id);
}
