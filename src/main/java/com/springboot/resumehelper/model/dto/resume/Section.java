package com.springboot.resumehelper.model.dto.resume;

import lombok.Data;

@Data
public class Section {
    private String label;    // 模块标题
    private String content;  // 模块内容
}