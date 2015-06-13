package at.iplan.controller;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import at.iplan.model.Activity;
import at.iplan.model.Course;
import at.iplan.model.IPlanCalendar;
import at.iplan.service.CalendarService;
import at.iplan.service.SchedulerService;

@Controller
@RequestMapping("/calendar")
public class CalendarController {

	@Autowired
	private CalendarService calendarService;
	
	@Autowired
	private SchedulerService schedulerService;

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	IPlanCalendar getCalendar(@PathVariable Long id, HttpServletResponse response) {
		IPlanCalendar cal = calendarService.getById(id);
		if(cal == null) response.setStatus(HttpStatus.NOT_FOUND.value());
		return cal;
	}

	@RequestMapping(value = "get", method = RequestMethod.POST)
	@ResponseBody
	IPlanCalendar saveCalendar(HttpSession session) {
		Long id = (Long) session.getAttribute("calendarId");
		IPlanCalendar cal;
		if(id == null){
			cal = calendarService.createCalendar();
			session.setAttribute("calendarId", cal.getId());
		} else {
			cal = calendarService.getById(id);
		}
		
		return cal;
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
	@ResponseBody
	Activity getActivity(@PathVariable Long id, @PathVariable Long aid) {
		IPlanCalendar cal = calendarService.getById(id);

		return cal.getActivities().stream().filter(ac -> ac.getId().equals(aid)).findFirst().get();
	}
	
	@RequestMapping(value = "{id}/activity/{aid}", method = RequestMethod.POST)
	@ResponseBody
	Activity updateActivity(@PathVariable Long id, @PathVariable Long aid, @RequestBody Activity activity) {
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
	@ResponseBody
	IPlanCalendar newCourse(@PathVariable Long id, @RequestBody Course course, HttpServletResponse response) {
		IPlanCalendar cal = calendarService.getById(id);
		System.out.println(schedulerService);
		if(cal != null){
			if(schedulerService.isNonOverlapping(cal, course)){
				cal.getCourses().add(course);
			} else {
				response.setStatus(HttpStatus.FORBIDDEN.value());
			}
		} else {
			response.setStatus(HttpStatus.NOT_FOUND.value());
		}
		
		return cal;
	}
	
	@RequestMapping(value = "{id}/course/{cid}", method = RequestMethod.GET)
	@ResponseBody
	Course getCourse(@PathVariable Long id, @PathVariable Long cid) {
		IPlanCalendar cal = calendarService.getById(id);

		return cal.getCourses().stream().filter(ac -> ac.getId().equals(cid)).findFirst().get();
	}
	
	@RequestMapping(value = "{id}/course/{cid}", method = RequestMethod.POST)
	@ResponseBody
	IPlanCalendar updateCourse(@PathVariable Long id, @PathVariable Long cid,@RequestParam Activity activity) {
		IPlanCalendar cal = calendarService.getById(id);
		if(cal != null){
			cal.getActivities().add(activity);
		}
		
		return cal;
	}

	@RequestMapping(value = "{id}/clear", method = RequestMethod.POST)
	@ResponseBody
	IPlanCalendar clearCalendar(@PathVariable Long id) {
		calendarService.clearCalendar(id);
		return calendarService.getById(id);
	}
	
	private String getReflectionString(Object o){
		return ReflectionToStringBuilder.toString(o, ToStringStyle.MULTI_LINE_STYLE);
	}
}
