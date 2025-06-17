package com.facebookMessenger.FacebookMessnger.domain;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class ChatRoom {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(unique = true)

	private Long id;
	private String chatId;
	private String senderId;
	private String recipientId;
}
