package org.kirkiano.rpg.bank;

import org.kirkiano.rpg.bank.config.ObjectMapperConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;


/**
 * Top-level application
 */
@SpringBootApplication
@Import(ObjectMapperConfig.class)
public class BankApplication {

	/**
	 * Default constructor
	 */
	public BankApplication() {}

	/**
	 * Main method
	 * @param args Runtime args
	 */
	public static void main(String[] args) {
		SpringApplication.run(BankApplication.class, args);
	}

}