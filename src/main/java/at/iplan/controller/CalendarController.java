package at.iplan.controller;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.websocket.server.PathParam;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import sun.awt.HToolkit;
import at.iplan.model.Activity;
import at.iplan.model.IPlanCalendar;
import at.iplan.service.CalendarService;

@Controller
@RequestMapping("/calendar")
public class CalendarController {

	@Autowired
	private CalendarService calendarService;

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	IPlanCalendar getCalendar(@PathVariable Long id, HttpServletResponse response) {
		IPlanCalendar cal = calendarService.getById(id);
		if(cal == null) response.setStatus(HttpStatus.NOT_FOUND.value());
		return cal;
	}

	@RequestMapping(value = "new", method = RequestMethod.POST)
	@ResponseBody
	IPlanCalendar saveCalendar() {
		return calendarService.createCalendar();
	}

	@RequestMapping(value = "upload", method = RequestMethod.POST)
	public @ResponseBody IPlanCalendar handleFileUpload(
			MultipartHttpServletRequest request, HttpServletResponse response) {
		
        Iterator<String> itr =  request.getFileNames();
        MultipartFile mpf = null;
        IPlanCalendar cal = null;
        while(itr.hasNext()){
            mpf = request.getFile(itr.next()); 
            try {
                String content = new String(mpf.getBytes());
				cal = calendarService.parseFromICalFile(content);
           } catch (IOException e) {
               e.printStackTrace();
           }
        }
        return cal;
   }
		
	
	@RequestMapping(value = "{id}/activity/new", method = RequestMethod.POST)
	@ResponseBody
	Activity newActivity(@PathVariable Long id, @RequestBody Activity activity) {
		IPlanCalendar cal = calendarService.getById(id);
		if(cal != null){
			cal.getActivities().add(activity);
		}
		System.out.println("Added Activity "+ReflectionToStringBuilder.toString(activity, ToStringStyle.MULTI_LINE_STYLE));
		return activity;
	}
	
	@RequestMapping(value = "{id}/activity/{aid}", method = RequestMethod.GET)
	Activity updateActivity(@PathVariable Long id, @PathVariable Long aid) {
		IPlanCalendar cal = calendarService.getById(id);

		return cal.getActivities().stream().filter(ac -> ac.getId().equals(aid)).findFirst().get();
	}
	
	@RequestMapping(value = "{id}/activity/{aid}", method = RequestMethod.POST)
	Activity updateActivity(@PathVariable Long id, @PathVariable Long aid, @RequestParam Activity activity) {
		IPlanCalendar cal = calendarService.getById(id);
		if(cal != null && activity.getId() != null){
			List<Activity> filtered = cal.getActivities().stream().filter(ac -> !ac.getId().equals(activity.getId())).collect(Collectors.toList());
			cal.setActivities(filtered);
			System.out.println("Updated Activity "+ReflectionToStringBuilder.toString(activity, ToStringStyle.MULTI_LINE_STYLE));
		} else {
			System.out.println("No activity saved");
		}
		
		return activity;
	}
	
	@RequestMapping(value = "{id}/course/new", method = RequestMethod.POST)
	IPlanCalendar newCourse(@PathVariable Long id, @RequestParam Activity activity) {
		IPlanCalendar cal = calendarService.getById(id);
		if(cal != null){
			cal.getActivities().add(activity);
		}
		
		return cal;
	}
	
	@RequestMapping(value = "{id}/activity/{cid}", method = RequestMethod.POST)
	IPlanCalendar updateCourse(@PathVariable Long id, @PathVariable Long cid,@RequestParam Activity activity) {
		IPlanCalendar cal = calendarService.getById(id);
		if(cal != null){
			cal.getActivities().add(activity);
		}
		
		return cal;
	}

	@RequestMapping(value = "{id}/clear", method = RequestMethod.POST)
	IPlanCalendar clearCalendar(@PathVariable Long id) {
		calendarService.clearCalendar(id);
		return calendarService.getById(id);
	}
	
	private String getReflectionString(Object o){
		return ReflectionToStringBuilder.toString(o, ToStringStyle.MULTI_LINE_STYLE);
	}
}
