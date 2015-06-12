package at.iplan.controller;

import java.io.IOException;
import java.util.Properties;

import javax.servlet.http.HttpSession;
import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import at.iplan.model.Activity;
import at.iplan.model.IPlanCalendar;
import at.iplan.service.CalendarService;

@Controller
@RequestMapping("/calendar")
public class CalendarController {

	@Autowired
	private CalendarService calendarService;

	@RequestMapping(value = "{id}", method = RequestMethod.GET)
	@ResponseBody
	IPlanCalendar getCalendar(@RequestParam(value = "id") Long id, HttpSession session) {
		return calendarService.getById(id);
	}

	@RequestMapping(value = "new", method = RequestMethod.POST)
	IPlanCalendar saveCalendar(@RequestParam(value = "id") Long id, HttpSession session) {
		return calendarService.createCalendar();
	}

	@RequestMapping(value = "upload", method = RequestMethod.POST)
	public @ResponseBody String handleFileUpload(
			@RequestParam("file") MultipartFile file) {
		if (!file.isEmpty()) {
				String content;
				try {
					content = new String(file.getBytes());
					calendarService.parseFromICalFile(content);
				} catch (IOException e) {
					return e.getMessage();
				}
				return "You successfully uploaded " + file.getName() + "!";
		} else {
			return "You failed to upload " + file.getName()
					+ " because the file was empty.";
		}
	}
	
	@RequestMapping(value = "calendar/{id}/activity/new", method = RequestMethod.POST)
	@ResponseBody
	Activity newActivity(@PathParam(value = "id") Long calendarId, @RequestParam(value = "activity") Activity activity) {
		IPlanCalendar cal = calendarService.getById(calendarId);
		if(cal != null){
			cal.getActivities().add(activity);
		}
		return activity;
	}
	
	@RequestMapping(value = "calendar/{id}/activity/{aid}", method = RequestMethod.POST)
	Activity updateActivity(@PathParam(value = "id") Long calendarId, @RequestParam(value = "activity") Activity activity) {
		IPlanCalendar cal = calendarService.getById(calendarId);
		if(cal != null){
			cal.getActivities().add(activity);
		}
		
		return activity;
	}
	
	@RequestMapping(value = "calendar/{id}/course/new", method = RequestMethod.POST)
	IPlanCalendar newCourse(@PathParam(value = "id") Long calendarId, @RequestParam(value = "activity") Activity activity) {
		IPlanCalendar cal = calendarService.getById(calendarId);
		if(cal != null){
			cal.getActivities().add(activity);
		}
		
		return cal;
	}
	
	@RequestMapping(value = "calendar/{id}/activity/{cid}", method = RequestMethod.POST)
	IPlanCalendar updateCourse(@PathParam(value = "id") Long calendarId, @RequestParam(value = "activity") Activity activity) {
		IPlanCalendar cal = calendarService.getById(calendarId);
		if(cal != null){
			cal.getActivities().add(activity);
		}
		
		return cal;
	}

	@RequestMapping(value = "{id}/clear", method = RequestMethod.POST)
	IPlanCalendar clearCalendar(@RequestParam(value = "id") Long id) {
		calendarService.clearCalendar(id);
		return calendarService.getById(id);
	}

	@RequestMapping("properties")
	@ResponseBody
	Properties properties() {
		return System.getProperties();
	}
}
