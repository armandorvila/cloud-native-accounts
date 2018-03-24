package com.armandorvila.poc.transactions.config;

import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

import com.armandorvila.poc.transactions.domain.Transaction;
import com.armandorvila.poc.transactions.repository.TransactionRepository;

@Configuration
@EnableMongoAuditing
@EnableReactiveMongoRepositories(basePackages = "com.armandorvila.poc.transactions.repository")
public class DatabseConfiguration {

	@Autowired
    private TransactionRepository transactionRepository;
	
	@PostConstruct
	public void initializeDatabase() {
		transactionRepository.saveAll(Arrays.asList(
				new Transaction("Some transaction"),
				new Transaction("Some other transaction")
				)).subscribe();
	}
}
