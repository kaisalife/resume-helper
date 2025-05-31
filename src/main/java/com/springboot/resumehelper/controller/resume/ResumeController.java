package com.springboot.resumehelper.controller.resume;

import com.springboot.resumehelper.model.dto.resume.ResumeDTO;
import com.springboot.resumehelper.service.ResumeService;
import io.jsonwebtoken.ClaimJwtException;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resume")
public class ResumeController {

    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @PostMapping("/save")
    public ResponseEntity<ResumeDTO> createResume(@RequestBody ResumeDTO resumeDTO) {
        ResumeDTO createdResume = resumeService.createResume(resumeDTO);
        return ResponseEntity.ok(createdResume);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResumeDTO> getResume(@PathVariable Long id) {
        ResumeDTO resume = resumeService.getResumeById(id);
        return ResponseEntity.ok(resume);
    }

    @GetMapping("/list")
    public ResponseEntity<List<ResumeDTO>> getAllResumes() {
        List<ResumeDTO> resumes = resumeService.getAllResumes();
        return ResponseEntity.ok(resumes);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResumeDTO> updateResume(@PathVariable Long id, @RequestBody ResumeDTO resumeDTO) {
        ResumeDTO updatedResume = resumeService.updateResume(id, resumeDTO);
        return ResponseEntity.ok(updatedResume);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResume(@PathVariable Long id) {
        resumeService.deleteResume(id);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/share/{resumeTitle}")
    public ResponseEntity<Boolean> updateShareStatus(@PathVariable String resumeTitle) {
       boolean isShare = resumeService.updateShareStatus(resumeTitle);
        return ResponseEntity.ok(isShare);
    }
}    