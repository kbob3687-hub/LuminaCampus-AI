package com.luminacampus.trigger.http;

import com.luminacampus.domain.adapter.IPythonGateway;
import com.luminacampus.domain.conversation.model.entity.ConversationEntity;
import com.luminacampus.domain.conversation.service.ConversationService;
import com.luminacampus.types.enums.ResponseCode;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ConversationController {

    private final ConversationService conversationService;
    private final IPythonGateway pythonGateway;

    public ConversationController(ConversationService conversationService, IPythonGateway pythonGateway) {
        this.conversationService = conversationService;
        this.pythonGateway = pythonGateway;
    }

    @PostMapping("/chat")
    public Map<String, Object> chat(@RequestAttribute("userId") Long userId,
                                    @RequestParam String question,
                                    @RequestParam(defaultValue = "") String subject,
                                    @RequestParam(defaultValue = "") String docId) {
        // 调用 Python Agent 服务
        String answer = pythonGateway.chat(question, subject, docId);

        // 保存对话记录
        conversationService.saveConversation(userId, subject, question, answer);

        Map<String, Object> result = new HashMap<>();
        result.put("code", ResponseCode.SUCCESS.getCode());
        result.put("data", answer);
        return result;
    }

    @GetMapping("/conversation/list")
    public Map<String, Object> list(@RequestAttribute("userId") Long userId) {
        List<ConversationEntity> conversations = conversationService.queryByUserId(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("code", ResponseCode.SUCCESS.getCode());
        result.put("data", conversations);
        return result;
    }

}
