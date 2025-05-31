package com.springboot.resumehelper.model.dto.ai;

import lombok.Data;

@Data
public class AISimulateResult {
    private Long resumeId;         // 关联的简历 ID
    private boolean passed;        // 是否通过简历筛选
    private int score;          // 简历得分
    private String evaluation;     // 评价内容
    private String improvement;    // 改进建议
    private String interviewer;    // 面试官角色
    private String interviewType;  // 预计面试类型（技术面、HR面等）
    private String transcript;     // 模拟面试对话文本
    private String analysis;       // 详细分析报告
}