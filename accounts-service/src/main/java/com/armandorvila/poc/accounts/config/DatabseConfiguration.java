package com.armandorvila.poc.accounts.config;

import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

import com.armandorvila.poc.accounts.domain.Account;
import com.armandorvila.poc.accounts.repository.AccountRepository;

@Configuration
@EnableMongoAuditing
@EnableReactiveMongoRepositories("com.armandorvila.poc.accounts.repository")
public class DatabseConfiguration {

	@Autowired
    private AccountRepository accountRepository;
	
	@PostConstruct
	public void initializeDatabase() {
		//This is going to get duplicated when running more than one instance.
		accountRepository.saveAll(Arrays.asList(
				new Account("Some account"),
				new Account("Some other account")
				)).subscribe();
	}
}
