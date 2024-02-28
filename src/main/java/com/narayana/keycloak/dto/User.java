package com.narayana.keycloak.dto;

import lombok.Data;

@Data
public class User {

	private String id;
	private String username;
	private String password;
	private String firstName;
	private String lastName;
	private String mobileNumb;
	private String email;
}
