package com.narayana.keycloak.entity;

import org.keycloak.representations.idm.UserRepresentation;

public class UserEntity extends UserRepresentation {

	protected String mobileNumb;

	public String getMobileNumb() {
		return mobileNumb;
	}

	public void setMobileNumb(String mobileNumb) {
		this.mobileNumb = mobileNumb;
	}
	
}
