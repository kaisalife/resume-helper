package com.springboot.resumehelper.service;

import com.springboot.resumehelper.model.dto.resume.ResumeDTO;
import com.springboot.resumehelper.model.dto.resume.ResumeGenRequest;

import java.util.List;

public interface ResumeService {

    ResumeDTO createResume(ResumeDTO resumeDTO);

    ResumeDTO getResumeById(Long id);

    List<ResumeDTO> getAllResumes();

    ResumeDTO updateResume(Long id, ResumeDTO resumeDTO);

    void deleteResume(Long id);

    void deleteResumeByTitle(String title);

    byte[] generateResume(ResumeGenRequest request);

    Boolean updateShareStatus(String resumeTitle);
}    