package com.example.ai.config;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.PgVectorStore;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class OpenAiChatClientConfig {
    private final PgVectorStore pgVectorStore;
    private final ChatClient openAiChatClient;

    public OpenAiChatClientConfig(PgVectorStore pgVectorStore, ChatClient openAiChatClient) {
        this.pgVectorStore = pgVectorStore;
        this.openAiChatClient = openAiChatClient;
    }

    public String chat(String message, Resource template) {
        List<Document> documents = this.pgVectorStore.similaritySearch(message);
        String collect =
                documents.stream().map(Document::getContent).collect(Collectors.joining(System.lineSeparator()));
        Message createdMessage = new SystemPromptTemplate(template).createMessage(Map.of("documents", collect));
        UserMessage userMessage = new UserMessage(message);
        Prompt prompt = new Prompt(List.of(createdMessage, userMessage));
        ChatResponse chatResponse = openAiChatClient.call(prompt);
        return chatResponse.getResults().stream().map(generation -> {
            return generation.getOutput().getContent();
        }).collect(Collectors.joining("/n"));
    }

}
