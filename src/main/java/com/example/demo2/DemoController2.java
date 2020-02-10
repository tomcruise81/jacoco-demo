package com.example.demo2;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController2 {

	@RequestMapping(value = "/two", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public String helloWorld2(@RequestParam(required = false) String name) throws Exception {
        String salutation = "World";
        if (name != null) {
            salutation = name.trim();
        }
		return "Hello " + salutation + "!";
    }

}
