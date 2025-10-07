package com.facebookMessenger.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.facebookMessenger.domain.ChatRoom;

@Repository
public interface ChatRoomRepository  extends JpaRepository<ChatRoom, String>{
	Optional<ChatRoom> findBySenderIdAndRecipientId(String senderId, String recipientId);
}
