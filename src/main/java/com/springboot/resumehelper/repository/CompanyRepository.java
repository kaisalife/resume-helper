package com.springboot.resumehelper.repository;

import com.springboot.resumehelper.model.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long> {
}    