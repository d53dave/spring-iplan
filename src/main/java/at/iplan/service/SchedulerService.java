package at.iplan.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import at.iplan.model.Activity;
import at.iplan.model.Course;
import at.iplan.model.IPlanCalendar;

@Service
public class SchedulerService {
	
	public IPlanCalendar schedule(IPlanCalendar calendar){
		List<Activity> activities = calendar.getActivities();
		List<Course> courses = calendar.getCourses();
		
		for(Activity activity:activities){
			
		}
		
		return calendar;
	}
	
	public boolean isNonOverlapping(IPlanCalendar calendar, Course course){
		LocalDateTime courseStart = course.getStartTime().plusMinutes(1);
		LocalDateTime courseEnd = course.getEndTime().minusMinutes(1);
		
		for(Course alreadyScheduledCourse:calendar.getCourses()){
			if((alreadyScheduledCourse.getStartTime().isBefore(courseStart) && alreadyScheduledCourse.getEndTime().isAfter(courseEnd))
					|| between(courseStart, courseEnd, alreadyScheduledCourse.getStartTime())
					|| between(courseStart, courseEnd, alreadyScheduledCourse.getEndTime())) return false;
		}
		
		return true;
	}
	
	private boolean between(LocalDateTime start, LocalDateTime end, LocalDateTime test){
		return test.isAfter(start) && test.isBefore(end);
	}

}
