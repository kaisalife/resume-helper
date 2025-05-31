package com.springboot.resumehelper.repository;

import com.springboot.resumehelper.model.entity.SharePost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SharePostRepository extends JpaRepository<SharePost, Long> {
    void deleteByResumeId(Long id);
}    