package at.iplan.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.iplan.model.Activity;
import at.iplan.model.CalendarItem;
import at.iplan.model.Course;
import at.iplan.model.IPlanCalendar;
import at.iplan.model.Options;
import biweekly.Biweekly;
import biweekly.ICalendar;
import biweekly.component.VEvent;

import java.time.LocalDateTime;

import com.google.common.collect.Maps;

@Service
public class CalendarService {

	private static long idCounter;
	private Map<Long, IPlanCalendar> calendars = Maps.newHashMap();

	@Autowired
	private SchedulerService schedulerService;

	public IPlanCalendar createCalendar() {
		IPlanCalendar cal = new IPlanCalendar();
		cal.setId(this.getNewId());
		cal.setOptions(new Options());

		calendars.put(cal.getId(), cal);
		System.out.println("Now having " + calendars.size() + " Calendars");

		printCals();
		return cal;
	}

	public IPlanCalendar parseFromICalFile(String content) {
		ICalendar ical = Biweekly.parse(content).first();
		if (ical == null) {
			System.out.println("LOLMate");
			return null;
		}
		List<VEvent> events = ical.getEvents();
		for (VEvent event : events) {
			System.out.println(event.getDateStart().getValue().toGMTString());
		}
		return null;
	}

	public IPlanCalendar getById(Long id) {
		System.out.println("Calendars contains id: "
				+ calendars.containsKey(id));
		IPlanCalendar cal = calendars.get(id);
		System.out.println("Returning cal " + getReflectionString(cal));
		return cal;
	}

	public IPlanCalendar addCourseToCalendar(Long id, Course course) {
		IPlanCalendar cal = calendars.get(id);
		if (cal != null) {
			cal.getCourses().add(course);
		}
		return cal;
	}

	public IPlanCalendar addActivityToCalendar(Long id, Activity activity) {
		IPlanCalendar cal = calendars.get(id);
		if (cal != null) {
			cal.getActivities().add(activity);
		}
		return cal;
	}

	public IPlanCalendar saveCalendar(IPlanCalendar cal) {
		if (cal.getId() == null) {
			cal.setId(getNewId());
		}
		calendars.put(cal.getId(), cal);
		return cal;
	}

	public IPlanCalendar removeCourse(IPlanCalendar cal, Long id) {
		List<Course> filtered = filterById(cal.getCourses(), id);
		cal.setCourses(filtered);
		return cal;
	}

	public IPlanCalendar removeActivity(IPlanCalendar cal, Long id) {
		List<Activity> filtered = filterById(cal.getActivities(), id);
		cal.setActivities(filtered);
		return cal;
	}

	private <T extends CalendarItem> List<T> filterById(List<T> items, Long id) {
		return items.stream().filter(item -> !item.getId().equals(id))
				.collect(Collectors.toList());
	}

	private long getNewId() {
		return idCounter++;
	}

	public IPlanCalendar clearCalendar(Long id) {
		IPlanCalendar cal = this.getById(id);
		if (cal != null) {
			cal.setOptions(new Options());
			cal.getActivities().clear();
			cal.getCourses().clear();
		}
		return cal;
	}

	public IPlanCalendar saveOptions(Long id, Options opt) {
		IPlanCalendar cal = this.getById(id);
		if (cal != null) {
			cal.setOptions(opt);
		}
		return cal;
	}

	private void printCals() {
		calendars.forEach((k, v) -> System.out.println(k + ": "
				+ getReflectionString(v)));
	}

	private String getReflectionString(Object o) {
		return ReflectionToStringBuilder.toString(o,
				ToStringStyle.MULTI_LINE_STYLE);
	}

	public boolean scheduleActivity(IPlanCalendar cal, Activity activity) {
		LocalDateTime start = schedulerService.getScheduledStartForActivity(
				cal, activity);
		if (start != null) {

			activity.setStartTime(start);
			cal.addActivity(activity);
			return true;
		}
		return false;
	}
}
