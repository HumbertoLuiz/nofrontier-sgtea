package br.com.xfrontier.sgetea.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.xfrontier.sgetea.auth.model.AuthUser;
import br.com.xfrontier.sgetea.core.repository.UserRepository;

@Service
public class AuthService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		var mensagem = String.format("User with email %s not found", email);

		return userRepository.findByEmail(email).map(AuthUser::new)
				.orElseThrow(() -> new UsernameNotFoundException(mensagem));
	}

}