package com.springboot.resumehelper.service;

import com.springboot.resumehelper.model.dto.ai.AISimulateResult;
import com.springboot.resumehelper.model.dto.resume.ResumeDTO;
import com.springboot.resumehelper.model.entity.Resume;

import java.util.List;

public interface AISimulateService {

    AISimulateResult simulateFiltering(ResumeDTO resume);
}    