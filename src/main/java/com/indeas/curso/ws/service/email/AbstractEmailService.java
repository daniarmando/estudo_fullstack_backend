package com.indeas.curso.ws.service.email;

import java.util.Date;
import java.util.UUID;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.indeas.curso.ws.domain.User;
import com.indeas.curso.ws.domain.VerificationToken;
import com.indeas.curso.ws.service.UserService;
import com.indeas.curso.ws.service.exception.ObjectNotFoundException;

public abstract class AbstractEmailService implements EmailService {

	@Value("${default.sender}")
	private String sender;
	
	@Value("${default.url}")
	private String contextPath;
	
	@Autowired
	private TemplateEngine templateEngine;
	
	@Autowired
	private JavaMailSender javaMailSender;
	
	@Autowired
	private UserService userService;

	@Override
	public void sendConfirmationHtmlEmail(User user, VerificationToken vToken, int select) {
		try {
			MimeMessage mimeMessage = prepareMimeMessageFromUser(user, vToken, select);
			sendHtmlEmail(mimeMessage);
		} catch (MessagingException msg) {
			throw new ObjectNotFoundException(String.format("Erro ao tentar enviar o e-mail"));
		}
	}

	protected MimeMessage prepareMimeMessageFromUser(User user, VerificationToken vToken, int select) throws MessagingException {
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
		mimeMessageHelper.setTo(user.getEmail());
		mimeMessageHelper.setFrom(this.sender);
		mimeMessageHelper.setSubject((select == 1) ? "Reset senha de usuário" : "Confirmação de Registro");
		mimeMessageHelper.setSentDate(new Date((System.currentTimeMillis())));
		mimeMessageHelper.setText(htmlFromTemplateUser(user, vToken, select), true);
		return mimeMessage;
	}

	protected String htmlFromTemplateUser(User user, VerificationToken vToken, int select) {
		String token = UUID.randomUUID().toString();
		if (vToken == null) {
			this.userService.createVerificationTokenForUser(user, token);
		} else {
			token = vToken.getToken();
		}
		String confirmationUrl = (select == 1) ?
				this.contextPath + "/change-password?id"+ user.getId()+"&token=" + token :
				this.contextPath + "/register-confirmation?token=" + token;
		Context context = new Context();
		context.setVariable("user", user);
		context.setVariable("confirmationUrl", confirmationUrl);
		return templateEngine.process("email/registerUser", context);
	}

}
