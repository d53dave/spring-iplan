package at.iplan.service;

import java.util.List;

import at.iplan.model.Activity;
import at.iplan.model.Course;
import at.iplan.model.IPlanCalendar;

public class SchedulerService {
	
	public IPlanCalendar schedule(IPlanCalendar calendar){
		List<Activity> activities = calendar.getActivities();
		List<Course> courses = calendar.getCourses();
		
		for(Course course:courses){
			
		}
		
		for(Activity activity:activities){
			
		}
		
		return calendar;
	}
	
	public boolean isNonOverlapping(IPlanCalendar calendar, Course course){
		
		return false;
	}

}
