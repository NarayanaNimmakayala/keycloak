package com.narayana.keycloak.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.common.util.CollectionUtil;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.narayana.keycloak.dto.Role;
import com.narayana.keycloak.dto.User;
import com.narayana.keycloak.security.KeycloakSecurityUtil;

import jakarta.ws.rs.core.Response;

@RestController
@RequestMapping("/keycloak/api/v1/user")
//@SecurityRequirement(name = "Keycloak")
public class UserController {
	
	@Autowired
	KeycloakSecurityUtil keycloakUtil;
	
	@Value("${realm}")
	private String realm;	
	
	@GetMapping(value="/getusers")
//	@Operation(summary = "View User's", tags = "USER")
	public List<User> getUsers() {
		Keycloak keycloak = keycloakUtil.getKeycloakInstance();
		List<UserRepresentation> userRepresentations = 
				keycloak.realm(realm).users().list();
		return mapUsers(userRepresentations);
    }
	
	@GetMapping(value = "/getuser/{id}")
//	@Operation(summary = "View User", tags = "USER")
	public User getUser(@PathVariable("id") String id) {
		Keycloak keycloak = keycloakUtil.getKeycloakInstance();
		return mapUser(keycloak.realm(realm).users().get(id).toRepresentation());
	}
	
	@PostMapping(value = "/createuser")
//	@Operation(summary = "Create User", tags = "USER")
	public Response createUser(@RequestBody User user) {
		UserRepresentation userRep = mapUserRep(user);
		Keycloak keycloak = keycloakUtil.getKeycloakInstance();
		Response res = keycloak.realm(realm).users().create(userRep);
		return Response.ok(user).build();
	}
	
	@PutMapping(value = "/updateuser")
//	@Operation(summary = "Update User", tags = "USER")
	public Response updateUser(@RequestBody User user) {
		UserRepresentation userRep = mapUserRep(user);
		Keycloak keycloak = keycloakUtil.getKeycloakInstance();
		keycloak.realm(realm).users().get(user.getId()).update(userRep);
		return Response.ok(user).build();
	}
	
	@DeleteMapping(value = "/deleteusers/{id}")
//	@Operation(summary = "Delete User", tags = "USER")
	public Response deleteUser(@PathVariable("id") String id) {
		Keycloak keycloak = keycloakUtil.getKeycloakInstance();
		keycloak.realm(realm).users().delete(id);
		return Response.ok().build();
	}
	
	@GetMapping(value = "/users/{id}/roles")
//	@Operation(summary = "Get Roles", tags = "USER")
	public List<Role> getRoles(@PathVariable("id") String id) {
		Keycloak keycloak = keycloakUtil.getKeycloakInstance();
		return RoleResource.mapRoles(keycloak.realm(realm).users()
				.get(id).roles().realmLevel().listAll());
	}

	@PostMapping(value = "/users/{id}/roles/{roleName}")
//	@Operation(summary = "Create Role", tags = "USER")
	public Response createRole(@PathVariable("id") String id, 
			@PathVariable("roleName") String roleName) {
		Keycloak keycloak = keycloakUtil.getKeycloakInstance();
		RoleRepresentation role = keycloak.realm(realm).roles().get(roleName).toRepresentation();
		keycloak.realm(realm).users().get(id).roles().realmLevel().add(Arrays.asList(role));
		return Response.ok().build();
	}

	private List<User> mapUsers(List<UserRepresentation> userRepresentations) {
		List<User> users = new ArrayList<>();
		if(CollectionUtil.isNotEmpty(userRepresentations)) {
			userRepresentations.forEach(userRep -> {
				users.add(mapUser(userRep));
			});
		}
		return users;
	}
	
	private User mapUser(UserRepresentation userRep) {
		User user = new User();
		user.setId(userRep.getId());
		user.setFirstName(userRep.getFirstName()!=null?userRep.getFirstName():null);
		user.setLastName(userRep.getLastName()!=null?userRep.getLastName():null);
		user.setEmail(userRep.getEmail()!=null?userRep.getEmail():null);
		user.setUsername(userRep.getUsername()!=null?userRep.getUsername():null);
		if(userRep.getAttributes()!=null) {
		user.setMobileNumb(userRep.getAttributes().get("Mobile_num").get(0));
		}
		return user;
	}
	
	private UserRepresentation mapUserRep(User user) {
		UserRepresentation userRep = new UserRepresentation();
		userRep.setId(user.getId());
		userRep.setUsername(user.getUsername()!=null?user.getUsername():null);
		userRep.setFirstName(user.getFirstName()!=null?user.getFirstName():null);
		userRep.setLastName(user.getLastName()!=null?user.getLastName():null);
		userRep.setEmail(user.getEmail()!=null?user.getEmail():null);
		userRep.setEnabled(true);
		userRep.setEmailVerified(true);
		Map<String, List<String>> attributes = new HashMap<>();
		List<String> mob_num=new ArrayList<String>();
		mob_num.add(user.getMobileNumb()!=null?user.getMobileNumb():null);
		attributes.put("Mobile_num", mob_num);
		userRep.setAttributes(attributes);
		List<CredentialRepresentation> creds = new ArrayList<>();
		CredentialRepresentation cred = new CredentialRepresentation();
		cred.setTemporary(false);
		cred.setValue(user.getPassword()!=null?user.getPassword():null);
		creds.add(cred);
		userRep.setCredentials(creds);
		return userRep;
	}
}
