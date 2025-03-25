package com.example.backend.service;// src/main/java/com/example/backend/service/RepairService.java
import com.example.backend.model.RepairTask;
import com.example.backend.model.TaskStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class RepairService {

    @Value("${esrgan.python.path}")
    private String pythonPath;

    @Value("${esrgan.script.path}")
    private String scriptPath;

    // 内存存储任务状态
    private final Map<String, RepairTask> taskMap = new ConcurrentHashMap<>();

    /**
     * 创建任务（上传时调用）
     */
    public RepairTask createTask(String inputPath) {
        String taskId = UUID.randomUUID().toString();
        String outputPath = inputPath.replace("LR", "result");
        RepairTask task = new RepairTask(
                taskId, inputPath, outputPath, TaskStatus.UPLOADED, "文件已上传，等待修复");
        taskMap.put(taskId, task);
        return task;
    }

    /**
     * 同步处理任务（点击“开始修复”时调用）
     */
    public RepairTask processTask(String taskId) {
        RepairTask task = taskMap.get(taskId);
        if (task == null) {
            throw new IllegalArgumentException("任务不存在");
        }

        task.setStatus(TaskStatus.PROCESSING);
        task.setMessage("任务处理中");

        try {
            log.info("开始处理任务: {}", taskId);
            System.out.println("开始处理任务: " + taskId);

            // 执行Python脚本（同步阻塞）
            ProcessBuilder processBuilder = new ProcessBuilder("python", "test.py");
            processBuilder.directory(Paths.get(scriptPath).toFile());
            Process process = processBuilder.start();

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                task.setStatus(TaskStatus.COMPLETED);
                task.setMessage("修复成功");
                // 删除原始文件（可选）
                Files.deleteIfExists(Paths.get(task.getInputPath()));
            } else {
                task.setStatus(TaskStatus.FAILED);
                task.setMessage("Python脚本执行失败，退出码: " + exitCode);
            }
        } catch (Exception e) {
            task.setStatus(TaskStatus.FAILED);
            task.setMessage("内部错误: " + e.getMessage());
            log.error("任务处理失败: {}", taskId, e);
        }
        return task;
    }

    /**
     * 查询任务状态
     */
    public RepairTask getTask(String taskId) {
        return taskMap.get(taskId);
    }
}