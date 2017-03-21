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

import ftn.sct.model.User;
import ftn.sct.persistance.FileStorageDao;
import ftn.sct.persistance.UserRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserControllerTest {

	private static final Logger log = LoggerFactory.getLogger(UserControllerTest.class);

	private static final String URL_PREFIX = "/user";

	private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
			MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	private UserRepository repository;

	@Autowired
	private FileStorageDao fileStorage;

	@Before
	public void prepare() {
		repository.save(new User("testGetUserById", "testGetUserByIdLastName", "password", "buyer"));
		repository.save(new User("testGetUserByUsername", "testGetUserByUsernameLastName", "password", "buyer"));
		repository.save(new User("testGetUser", "testGetUserLastName", "password", "buyer"));
		repository.save(new User("testUpdateUser", "testUpdateUserLastName", "password", "buyer"));
		repository.save(new User("testDeleteUser", "testDeleteUserLastName", "password", "buyer"));
		try {
			String testGetUserPicturePictureId = fileStorage.store(
					Files.newInputStream(Paths.get("src/test/resources/Desert.jpg")), "Desert.jpg", "image/jpeg", null);
			User testGetUserPicture = new User("testGetUserPicture", "testGetUserPictureLastName", "password", "buyer");
			testGetUserPicture.setPicture(testGetUserPicturePictureId);
			repository.save(testGetUserPicture);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			String testDeleteUserPicturePictureId = fileStorage.store(
					Files.newInputStream(Paths.get("src/test/resources/Desert.jpg")), "Desert.jpg", "image/jpeg", null);
			User testGetUserPicture = new User("testDeleteUserPicture", "testDeleteUserPictureLastName", "password",
					"buyer");
			testGetUserPicture.setPicture(testDeleteUserPicturePictureId);
			repository.save(testGetUserPicture);
		} catch (IOException e) {
			e.printStackTrace();
		}
		repository.save(new User("testChangeUserPicture", "testChangeUserPictureLastName", "password", "buyer"));
	}

	@PostConstruct
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@After
	public void cleanup() {
		deleteIfEntityExists(repository.findByUsername("testGetUserById"));
		deleteIfEntityExists(repository.findByUsername("testGetUserByUsername"));
		deleteIfEntityExists(repository.findByUsername("testGetUser"));
		deleteIfEntityExists(repository.findByUsername("testCreateUser"));
		deleteIfEntityExists(repository.findByUsername("testUpdateUser"));
		deleteIfEntityExists(repository.findByUsername("testDeleteUser"));
		deleteIfEntityExists(repository.findByUsername("testGetUserPicture"));
		deleteIfEntityExists(repository.findByUsername("testChangeUserPicture"));
		deleteIfEntityExists(repository.findByUsername("testDeleteUserPicture"));
		fileStorage.removeByFileName("Desert.jpg");
		fileStorage.removeByFileName("Chrysanthemum.jpg");
	}

	@Test
	public void testGetUserById() throws Exception {
		mockMvc.perform(get(URL_PREFIX + "/get/id/" + repository.findByUsername("testGetUserById").getId()))//
				.andExpect(status().isOk())//
				.andExpect(content().contentType(contentType))//
				.andExpect(jsonPath("username").value("testGetUserById"));
	}

	@Test
	public void testGetUserByUsername() throws Exception {
		mockMvc.perform(get(URL_PREFIX + "/get/username/testGetUserByUsername"))//
				.andExpect(status().isOk())//
				.andExpect(content().contentType(contentType))//
				.andExpect(jsonPath("username").value("testGetUserByUsername"));
	}

	@Test
	public void testGetUserByFalseUsername() throws Exception {
		mockMvc.perform(get(URL_PREFIX + "/get/username/nonexistingname123"))//
				.andExpect(status().isNotFound());
	}

	@Test
	public void testGetUser() throws Exception {
		User dummyUser = new User("testGetUser", "testGetUserLastName");
		MvcResult mvcResult = mockMvc
				.perform(post(URL_PREFIX + "/get")//
						.contentType(contentType)//
						.content(TestUtil.json(dummyUser)))//
				.andExpect(status().isOk())//
				.andExpect(content().contentType(contentType))//
				.andExpect(jsonPath("$.[*].id").isNotEmpty())//
				.andExpect(jsonPath("$.[*].username").value("testGetUser"))//
				.andReturn();
		log.debug(mvcResult.getResponse().getContentAsString());
	}

	@Test
	public void testCreateUser() throws Exception {
		User dummyUser = new User("testCreateUser", "testCreateUserLastName");
		dummyUser.setPassword("create");
		MvcResult mvcResult = mockMvc
				.perform(post(URL_PREFIX)//
						.contentType(contentType)//
						.content(TestUtil.json(dummyUser)))//
				.andExpect(status().isOk())//
				.andExpect(content().contentType(contentType))//
				.andExpect(jsonPath("$.username").value("testCreateUser"))//
				.andExpect(jsonPath("$.id").isNotEmpty())//
				.andExpect(jsonPath("$.registeredDate").isNotEmpty())//
				.andExpect(jsonPath("$.password").isNotEmpty())//
				.andReturn();
		log.debug(mvcResult.getResponse().getContentAsString());
	}

	@Test
	public void testUpdateUser() throws Exception {
		User user = repository.findByUsername("testUpdateUser");
		user.setFirstName("AfterTestName");
		user.setEmail("testemail@test.com");
		mockMvc.perform(put(URL_PREFIX)//
				.contentType(contentType)//
				.content(TestUtil.json(user)))//
				.andExpect(status().isOk())//
				.andExpect(content().contentType(contentType))//
				.andExpect(jsonPath("$.firstName").value("AfterTestName"))//
				.andExpect(jsonPath("$.email").value("testemail@test.com"));
	}

	@Test
	public void testDeleteUser() throws Exception {
		User user = repository.findByUsername("testDeleteUser");
		mockMvc.perform(delete(URL_PREFIX)//
				.contentType(contentType)//
				.content(TestUtil.json(user)))//
				.andExpect(status().isOk());
	}

	@Test
	public void testGetUserPicture() throws Exception {
		User user = repository.findByUsername("testGetUserPicture");
		mockMvc.perform(get(URL_PREFIX + "/picture/" + user.getPicture()))//
				.andExpect(status().isOk())//
				.andExpect(content().contentType(MediaType.MULTIPART_FORM_DATA));
	}

	@Test
	public void testDeleteUserPicture() throws Exception {
		User user = repository.findByUsername("testDeleteUserPicture");
		mockMvc.perform(delete(URL_PREFIX + "/picture/" + user.getPicture()))//
				.andExpect(status().isOk());
	}

	@Test
	public void testChangeUserPicture() throws Exception {
		User user = repository.findByUsername("testChangeUserPicture");
		MockMultipartFile multipartFile = new MockMultipartFile("file", "Chrysanthemum.jpg", "image/jpeg",
				Files.newInputStream(Paths.get("src/test/resources/Chrysanthemum.jpg")));

		mockMvc.perform(MockMvcRequestBuilders.fileUpload(URL_PREFIX + "/picture")//
				.file(multipartFile)//
				.param("userId", user.getId())//
				.param("fileName", "Chrysanthemum.jpg")//
				.param("contentType", "image/jpeg"))//
				.andExpect(status().isOk())//
				.andExpect(content().contentType(MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8"));
	}

	private void deleteIfEntityExists(User user) {
		if (user != null) {
			repository.delete(user);
		}
	}
}
