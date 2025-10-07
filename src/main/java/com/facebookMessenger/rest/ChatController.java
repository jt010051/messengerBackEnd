package com.facebookMessenger.rest;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import com.facebookMessenger.domain.ChatMessage;
import com.facebookMessenger.domain.ChatNotification;
import com.facebookMessenger.service.ChatMessageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;
@MessageMapping("/chat")
public void processMessage(@Payload ChatMessage chatMessage) {
        ChatMessage savedMsg = chatMessageService.save(chatMessage);
        messagingTemplate.convertAndSendToUser(
                chatMessage.getRecipientId(), "/queue/messages",
                new ChatNotification(
                        savedMsg.getId(),
                        savedMsg.getSenderId(),
                        savedMsg.getRecipientId(),
                        savedMsg.getContent(),
                        savedMsg.isRead()
                )
        );
}
@GetMapping("/isread/{senderId}/{recipientId}")
public ResponseEntity<Boolean> messageIsRead(@PathVariable("senderId") String senderId,
        @PathVariable("recipientId") String recipientId) {
	 return ResponseEntity.ok(chatMessageService.checkIsRead(senderId, recipientId));
}
@PutMapping("/read/{senderId}/{recipientId}")
public ResponseEntity<?> readMessage(@PathVariable("senderId") String senderId,
        @PathVariable("recipientId") String recipientId) {
	chatMessageService.readMessage(senderId, recipientId);
	 return ResponseEntity.ok().build();
}
@GetMapping("/messages/{senderId}/{recipientId}")
public ResponseEntity<List<ChatMessage>> findChatMessages(@PathVariable("senderId") String senderId,
                                                 @PathVariable("recipientId") String recipientId) { 
	
        return ResponseEntity.ok(chatMessageService.
        	   findChatMessage(senderId, recipientId));
}
}
