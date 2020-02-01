package com.indeas.curso.ws.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.indeas.curso.ws.service.email.EmailService;
import com.indeas.curso.ws.service.email.SmtpEmailService;

@Configuration
@PropertySource("classpath:application.properties")
public class EmailConfig {

	@Bean
	public EmailService emailService(){
        return new SmtpEmailService();
    }

}
