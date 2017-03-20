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

import ftn.sct.enums.CompanyTypeEnum;
import ftn.sct.model.Company;
import ftn.sct.persistance.CompanyRepository;

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

	@Before
	public void prepare() {
		repository.save(new Company("testGetCompanyById", "password", CompanyTypeEnum.DD, "vat123456"));
		repository.save(new Company("testGetCompanyByName", "password", CompanyTypeEnum.DD, "vat123457"));
		repository.save(new Company("testGetCompanyByVat", "password", CompanyTypeEnum.DD, "vat123457asd13"));
		repository.save(new Company("testGetCompany", "password", CompanyTypeEnum.DD, "vat123458"));
		repository.save(new Company("testUpdateCompany", "password", CompanyTypeEnum.DD, "vat123459"));
		repository.save(new Company("testDeleteCompany", "password", CompanyTypeEnum.DD, "vat123455"));
	}

	@PostConstruct
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@After
	public void cleanup() {
		deleteIfEntityExists(repository.findByName("testGetCompanyById"));
		deleteIfEntityExists(repository.findByName("testGetCompanyByName"));
		deleteIfEntityExists(repository.findByName("testGetCompany"));
		deleteIfEntityExists(repository.findByName("testCreateCompany"));
		deleteIfEntityExists(repository.findByName("testUpdateCompany"));
		deleteIfEntityExists(repository.findByName("testDeleteCompany"));
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
		company.setName("AfterTestName");
		company.setEmail("testemailcompany@test.com");
		mockMvc.perform(put(URL_PREFIX)//
				.contentType(contentType)//
				.content(TestUtil.json(company)))//
				.andExpect(status().isOk())//
				.andExpect(content().contentType(contentType))//
				.andExpect(jsonPath("$.name").value("AfterTestName"))//
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
		// TODO implement
	}

	private void deleteIfEntityExists(Company company) {
		if (company != null) {
			repository.delete(company);
		}
	}
}
