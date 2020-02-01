package com.indeas.curso.ws.service;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.indeas.curso.ws.domain.User;
import com.indeas.curso.ws.domain.VerificationToken;
import com.indeas.curso.ws.dto.UserDTO;
import com.indeas.curso.ws.repository.RoleRepository;
import com.indeas.curso.ws.repository.UserRepository;
import com.indeas.curso.ws.repository.VerificationTokenRepository;
import com.indeas.curso.ws.service.email.EmailService;
import com.indeas.curso.ws.service.exception.ObjectAlreadyExistException;
import com.indeas.curso.ws.service.exception.ObjectNotFoundException;

@Service
public class UserService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired	
	private RoleRepository roleRepository;
	
	@Autowired
	private VerificationTokenRepository verificationTokenRepository;
	
	@Autowired
    private PasswordEncoder passwordEncoder;
	
	@Autowired
    private EmailService emailService;

	public Optional<User> findyByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	public List<User> findAll() {
		return userRepository.findAll();
	}
	
	public  User findById(Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.orElseThrow(() -> new ObjectNotFoundException("Objeto não encontrado!"));
    }

    public User create(User user){
    	user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User fromDTO (UserDTO userDTO) {
        return new User(userDTO);
    }

    public User update(User user) {
        Optional<User> updateUser = userRepository.findById(user.getId());
        return updateUser.map(u -> userRepository.save(new User(u.getId(), user.getFirstName(), user.getLastName(), user.getEmail(),
                                u.getPassword(), u.isEnabled())))
                .orElseThrow(() -> new ObjectNotFoundException("Usuário não encontrado!"));
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }
	
    public User registerUser(User user) {
        if (emailExist(user.getEmail())){
            throw new ObjectAlreadyExistException(String.format("Já extiste uma conta com esse endereço de email"));
        }
        user.setRoles(Arrays.asList(roleRepository.findByName("ROLE_USER").get()));
        user.setEnabled(false);
        user = create(user);
        this.emailService.sendConfirmationHtmlEmail(user, null);
        return user;
    }
	
    private boolean emailExist(final String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if(user.isPresent()){
            return true;
        }
        return false;
    }
    
    public void createVerificationTokenForUser(User user, String token) {
        final VerificationToken vToken = new VerificationToken(token, user);
        verificationTokenRepository.save(vToken);
    }
    
    public String validateVerificationToken(String token) {
        final Optional<VerificationToken> vToken = verificationTokenRepository.findByToken(token);
        if (!vToken.isPresent()) {
            return "invalidToken";
        }
        final User user = vToken.get().getUser();
        final Calendar cal = Calendar.getInstance();
        if ((vToken.get().getExpiryDate().getTime() - cal.getTime().getTime()) <= 0)
        {
            return "expired";
        }
        user.setEnabled(true);
        this.userRepository.save(user);
        return null;
    }

    public User findByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.orElseThrow(() -> new ObjectNotFoundException(String.format("Usuário não encontrado!")));
    }

    public VerificationToken generateNewVerificationToken(String email) {
        User user = findByEmail(email);
        Optional<VerificationToken> vToken = verificationTokenRepository.findByUser(user);
        vToken.get().updateToken(UUID.randomUUID().toString());
        VerificationToken updateVToken = verificationTokenRepository.save(vToken.get());
        emailService.sendConfirmationHtmlEmail(user, updateVToken);
        return updateVToken;
    }

}
