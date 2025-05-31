package com.springboot.resumehelper.model.dto.resume;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ResumeGenRequest {
    private String name;
    private String email;
    private String phone;
    private String objective;
    private List<Section> sections;
    private String skills;
    private long resumeScore;
    private String resumeTitle;
    private List<Education> education = new ArrayList<>(); // 改为education与前端一致
    
    @Data
    public static class Section {
        private String label;
        private String content;
    }
    
    @Data
    public static class Education {
        private String school;     // 学校
        private String major;      // 专业及学位
        private String time;       // 时间，与前端保持一致
    }
}