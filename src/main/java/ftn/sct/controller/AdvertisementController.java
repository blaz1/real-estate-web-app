package ftn.sct.controller;

import java.io.IOException;
import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mongodb.gridfs.GridFSDBFile;

import ftn.sct.model.Advertisement;
import ftn.sct.persistance.AdvertisementRepository;
import ftn.sct.persistance.FileStorageDao;

@RestController
@RequestMapping("/ad")
public class AdvertisementController {

	private static final Logger log = LoggerFactory.getLogger(AdvertisementController.class);

	@Autowired
	private AdvertisementRepository repository;

	@Autowired
	private FileStorageDao fileStorage;

	@RequestMapping(value = "/get/id/{id}", method = RequestMethod.GET)
	public ResponseEntity<Advertisement> getAdvertisementById(@PathVariable String id) {
		log.debug("Searching for advertisement, id: " + id);
		Advertisement ad = repository.findOne(id);
		if (ad == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(ad, HttpStatus.OK);
	}

	@RequestMapping(value = "/get", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Advertisement>> getAdvertisement(@RequestBody Advertisement ad) {
		List<Advertisement> ads = repository.findAll(Example.of(ad));
		if (ads == null || ads.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(ads, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Advertisement> createAdvertisement(@RequestBody Advertisement ad) {
		if (repository.findByName(ad.getName()) != null) {
			return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
		}
		Calendar c = Calendar.getInstance();
		ad.setCreatedDate(c.getTime());

		return new ResponseEntity<>(repository.save(ad), HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Advertisement> updateAdvertisement(@RequestBody Advertisement ad) {
		Advertisement us = repository.findOne(ad.getId());
		if (us == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(repository.save(ad), HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.DELETE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Advertisement> deleteAdvertisement(@RequestBody Advertisement ad) {
		Advertisement us = repository.findOne(ad.getId());
		if (us == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		repository.delete(us);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/picture/{id}", method = RequestMethod.GET, produces = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<InputStreamResource> getAdvertisementPictures(@PathVariable String id) {
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

	@RequestMapping(value = "/picture", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> addAdvertisementPictures(@RequestParam("adId") String adId,
			@RequestParam("file") MultipartFile file, @RequestParam("fileName") String fileName,
			@RequestParam("contentType") String contentType) {
		Advertisement ad = repository.findOne(adId);
		try {
			String id = fileStorage.store(file.getInputStream(), fileName, contentType, null);
			if (id == null || id.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			} else if (ad.getPictureIds() == null) {
				ad.setPictureIds(new ArrayList<>());
			}
			ad.getPictureIds().add(id);
			return new ResponseEntity<>(id, HttpStatus.OK);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

	}

	@RequestMapping(value = "/picture", method = RequestMethod.DELETE)
	public ResponseEntity<MultipartFile> deleteAdvertisementPicture(@RequestParam("adId") String adId,
			@RequestParam("pictureId") String pictureId) {
		Advertisement ad = repository.findOne(adId);

		if (ad.getPictureIds() != null && ad.getPictureIds().remove(pictureId)) {
			fileStorage.removeById(pictureId);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
