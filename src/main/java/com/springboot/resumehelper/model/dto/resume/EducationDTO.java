package com.springboot.resumehelper.model.dto.resume;

import lombok.Data;

@Data
public class EducationDTO {
    private Long id;
    private String school;     // 学校名称
    private String major;      // 专业及学位
    private String time;       // 时间范围，例如"2018-09 至 2022-07"，与前端保持一致
} 