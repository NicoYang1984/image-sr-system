package com.example.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RepairTask {
    private String taskId;       // 任务ID（与上传文件名一致）
    private String inputPath;    // 输入文件路径（ESRGAN/LR/xxx.jpg）
    private String outputPath;   // 输出文件路径（ESRGAN/result/xxx.jpg）
    private TaskStatus status;   // 当前状态
    private String message;      // 附加信息（如错误日志）

}
