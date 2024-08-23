package com.facebookMessenger.FacebookMessnger.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.facebookMessenger.FacebookMessnger.domain.ChatRoom;

public interface ChatRoomRepository  extends JpaRepository<ChatRoom, String>{
	Optional<ChatRoom> findBySenderIdAndRecipientId(String senderId, String recipientId);
}
