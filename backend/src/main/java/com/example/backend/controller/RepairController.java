package com.example.backend.controller;// src/main/java/com/example/backend/controller/RepairController.java
import com.example.backend.model.RepairTask;
import com.example.backend.model.TaskStatus;
import com.example.backend.service.RepairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/repair")
public class RepairController {

    @Autowired
    private RepairService repairService;

    @PostMapping("/start")
    public ResponseEntity<Map<String, Object>> startRepair(@RequestParam String taskId) {
        RepairTask task = repairService.getTask(taskId);
        if (task == null) {
            return ResponseEntity.status(404).body(Map.of(
                    "success", false,
                    "message", "任务不存在"
            ));
        }

        // 同步处理任务（阻塞直到完成）
        RepairTask processedTask = repairService.processTask(taskId);

        return ResponseEntity.ok(Map.of(
                "taskId", taskId,
                "status", processedTask.getStatus().name(),
                "resultUrl", processedTask.getStatus() == TaskStatus.COMPLETED ?
                        "/api/result/" + taskId : null,
                "message", processedTask.getMessage()
        ));
    }

    @GetMapping("/status/{taskId}")
    public ResponseEntity<Map<String, Object>> getStatus(@PathVariable String taskId) {
        RepairTask task = repairService.getTask(taskId);
        if (task == null) {
            return ResponseEntity.status(404).body(Map.of(
                    "success", false,
                    "message", "任务不存在"
            ));
        }
        return ResponseEntity.ok(Map.of(
                "taskId", taskId,
                "status", task.getStatus().name(),
                "resultUrl", task.getStatus() == TaskStatus.COMPLETED ?
                        "/api/result/" + taskId : null,
                "message", task.getMessage()
        ));
    }
}