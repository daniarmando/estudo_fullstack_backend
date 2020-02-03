package com.indeas.curso.ws.resources;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.indeas.curso.ws.domain.Role;
import com.indeas.curso.ws.domain.User;
import com.indeas.curso.ws.dto.UserDTO;
import com.indeas.curso.ws.service.UserService;

@RestController
@RequestMapping("/api")
public class UserResource {

	@Autowired
	private UserService userService;
	
	@Autowired
	private DefaultTokenServices tokenServices;

	private TokenStore tokenStore = new InMemoryTokenStore();

	@GetMapping("/users")
	public ResponseEntity<List<UserDTO>> findAll() {
		List<User> users = userService.findAll();
		List<UserDTO> listDTO = users.stream().map(x -> new UserDTO(x)).collect(Collectors.toList());
		return ResponseEntity.ok().body(listDTO);
	}

	@GetMapping("/users/{id}")
	public ResponseEntity<UserDTO> findById(@PathVariable Long id) {
		User user = userService.findById(id);
		return ResponseEntity.ok().body(new UserDTO(user));
	}

	@PostMapping("/users")
	public ResponseEntity<UserDTO> create(@RequestBody UserDTO userDTO) {
		User user = userService.fromDTO(userDTO);
		return ResponseEntity.ok().body(new UserDTO(userService.create(user)));
	}

	@PutMapping("/users/{id}")
	public ResponseEntity<UserDTO> update(@PathVariable Long id, @RequestBody UserDTO userDTO) {

		User user = userService.fromDTO(userDTO);
		user.setId(id);
		return ResponseEntity.ok().body(new UserDTO(userService.update(user)));
	}

	@DeleteMapping("/users/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		userService.delete(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/users/{id}/roles")
	public ResponseEntity<List<Role>> findRoles(@PathVariable Long id) {
		User user = userService.findById(id);

		return ResponseEntity.ok().body(user.getRoles());
	}

	@GetMapping(value = "/users/main")
	public ResponseEntity<UserDTO> getUserMain(Principal principal) {
		User user = this.userService.findByEmail(principal.getName());
		UserDTO userDTO = new UserDTO(user);
		userDTO.setPassword("");
		return ResponseEntity.ok().body(userDTO);
	}

	@GetMapping(value = "/logout")
	public ResponseEntity<Void> logout(HttpServletRequest request) {
		String authHeader = request.getHeader("Authorization");
		if (authHeader != null) {
			String tokenValue = authHeader.replace("Bearer", "").trim();
			OAuth2AccessToken accessToken = tokenServices.readAccessToken(tokenValue);
			tokenStore.removeAccessToken(accessToken);
			tokenServices.revokeToken(String.valueOf(accessToken));
		}
		return ResponseEntity.noContent().build();
	}

}
