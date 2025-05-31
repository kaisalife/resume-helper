package com.springboot.resumehelper.model.dto.social;

import com.springboot.resumehelper.model.dto.resume.EducationDTO;
import com.springboot.resumehelper.model.dto.resume.Section;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class SharePostDTO {
    private Long id;
    private Long userId;
    private String name;     // 与前端的 name 对应
    private String email;    // 与前端的 email 对应
    private String phone;    // 与前端的 phone 对应
    private String objective;// 与前端的 objective 对应
    private List<Section> sections; // 与前端的 sections 对应
    private String skills;   // 与前端的 skills 对应
    private long resumeScore;
    private String resumeTitle;
    private List<EducationDTO> education = new ArrayList<>(); // 添加教育背景支持
}