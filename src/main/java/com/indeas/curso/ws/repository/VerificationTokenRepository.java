package com.indeas.curso.ws.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.indeas.curso.ws.domain.User;
import com.indeas.curso.ws.domain.VerificationToken;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

	Optional<VerificationToken> findByToken(String token);

    Optional<VerificationToken> findByUser(User user);
	
}
