package com.reggie.controller;


import com.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/common")
public class CommonController {

    @Value("${reggie.path}")
    private String basePath;

    @PostMapping("/upload")
    //@GetMapping("/upload")
    public R<String> upload(MultipartFile file) throws IOException {

        log.info("file = {}",  file.toString());

        //文件名
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        String fileName = UUID.randomUUID().toString() + suffix;

        file.transferTo(new File(basePath + fileName));


        //文件路径
        return R.success(fileName);
    }


    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) throws Exception {
        FileInputStream fileInputStream;
            fileInputStream = new FileInputStream(new File(basePath + "/1.jpeg"));



        ServletOutputStream outputStream = response.getOutputStream();

        int len = 0;
        byte[] bytes = new byte[1024];
        while((len = fileInputStream.read(bytes)) != -1){
            outputStream.write(bytes, 0, len);
            outputStream.flush();
        }
        outputStream.close();
        fileInputStream.close();
    }
}
