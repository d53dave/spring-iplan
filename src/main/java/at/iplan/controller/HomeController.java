package at.iplan.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HomeController {

	@RequestMapping("")
	String index() {
		return "index";
	}

}
