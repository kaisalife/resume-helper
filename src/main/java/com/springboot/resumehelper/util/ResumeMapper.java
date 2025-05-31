package com.springboot.resumehelper.util;

import com.springboot.resumehelper.model.dto.resume.EducationDTO;
import com.springboot.resumehelper.model.dto.resume.Section;
import com.springboot.resumehelper.model.entity.Education;
import com.springboot.resumehelper.model.entity.ResumeSection;

public class ResumeMapper {

    // 将DTO的Section转换为实体类的ResumeSection
    public static ResumeSection toEntity(Section dto) {
        if (dto == null) return null;

        ResumeSection entity = new ResumeSection();
        entity.setLabel(dto.getLabel());
        entity.setContent(dto.getContent());
        return entity;
    }

    // 将实体类的ResumeSection转换为DTO的Section
    public static Section toDto(ResumeSection entity) {
        if (entity == null) return null;

        Section dto = new Section();
        dto.setLabel(entity.getLabel());
        dto.setContent(entity.getContent());

        return dto;
    }
    
    // 将DTO的EducationDTO转换为实体类的Education
    public static Education toEntity(EducationDTO dto) {
        if (dto == null) return null;

        Education entity = new Education();
        entity.setId(dto.getId());
        entity.setSchool(dto.getSchool());
        entity.setMajor(dto.getMajor());
        entity.setTime(dto.getTime());
        return entity;
    }

    // 将实体类的Education转换为DTO的EducationDTO
    public static EducationDTO toDto(Education entity) {
        if (entity == null) return null;

        EducationDTO dto = new EducationDTO();
        dto.setId(entity.getId());
        dto.setSchool(entity.getSchool());
        dto.setMajor(entity.getMajor());
        dto.setTime(entity.getTime());
        return dto;
    }
}