
package com.test;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

@RestController
public class GreetingController {

	private static final String template = "Hello, %s!";
	private final AtomicLong counter = new AtomicLong();
	private final Counter greetingAPICounter;


	public GreetingController(MeterRegistry meterRegistry){
		greetingAPICounter = meterRegistry.counter("counter-for-greeting-API");
	}

	@GetMapping("/greeting")
	public String greeting() {
		greetingAPICounter.increment();
		return String.format(template, counter.incrementAndGet());
	}
}