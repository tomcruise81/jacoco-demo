package com.example.demo;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ComponentScan(basePackages = { "com.example.demo", "com.example.demo2"} )
public class DemoController {

	@RequestMapping(value = "/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public String helloWorld(@RequestParam(required = false) String name) throws Exception {
        String salutation = "World";
        if (name != null) {
            salutation = name.trim();
        }
		return "Hello " + salutation + "!";
    }

}
