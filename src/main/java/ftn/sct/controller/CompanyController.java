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

import ftn.sct.model.Company;
import ftn.sct.persistance.CompanyRepository;
import ftn.sct.persistance.FileStorageDao;

@RestController
@RequestMapping("/company")
public class CompanyController {

	private BCryptPasswordEncoder sfe = new BCryptPasswordEncoder();

	private static final Logger log = LoggerFactory.getLogger(CompanyController.class);

	@Autowired
	private CompanyRepository repository;

	@Autowired
	private FileStorageDao fileStorage;

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

	@RequestMapping(value = "/get", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Company>> getCompany(@RequestBody Company company) {
		List<Company> c = repository.findAll(Example.of(company));
		if (c == null || c.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(c, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Company> createCompany(@RequestBody Company company) {
		if (repository.findByName(company.getName()) != null) {
			return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
		}
		company.setPassword(sfe.encode(company.getPassword()));
		Calendar c = Calendar.getInstance();
		company.setRegisteredDate(c.getTime());
		return new ResponseEntity<>(repository.save(company), HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Company> updateCompany(@RequestBody Company company) {
		Company us = repository.findOne(company.getId());
		if (us == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(repository.save(company), HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.DELETE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Company> deleteCompany(@RequestBody Company company) {
		Company us = repository.findOne(company.getId());
		if (us == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		repository.delete(us);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/picture/{id}", method = RequestMethod.GET, produces = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<InputStreamResource> getCompanyPicture(@PathVariable String id) {
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
	public ResponseEntity<InputStreamResource> deleteCompanyPicture(@PathVariable String id) {
		GridFSDBFile file = fileStorage.getById(id);
		if (file == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		fileStorage.removeById(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/picture", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> changeUserPicture(@RequestParam("userId") String companyId,
			@RequestParam("file") MultipartFile file, @RequestParam("fileName") String fileName,
			@RequestParam("contentType") String contentType) {
		try {
			Company c = repository.findOne(companyId);
			String oldImgId = c.getPicture();
			String id = fileStorage.store(file.getInputStream(), fileName, contentType, null);
			if (id == null || id.isEmpty()) {
				return new ResponseEntity<>(id, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			c.setPicture(id);
			fileStorage.removeById(oldImgId);
			repository.save(c);
			return new ResponseEntity<>(id, HttpStatus.OK);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
