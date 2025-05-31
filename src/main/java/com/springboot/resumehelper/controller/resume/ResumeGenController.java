package com.springboot.resumehelper.controller.resume;

import com.springboot.resumehelper.model.dto.resume.ResumeGenRequest;
import com.springboot.resumehelper.service.ResumeService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpHeaders;
@RestController
@RequestMapping("/api/resume")
public class ResumeGenController {

    private final ResumeService resumeService;

    public ResumeGenController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @PostMapping("/generate")
    public ResponseEntity<byte[]> generateResume(@RequestBody ResumeGenRequest request) {
        byte[] generatedResume = resumeService.generateResume(request);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "generated_resume.pdf");
        return ResponseEntity.ok()
                .headers(headers)
                .body(generatedResume);
    }
}    