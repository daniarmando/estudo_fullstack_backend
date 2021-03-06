package com.indeas.curso.ws.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.indeas.curso.ws.domain.User;
import com.indeas.curso.ws.dto.UserDTO;
import com.indeas.curso.ws.resources.util.GenericResponse;
import com.indeas.curso.ws.service.UserService;

@RestController
@RequestMapping("/api/public")
public class RegistrationResource {

	@Autowired
    UserService userService;

    @PostMapping("/registration/users")
    public ResponseEntity<Void> registerUser(@RequestBody UserDTO userDTO){
        User user = this.userService.fromDTO(userDTO);
        this.userService.registerUser(user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/regitrationConfirm/users")
    public ResponseEntity<GenericResponse> confirmRegistrationUser(@RequestParam("token") String token){
        final Object result = this.userService.validateVerificationToken(token);
        if (result == null) {
            return ResponseEntity.ok().body(new GenericResponse("Success"));
        }
        return ResponseEntity.status(HttpStatus.SEE_OTHER).body(new GenericResponse(result.toString()));
    }

    @GetMapping(value = "/resendRegistrationToken/users")
    public ResponseEntity<Void> resendRegistrationToken(@RequestParam("email") String email) {
        this.userService.generateNewVerificationToken(email, 0);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping(value="/resetPassword/users")
    public ResponseEntity<Void> resetPassword(@RequestParam("email") final String email){
    	this.userService.generateNewVerificationToken(email, 1);
    	return ResponseEntity.noContent().build();
    }

}
