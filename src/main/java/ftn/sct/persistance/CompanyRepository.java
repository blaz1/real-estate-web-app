package ftn.sct.persistance;

import org.springframework.data.mongodb.repository.MongoRepository;

import ftn.sct.model.Company;

public interface CompanyRepository extends MongoRepository<Company, String> {

	public Company findByName(String name);

	public Company findByEmail(String email);

	public Company findByVat(String vat);
}
