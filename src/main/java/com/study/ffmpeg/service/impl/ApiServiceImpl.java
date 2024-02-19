package com.study.ffmpeg.service.impl;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import com.study.ffmpeg.service.ApiService;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class ApiServiceImpl implements ApiService {

    @Value("${ffmpeg.base.location}")
    public String ffmpegBase;
    @Value("${ffmpeg.main.location}")
    public String ffmpegLocation;
    @Value("${ffmpeg.probe.location}")
    private String ffprobeLocation;
    @Value("${ffmpeg.upload.location}")
    public String UploadLocation;

    @Override
    public Integer exportThumbnail(File file, Integer per, String filepath) throws IOException {

        try {
            FFmpeg ffmpeg = new FFmpeg(ffmpegBase + ffmpegLocation);        // ffmpeg.exe 파일 경로
            FFprobe ffprobe = new FFprobe(ffmpegBase + ffprobeLocation);
            String inputPath = file.getAbsolutePath();

            //ffmpeg -i [input] -vf fps=1/60 img_%03d.jpg
            FFmpegBuilder builder = new FFmpegBuilder()
                    .overrideOutputFiles(true)                    // output 파일을 덮어쓸 것인지 여부(false일 경우, output path에 해당 파일이 존재할 경우 예외 발생 - File '파일경로' already exists. Exiting.)
                    .setInput(inputPath)                        // 썸네일 이미지 추출에 사용할 영상 파일의 절대 경로
                    .addOutput(UploadLocation + filepath + "/img_%03d.jpg")        // 저장 절대 경로(확장자 미 지정 시 예외 발생 - [NULL @ 000002cc1f9fa500] Unable to find a suitable output format for '파일경로')
                    .addExtraArgs("-vf", "fps=1/" + per)  //per(초)당 1장의 이미지 추출
                    .done();

            FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);        // FFmpeg 명령어 실행을 위한 FFmpegExecutor 객체 생성
            executor.createJob(builder).run();
        }catch(IOException e){
            System.out.println(e.getMessage());
            return 0;
        }
        return 1;
    }
}
