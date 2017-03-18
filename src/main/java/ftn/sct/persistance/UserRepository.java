package ftn.sct.persistance;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import ftn.sct.model.User;

public interface UserRepository extends MongoRepository<User, String> {

	public User findByUsername(String username);

	public User findByEmail(String email);

	public User findByFirstName(String firstName);

	public List<User> findByLastName(String lastName);

}
