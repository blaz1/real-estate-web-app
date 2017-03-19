package ftn.sct.controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.mongodb.gridfs.GridFSDBFile;

import ftn.sct.model.User;
import ftn.sct.persistance.UserRepository;

@RestController
@RequestMapping("/user")
public class UserController {

	private BCryptPasswordEncoder sfe = new BCryptPasswordEncoder();

	private static final Logger log = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserRepository repository;

	@Autowired
	private GridFsTemplate gridFsTemplate;

	@RequestMapping(value = "/{username}", method = RequestMethod.GET)
	public ResponseEntity<User> findUser(@PathVariable String username) {
		log.debug("Searching for user: " + username);
		User u = repository.findByUsername(username);
		if (u == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(u, HttpStatus.OK);
	}

	@RequestMapping(value = "/password/{username}/{password}", method = RequestMethod.GET)
	public ResponseEntity<Boolean> validateUser(@PathVariable String username, @PathVariable String password) {
		if (!sfe.matches(password, repository.findByUsername(username).getPassword())) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/find", method = RequestMethod.POST, consumes = "application/json")
	public ResponseEntity<List<User>> findUser(@RequestBody User user) {
		List<User> c = repository.findAll(Example.of(user));
		if (c == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(c, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, consumes = "application/json")
	public ResponseEntity<User> createUser(@RequestBody User user) {
		if (repository.findByUsername(user.getUsername()) != null) {
			return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
		}
		user.setPassword(sfe.encode(user.getPassword()));
		Calendar c = Calendar.getInstance();
		user.setRegisteredDate(c.getTime());

		if (user.getPicture() != null) {
			String pic = user.getPicture();
			InputStream inputStream;
			try {
				// TODO retrieve stream from frontend
				inputStream = new FileInputStream(pic);
				String id = gridFsTemplate.store(inputStream, pic.substring(pic.lastIndexOf("/") + 1),
						"image/" + pic.substring(pic.lastIndexOf(".") + 1)).getId().toString();
				user.setPicture(id);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		ResponseEntity<User> re = new ResponseEntity<>(repository.save(user), HttpStatus.OK);
		return re;
	}

	@RequestMapping(method = RequestMethod.PUT, consumes = "application/json")
	public ResponseEntity<User> updateUser(@RequestBody User user) {
		User us = repository.findByUsername(user.getUsername());
		if (us == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		user.setId(us.getId());
		return new ResponseEntity<>(repository.save(user), HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.DELETE, consumes = "application/json")
	public ResponseEntity<User> deleteUser(@RequestBody User user) {
		User us = repository.findByUsername(user.getUsername());
		if (us == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		repository.delete(us);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/picture", method = RequestMethod.GET)
	public InputStream getUserPicture(@PathVariable String id) {
		Query q = new Query();
		q.addCriteria(Criteria.where("_id").is(id));
		GridFSDBFile gfsf = gridFsTemplate.findOne(q);
		// TODO implement frontend accepting stream
		return gfsf.getInputStream();
	}
}
