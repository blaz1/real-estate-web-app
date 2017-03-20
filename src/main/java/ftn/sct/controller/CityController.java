package ftn.sct.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ftn.sct.persistance.CompanyRepository;

@RestController
@RequestMapping("/city")
public class CityController {

	private static final Logger log = LoggerFactory.getLogger(CityController.class);

	@Autowired
	private CompanyRepository repository;

	// TODO implement calling advertisementController

}
