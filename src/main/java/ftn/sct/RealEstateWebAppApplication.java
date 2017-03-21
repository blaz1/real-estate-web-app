package ftn.sct;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import ftn.sct.persistance.FileStorageDaoImpl;

@SpringBootApplication
public class RealEstateWebAppApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(RealEstateWebAppApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// TODO Auto-generated method stub
	}

	@Bean
	public FileStorageDaoImpl transferService() {
		return new FileStorageDaoImpl();
	}
}
