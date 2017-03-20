package ftn.sct.persistance;

import org.springframework.data.mongodb.repository.MongoRepository;

import ftn.sct.model.User;

public interface AdvertisementRepository extends MongoRepository<User, String> {

}
