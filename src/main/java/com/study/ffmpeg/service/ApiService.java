package com.study.ffmpeg.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public interface ApiService {
    public Integer exportThumbnail(File file, Integer per, String filepath) throws IOException;
}
