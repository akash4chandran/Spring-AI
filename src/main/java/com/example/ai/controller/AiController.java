package com.example.ai.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.ai.config.OpenAiChatClientConfig;

@RestController
@RequestMapping("/ai")
public class AiController {
    private final OpenAiChatClientConfig chatClient;

    @Value("classpath:/prompts/assist.st")
    private Resource assist;

    public AiController(OpenAiChatClientConfig chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping
    public String retrieveAlmData(@RequestParam(name = "message") String message) {
        return chatClient.chat(message, assist);
    }
}
