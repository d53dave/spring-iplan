package at.iplan.controller;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
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

import biweekly.Biweekly;
import biweekly.ICalendar;
import at.iplan.model.Activity;
import at.iplan.model.Course;
import at.iplan.model.IPlanCalendar;
import at.iplan.model.Options;
import at.iplan.model.Statistics;
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
	IPlanCalendar getCalendar(@PathVariable Long id,
			HttpServletResponse response) {
		IPlanCalendar cal = calendarService.getById(id);
		if (cal == null)
			response.setStatus(HttpStatus.NOT_FOUND.value());
		return cal;
	}

	@RequestMapping(value = "/{id}/export", method = RequestMethod.GET)
	void exportToIcal(@PathVariable Long id, HttpServletResponse response) {
		ICalendar cal = calendarService.getDownloadableICalFile(id);
		
		if (cal == null) {
			response.setStatus(HttpStatus.NOT_FOUND.value());
			return;
		}
		try {
			String fileName = URLEncoder.encode("iPlanExport_"+id+".ical", "UTF-8");
			fileName = URLDecoder.decode(fileName, "ISO8859_1");
			response.setContentType("text/calendar");
			response.setHeader("Content-disposition", "attachment; filename="+ fileName);
			Biweekly.write(cal).go(response.getOutputStream());
			response.flushBuffer();
		} catch (IOException ex) {
			throw new RuntimeException("IOError writing file to output stream");
		}
	}

	@RequestMapping(value = "get", method = RequestMethod.POST)
	@ResponseBody
	IPlanCalendar saveCalendar(HttpSession session) {
		Long id = (Long) session.getAttribute("calendarId");
		IPlanCalendar cal;
		if (id == null) {
			cal = calendarService.createCalendar();
			session.setAttribute("calendarId", cal.getId());
		} else {
			cal = calendarService.getById(id);
			if (cal == null) { // for debugging
				cal = calendarService.createCalendar();
				session.setAttribute("calendarId", cal.getId());
			}
		}
		return cal;
	}

	@RequestMapping(value = "{id}/options", method = RequestMethod.POST)
	@ResponseBody
	void saveOptions(@PathVariable Long id, @RequestBody Options options,
			HttpServletResponse response) {
		IPlanCalendar cal = calendarService.getById(id);
		if (cal == null) {
			response.setStatus(HttpStatus.NOT_FOUND.value());
			return;
		}
		cal.setOptions(options);
	}

	@RequestMapping(value = "{id}/statistics", method = RequestMethod.GET)
	@ResponseBody
	Statistics getStatistics(@PathVariable Long id, HttpSession session) {
		IPlanCalendar cal = calendarService.getById(id);
		return cal.getStatistics();
	}

	@RequestMapping(value = "upload", method = RequestMethod.POST)
	@ResponseBody 
	IPlanCalendar handleFileUpload(
			MultipartHttpServletRequest request, HttpServletResponse response, HttpSession session) {

		Iterator<String> itr = request.getFileNames();
		MultipartFile mpf = null;
		IPlanCalendar cal = null;
		while (itr.hasNext()) {
			mpf = request.getFile(itr.next());
			try {
				String content = new String(mpf.getBytes());
				cal = calendarService.parseFromICalFile(content, (Long) session.getAttribute("calendarId"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("IMPORT: Returning "+getReflectionString(cal));
		return cal;
	}

	@RequestMapping(value = "{id}/activity/new", method = RequestMethod.POST)
	@ResponseBody
	Activity newActivity(@PathVariable Long id, @RequestBody Activity activity,
			HttpServletResponse response) {
		IPlanCalendar cal = calendarService.getById(id);
		if (cal != null) {
			boolean result = calendarService.scheduleActivity(cal, activity);
			if (!result) {
				response.setStatus(HttpStatus.CONFLICT.value());
			}
		}
		return activity;
	}

	@RequestMapping(value = "{id}/activity/{aid}/delete", method = RequestMethod.POST)
	@ResponseBody
	IPlanCalendar removeActivity(@PathVariable Long id, @PathVariable Long aid,
			HttpServletResponse response) {
		IPlanCalendar cal = calendarService.getById(id);
		if (cal != null) {
			boolean success = cal.removeActivity(aid);
			if (!success) {
				response.setStatus(HttpStatus.NOT_FOUND.value());
			}
		}
		return cal;
	}

	@RequestMapping(value = "{id}/course/new", method = RequestMethod.POST)
	@ResponseBody
	IPlanCalendar newCourse(@PathVariable Long id, @RequestBody Course course,
			HttpServletResponse response) {
		IPlanCalendar cal = calendarService.getById(id);
		System.out.println(schedulerService);
		if (cal != null) {
			if (schedulerService.isNonOverlapping(cal, course)) {
				cal.addCourse(course);
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
	Course getCourse(@PathVariable Long id, @PathVariable Long cid,
			HttpServletResponse response) {
		IPlanCalendar cal = calendarService.getById(id);

		Optional<Course> res = cal.getCourses().stream()
				.filter(c -> c.getId().equals(cid)).findFirst();
		if (!res.isPresent()) {
			response.setStatus(HttpStatus.NOT_FOUND.value());
		}
		return res.orElse(null);
	}

	@RequestMapping(value = "{id}/course/{cid}/delete", method = RequestMethod.POST)
	@ResponseBody
	IPlanCalendar removeCourse(@PathVariable Long id, @PathVariable Long cid,
			HttpServletResponse response) {
		IPlanCalendar cal = calendarService.getById(id);
		if (cal != null) {
			boolean success = cal.removeCourse(cid);
			if (!success) {
				response.setStatus(HttpStatus.NOT_FOUND.value());
			}
		}
		return cal;
	}

	@RequestMapping(value = "{id}/clear", method = RequestMethod.POST)
	@ResponseBody
	IPlanCalendar clearCalendar(@PathVariable Long id) {
		return calendarService.clearCalendar(id);
	}

	private String getReflectionString(Object o) {
		return ReflectionToStringBuilder.toString(o,
				ToStringStyle.MULTI_LINE_STYLE);
	}
}
