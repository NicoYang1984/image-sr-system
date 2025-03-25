package com.example.backend.model;

public enum TaskStatus {
    UPLOADED,   // 已上传但未开始修复
    PROCESSING, // 处理中
    COMPLETED,  // 完成
    FAILED      // 失败
}
