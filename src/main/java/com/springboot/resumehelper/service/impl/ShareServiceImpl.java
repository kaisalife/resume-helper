package com.springboot.resumehelper.service.impl;

import com.springboot.resumehelper.model.dto.resume.EducationDTO;
import com.springboot.resumehelper.model.dto.resume.Section;
import com.springboot.resumehelper.model.dto.social.SharePostDTO;
import com.springboot.resumehelper.model.entity.Resume;
import com.springboot.resumehelper.model.entity.SharePost;
import com.springboot.resumehelper.repository.ResumeRepository;
import com.springboot.resumehelper.repository.SharePostRepository;
import com.springboot.resumehelper.service.ShareService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ShareServiceImpl implements ShareService {

    private final SharePostRepository sharePostRepository;
    private final ModelMapper modelMapper;
    private final ResumeRepository resumeRepository;

    public ShareServiceImpl(SharePostRepository sharePostRepository, ModelMapper modelMapper,ResumeRepository resumeRepository) {
        this.sharePostRepository = sharePostRepository;
        this.modelMapper = modelMapper;
        this.resumeRepository = resumeRepository;
    }

    @Override
    public void createSharePost(SharePost sharePost) {
           sharePostRepository.save(sharePost);
    }

    @Override
    public SharePostDTO getSharePostById(Long id) {
        Optional<SharePost> sharePostOptional = sharePostRepository.findById(id);
        return sharePostOptional.map(post -> modelMapper.map(post, SharePostDTO.class)).orElse(null);
    }

    @Override
    public List<SharePostDTO> getAllSharePosts() {
        // 直接调用 JPQL 关联查询获取所有被分享的简历
        List<Resume> resumes = resumeRepository.findAllSharedResumes();

        return resumes.stream()
                .map(resume -> {
                    SharePostDTO dto = new SharePostDTO();
                    dto.setId(resume.getId());
                    dto.setName(resume.getName() != null ? resume.getName() : "匿名用户");
                    dto.setEmail(resume.getEmail() != null ? resume.getEmail() : "default@example.com");
                    dto.setPhone(resume.getPhone() != null ? resume.getPhone() : "隐藏号码");
                    dto.setObjective(resume.getObjective());
                    dto.setSkills(resume.getSkills());
                    dto.setResumeScore(resume.getResumeScore());
                    dto.setResumeTitle(resume.getResumeTitle());

                    // 映射 sections（假设 Resume 的 sections 已包含 label 和 content）
                    dto.setSections(resume.getSections().stream()
                            .map(sectionEntity -> {
                                Section sectionDTO = new Section();
                                sectionDTO.setLabel(sectionEntity.getLabel()); // 使用 ResumeSection 的 label
                                sectionDTO.setContent(sectionEntity.getContent());
                                return sectionDTO;
                            })
                            .collect(Collectors.toList()));
                    
                    // 映射教育背景信息
                    if (resume.getEducations() != null && !resume.getEducations().isEmpty()) {
                        dto.setEducation(resume.getEducations().stream()
                                .map(education -> {
                                    EducationDTO educationDTO = new EducationDTO();
                                    educationDTO.setId(education.getId());
                                    educationDTO.setSchool(education.getSchool());
                                    educationDTO.setMajor(education.getMajor());
                                    educationDTO.setTime(education.getTime());
                                    return educationDTO;
                                })
                                .collect(Collectors.toList()));
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public SharePostDTO updateSharePost(Long id, SharePostDTO sharePostDTO) {
        Optional<SharePost> sharePostOptional = sharePostRepository.findById(id);
        if (sharePostOptional.isPresent()) {
            SharePost sharePost = sharePostOptional.get();
            modelMapper.map(sharePostDTO, sharePost);
            SharePost updatedPost = sharePostRepository.save(sharePost);
            return modelMapper.map(updatedPost, SharePostDTO.class);
        }
        return null;
    }

    @Transactional
    @Override
    public void deleteSharePostByResumeId(Long id){
        sharePostRepository.deleteByResumeId(id);
    }

}    