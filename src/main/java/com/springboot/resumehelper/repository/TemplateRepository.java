package com.springboot.resumehelper.repository;

import com.springboot.resumehelper.model.entity.Template;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TemplateRepository extends JpaRepository<Template, Long> {
}    