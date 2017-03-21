package ftn.sct.persistance;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import ftn.sct.model.Advertisement;
import ftn.sct.model.City;

public interface AdvertisementRepository extends MongoRepository<Advertisement, String> {
	public Advertisement findByName(String name);

	public List<Advertisement> findByAddress_City(City city);

	public List<Advertisement> findByAddress_CityOrderByPriceDesc(City city);

	public List<Advertisement> findByOwnerId(String ownerId);

	// public List<Advertisement> findAllOrderByPublishedDateDesc();

	// @Query("{ 'firstname' : ?0 }")
	// List<Person> findByThePersonsFirstname(String firstname);
}
