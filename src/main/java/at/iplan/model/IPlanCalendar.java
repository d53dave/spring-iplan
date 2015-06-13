package at.iplan.model;

import java.util.List;

import com.google.common.collect.Lists;

public class IPlanCalendar {
	
	private Long id;
	private List<Course> courses = Lists.newArrayList();
	private List<Activity> activities = Lists.newArrayList();
	private Options options;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<Course> getCourses() {
		return courses;
	}

	public void setCourses(List<Course> courses) {
		this.courses = courses;
	}

	public List<Activity> getActivities() {
		return activities;
	}

	public void setActivities(List<Activity> activities) {
		this.activities = activities;
	}

	public Options getOptions() {
		return options;
	}

	public void setOptions(Options options) {
		this.options = options;
	}

}
