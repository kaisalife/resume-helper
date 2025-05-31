package com.springboot.resumehelper.service;

import com.springboot.resumehelper.model.dto.ai.AICheckRequest;
import com.springboot.resumehelper.model.dto.ai.AICheckResult;
import com.springboot.resumehelper.model.dto.resume.ResumeDTO;

public interface AICheckService {

    AICheckResult checkResume(ResumeDTO resume);
}    