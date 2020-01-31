package com.indeas.curso.ws;

import java.util.Arrays;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.indeas.curso.ws.domain.Role;
import com.indeas.curso.ws.domain.User;
import com.indeas.curso.ws.repository.RoleRepository;
import com.indeas.curso.ws.repository.UserRepository;

@SpringBootApplication
public class WsApplication implements CommandLineRunner {
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
    RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(WsApplication.class, args);
	}

	public void run(String... args) throws Exception {
		
		/*
		Role roleAdmin = createRoleIfNotFound("ROLE_ADMIN");
        Role roleUser = createRoleIfNotFound("ROLE_USER");

        roleRepository.saveAll(Arrays.asList(roleAdmin,roleUser));
        
        User joao = new User("João", "Souza", "joao@gmail.com");
        User maria = new User("Maria", "Teixeira", "maria@gmail.com");

        joao.setRoles(Arrays.asList(roleAdmin));
        joao.setPassword(passwordEncoder.encode("123"));
        joao.setEnabled(true);
        maria.setRoles(Arrays.asList(roleUser));
        maria.setPassword(passwordEncoder.encode("123"));
        maria.setEnabled(true);
		
        userRepository.saveAll(Arrays.asList(joao,maria));
        */
		
	}
	
	private User createUserIfNotFound(final User user) {
        Optional<User> obj = userRepository.findByEmail(user.getEmail());
        if(obj.isPresent()) {
            return obj.get();
        }
        return userRepository.save(user);
    }

    private Role createRoleIfNotFound(String name){
        Optional<Role> role = roleRepository.findByName(name);
        if (role.isPresent()){
            return role.get();
        }
        return roleRepository.save(new Role(name));
    }
	
}
