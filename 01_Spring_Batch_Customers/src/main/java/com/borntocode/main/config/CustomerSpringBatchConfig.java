package com.borntocode.main.config;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import com.borntocode.main.domain.Customer;
import com.borntocode.main.processor.CustomerProcessor;

@Configuration
@EnableBatchProcessing
public class CustomerSpringBatchConfig {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	public CustomerProcessor customerProcessor() {
		return new CustomerProcessor();
	}

	@Bean
	public JdbcBatchItemWriter<Customer> writer(DataSource dataSource) {
		JdbcBatchItemWriterBuilder<Customer> batchItemWriterBuilder = new JdbcBatchItemWriterBuilder<>();
		batchItemWriterBuilder
				.itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Customer>());
		batchItemWriterBuilder.sql("insert into customer_details values(:customerId , :firstName , :lastName)");
		batchItemWriterBuilder.dataSource(dataSource);

		JdbcBatchItemWriter<Customer> batchItemWriter = batchItemWriterBuilder.build();
		return batchItemWriter;
	}

	public LineMapper<Customer> lineMapper() {
		DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer();
		delimitedLineTokenizer.setDelimiter(",");
		delimitedLineTokenizer.setStrict(false);
		delimitedLineTokenizer.setNames("customerId", "firstName", "lastName");

		BeanWrapperFieldSetMapper<Customer> beanWrapperFieldSetMapper = new BeanWrapperFieldSetMapper<>();
		beanWrapperFieldSetMapper.setTargetType(Customer.class);

		DefaultLineMapper<Customer> defaultLineMapper = new DefaultLineMapper<>();
		defaultLineMapper.setLineTokenizer(delimitedLineTokenizer);
		defaultLineMapper.setFieldSetMapper(beanWrapperFieldSetMapper);

		return defaultLineMapper;
	}

	@Bean
	public FlatFileItemReader<Customer> customerReader() {
		FlatFileItemReader<Customer> flatFileItemReader = new FlatFileItemReader<>();
		flatFileItemReader.setResource(new FileSystemResource("src/main/resources/customer.csv"));
		flatFileItemReader.setName("csvReader");
		flatFileItemReader.setLinesToSkip(1);
		flatFileItemReader.setLineMapper(lineMapper());

		return flatFileItemReader;
	}

	@Bean
	public Step step1(JdbcBatchItemWriter<Customer> jdbcBatchItemWriter) {
		Step step = stepBuilderFactory.get("step1").<Customer, Customer>chunk(10).reader(customerReader())
				.processor(customerProcessor()).writer(jdbcBatchItemWriter).build();
		return step;
	}

	@Bean
	public Job job(Step step1) {
		Job job = jobBuilderFactory.get("importCustomers").flow(step1).end().build();
		return job;
	}

}
