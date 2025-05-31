package com.springboot.resumehelper.repository;

import com.springboot.resumehelper.model.entity.ResumeSection;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResumeSectionRepository extends JpaRepository<ResumeSection, Long> {
    // 可以添加自定义查询方法（如果需要）
}