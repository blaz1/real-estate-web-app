package ftn.sct.controller;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mongodb.gridfs.GridFSDBFile;

import ftn.sct.model.User;
import ftn.sct.persistance.FileStorageDao;
import ftn.sct.persistance.UserRepository;

@RestController
@RequestMapping("/user")
public class UserController {

	private BCryptPasswordEncoder sfe = new BCryptPasswordEncoder();

	private static final Logger log = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserRepository repository;

	@Autowired
	private FileStorageDao fileStorage;

	@RequestMapping(value = "/get/id/{id}", method = RequestMethod.GET)
	public ResponseEntity<User> getUserById(@PathVariable String id) {
		log.debug("Searching for user, id: " + id);
		User u = repository.findOne(id);
		if (u == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(u, HttpStatus.OK);
	}

	@RequestMapping(value = "/get/username/{username}", method = RequestMethod.GET)
	public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
		log.debug("Searching for user, username: " + username);
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

	@RequestMapping(value = "/get", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<User>> getUser(@RequestBody User user) {
		List<User> c = repository.findAll(Example.of(user));
		if (c == null || c.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(c, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<User> createUser(@RequestBody User user) {
		if (repository.findByUsername(user.getUsername()) != null) {
			return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
		}
		user.setPassword(sfe.encode(user.getPassword()));
		Calendar c = Calendar.getInstance();
		user.setRegisteredDate(c.getTime());
		return new ResponseEntity<>(repository.save(user), HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<User> updateUser(@RequestBody User user) {
		User us = repository.findOne(user.getId());
		if (us == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		user.setId(us.getId());
		return new ResponseEntity<>(repository.save(user), HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.DELETE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<User> deleteUser(@RequestBody User user) {
		User us = repository.findOne(user.getId());
		if (us == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		repository.delete(us);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/picture/{id}", method = RequestMethod.GET, produces = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<InputStreamResource> getUserPicture(@PathVariable String id) {
		GridFSDBFile file = fileStorage.getById(id);
		if (file == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		// TODO implement frontend accepting stream
		InputStreamResource inputStreamResource = new InputStreamResource(file.getInputStream());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		ResponseEntity<InputStreamResource> re = new ResponseEntity<>(inputStreamResource, headers, HttpStatus.OK);
		return re;
	}

	@RequestMapping(value = "/picture/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<String> deleteUserPicture(@PathVariable String id) {
		GridFSDBFile file = fileStorage.getById(id);
		if (file == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		fileStorage.removeById(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/picture", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> changeUserPicture(@RequestParam("userId") String userId,
			@RequestParam("file") MultipartFile file, @RequestParam("fileName") String fileName,
			@RequestParam("contentType") String contentType) {
		try {
			User u = repository.findOne(userId);
			String oldImgId = u.getPicture();
			String id = fileStorage.store(file.getInputStream(), fileName, contentType, null);
			if (id == null || id.isEmpty()) {
				return new ResponseEntity<>(id, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			u.setPicture(id);
			fileStorage.removeById(oldImgId);
			repository.save(u);
			return new ResponseEntity<>(id, HttpStatus.OK);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
