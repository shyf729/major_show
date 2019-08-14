package com.hrong.major.controller.common;


import com.hrong.major.annotation.ClickLog;
import com.hrong.major.model.ClickType;
import com.hrong.major.service.SubjectService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;

/**
 * @Author hrong
 **/
@Controller
public class IndexController {

	@Resource
	private SubjectService subjectService;

	@ClickLog(type = ClickType.subject)
	@GetMapping("/")
	public String index(Model model) {
		model.addAttribute("subjects", subjectService.list());
		return "index";
	}
	@GetMapping("/about")
	public String about() {
		return "about/about";
	}

	@GetMapping("/contact")
	public String contact() {
		return "contact/contact";
	}
}