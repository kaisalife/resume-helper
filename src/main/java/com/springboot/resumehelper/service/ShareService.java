package com.springboot.resumehelper.service;

import com.springboot.resumehelper.model.dto.social.SharePostDTO;
import com.springboot.resumehelper.model.entity.SharePost;

import java.util.List;

public interface ShareService {

    void createSharePost(SharePost sharePost);

    SharePostDTO getSharePostById(Long id);

    List<SharePostDTO> getAllSharePosts();

    SharePostDTO updateSharePost(Long id, SharePostDTO sharePostDTO);

    void deleteSharePostByResumeId(Long id);
}    