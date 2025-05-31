package com.springboot.resumehelper.controller.ai;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("chat")
public class ChatController {
    @Autowired
    private ChatClient chatClient;

    @GetMapping("/chat")
    public String chat(@RequestParam("msg") String msg) {
        System.out.println(msg);
        return chatClient.prompt().user(msg).call().content();
    }
    @GetMapping("/streamChat")
    public Flux<String> streamChat(@RequestParam("msg") String msg, HttpServletResponse response) {
        System.out.println(msg);
        response.setContentType("text/html;charset=utf-8");
        return chatClient.prompt().user(msg).stream().content();
    }
}
