package com.indeas.curso.ws.service.email;

import javax.mail.internet.MimeMessage;

import com.indeas.curso.ws.domain.User;
import com.indeas.curso.ws.domain.VerificationToken;

public interface EmailService {
	
	void sendHtmlEmail(MimeMessage msg);
    void sendConfirmationHtmlEmail(User user, VerificationToken vToken, int select);

}
