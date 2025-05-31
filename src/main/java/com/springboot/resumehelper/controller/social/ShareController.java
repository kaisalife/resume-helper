package com.springboot.resumehelper.controller.social;

import com.springboot.resumehelper.model.dto.social.SharePostDTO;
import com.springboot.resumehelper.service.ShareService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/social")
public class ShareController {

    private final ShareService shareService;

    public ShareController(ShareService shareService) {
        this.shareService = shareService;
    }


    @GetMapping("/{id}")
    public ResponseEntity<SharePostDTO> getSharePost(@PathVariable Long id) {
        SharePostDTO post = shareService.getSharePostById(id);
        return ResponseEntity.ok(post);
    }

    @GetMapping("/share-resumes")
    public ResponseEntity<List<SharePostDTO>> getAllSharePosts() {
        List<SharePostDTO> posts = shareService.getAllSharePosts();
        return ResponseEntity.ok(posts);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SharePostDTO> updateSharePost(@PathVariable Long id, @RequestBody SharePostDTO sharePostDTO) {
        SharePostDTO updatedPost = shareService.updateSharePost(id, sharePostDTO);
        return ResponseEntity.ok(updatedPost);
    }

}    