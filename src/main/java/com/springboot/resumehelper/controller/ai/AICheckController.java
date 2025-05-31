package com.springboot.resumehelper.controller.ai;

import com.springboot.resumehelper.model.dto.ai.AICheckResult;
import com.springboot.resumehelper.model.dto.resume.ResumeDTO;
import com.springboot.resumehelper.service.AICheckService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai/check")
public class AICheckController {

    private final AICheckService aiCheckService;

    public AICheckController(AICheckService aiCheckService) {
        this.aiCheckService = aiCheckService;
    }

    @PostMapping
    public ResponseEntity<AICheckResult> checkResume(@RequestBody ResumeDTO resumeDTO) {
        AICheckResult result = aiCheckService.checkResume(resumeDTO);
        return ResponseEntity.ok(result);
    }
}    