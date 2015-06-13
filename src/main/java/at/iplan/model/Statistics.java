package at.iplan.model;

import java.time.Duration;

public class Statistics {
	
	private static final Duration ONE_WEEK = Duration.ofDays(7);
	
	private Duration total;
	private Duration courseWork;
	private Duration freeTime;
	private Duration afterClasses;
	private Duration preparation;
	private Duration rest;
	
	public Statistics(IPlanCalendar cal){
		for(Activity activity:cal.getActivities()){
			
		}
		
		for(Course course: cal.getCourses()){
			
		}
	}
	

}
