package ftn.sct;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ftn.sct.model.User;
import ftn.sct.persistance.UserRepository;

@RestController
@RequestMapping("/user")
public class UserController {

	private BCryptPasswordEncoder sfe = new BCryptPasswordEncoder();

	@Autowired
	private UserRepository repository;

	@RequestMapping(value = "/find/{username}", method = RequestMethod.GET)
	public User findUser(@PathVariable String username) {
		return repository.findByUsername(username);
	}

	@RequestMapping(value = "/password/{username}/{password}", method = RequestMethod.GET)
	public boolean validateUser(@PathVariable String username, @PathVariable String password) {
		return sfe.matches(password, repository.findByUsername(username).getPassword());
	}

	@RequestMapping(value = "/find", method = RequestMethod.POST)
	public List<User> findUser(@RequestBody User user) {
		List<User> c = repository.findAll(Example.of(user));
		return c;
	}

	@RequestMapping(value = "/", method = RequestMethod.POST)
	public ResponseWrapper<User> createUser(@RequestBody User user) {
		ResponseWrapper<User> rw = new ResponseWrapper<>();
		if (repository.findByUsername(user.getUsername()) != null) {
			rw.setError("Username taken");
			return rw;
		}
		user.setPassword(sfe.encode(user.getPassword()));
		rw.setObject(repository.save(user));
		return rw;
	}

	@RequestMapping(value = "/", method = RequestMethod.PUT)
	public ResponseWrapper<User> updateUser(@RequestBody User user) {
		ResponseWrapper<User> rw = new ResponseWrapper<>();
		User us = repository.findByUsername(user.getUsername());
		if (us != null) {
			user.setId(us.getId());
			repository.save(user);
			rw.setObject(user);
		} else {
			rw.setError("User doesn't exist.");
		}
		return rw;
	}

	@RequestMapping(value = "/", method = RequestMethod.DELETE)
	public ResponseWrapper<User> deleteUser(@RequestBody User user) {
		ResponseWrapper<User> rw = new ResponseWrapper<>();
		User us = repository.findByUsername(user.getUsername());
		if (us != null) {
			repository.delete(us);
			rw.setObject(us);
		} else {
			rw.setError("User doesn't exist.");
		}
		return rw;
	}
}
