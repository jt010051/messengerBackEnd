package com.facebookMessenger.FacebookMessnger.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.facebookMessenger.FacebookMessnger.domain.ChatMessage;


public interface ChatMessageRepository extends JpaRepository<ChatMessage, String> {
	List<ChatMessage> findByChatId(String s);
}
