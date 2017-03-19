package ftn.sct.persistance;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import ftn.sct.model.Company;
import ftn.sct.model.User;

public interface CompanyRepository extends MongoRepository<User, String> {

	public Company findByName(String name);

	public Company findByEmail(String email);

	public List<Company> findByVat(String vat);
}
