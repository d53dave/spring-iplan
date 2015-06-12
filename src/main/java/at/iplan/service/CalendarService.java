package at.iplan.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import at.iplan.model.Activity;
import at.iplan.model.Course;
import at.iplan.model.IPlanCalendar;
import biweekly.Biweekly;
import biweekly.ICalendar;
import biweekly.component.VEvent;

import com.google.common.collect.Maps;

@Service
public class CalendarService {
	
	private static long idCounter;
	private Map<Long, IPlanCalendar> calendars = Maps.newHashMap();
	
	public IPlanCalendar createCalendar(){
		IPlanCalendar cal = new IPlanCalendar();
		cal.setId(this.getNewId());
		
		calendars.put(idCounter, cal);
		
		return cal;
	}
	
	public IPlanCalendar parseFromICalFile(String content){
		ICalendar ical = Biweekly.parse(content).first();
		if(ical == null){
			return null;
		}
		List<VEvent> events = ical.getEvents();
		for(VEvent event : events){
			System.out.println(event.getDateStart().getValue().toGMTString());
		}
		return null;
	}
	
	public IPlanCalendar getById(Long id){
		return calendars.get(id);
	}
	
	public IPlanCalendar addCourseToCalendar(Long id, Course course){
		IPlanCalendar cal = calendars.get(id);
		if(cal != null) {
			cal.getCourses().add(course);
		}
		return cal;
	}
	
	public IPlanCalendar addActivityToCalendar(Long id, Activity activity){
		IPlanCalendar cal = calendars.get(id);
		if(cal != null) {
			cal.getActivities().add(activity);
		}
		return cal;
	}
	
	public IPlanCalendar saveCalendar(IPlanCalendar cal){
		if(cal.getId() == null){
			cal.setId(getNewId());
		}
		calendars.put(cal.getId(), cal);
		return cal;
	}
	
	private long getNewId(){
		return idCounter++;
	}

	public IPlanCalendar clearCalendar(Long id) {
		IPlanCalendar cal = this.getById(id);
		if(cal != null) {
			cal.getActivities().clear();
			cal.getCourses().clear();
		}
		return cal;
	}
}
