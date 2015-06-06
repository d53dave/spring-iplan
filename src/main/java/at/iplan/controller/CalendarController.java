package at.iplan.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import biweekly.Biweekly;
import biweekly.ICalendar;
import at.iplan.model.Activity;
import at.iplan.model.IPlanCalendar;
import at.iplan.service.CalendarService;

@Controller
@RequestMapping("/calendar")
public class CalendarController {

	@Autowired
	private CalendarService calendarService;

	@RequestMapping(value = "{id}", method = RequestMethod.GET)
	String getCalendar(@RequestParam(value = "id") Long id, HttpSession session) {
		return "calendar";
	}

	@RequestMapping(value = "new", method = RequestMethod.POST)
	String saveCalendar(@RequestParam(value = "id") Long id, HttpSession session) {
		calendarService.createCalendar();
		return "calendar";
	}

	@RequestMapping(value = "upload", method = RequestMethod.POST)
	String uploadCalendar(@RequestParam(value = "id") Long id,
			HttpSession session) {
		calendarService.createCalendar();
		return "calendar";
	}

	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public @ResponseBody String handleFileUpload(
			@RequestParam("name") String name,
			@RequestParam("file") MultipartFile file) {
		if (!file.isEmpty()) {
				String content;
				try {
					content = new String(file.getBytes());
					calendarService.parseFromICalFile(content);
				} catch (IOException e) {
					return e.getMessage();
				}
				return "You successfully uploaded " + name + "!";
		} else {
			return "You failed to upload " + name
					+ " because the file was empty.";
		}
	}
	
	@RequestMapping(value = "calendar/{id}/activity/new", method = RequestMethod.POST)
	ModelAndView newActivity(@RequestParam(value = "id") Long calendarId, @RequestParam(value = "activity") Activity activity) {
		IPlanCalendar cal = calendarService.getById(calendarId);
		if(cal != null){
			cal.getActivities().add(activity);
		}
		
		ModelAndView model = new ModelAndView("calendar");
		model.addObject("calendar", cal);
		return model;
	}
	
	@RequestMapping(value = "calendar/{id}/activity/{aid}", method = RequestMethod.POST)
	ModelAndView updateActivity(@RequestParam(value = "id") Long calendarId, @RequestParam(value = "activity") Activity activity) {
		IPlanCalendar cal = calendarService.getById(calendarId);
		if(cal != null){
			cal.getActivities().add(activity);
		}
		
		ModelAndView model = new ModelAndView("calendar");
		model.addObject("calendar", cal);
		return model;
	}
	
	@RequestMapping(value = "calendar/{id}/course/new", method = RequestMethod.POST)
	ModelAndView newCourse(@RequestParam(value = "id") Long calendarId, @RequestParam(value = "activity") Activity activity) {
		IPlanCalendar cal = calendarService.getById(calendarId);
		if(cal != null){
			cal.getActivities().add(activity);
		}
		
		ModelAndView model = new ModelAndView("calendar");
		model.addObject("calendar", cal);
		return model;
	}
	
	@RequestMapping(value = "calendar/{id}/activity/{cid}", method = RequestMethod.POST)
	ModelAndView updateCourse(@RequestParam(value = "id") Long calendarId, @RequestParam(value = "activity") Activity activity) {
		IPlanCalendar cal = calendarService.getById(calendarId);
		if(cal != null){
			cal.getActivities().add(activity);
		}
		
		ModelAndView model = new ModelAndView("calendar");
		model.addObject("calendar", cal);
		return model;
	}

	@RequestMapping(value = "{id}/clear", method = RequestMethod.POST)
	String clearCalendar(@RequestParam(value = "id") Long id) {
		calendarService.clearCalendar(id);
		return "index";
	}

	@RequestMapping("properties")
	@ResponseBody
	Properties properties() {
		return System.getProperties();
	}
}
