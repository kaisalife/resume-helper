package com.springboot.resumehelper.model.dto.resume;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class ResumeDTO {
    private Long id;
    private String name;     // 与前端的 name 对应
    private String email;    // 与前端的 email 对应
    private String phone;    // 与前端的 phone 对应
    private String objective;// 与前端的 objective 对应
    private List<Section> sections; // 与前端的 sections 对应
    private String skills;   // 与前端的 skills 对应
    private Long userId;     // 用户关联 ID
    private long resumeScore;
    private String resumeTitle;
    private Boolean isShare;
    private List<EducationDTO> education = new ArrayList<>(); // 改为education与前端一致
    
    public String getAllContent() {
        StringBuilder content = new StringBuilder();
        content.append("姓名：").append(name).append("\n")
               .append("邮箱：").append(email).append("\n")
               .append("求职意向：").append(objective).append("\n")
               .append("核心技能：").append(skills).append("\n")
               .append("简历标题：").append(resumeTitle).append("\n");
        
        // 添加教育背景内容
        if (education != null && !education.isEmpty()) {
            content.append("教育背景：\n");
            for (EducationDTO edu : education) {
                content.append("  学校：").append(edu.getSchool()).append("\n")
                       .append("  专业：").append(edu.getMajor()).append("\n")
                       .append("  时间：").append(edu.getTime()).append("\n");
            }
        }
        
        // 添加其他部分内容
        content.append("sections内容：").append(sections.stream()
                .map(Section::getContent)
                .collect(Collectors.joining("\n")));
        
        return content.toString();
    }
}

