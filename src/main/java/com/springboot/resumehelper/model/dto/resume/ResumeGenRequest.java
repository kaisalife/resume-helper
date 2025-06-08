package com.springboot.resumehelper.model.dto.resume;

import lombok.Data;

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
    private List<Education> education;
    
    @Data
    public static class Section {
        private String label;
        private String content;
    }
    
    @Data
    public static class Education {
        private String school;
        private String major;
        private String time;
    }
}