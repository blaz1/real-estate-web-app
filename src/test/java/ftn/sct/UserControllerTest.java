package ftn.sct;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.Charset;

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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import ftn.sct.model.User;
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
	private UserRepository userRepository;

	@Before
	public void prepare() {
		userRepository.save(new User("testGetUserById", "testGetUserByIdLastName", "password", "buyer"));
		userRepository.save(new User("testGetUserByUsername", "testGetUserByUsernameLastName", "password", "buyer"));
		userRepository.save(new User("testGetUser", "testGetUserLastName", "password", "buyer"));
		userRepository.save(new User("testUpdateUser", "testUpdateUserLastName", "password", "buyer"));
		userRepository.save(new User("testDeleteUser", "testDeleteUserLastName", "password", "buyer"));
	}

	@PostConstruct
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@After
	public void cleanup() {
		deleteIfEntityExists(userRepository.findByUsername("testGetUserById"));
		deleteIfEntityExists(userRepository.findByUsername("testGetUserByUsername"));
		deleteIfEntityExists(userRepository.findByUsername("testGetUser"));
		deleteIfEntityExists(userRepository.findByUsername("testCreateUser"));
		deleteIfEntityExists(userRepository.findByUsername("testUpdateUser"));
		deleteIfEntityExists(userRepository.findByUsername("testDeleteUser"));
	}

	@Test
	public void testGetUserById() throws Exception {
		mockMvc.perform(get(URL_PREFIX + "/get/id/" + userRepository.findByUsername("testGetUserById").getId()))//
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
		User user = userRepository.findByUsername("testUpdateUser");
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
		User user = userRepository.findByUsername("testDeleteUser");
		mockMvc.perform(delete(URL_PREFIX)//
				.contentType(contentType)//
				.content(TestUtil.json(user)))//
				.andExpect(status().isOk());
	}

	@Test
	public void testGetUserPicture() throws Exception {
		// TODO implement
	}

	private void deleteIfEntityExists(User user) {
		if (user != null) {
			userRepository.delete(user);
		}
	}
}
