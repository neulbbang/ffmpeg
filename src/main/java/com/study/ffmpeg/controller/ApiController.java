package com.study.ffmpeg.controller;

import com.study.ffmpeg.service.ApiService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Random;

@RestController
public class ApiController {
    @Autowired
    ApiService apiService;

    @Value("${ffmpeg.upload.location}")
    public String UploadLocation;

    @PostMapping("/api/thmbExport")
    public ResponseEntity<Object> thmbExport(@RequestParam("multipartFile") MultipartFile multipartFile, @RequestParam("thmbUnit") Integer thmbUnit) { // 파라미터의 이름은 client의 formData key값과 동일해야함
        String fileId = "";

        try {
                fileId = (new Date().getTime()) + "" + (new Random().ints(1000, 9999).findAny().getAsInt()); // 현재 날짜와 랜덤 정수값으로 새로운 파일명 만들기
                String originName = multipartFile.getOriginalFilename(); // ex) 파일.jpg
                String fileExtension = originName.substring(originName.lastIndexOf(".") + 1); // ex) jpg
                originName = originName.substring(0, originName.lastIndexOf(".")); // ex) 파일
                long fileSize = multipartFile.getSize(); // 파일 사이즈

                File fileSave = new File(UploadLocation+fileId, fileId + "." + fileExtension); // ex) fileId.jpg
                if(!fileSave.exists()) { // 폴더가 없을 경우 폴더 만들기
                    fileSave.mkdirs();
                }

                multipartFile.transferTo(fileSave); // fileSave의 형태로 파일 저장

                System.out.println("fileId= " + fileId);
                System.out.println("originName= " + originName);
                System.out.println("fileExtension= " + fileExtension);
                System.out.println("fileSize= " + fileSize);

                apiService.exportThumbnail(fileSave, thmbUnit, fileId);
        } catch(IOException e) {
            return new ResponseEntity<Object>(null, HttpStatus.CONFLICT);
        }

        return new ResponseEntity<Object>(fileId, HttpStatus.OK);
    }
}
