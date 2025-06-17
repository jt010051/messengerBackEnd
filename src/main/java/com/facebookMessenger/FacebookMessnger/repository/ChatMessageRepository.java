package com.facebookMessenger.FacebookMessnger.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.facebookMessenger.FacebookMessnger.domain.ChatMessage;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, String> {
	List<ChatMessage> findByChatId(String s);
}
