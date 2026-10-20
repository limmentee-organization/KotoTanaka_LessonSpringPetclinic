package org.springframework.samples.petclinic.system;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CrashController {

	/*
	 * ◯例外を意図的に発生させるためのGetMapping
	 * ・Spring Boot が例外をキャッチして、自動的に error という名前の画面（error.html）を表示
	 * ・メニューのERROR押下時に例外を意図的に発生
	 */
	@GetMapping("/oups")
	public String triggerException() {
		throw new RuntimeException(
				"Expected: controller used to showcase what " + "happens when an exception is thrown");
		
	}
}
