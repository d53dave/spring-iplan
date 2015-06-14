package at.iplan.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
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
import biweekly.property.DateStart;
import biweekly.property.Summary;
import biweekly.util.Duration;
import biweekly.util.Recurrence;
import biweekly.util.Recurrence.Frequency;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoField;

import com.google.common.collect.Maps;

@Service
public class CalendarService {

	private static long idCounter;
	private Map<Long, IPlanCalendar> calendars = Maps.newHashMap();

	private static final String IPLAN_ACTIVITY_TYPE = "iplan-activity";
	private static final String IPLAN_COURSE_TYPE = "iplan-course";
	
	@Autowired
	private SchedulerService schedulerService;
	
	public Map<Long, IPlanCalendar> getCalendars(){
		return this.calendars;
	}

	public IPlanCalendar createCalendar() {
		return createWithId(this.getNewId());
	}
	
	private IPlanCalendar createWithId(Long id){
		IPlanCalendar cal = new IPlanCalendar();
		cal.setId(id);
		cal.setOptions(new Options());

		calendars.put(cal.getId(), cal);
		return cal;
	}



	public IPlanCalendar getById(Long id) {
		System.out.println("Calendars contains id: "
				+ calendars.containsKey(id));
		IPlanCalendar cal = calendars.get(id);
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
			
			cal.clear();
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
	
	public IPlanCalendar parseFromICalFile(String content, Long id) {
		ICalendar ical = Biweekly.parse(content).first();
		
		if (ical == null) {
			return null;
		}
		
		IPlanCalendar internalCal = this.getById(id);
		if(internalCal != null){
			this.clearCalendar(id);
		} else {
			internalCal = createWithId(id);
		}
		
		List<VEvent> events = ical.getEvents();
		for (VEvent event : events) {
			
			System.out.println("Parsing event "+event);
			
			if(event.getExperimentalProperty("iplantype").getValue().equals(IPLAN_ACTIVITY_TYPE)){
				
				System.out.println("Its an activity");
				
				Activity act = new Activity();
				act.setDuration(java.time.Duration.ofMinutes(event.getDuration().getValue().getMinutes()));
				act.setId(Long.valueOf(event.getExperimentalProperty("iplanid").getValue()));
				act.setName(event.getSummary().getValue());
				
				//parse time but shift date to this week (while keeping the weekday)
				LocalDateTime parsedDateTime = LocalDateTime.ofInstant(event.getDateStart().getValue().toInstant(), ZoneId.systemDefault());
				LocalDate shiftedDate = LocalDate.now().with(ChronoField.DAY_OF_WEEK, parsedDateTime.getDayOfWeek().getValue());
				LocalDateTime shiftedDateTime = LocalDateTime.of(shiftedDate, LocalTime.from(parsedDateTime));
				
				act.setStartTime(shiftedDateTime);
				act.setText(event.getDescription().getValue());
				
				System.out.println("Adding activity "+ReflectionToStringBuilder.toString(act, ToStringStyle.MULTI_LINE_STYLE));
				
				internalCal.addActivity(act);
			} else if (event.getExperimentalProperty("iplantype").getValue().equals(IPLAN_COURSE_TYPE)){
				
				System.out.println("Its a course");
				
				Course c = new Course();
				c.setDuration(java.time.Duration.ofMinutes(event.getDuration().getValue().getMinutes()));
				c.setId(Long.valueOf(event.getExperimentalProperty("iplanid").getValue()));
				c.setName(event.getSummary().getValue());
				
				//parse time but shift date to this week (while keeping the weekday)
				LocalDateTime parsedDateTime = LocalDateTime.ofInstant(event.getDateStart().getValue().toInstant(), ZoneId.systemDefault());
				LocalDate shiftedDate = LocalDate.now().with(ChronoField.DAY_OF_WEEK, parsedDateTime.getDayOfWeek().getValue());
				LocalDateTime shiftedDateTime = LocalDateTime.of(shiftedDate, LocalTime.from(parsedDateTime));
				
				c.setStartTime(shiftedDateTime);
				c.setText(event.getDescription().getValue());
				c.setPriority(event.getPriority().getValue().longValue());
				String reworkString = event.getExperimentalProperty("iplanrework").getValue();
				if(StringUtils.isNumeric(reworkString)){
					c.setReworkTime(java.time.Duration.ofMinutes(Long.valueOf(reworkString)));
				}
				String prepString = event.getExperimentalProperty("iplanprep").getValue();
				if(StringUtils.isNumeric(prepString)){
					c.setPreparationTime(java.time.Duration.ofMinutes(Long.valueOf(prepString)));
				}
				internalCal.addCourse(c);
			}
		}
		
		internalCal.setOptions(Options.parseFromString(ical.getExperimentalProperty("iplanoptions").getValue()));
		
		return internalCal;
	}
	
	private void addItemToIcal(ICalendar ical, CalendarItem calItem, String iplanType){
		VEvent event = new VEvent();
		
		LocalDateTime ldt = calItem.getStartTime();
		Date start = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
		event.setExperimentalProperty("iplantype", iplanType);
		event.setExperimentalProperty("iplanid", calItem.getId().toString());
		event.setSummary(calItem.getName());
		event.getSummary().setLanguage("de-de");
		event.setDescription(calItem.getText());
		event.setDateStart(new DateStart(start, true));
		event.setDuration(new Duration.Builder().minutes((int) calItem.getDuration().toMinutes()).build());
		
		if(calItem instanceof Course){
			Course c = (Course) calItem;
			event.setPriority(c.getPriority() == null ? 0 : c.getPriority().intValue());
			event.setExperimentalProperty("iplanrework", c.getReworkTime().toMinutes()+"");
			event.setExperimentalProperty("iplanprep", c.getPreparationTime().toMinutes()+"");
		}

		ical.addEvent(event);
	}
	
	public ICalendar getDownloadableICalFile(Long id){
		ICalendar ical = new ICalendar();
		IPlanCalendar internalCal = this.getById(id);
		
		ical.setExperimentalProperty("iplanoptions", internalCal.getOptions().getSerializedString());
		
		if(internalCal != null){
			
			internalCal.getActivities().stream().forEach(act -> addItemToIcal(ical, act, IPLAN_ACTIVITY_TYPE));
			internalCal.getCourses().stream().forEach(crs -> addItemToIcal(ical, crs, IPLAN_COURSE_TYPE));
			
		}
		
		return ical;
	}
}
