package com.borntocode.main.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {
	@RequestMapping("/")
	public String greet() {
		return "Welcome to Spring Security";
	}

	@RequestMapping("user")
	public String greetUser() {
		return "Welcome to Spring Security :: User";
	}

	@RequestMapping("admin")
	public String greetAdmin() {
		return "Welcome to Spring Security :: Admin ";
	}

}
