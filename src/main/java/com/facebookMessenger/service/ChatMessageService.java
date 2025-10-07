package com.facebookMessenger.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.facebookMessenger.domain.ChatMessage;
import com.facebookMessenger.repository.ChatMessageRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageService {
	private final ChatMessageRepository repository;
	private final ChatRoomService chatRoomService;
	
	public ChatMessage save(ChatMessage chatMessage) {
		var chatId = chatRoomService.getChatRoomId(
						chatMessage.getSenderId(), 
						chatMessage.getRecipientId(), true).orElseThrow();
						chatMessage.setChatId(chatId);
						chatMessage.isRead();
						repository.save(chatMessage);
						log.info(chatMessage.toString());
						return chatMessage;
	}
	public void readMessage(String senderId, String recipientId) {
		 List<ChatMessage> thisChatMessage = findChatMessage(senderId, recipientId);
	if(thisChatMessage.isEmpty()) return;
		 ChatMessage currentChatMessage = thisChatMessage.get(thisChatMessage.size()-1);
		 currentChatMessage.setRead(true);
			repository.save(currentChatMessage);
			log.info(currentChatMessage.toString());

	}
	public boolean checkIsRead(String senderId, String recipientId) {
		 List<ChatMessage> thisChatMessage = findChatMessage(senderId, recipientId);
	if(thisChatMessage.isEmpty()) return true;
		 ChatMessage currentChatMessage = thisChatMessage.get(thisChatMessage.size()-1);
System.out.println(currentChatMessage.isRead());
		 return currentChatMessage.isRead();

	}
	public List<ChatMessage> findChatMessage(String senderId, String recipientId){
		var chatId = chatRoomService.getChatRoomId(senderId, 
					 recipientId, false);
		
		return chatId.map(repository ::findByChatId).orElse(new ArrayList<>());
	}
}
