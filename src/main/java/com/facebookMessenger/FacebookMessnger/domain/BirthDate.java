package com.facebookMessenger.FacebookMessnger.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BirthDate {
	String Month;
	int day;
	int year;
}
