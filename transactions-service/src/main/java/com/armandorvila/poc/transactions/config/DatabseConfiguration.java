package com.armandorvila.poc.transactions.config;

import java.math.BigDecimal;
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
@EnableReactiveMongoRepositories("com.armandorvila.poc.transactions.repository")
public class DatabseConfiguration {

	@Autowired
    private TransactionRepository transactionRepository;
	
	/*
	 * Development only.
	 */
	@PostConstruct
	public void initializeDatabase() {
		transactionRepository.deleteAll().thenMany(
		transactionRepository.saveAll(Arrays.asList(
				new Transaction("5ab698238d14c64fc85b3d38", "Some transaction", BigDecimal.valueOf(-10.0), BigDecimal.valueOf(8000.00)),
				new Transaction("5ab698238d14c64fc85b3d38", "Some other transaction", BigDecimal.valueOf(-10.0), BigDecimal.valueOf(7990.00)),
				new Transaction("5ab698238d14c64fc85b3d38", "Some transaction", BigDecimal.valueOf(-10.0), BigDecimal.valueOf(7980.00)),
				new Transaction("5ab698238d14c64fc85b3d38", "Some other transaction", BigDecimal.valueOf(-10.0), BigDecimal.valueOf(7970.00)),
				new Transaction("57b698238d14c64fc85b3d39", "Some transaction", BigDecimal.valueOf(-10.0), BigDecimal.valueOf(7960.00)),
				new Transaction("57b698238d14c64fc85b3d39", "Some other transaction", BigDecimal.valueOf(-10.0), BigDecimal.valueOf(7950.00))
				))).subscribe();
	}
}
