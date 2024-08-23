package com.facebookMessenger.FacebookMessnger.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.facebookMessenger.FacebookMessnger.domain.ChatMessage;
import com.facebookMessenger.FacebookMessnger.repository.ChatMessageRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
	private final ChatMessageRepository repository;
	private final ChatRoomService chatRoomService;
	
	public ChatMessage save(ChatMessage chatMessage) {
		var chatId = chatRoomService.getChatRoomId(
						chatMessage.getSenderId(), 
						chatMessage.getRecipientId(), true).orElseThrow();
						chatMessage.setChatId(chatId);
		repository.save(chatMessage);
		
		return chatMessage;
	}
	
	public List<ChatMessage> findChatMessage(String senderId, String recipientId){
		var chatId = chatRoomService.getChatRoomId(senderId, 
					 recipientId, false);
		
		return chatId.map(repository ::findByChatId).orElse(new ArrayList<>());
	}
}
