package com.example.backend.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    @PostMapping
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        // 1. 校验文件非空
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("文件不能为空");
        }

        // 获取项目根目录
        String projectRoot = System.getProperty("user.dir");
        // 构建保存目录的绝对路径
        String parentDir = projectRoot + "/ESRGAN/LR";
        File dir = new File(parentDir);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                return ResponseEntity.status(500).body("无法创建保存目录");
            }
        }

        // 自动为文件命名和编号
        String fileExtension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        String fileName = UUID.randomUUID().toString() + fileExtension;
        Path filePath = Paths.get(parentDir, fileName);

        try {
            file.transferTo(filePath.toFile());
            return ResponseEntity.ok("{\"uploaded\": true, \"message\": \"文件上传成功\"}");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("{\"uploaded\": false, \"message\": \"文件保存失败\"}");
        }
    }
}