package com.example.backend.controller;// src/main/java/com/example/backend/controller/FileUploadController.java
import com.example.backend.model.RepairTask;
import com.example.backend.service.RepairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    @Autowired
    private RepairService repairService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> uploadImage(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();

        // 1. 校验文件非空
        if (file.isEmpty()) {
            response.put("success", false);
            response.put("message", "文件不能为空");
            return ResponseEntity.badRequest().body(response);
        }

        // 2. 生成唯一文件名并保存到LR目录
        String fileExtension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        String fileName = UUID.randomUUID().toString() + fileExtension;
        String projectRoot = System.getProperty("user.dir");
        Path lrDir = Paths.get(projectRoot, "ESRGAN", "LR");

        try {
            if (!Files.exists(lrDir)) {
                Files.createDirectories(lrDir);
            }
            Path filePath = lrDir.resolve(fileName);
            file.transferTo(filePath.toFile());

            // 3. 创建任务并返回taskId
            RepairTask task = repairService.createTask(filePath.toString());
            response.put("success", true);
            response.put("taskId", task.getTaskId());
            response.put("message", "文件上传成功");
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            response.put("success", false);
            response.put("message", "文件保存失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}