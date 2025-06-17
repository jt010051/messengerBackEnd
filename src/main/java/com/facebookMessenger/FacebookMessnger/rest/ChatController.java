package com.facebookMessenger.FacebookMessnger.rest;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.facebookMessenger.FacebookMessnger.domain.ChatMessage;
import com.facebookMessenger.FacebookMessnger.domain.ChatNotification;
import com.facebookMessenger.FacebookMessnger.domain.User;
import com.facebookMessenger.FacebookMessnger.service.ChatMessageService;
import com.facebookMessenger.FacebookMessnger.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;
    private final UserService service;
@MessageMapping("/chat")
public void processMessage(@Payload ChatMessage chatMessage) {
        ChatMessage savedMsg = chatMessageService.save(chatMessage);
        messagingTemplate.convertAndSendToUser(
                chatMessage.getRecipientId(), "/queue/messages",
                new ChatNotification(
                        savedMsg.getId(),
                        savedMsg.getSenderId(),
                        savedMsg.getRecipientId(),
                        savedMsg.getContent()
                )
        );
}

@GetMapping("/messages/{senderId}/{recipientId}")
public ResponseEntity<List<ChatMessage>> findChatMessages(@PathVariable("senderId") String senderId,
                                                 @PathVariable("recipientId") String recipientId) { 
	
        return ResponseEntity.ok(chatMessageService.
        	   findChatMessage(senderId, recipientId));
}
}
