package at.iplan.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import at.iplan.model.Activity;
import at.iplan.model.IPlanCalendar;
import at.iplan.service.CalendarService;


@Controller
@RequestMapping("/activity")
public class ActivityController {
	
	@Autowired
	private CalendarService calendarService;

	@RequestMapping(value = "new", method = RequestMethod.POST)
		ModelAndView saveCalendar(@RequestParam(value = "id") Long calendarId, @RequestParam(value = "activity") Activity activity) {
			IPlanCalendar cal = calendarService.getById(calendarId);
			if(cal != null){
				cal.getActivities().add(activity);
			}
			
			ModelAndView model = new ModelAndView("calendar");
			model.addObject("calendar", cal);
			return model;
		}

}
