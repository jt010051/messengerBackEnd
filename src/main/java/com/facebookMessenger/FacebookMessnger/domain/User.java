package com.facebookMessenger.FacebookMessnger.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long ID;
private String email;
private String password;
private String firstName;
private String lastName;
private String birthDate;
private String gender;


@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
private Collection<Roles> roles = new ArrayList<>();

private String phone;
private boolean isPending;
private Status status;

@OneToMany(cascade = CascadeType.ALL)
private List<User> friends;

}
