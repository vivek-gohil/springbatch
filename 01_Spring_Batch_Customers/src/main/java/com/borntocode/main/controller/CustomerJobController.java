package com.borntocode.main.controller;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("customerapi")
public class CustomerJobController {
	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	private Job job;

	@RequestMapping(value = "importcustomers", method = RequestMethod.POST)
	public void importCustomersToDatabase() {
		System.out.println("in importCustomersToDatabase");
		try {
			JobParameters jobParameters = new JobParametersBuilder().addLong("startAt", System.currentTimeMillis())
					.toJobParameters();
			jobLauncher.run(job, jobParameters);
		} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
				| JobParametersInvalidException e) {
			System.out.println("Exception!!");
			e.printStackTrace();
		}

	}
}
