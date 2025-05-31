package com.springboot.resumehelper.repository;

import com.springboot.resumehelper.model.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ResumeRepository extends JpaRepository<Resume, Long> {
    List<Resume> findByUserId(Long userId);

    Optional<Resume> findByresumeTitle(String resumeTitle);

    // 使用 JPQL 关联查询（推荐，数据库无关）
    @Query("SELECT r FROM Resume r JOIN SharePost sp ON r.id = sp.resumeId")
    List<Resume> findAllSharedResumes();
}    