package com.rustik.rustik;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.rustik.rustik"})
public class RustikApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.load();
		System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
		System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
		System.setProperty("DB_URL", dotenv.get("DB_URL"));
		System.setProperty("CLOUDINARY_URL", dotenv.get("CLOUDINARY_URL"));
		System.setProperty("CORS", dotenv.get("CORS"));
		System.setProperty("SECRET", dotenv.get("SECRET"));
		System.setProperty("ISSUER", dotenv.get("ISSUER"));
		System.setProperty("SPRING_MAIL_USERNAME", dotenv.get("MAIL_USERNAME"));
		System.setProperty("SPRING_MAIL_PASSWORD", dotenv.get("MAIL_PASSWORD"));
		System.setProperty("RUSTIK_URL", dotenv.get("RUSTIK_URL"));

		SpringApplication.run(RustikApplication.class, args);

	}

}
