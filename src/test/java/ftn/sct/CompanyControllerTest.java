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

import ftn.sct.enums.CompanyTypeEnum;
import ftn.sct.model.Company;
import ftn.sct.persistance.CompanyRepository;
import ftn.sct.persistance.FileStorageDao;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CompanyControllerTest {

	private static final Logger log = LoggerFactory.getLogger(CompanyControllerTest.class);

	private static final String URL_PREFIX = "/company";

	private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
			MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	private CompanyRepository repository;

	@Autowired
	private FileStorageDao fileStorage;

	@Before
	public void prepare() {
		repository.save(new Company("testGetCompanyById", "password", CompanyTypeEnum.DD, "vat123456"));
		repository.save(new Company("testGetCompanyByName", "password", CompanyTypeEnum.DD, "vat123457"));
		repository.save(new Company("testGetCompanyByVat", "password", CompanyTypeEnum.DD, "vat123457asd13"));
		repository.save(new Company("testGetCompany", "password", CompanyTypeEnum.DD, "vat123458"));
		repository.save(new Company("testUpdateCompany", "password", CompanyTypeEnum.DD, "vat123459"));
		repository.save(new Company("testDeleteCompany", "password", CompanyTypeEnum.DD, "vat123455"));
		try {
			String testGetPictureCompanyPictureId = fileStorage.store(
					Files.newInputStream(Paths.get("src/test/resources/Desert.jpg")), "Desert.jpg", "image/jpeg", null);
			Company testGetPictureCompany = new Company("testGetPictureCompany", "password", CompanyTypeEnum.DOO,
					"vat123452123");
			testGetPictureCompany.setPicture(testGetPictureCompanyPictureId);
			repository.save(testGetPictureCompany);
		} catch (IOException e) {
			e.printStackTrace();
		}
		repository.save(new Company("testChangePictureCompany", "password", CompanyTypeEnum.DOO, "vat12345645123"));
		try {
			String testDeletePictureCompanyPictureId = fileStorage.store(
					Files.newInputStream(Paths.get("src/test/resources/Desert.jpg")), "Desert.jpg", "image/jpeg", null);
			Company testDeletePictureCompanz = new Company("testDeletePictureCompany", "password", CompanyTypeEnum.DD,
					"vat123654321");
			testDeletePictureCompanz.setPicture(testDeletePictureCompanyPictureId);
			repository.save(testDeletePictureCompanz);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@PostConstruct
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@After
	public void cleanup() {
		deleteIfEntityExists(repository.findByName("testGetCompanyById"));
		deleteIfEntityExists(repository.findByName("testGetCompanyByName"));
		deleteIfEntityExists(repository.findByName("testGetCompanyByVat"));
		deleteIfEntityExists(repository.findByName("testGetCompany"));
		deleteIfEntityExists(repository.findByName("testCreateCompany"));
		deleteIfEntityExists(repository.findByName("testUpdateCompany"));
		deleteIfEntityExists(repository.findByName("testDeleteCompany"));
		deleteIfEntityExists(repository.findByName("testGetPictureCompany"));
		fileStorage.removeByFileName("Desert.jpg");
		deleteIfEntityExists(repository.findByName("testChangePictureCompany"));
		fileStorage.removeByFileName("Chrysanthemum.jpg");
		deleteIfEntityExists(repository.findByName("testDeletePictureCompany"));
	}

	@Test
	public void testGetCompanyById() throws Exception {
		mockMvc.perform(get(URL_PREFIX + "/get/id/" + repository.findByName("testGetCompanyById").getId()))//
				.andExpect(status().isOk())//
				.andExpect(content().contentType(contentType))//
				.andExpect(jsonPath("name").value("testGetCompanyById"));
	}

	@Test
	public void testGetCompanyByName() throws Exception {
		mockMvc.perform(get(URL_PREFIX + "/get/name/testGetCompanyByName"))//
				.andExpect(status().isOk())//
				.andExpect(content().contentType(contentType))//
				.andExpect(jsonPath("name").value("testGetCompanyByName"));
	}

	@Test
	public void testGetCompanyByVat() throws Exception {
		mockMvc.perform(get(URL_PREFIX + "/get/vat/vat123457asd13"))//
				.andExpect(status().isOk())//
				.andExpect(content().contentType(contentType))//
				.andExpect(jsonPath("vat").value("vat123457asd13"))
				.andExpect(jsonPath("name").value("testGetCompanyByVat"));
	}

	@Test
	public void testGetCompany() throws Exception {
		Company dummyCompany = new Company("testGetCompany");
		MvcResult mvcResult = mockMvc
				.perform(post(URL_PREFIX + "/get")//
						.contentType(contentType)//
						.content(TestUtil.json(dummyCompany)))//
				.andExpect(status().isOk())//
				.andExpect(content().contentType(contentType))//
				.andExpect(jsonPath("$.[*].id").isNotEmpty())//
				.andExpect(jsonPath("$.[*].name").value("testGetCompany"))//
				.andReturn();
		log.debug(mvcResult.getResponse().getContentAsString());
	}

	@Test
	public void testCreateCompany() throws Exception {
		Company dummyCompany = new Company("testCreateCompany", "password", CompanyTypeEnum.DD, "vat12123456");
		dummyCompany.setPassword("create");
		MvcResult mvcResult = mockMvc
				.perform(post(URL_PREFIX)//
						.contentType(contentType)//
						.content(TestUtil.json(dummyCompany)))//
				.andExpect(status().isOk())//
				.andExpect(content().contentType(contentType))//
				.andExpect(jsonPath("$.name").value("testCreateCompany"))//
				.andExpect(jsonPath("$.id").isNotEmpty())//
				.andExpect(jsonPath("$.registeredDate").isNotEmpty())//
				.andExpect(jsonPath("$.password").isNotEmpty())//
				.andReturn();
		log.debug(mvcResult.getResponse().getContentAsString());
	}

	@Test
	public void testUpdateCompany() throws Exception {
		Company company = repository.findByName("testUpdateCompany");
		company.setEmail("testemailcompany@test.com");
		company.setTelephone("031-031-031");
		mockMvc.perform(put(URL_PREFIX)//
				.contentType(contentType)//
				.content(TestUtil.json(company)))//
				.andExpect(status().isOk())//
				.andExpect(content().contentType(contentType))//
				.andExpect(jsonPath("$.telephone").value("031-031-031"))//
				.andExpect(jsonPath("$.email").value("testemailcompany@test.com"));
	}

	@Test
	public void testDeleteCompany() throws Exception {
		Company company = repository.findByName("testDeleteCompany");
		mockMvc.perform(delete(URL_PREFIX)//
				.contentType(contentType)//
				.content(TestUtil.json(company)))//
				.andExpect(status().isOk());
	}

	@Test
	public void testGetCompanyPicture() throws Exception {
		Company company = repository.findByName("testGetPictureCompany");
		mockMvc.perform(get(URL_PREFIX + "/picture/" + company.getPicture()))//
				.andExpect(status().isOk())//
				.andExpect(content().contentType(MediaType.MULTIPART_FORM_DATA));
	}

	@Test
	public void testDeleteCompanyPicture() throws Exception {
		Company company = repository.findByName("testDeletePictureCompany");
		mockMvc.perform(delete(URL_PREFIX + "/picture/" + company.getPicture()))//
				.andExpect(status().isOk());
	}

	@Test
	public void testChangeCompanyPicture() throws Exception {
		Company company = repository.findByName("testChangePictureCompany");
		MockMultipartFile multipartFile = new MockMultipartFile("file", "Chrysanthemum.jpg", "image/jpeg",
				Files.newInputStream(Paths.get("src/test/resources/Chrysanthemum.jpg")));

		mockMvc.perform(MockMvcRequestBuilders.fileUpload(URL_PREFIX + "/picture")//
				.file(multipartFile)//
				.param("userId", company.getId())//
				.param("fileName", "Chrysanthemum.jpg")//
				.param("contentType", "image/jpeg"))//
				.andExpect(status().isOk())//
				.andExpect(content().contentType(MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8"));
	}

	private void deleteIfEntityExists(Company company) {
		if (company != null) {
			repository.delete(company);
		}
	}
}
