package com.springboot.resumehelper.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.resumehelper.model.dto.ai.AICheckRequest;
import com.springboot.resumehelper.model.dto.ai.AICheckResult;
import com.springboot.resumehelper.model.dto.resume.ResumeDTO;
import com.springboot.resumehelper.service.AICheckService;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AICheckServiceImpl implements AICheckService {
    @Autowired // 或 @Autowired
    ChatClient chatClient; // 确保类型正确

    @Override
    public AICheckResult checkResume(ResumeDTO resume) {
        AICheckResult result = new AICheckResult();

        // 优化系统提示词，严格要求JSON格式
        String content = chatClient.prompt()
                .system("""
                你是一位专业的简历审查专家，需根据以下简历内容分析问题并提供修改建议。
                请严格按以下JSON格式返回结果，确保字段与AICheckResult类完全对应：
                {
                  "problems": "语法错误1；拼写错误2；格式问题3",
                  "suggestion": "修改建议1；修改建议2；修改建议3"
                }
                
                注意事项：
                1. 问题和建议都使用分号作为分隔符
                2. 不要包含任何其他文本，仅返回符合格式的JSON
                3. 确保所有特殊字符正确转义（如双引号需转义为\\"）
                """)
                .user("请分析以下简历并按指定JSON格式返回结果：\n" + resume.getAllContent())
                .call().content();

        // 清理JSON前后可能的标记（如```json和```）
        content = content.replaceAll("```json", "").replaceAll("```", "").trim();
        System.out.println("大模型返回的内容：" + content);

        try {
            // 使用Jackson将JSON字符串映射到AICheckResult对象
            ObjectMapper objectMapper = new ObjectMapper();
            result = objectMapper.readValue(content, AICheckResult.class);
        } catch (JsonProcessingException e) {
            // 处理JSON解析异常
            System.err.println("解析AI返回的JSON失败：" + e.getMessage());
            System.err.println("原始内容：" + content);

            // 设置默认错误结果
            result.setProblems("无法解析简历问题，请检查格式");
            result.setSuggestion("请确保AI返回格式符合{\"problems\":\"...\",\"suggestion\":\"...\"}");
        }

        return result;
    }
}    