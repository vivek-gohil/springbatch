package com.borntocode.main.processor;

import org.springframework.batch.item.ItemProcessor;

import com.borntocode.main.domain.Customer;

public class CustomerProcessor implements ItemProcessor<Customer, Customer> {
	@Override
	public Customer process(Customer customer) throws Exception {
		return customer;
	}
}
