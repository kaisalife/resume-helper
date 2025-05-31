package com.springboot.resumehelper.controller.ai;

import com.springboot.resumehelper.model.dto.ai.AISimulateResult;
import com.springboot.resumehelper.model.dto.resume.ResumeDTO;
import com.springboot.resumehelper.service.AISimulateService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ai/simulate")
public class AISimulateController {

    private final AISimulateService aiSimulateService;

    public AISimulateController(AISimulateService aiSimulateService) {
        this.aiSimulateService = aiSimulateService;
    }

    @PostMapping
    public ResponseEntity<AISimulateResult> simulateFiltering(@RequestBody ResumeDTO resumeDTO) {
        AISimulateResult result = aiSimulateService.simulateFiltering(resumeDTO);
        return ResponseEntity.ok(result);
    }
}    