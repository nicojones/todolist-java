package com.minimaltodo.list.user;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class UserService implements UserDetailsService {

	private final UserRepository repository;

	@Autowired
	public UserService(UserRepository repository) {
		this.repository = repository;
	}

	public List<User> getAllUsers() {
		return repository.findAll();
	}

	public User loadUserByUsername(String email) throws UsernameNotFoundException {
		Optional<User> optionalUser = repository.findUserByEmail(email);

		if (optionalUser.isEmpty()) {
			throw new UsernameNotFoundException("username not found");
		}

		return optionalUser.orElse(null);
	}

	public void addnewUser(User user) throws IllegalStateException {
		Optional<User> optionalUser = repository.findUserByEmail(user.getEmail());

		if (optionalUser.isPresent()) {
			throw new IllegalStateException("email taken");
		}

		repository.save(user);
	}

	public List<User> findByEmail(String email) {
		return repository.findAllByEmail(email);
	}

}
