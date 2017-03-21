package ftn.sct;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.annotation.PostConstruct;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import ftn.sct.enums.RealEstateTypeEnum;
import ftn.sct.model.Advertisement;
import ftn.sct.persistance.AdvertisementRepository;
import ftn.sct.persistance.FileStorageDao;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AdvertisementControllerTest {

	private static final Logger log = LoggerFactory.getLogger(AdvertisementControllerTest.class);

	private static final String URL_PREFIX = "/ad";

	private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
			MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	private AdvertisementRepository repository;

	@Autowired
	private FileStorageDao fileStorage;

	@Before
	public void prepare() {
		repository.save(new Advertisement("testGetAdvertisementById", RealEstateTypeEnum.APPARTMENT));
		repository.save(new Advertisement("testGetAdvertisement", RealEstateTypeEnum.APPARTMENT));
		repository.save(new Advertisement("testUpdateAdvertisement", RealEstateTypeEnum.APPARTMENT));
		repository.save(new Advertisement("testDeleteAdvertisement", RealEstateTypeEnum.APPARTMENT));
		try {
			String testGetAdvertisementPicturePictureId = fileStorage.store(
					Files.newInputStream(Paths.get("src/test/resources/Desert.jpg")), "Desert.jpg", "image/jpeg", null);
			Advertisement testGetAdvertisementPicture = new Advertisement("testGetAdvertisementPicture",
					RealEstateTypeEnum.APPARTMENT);
			testGetAdvertisementPicture.setPictureIds(new ArrayList<>());
			testGetAdvertisementPicture.getPictureIds().add(testGetAdvertisementPicturePictureId);
			repository.save(testGetAdvertisementPicture);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			String testDeleteAdvertisementPicturePictureId = fileStorage.store(
					Files.newInputStream(Paths.get("src/test/resources/Desert.jpg")), "Desert.jpg", "image/jpeg", null);
			Advertisement testDeleteAdvertisementPicture = new Advertisement("testDeleteAdvertisementPicture",
					RealEstateTypeEnum.APPARTMENT);
			testDeleteAdvertisementPicture.setPictureIds(new ArrayList<>());
			testDeleteAdvertisementPicture.getPictureIds().add(testDeleteAdvertisementPicturePictureId);
			repository.save(testDeleteAdvertisementPicture);
		} catch (IOException e) {
			e.printStackTrace();
		}
		repository.save(new Advertisement("testAddAdvertisementPicture", RealEstateTypeEnum.APPARTMENT));
	}

	@PostConstruct
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@After
	public void cleanup() {
		deleteIfEntityExists(repository.findByName("testGetAdvertisementById"));
		deleteIfEntityExists(repository.findByName("testGetAdvertisement"));
		deleteIfEntityExists(repository.findByName("testCreateAdvertisement"));
		deleteIfEntityExists(repository.findByName("testUpdateAdvertisement"));
		deleteIfEntityExists(repository.findByName("testDeleteAdvertisement"));
		deleteIfEntityExists(repository.findByName("testGetAdvertisementPicture"));
		deleteIfEntityExists(repository.findByName("testDeleteAdvertisementPicture"));
		deleteIfEntityExists(repository.findByName("testAddAdvertisementPicture"));
		fileStorage.removeByFileName("Desert.jpg");
		fileStorage.removeByFileName("Chrysanthemum.jpg");
	}

	@Test
	public void testGetAdvertisementById() throws Exception {
		mockMvc.perform(get(URL_PREFIX + "/get/id/" + repository.findByName("testGetAdvertisementById").getId()))//
				.andExpect(status().isOk())//
				.andExpect(content().contentType(contentType))//
				.andExpect(jsonPath("name").value("testGetAdvertisementById"));
	}

	@Test
	public void testGetAdvertisement() throws Exception {
		Advertisement dummyAdvertisement = new Advertisement("testGetAdvertisement", RealEstateTypeEnum.APPARTMENT);
		dummyAdvertisement.setEquipmentList(null);
		dummyAdvertisement.setPictureIds(null);
		MvcResult mvcResult = mockMvc
				.perform(post(URL_PREFIX + "/get")//
						.contentType(contentType)//
						.content(TestUtil.json(dummyAdvertisement)))//
				.andExpect(status().isOk())//
				.andExpect(content().contentType(contentType))//
				.andExpect(jsonPath("$.[*].id").isNotEmpty())//
				.andExpect(jsonPath("$.[*].name").value("testGetAdvertisement"))//
				.andReturn();
		log.debug(mvcResult.getResponse().getContentAsString());
	}

	@Test
	public void testCreateAdvertisement() throws Exception {
		Advertisement dummyAdvertisement = new Advertisement("testCreateAdvertisement", RealEstateTypeEnum.APPARTMENT);
		MvcResult mvcResult = mockMvc
				.perform(post(URL_PREFIX)//
						.contentType(contentType)//
						.content(TestUtil.json(dummyAdvertisement)))//
				.andExpect(status().isOk())//
				.andExpect(content().contentType(contentType))//
				.andExpect(jsonPath("$.name").value("testCreateAdvertisement"))//
				.andExpect(jsonPath("$.id").isNotEmpty())//
				.andExpect(jsonPath("$.createdDate").isNotEmpty())//
				.andReturn();
		log.debug(mvcResult.getResponse().getContentAsString());
	}

	@Test
	public void testUpdateAdvertisement() throws Exception {
		Advertisement ad = repository.findByName("testUpdateAdvertisement");
		ad.setEmail("testemail@test.com");
		ad.setBuildYear(1998);
		mockMvc.perform(put(URL_PREFIX)//
				.contentType(contentType)//
				.content(TestUtil.json(ad)))//
				.andExpect(status().isOk())//
				.andExpect(content().contentType(contentType))//
				.andExpect(jsonPath("$.email").value("testemail@test.com"))//
				.andExpect(jsonPath("$.buildYear").value("1998"));
	}

	@Test
	public void testDeleteAdvertisement() throws Exception {
		Advertisement ad = repository.findByName("testDeleteAdvertisement");
		mockMvc.perform(delete(URL_PREFIX)//
				.contentType(contentType)//
				.content(TestUtil.json(ad)))//
				.andExpect(status().isOk());
	}

	@Test
	public void testGetAdvertisementPicture() throws Exception {
		Advertisement ad = repository.findByName("testGetAdvertisementPicture");
		mockMvc.perform(get(URL_PREFIX + "/picture/" + ad.getPictureIds().get(0)))//
				.andExpect(status().isOk())//
				.andExpect(content().contentType(MediaType.MULTIPART_FORM_DATA));
	}

	@Test
	public void testDeleteAdvertisementPicture() throws Exception {
		Advertisement ad = repository.findByName("testDeleteAdvertisementPicture");
		mockMvc.perform(delete(URL_PREFIX + "/picture")//
				.param("adId", ad.getId())//
				.param("pictureId", ad.getPictureIds().get(0)))//
				.andExpect(status().isOk());
	}

	@Test
	public void testAddAdvertisementPicture() throws Exception {
		Advertisement ad = repository.findByName("testAddAdvertisementPicture");
		MockMultipartFile multipartFile = new MockMultipartFile("file", "Chrysanthemum.jpg", "image/jpeg",
				Files.newInputStream(Paths.get("src/test/resources/Chrysanthemum.jpg")));

		mockMvc.perform(MockMvcRequestBuilders.fileUpload(URL_PREFIX + "/picture")//
				.file(multipartFile)//
				.param("adId", ad.getId())//
				.param("fileName", "Chrysanthemum.jpg")//
				.param("contentType", "image/jpeg"))//
				.andExpect(status().isOk())//
				.andExpect(content().contentType(MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8"));
	}

	private void deleteIfEntityExists(Advertisement user) {
		if (user != null) {
			repository.delete(user);
		}
	}
}
