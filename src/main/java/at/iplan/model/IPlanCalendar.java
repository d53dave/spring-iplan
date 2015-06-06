package at.iplan.model;

import java.util.List;

import com.google.common.collect.Lists;

public class IPlanCalendar {
	
	private Long id;
	private List<Course> courses = Lists.newArrayList();
	private List<Activity> activities = Lists.newArrayList();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<Course> getCourses() {
		return courses;
	}


	public List<Activity> getActivities() {
		return activities;
	}

}
