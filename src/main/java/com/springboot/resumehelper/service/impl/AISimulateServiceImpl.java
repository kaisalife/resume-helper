package com.springboot.resumehelper.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.resumehelper.controller.ai.ChatController;
import com.springboot.resumehelper.model.dto.ai.AISimulateResult;
import com.springboot.resumehelper.model.dto.resume.ResumeDTO;
import com.springboot.resumehelper.model.entity.Resume;
import com.springboot.resumehelper.service.AISimulateService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AISimulateServiceImpl implements AISimulateService {
    @Autowired
    ChatClient chatClient;
    @Override
    public AISimulateResult simulateFiltering(ResumeDTO resume) {
        AISimulateResult result = new AISimulateResult();
        String content = chatClient.prompt()
                .system("你是专业HR面试官，负责简历筛选。需严格按指定JSON格式返回结果，确保可反序列化为AISimulateResult对象")
                .user("请根据以下简历信息生成结构化评估结果：\n" + resume.getAllContent() +
                        "\n输出格式必须为JSON，字段与AISimulateResult类严格对应，示例：\n" +
                        "{\"resumeId\":123,\"passed\":true,\"score\":85,\"evaluation\":\"专业匹配度高...\",\"improvement\":\"建议补充云计算技能...\",\"interviewer\":\"技术面试官\",\"interviewType\":\"技术面\",\"transcript\":\"模拟对话...\",\"analysis\":\"详细分析...\"}")
                .call().content();

        // 清理JSON前后可能的标记（如```json和```）
        content = content.replaceAll("```json", "").replaceAll("```", "").trim();
        System.out.println("大模型返回的内容：" + content);

        try {
            // 使用Jackson将JSON字符串映射到AISimulateResult对象
            ObjectMapper objectMapper = new ObjectMapper();
            result = objectMapper.readValue(content, AISimulateResult.class);
            // 设置关联的简历ID（如果AI未自动填充）
            result.setResumeId(resume.getId());
        } catch (JsonProcessingException e) {
            // 处理JSON解析异常
            System.err.println("解析AI返回的JSON失败：" + e.getMessage());
            System.err.println("原始内容：" + content);
            // 设置默认失败结果
            result.setPassed(false);
            result.setScore(0);
            result.setEvaluation("简历解析失败，请检查格式是否符合JSON规范");
        }

        return result;
    }
}    