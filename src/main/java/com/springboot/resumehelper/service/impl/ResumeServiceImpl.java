package com.springboot.resumehelper.service.impl;

import com.springboot.resumehelper.model.dto.resume.EducationDTO;
import com.springboot.resumehelper.model.dto.resume.ResumeDTO;
import com.springboot.resumehelper.model.dto.resume.ResumeGenRequest;
import com.springboot.resumehelper.model.entity.Education;
import com.springboot.resumehelper.model.entity.Resume;
import com.springboot.resumehelper.model.entity.ResumeSection;
import com.springboot.resumehelper.model.entity.SharePost;
import com.springboot.resumehelper.repository.ResumeRepository;
import com.springboot.resumehelper.repository.SharePostRepository;
import com.springboot.resumehelper.security.CustomUserDetails;
import com.springboot.resumehelper.service.ResumeService;
import com.springboot.resumehelper.service.ShareService;
import com.springboot.resumehelper.util.ResumeMapper;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.SocketHandler;
import java.util.stream.Collectors;

@Service
public class ResumeServiceImpl implements ResumeService {
    ShareService shareService;
    private final ResumeRepository resumeRepository;
    private final ModelMapper modelMapper;

    // 获取当前用户 ID
    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof CustomUserDetails) {
            return ((CustomUserDetails) principal).getUserId();
        }
        else{
            System.out.println("错了Principal is not a custom user");
        }
        // 如果不是 CustomUserDetails 类型，抛出异常
        throw new IllegalStateException("无法获取用户 ID，认证主体类型不匹配");
    }

    public ResumeServiceImpl(ResumeRepository resumeRepository, ModelMapper modelMapper,ShareService shareService) {
        this.resumeRepository = resumeRepository;
        this.modelMapper = modelMapper;
        this.shareService = shareService;
    }

    @Override
    public ResumeDTO createResume(ResumeDTO resumeDTO) {

        Long userId = getCurrentUserId();
        // 原有逻辑：获取用户所有简历（用于检查标题重复）
        List<Resume> userResumes = resumeRepository.findByUserId(userId);
        String resumeTitle = resumeDTO.getResumeTitle();

        // === 新增：检查是否存在相同标题的简历 ===
        Resume existingResume = null;
        for (Resume r : userResumes) {
            if (r.getResumeTitle().equals(resumeTitle)) { // 严格匹配标题（区分大小写）
                existingResume = r;
                break;
            }
        }
        // ==========================================

        // 原有逻辑：转换 DTO 为实体
        Resume resumeEntity = modelMapper.map(resumeDTO, Resume.class);

        resumeEntity.setName(resumeDTO.getName());
        resumeEntity.setEmail(resumeDTO.getEmail());
        resumeEntity.setPhone(resumeDTO.getPhone());
        resumeEntity.setObjective(resumeDTO.getObjective());
        resumeEntity.setSkills(resumeDTO.getSkills());
        resumeEntity.setResumeScore(resumeDTO.getResumeScore());
        resumeEntity.setIsShare(resumeDTO.getIsShare());
        resumeEntity.setResumeTitle(resumeDTO.getResumeTitle());
        
        // 转换 sections（原有逻辑）
        List<ResumeSection> sections = resumeDTO.getSections().stream()
                .map(ResumeMapper::toEntity)
                .collect(Collectors.toList());

        sections.forEach(section -> section.setResume(resumeEntity));
        resumeEntity.setSections(sections);
        
        // 处理教育背景
        if (resumeDTO.getEducation() != null && !resumeDTO.getEducation().isEmpty()) {
            List<Education> educations = new ArrayList<>();
            
            // 过滤掉不完整的教育背景记录
            for (EducationDTO educationDTO : resumeDTO.getEducation()) {
                // 检查必填字段
                if (educationDTO.getSchool() != null && !educationDTO.getSchool().trim().isEmpty() &&
                    educationDTO.getMajor() != null && !educationDTO.getMajor().trim().isEmpty() &&
                    educationDTO.getTime() != null && !educationDTO.getTime().trim().isEmpty()) {
                    
                    Education education = ResumeMapper.toEntity(educationDTO);
                    education.setResume(resumeEntity);
                    educations.add(education);
                }
            }
            
            // 只有当有有效的教育背景记录时才设置到简历实体
            if (!educations.isEmpty()) {
                resumeEntity.setEducations(educations);
            }
        }
        
        resumeEntity.setUserId(userId);

        // === 新增：根据是否存在执行创建或更新 ===
        Resume savedResume;
        if (existingResume != null) {
            // 更新现有简历（直接覆盖所有字段，保留原有 ID）
            resumeEntity.setId(existingResume.getId()); // 关键：设置现有简历的 ID
            savedResume = resumeRepository.save(resumeEntity);
        } else {
            // 原有创建逻辑
            savedResume = resumeRepository.save(resumeEntity);
        }
        // ==========================================

        // 转换回 DTO
        ResumeDTO resultDTO = modelMapper.map(savedResume, ResumeDTO.class);
        
        // 手动映射教育背景，确保正确转换
        if (savedResume.getEducations() != null && !savedResume.getEducations().isEmpty()) {
            resultDTO.setEducation(
                savedResume.getEducations().stream()
                    .map(ResumeMapper::toDto)
                    .collect(Collectors.toList())
            );
        }
        
        return resultDTO;
    }

    @Override
    public ResumeDTO getResumeById(Long id) {
        Optional<Resume> resumeOptional = resumeRepository.findById(id);
        if (resumeOptional.isPresent()) {
            Resume resume = resumeOptional.get();
            ResumeDTO dto = modelMapper.map(resume, ResumeDTO.class);
            
            // 手动映射教育背景
            if (resume.getEducations() != null && !resume.getEducations().isEmpty()) {
                dto.setEducation(
                    resume.getEducations().stream()
                        .map(ResumeMapper::toDto)
                        .collect(Collectors.toList())
                );
            }
            
            return dto;
        }
        return null;
    }

    @Override
    public List<ResumeDTO> getAllResumes() {
        List<Resume> resumes = resumeRepository.findByUserId(getCurrentUserId());
        return resumes.stream()
               .map(resume -> {
                   ResumeDTO dto = modelMapper.map(resume, ResumeDTO.class);
                   
                   // 手动映射教育背景
                   if (resume.getEducations() != null && !resume.getEducations().isEmpty()) {
                       dto.setEducation(
                           resume.getEducations().stream()
                               .map(ResumeMapper::toDto)
                               .collect(Collectors.toList())
                       );
                   }
                   
                   return dto;
               })
               .collect(Collectors.toList());
    }

    @Override
    public ResumeDTO updateResume(Long id, ResumeDTO resumeDTO) {
        Optional<Resume> resumeOptional = resumeRepository.findById(id);
        if (resumeOptional.isPresent()) {
            Resume resume = resumeOptional.get();
            modelMapper.map(resumeDTO, resume);
            
            // 处理教育背景更新
            if (resumeDTO.getEducation() != null) {
                // 清除旧的教育背景
                resume.getEducations().clear();
                
                // 添加新的教育背景，过滤掉不完整的记录
                List<Education> educations = new ArrayList<>();
                
                for (EducationDTO educationDTO : resumeDTO.getEducation()) {
                    // 检查必填字段
                    if (educationDTO.getSchool() != null && !educationDTO.getSchool().trim().isEmpty() &&
                        educationDTO.getMajor() != null && !educationDTO.getMajor().trim().isEmpty() &&
                        educationDTO.getTime() != null && !educationDTO.getTime().trim().isEmpty()) {
                        
                        Education education = ResumeMapper.toEntity(educationDTO);
                        education.setResume(resume);
                        educations.add(education);
                    }
                }
                
                // 只有当有有效的教育背景记录时才添加到简历实体
                if (!educations.isEmpty()) {
                    resume.getEducations().addAll(educations);
                }
            }
            
            Resume updatedResume = resumeRepository.save(resume);
            
            // 转换回DTO
            ResumeDTO resultDTO = modelMapper.map(updatedResume, ResumeDTO.class);
            
            // 手动映射教育背景
            if (updatedResume.getEducations() != null && !updatedResume.getEducations().isEmpty()) {
                resultDTO.setEducation(
                    updatedResume.getEducations().stream()
                        .map(ResumeMapper::toDto)
                        .collect(Collectors.toList())
                );
            }
            
            return resultDTO;
        }
        return null;
    }

    @Override
    public void deleteResume(Long id) {
        resumeRepository.deleteById(id);
    }

    @Override
    public void deleteResumeByTitle(String title) {
        Optional<Resume> resumeOptional = resumeRepository.findByresumeTitle(title);
        if (resumeOptional.isPresent()) {
            Long resumeId = resumeOptional.get().getId();
            shareService.deleteSharePostByResumeId(resumeId);
            resumeRepository.deleteById(resumeId);
        }
    }

    @Override
    public byte[] generateResume(ResumeGenRequest request) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            // 从类路径加载 Noto Sans SC 字体（假设是 OTF 格式，若实际是 TTF 需调整）
            InputStream fontStream = new ClassPathResource("fonts/NotoSansSCMedium-4.ttf").getInputStream();
            PDType0Font font = PDType0Font.load(document, fontStream);

            InputStream fontStream1 = new ClassPathResource("fonts/NotoSansSCLight-3.ttf").getInputStream();
            PDType0Font font1 = PDType0Font.load(document, fontStream1);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // 定义内容流的作用域
                float marginLeft = 36; // 增加左侧边距，使内容更居中
                float marginTop = 750;
                float contentWidth = page.getMediaBox().getWidth() - 2 * marginLeft;
                float lineHeight = 10; // 进一步减小行高，让内容更紧凑
                float currentY = marginTop;
                float titleHeight;
                float contactHeight;
                float skillsTitleHeight;
                float skillsContentHeight;
                float sectionTitleHeight;
                float sectionContentHeight;
                float educationTitleHeight;
                float educationContentHeight;
                String contactInfo;

                // 标题区域 - 动态计算高度 (使用较小字体)
                contentStream.setFont(font, 15); // 进一步减小字体大小
                titleHeight = renderTextBlock(contentStream, font, 15, request.getName(), marginLeft, currentY, lineHeight, contentWidth, false);
                currentY -= titleHeight + 2; // 减少标题与下一部分间距

                // 联系方式区域 - 动态计算高度 (使用较小字体)
                contentStream.setFont(font, 8); // 进一步减小字体大小
                contactInfo = String.format("邮箱: %s | 电话: %s | 求职意向: %s",
                        request.getEmail(), request.getPhone(), request.getObjective());
                contactHeight = renderTextBlock(contentStream, font, 8, contactInfo, marginLeft, currentY, lineHeight, contentWidth, false);
                currentY -= contactHeight + 2; // 减少内容与分隔线间距

                // 添加分隔线
                drawSeparator(contentStream, marginLeft, currentY, contentWidth);
                currentY -= 6; // 减少分隔线与下一部分间距

                // 教育背景部分
                if (request.getEducation() != null && !request.getEducation().isEmpty()) {
                    // 绘制渐变背景 - 直接在分隔线下方
                    float bgHeight = lineHeight + 4;
                    drawGradientTitleBackground(contentStream, marginLeft, currentY + bgHeight/2, contentWidth, bgHeight);
                    
                    // 居中显示白色文本标题
                    contentStream.setFont(font, 10); // 减小标题字体
                    contentStream.setNonStrokingColor(1.0f, 1.0f, 1.0f); // 设置白色文本
                    educationTitleHeight = renderTextBlock(contentStream, font, 10, "教育背景", marginLeft + 5, currentY + bgHeight/4, lineHeight, contentWidth, false);
                    contentStream.setNonStrokingColor(0, 0, 0); // 恢复黑色文本
                    
                    currentY -= educationTitleHeight + 2; // 减少标题与内容间距

                    // 渲染教育背景内容
                    contentStream.setFont(font1, 8); // 减小内容字体
                    
                    for (ResumeGenRequest.Education edu : request.getEducation()) {
                        // 计算每个部分的宽度和位置
                        float schoolWidth = font1.getStringWidth(edu.getSchool()) * 8 / 1000f;
                        float majorWidth = font1.getStringWidth(edu.getMajor()) * 8 / 1000f;
                        float timeWidth = font1.getStringWidth(edu.getTime()) * 8 / 1000f;
                        
                        // 学校左对齐
                        contentStream.beginText();
                        contentStream.setFont(font1, 8);
                        contentStream.newLineAtOffset(marginLeft, currentY);
                        contentStream.showText(edu.getSchool());
                        contentStream.endText();
                        
                        // 专业居中
                        contentStream.beginText();
                        contentStream.setFont(font1, 8);
                        float majorX = marginLeft + (contentWidth - majorWidth) / 2;
                        contentStream.newLineAtOffset(majorX, currentY);
                        contentStream.showText(edu.getMajor());
                        contentStream.endText();
                        
                        // 时间右对齐
                        contentStream.beginText();
                        contentStream.setFont(font1, 8);
                        float timeX = marginLeft + contentWidth - timeWidth;
                        contentStream.newLineAtOffset(timeX, currentY);
                        contentStream.showText(edu.getTime());
                        contentStream.endText();
                        
                        currentY -= lineHeight + 2; // 添加每条教育记录之间的间距
                    }
                    
                    currentY -= 4; // 教育背景部分与下一部分的间距
                }

                // 专业技能部分
                if (request.getSkills() != null && !request.getSkills().isEmpty()) {
                    // 绘制渐变背景 - 直接在分隔线下方
                    float bgHeight = lineHeight + 4;
                    drawGradientTitleBackground(contentStream, marginLeft, currentY + bgHeight/2, contentWidth, bgHeight);
                    
                    // 居中显示白色文本标题
                    contentStream.setFont(font, 10); // 减小标题字体
                    contentStream.setNonStrokingColor(1.0f, 1.0f, 1.0f); // 设置白色文本
                    skillsTitleHeight = renderTextBlock(contentStream, font, 10, "专业技能", marginLeft + 5, currentY + bgHeight/4, lineHeight, contentWidth, false);
                    contentStream.setNonStrokingColor(0, 0, 0); // 恢复黑色文本
                    
                    currentY -= skillsTitleHeight + 2; // 减少标题与内容间距

                    contentStream.setFont(font1, 8); // 减小内容字体
                    skillsContentHeight = renderTextBlock(contentStream, font1, 8,
                            request.getSkills(), marginLeft, currentY, lineHeight, contentWidth - 20, true);
                    currentY -= skillsContentHeight + 6; // 直接减少与下一部分标题的间距，无需分隔线
                }

                // 输出简历模块内容 - 动态计算高度
                int sectionCount = request.getSections().size();
                int currentSection = 0;
                
                for (ResumeGenRequest.Section section : request.getSections()) {
                    currentSection++;
                    // 绘制渐变背景 - 直接在上一部分内容下方
                    float bgHeight = lineHeight + 4;
                    drawGradientTitleBackground(contentStream, marginLeft, currentY + bgHeight/2, contentWidth, bgHeight);
                    
                    // 居中显示白色文本标题
                    contentStream.setFont(font, 10); // 减小标题字体
                    contentStream.setNonStrokingColor(1.0f, 1.0f, 1.0f); // 设置白色文本
                    sectionTitleHeight = renderTextBlock(contentStream, font, 10, section.getLabel(), marginLeft + 5, currentY + bgHeight/4, lineHeight, contentWidth, false);
                    contentStream.setNonStrokingColor(0, 0, 0); // 恢复黑色文本
                    
                    currentY -= sectionTitleHeight + 2; // 减少标题与内容间距

                    // 模块内容
                    contentStream.setFont(font1, 8); // 减小内容字体
                    sectionContentHeight = renderTextBlock(contentStream, font1, 8,
                            section.getContent(), marginLeft, currentY, lineHeight, contentWidth - 20, true);
                    
                    // 如果是最后一个部分，无需添加额外间距
                    if (currentSection < sectionCount) {
                        currentY -= sectionContentHeight + 6; // 直接减少与下一部分标题的间距，无需分隔线
                    } else {
                        currentY -= sectionContentHeight; // 最后一部分内容结束，不需要额外间距
                    }
                }
            } // 内容流在此处自动关闭

            // 在内容流关闭后保存文档
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }
    // 渲染文本块并返回高度
    private float renderTextBlock(PDPageContentStream contentStream, PDType0Font font, float fontSize,
                                  String text, float x, float y, float lineHeight, float maxWidth, boolean addBullets) throws IOException {
        if (text == null || text.isEmpty()) return 0;

        contentStream.beginText();
        contentStream.setFont(font, fontSize);
        contentStream.newLineAtOffset(x, y);

        String[] paragraphs = text.split("\\r?\\n");
        float blockHeight = 0;
        float totalHeight = 0;

        String bullet = "• ";
        float bulletWidth = font.getStringWidth(bullet) * fontSize / 1000f;
        float textStartX = addBullets ? bulletWidth : 0; // 子弹点后文本的起始位置
        
        // 计算用于显示文本的有效宽度（减去子弹点宽度）
        float effectiveWidth = addBullets ? maxWidth - bulletWidth : maxWidth;

        for (int i = 0; i < paragraphs.length; i++) {
            String paragraph = paragraphs[i].trim();
            if (paragraph.isEmpty()) {
                continue;
            }

            // 每个段落开始时重置坐标到行首
            if (i > 0) {
                contentStream.newLineAtOffset(0, -lineHeight);
                totalHeight += lineHeight;
            }

            // 处理段落内的每一行
            String[] lines = paragraph.split("\\r?\\n");
            boolean isFirstLineInParagraph = true; // 标记段落的第一行

            for (int j = 0; j < lines.length; j++) {
                String line = lines[j].trim();
                if (line.isEmpty()) continue;

                // 每行开始时移动到行首位置（除了第一行）
                if (j > 0 || i > 0) {
                    contentStream.newLineAtOffset(-textStartX, 0); // 先移回行首
                }

                // 只在段落的第一行显示子弹点（如果需要）
                if (addBullets && isFirstLineInParagraph) {
                    contentStream.showText(bullet);
                    contentStream.newLineAtOffset(textStartX, 0); // 移动到文本起始位置
                } else if (addBullets) {
                    // 对于段落的后续行，保持相同的缩进但不显示子弹点
                    contentStream.newLineAtOffset(textStartX, 0);
                }

                // 处理当前行文本
                StringBuilder currentLine = new StringBuilder();
                float currentLineWidth = 0;
                
                for (String word : line.split("\\s+")) {
                    float wordWidth = font.getStringWidth(word) * fontSize / 1000;
                    float spaceWidth = font.getStringWidth(" ") * fontSize / 1000;

                    // 检查添加当前单词是否会超出行宽
                    if (!currentLine.isEmpty() && (currentLineWidth + spaceWidth + wordWidth > effectiveWidth)) {
                        // 显示当前行并换行
                        contentStream.showText(currentLine.toString());
                        contentStream.newLineAtOffset(-textStartX, -lineHeight); // 移回行首并下移一行
                        totalHeight += lineHeight;
                        
                        // 续行不需要子弹点，只需要缩进对齐
                        contentStream.newLineAtOffset(textStartX, 0);
                        
                        // 重置当前行
                        currentLine.setLength(0);
                        currentLineWidth = 0;
                        isFirstLineInParagraph = false; // 后续行不再是段落的第一行
                    }

                    // 添加空格和单词
                    if (!currentLine.isEmpty()) {
                        currentLine.append(" ");
                        currentLineWidth += spaceWidth;
                    }
                    currentLine.append(word);
                    currentLineWidth += wordWidth;
                }

                // 显示剩余文本
                if (!currentLine.isEmpty()) {
                    float textWidth = font.getStringWidth(currentLine.toString()) * fontSize / 1000;
                    if (textWidth > effectiveWidth) {
                        StringBuilder part = new StringBuilder();
                        float availableWidth = 0;
                        for (int k = 0; k < currentLine.length(); k++) {
                            char c = currentLine.charAt(k);
                            String character = String.valueOf(c);
                            float charWidth = font.getStringWidth(character) * fontSize / 1000;

                            if (availableWidth + charWidth > effectiveWidth && !part.isEmpty()) {
                                contentStream.showText(part.toString());
                                contentStream.newLineAtOffset(-textStartX, -lineHeight); // 移回行首并下移一行
                                totalHeight += lineHeight;
                                part.setLength(0);
                                availableWidth = 0;

                                // 续行只需缩进，不需要子弹点
                                contentStream.newLineAtOffset(textStartX, 0);
                            }

                            part.append(c);
                            availableWidth += charWidth;
                        }

                        if (!part.isEmpty()) {
                            contentStream.showText(part.toString());
                        }
                    } else {
                        contentStream.showText(currentLine.toString());
                    }
                }

                // 如果还有下一行，换行
                if (j < lines.length - 1) {
                    contentStream.newLineAtOffset(-textStartX, -lineHeight); // 移回行首并下移一行
                    totalHeight += lineHeight;
                    isFirstLineInParagraph = false; // 后续行不再是段落的第一行
                }
            }

            blockHeight += lines.length * lineHeight;

            // 段落间添加减少的空行 (进一步减少为0.2倍行高以减少间距)
            if (i < paragraphs.length - 1 && !paragraphs[i + 1].trim().isEmpty()) {
                // 不需要移动 X 坐标，只需下移一行
                contentStream.newLineAtOffset(0, -lineHeight * 0.2f);
                totalHeight += lineHeight * 0.2f;
            }
        }

        contentStream.endText();
        return totalHeight > 0 ? totalHeight : blockHeight; // 返回实际使用的高度
    }
    // 绘制分隔线
    private void drawSeparator(PDPageContentStream contentStream, float x, float y, float width) throws IOException {
        contentStream.setLineWidth(0.5f);
        contentStream.moveTo(x, y);
        contentStream.lineTo(x + width, y);
        contentStream.stroke();
    }
    // 绘制渐变标题背景 (从左至右渐变的浅绿色到白色)
    private void drawGradientTitleBackground(PDPageContentStream contentStream, float x, float y, float width, float height) throws IOException {
        // 保存当前图形状态
        contentStream.saveGraphicsState();
        
        // 设置从浅绿色到白色的渐变
        int steps = 100; // 渐变步数
        float stepWidth = width / steps;
        
        for (int i = 0; i < steps; i++) {
            // 计算该步骤的颜色百分比 - 从左(浅绿色)到右(白色)
            float percent = i / (float)steps;
            
            // 从更深的绿色(120, 210, 120)逐渐过渡到白色(255, 255, 255)
            float r = (120 + (255 - 120) * percent) / 255f;
            float g = (210 + (255 - 210) * percent) / 255f;
            float b = (120 + (255 - 120) * percent) / 255f;
            
            // 设置填充颜色
            contentStream.setNonStrokingColor(r, g, b);
            
            // 绘制一个小矩形作为渐变的一部分
            contentStream.addRect(x + i * stepWidth, y - height/2, stepWidth + 0.5f, height);
            contentStream.fill();
        }
        
        // 恢复图形状态
        contentStream.restoreGraphicsState();
    }
    @Override
    public Boolean updateShareStatus(String resumeTitle) {
        // 1. 获取当前用户ID
        Long userId = getCurrentUserId();
        Boolean isShare=false;
        // 2. 查询用户的所有简历
        List<Resume> userResumes = resumeRepository.findByUserId(userId);

        // 3. 在列表中查找匹配的简历标题（忽略大小写）
        Resume targetResume = userResumes.stream()
                .filter(resume -> resume.getResumeTitle().equalsIgnoreCase(resumeTitle))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("未找到简历: " + resumeTitle));

        isShare=targetResume.getIsShare();
        targetResume.setIsShare(!isShare);
        // 4. 更新分享状态
        if(!isShare) {
            resumeRepository.save(targetResume);

            if (!targetResume.getIsShare()) {
                return false;
            }

            SharePost sharePost = new SharePost();
            sharePost.setResumeId(targetResume.getId());
            shareService.createSharePost(sharePost);
        }
        else{
            shareService.deleteSharePostByResumeId(targetResume.getId());
        }
        return true;
    }

}    