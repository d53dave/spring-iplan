package at.iplan.model;

import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.http.HttpStatus;

import com.google.common.collect.Lists;

public class IPlanCalendar {
	private long internalIds;

	public void setInternalId(long id) {
		internalIds = id;
	}

	private Long id;
	private List<Course> courses = Lists.newArrayList();
	private List<Activity> activities = Lists.newArrayList();
	private Options options;
	private Statistics statistics = new Statistics(this);

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<Course> getCourses() {
		return courses;
	}

	public void addActivity(Activity activity) {
		long newId = getNextId();
		if (activity.getId() == null) {
			activity.setId(newId);

		}
		activities.add(activity);
		this.statistics = new Statistics(this);
	}

	public void addCourse(Course course) {
		long newId = getNextId();
		if (course.getId() == null) {
			course.setId(newId);
		}
		courses.add(course);
		this.statistics = new Statistics(this);
	}

	public void setCourses(List<Course> courses) {
		this.courses = courses;
		this.statistics = new Statistics(this);
	}

	public List<Activity> getActivities() {
		return activities;
	}

	public void setActivities(List<Activity> activities) {
		this.activities = activities;
		this.statistics = new Statistics(this);
	}

	public Options getOptions() {
		return options;
	}

	public void setOptions(Options options) {
		this.options = options;
	}

	private long getNextId() {
		return internalIds++;
	}

	public Statistics getStatistics() {
		return statistics;
	}

	public boolean removeCourse(Long cid) {
		List<Course> res = this.getCourses().stream()
				.filter(c -> !c.getId().equals(cid))
				.collect(Collectors.toList());
		if (res.size() < getCourses().size()) {
			this.courses = res;
			this.statistics = new Statistics(this);
			return true;
		}
		return false;
	}

	public boolean removeActivity(Long aid) {
		System.out.println("Removing activity with id "+aid + " from list of size "+getActivities().size());
		List<Activity> res = this.getActivities().stream()
				.filter(c -> !c.getId().equals(aid))
				.collect(Collectors.toList());
		System.out.println("New size is "+res.size());
		if (res.size() < getActivities().size()) {
			this.activities = res;
			this.statistics = new Statistics(this);
			return true;
		}
		return false;
	}

	@Override
	public boolean equals(Object o) {
		System.out.println("Calendar " + this + " equals called");
		return EqualsBuilder.reflectionEquals(this, o, false);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this, false);
	}

	public void clear() {
		this.setOptions(new Options());
		this.getActivities().clear();
		this.getCourses().clear();
		this.statistics = new Statistics(this);
	}

}
