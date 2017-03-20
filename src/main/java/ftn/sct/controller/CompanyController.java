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

import ftn.sct.model.Company;
import ftn.sct.persistance.CompanyRepository;

@RestController
@RequestMapping("/company")
public class CompanyController {

	private BCryptPasswordEncoder sfe = new BCryptPasswordEncoder();

	private static final Logger log = LoggerFactory.getLogger(CompanyController.class);

	@Autowired
	private CompanyRepository repository;

	@Autowired
	private GridFsTemplate gridFsTemplate;

	@RequestMapping(value = "/get/id/{id}", method = RequestMethod.GET)
	public ResponseEntity<Company> getCompanyById(@PathVariable String id) {
		log.debug("Searching for company, id: " + id);
		Company c = repository.findOne(id);
		if (c == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(c, HttpStatus.OK);
	}

	@RequestMapping(value = "/get/name/{name}", method = RequestMethod.GET)
	public ResponseEntity<Company> getCompanyByName(@PathVariable String name) {
		log.debug("Searching for company, name: " + name);
		Company c = repository.findByName(name);
		if (c == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(c, HttpStatus.OK);
	}

	@RequestMapping(value = "/get/vat/{vat}", method = RequestMethod.GET)
	public ResponseEntity<Company> getCompanyByVat(@PathVariable String vat) {
		log.debug("Searching for company, name: " + vat);
		Company c = repository.findByVat(vat);
		if (c == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(c, HttpStatus.OK);
	}

	@RequestMapping(value = "/password/{name}/{password}", method = RequestMethod.GET)
	public ResponseEntity<Boolean> validateCompany(@PathVariable String name, @PathVariable String password) {
		if (!sfe.matches(password, repository.findByName(name).getPassword())) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/get", method = RequestMethod.POST, consumes = "application/json")
	public ResponseEntity<List<Company>> getCompany(@RequestBody Company company) {
		List<Company> c = repository.findAll(Example.of(company));
		if (c == null || c.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(c, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, consumes = "application/json")
	public ResponseEntity<Company> createCompany(@RequestBody Company company) {
		if (repository.findByName(company.getName()) != null) {
			return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
		}
		company.setPassword(sfe.encode(company.getPassword()));
		Calendar c = Calendar.getInstance();
		company.setRegisteredDate(c.getTime());

		if (company.getPicture() != null) {
			String pic = company.getPicture();
			InputStream inputStream;
			try {
				// TODO retrieve stream from frontend
				inputStream = new FileInputStream(pic);
				String id = gridFsTemplate.store(inputStream, pic.substring(pic.lastIndexOf("/") + 1),
						"image/" + pic.substring(pic.lastIndexOf(".") + 1)).getId().toString();
				company.setPicture(id);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		return new ResponseEntity<>(repository.save(company), HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.PUT, consumes = "application/json")
	public ResponseEntity<Company> updateCompany(@RequestBody Company company) {
		Company us = repository.findOne(company.getId());
		if (us == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(repository.save(company), HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.DELETE, consumes = "application/json")
	public ResponseEntity<Company> deleteCompany(@RequestBody Company company) {
		Company us = repository.findOne(company.getId());
		if (us == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		repository.delete(us);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/picture", method = RequestMethod.GET)
	public InputStream getCompanyPicture(@PathVariable String id) {
		Query q = new Query();
		q.addCriteria(Criteria.where("_id").is(id));
		GridFSDBFile gfsf = gridFsTemplate.findOne(q);
		// TODO implement frontend accepting stream
		return gfsf.getInputStream();
	}
}
