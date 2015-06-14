package at.iplan.service;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.sun.xml.internal.fastinfoset.algorithm.BuiltInEncodingAlgorithm.WordListener;

import at.iplan.model.Activity;
import at.iplan.model.CalendarItem;
import at.iplan.model.Course;
import at.iplan.model.IPlanCalendar;

@Service
public class SchedulerService {
	
	public LocalDateTime getScheduledStartForActivity(IPlanCalendar calendar, Activity activity){
		for(int dayNum = 1; dayNum<= 7-calendar.getOptions().getFreeDayCount(); ++dayNum){
			DayOfWeek day = DayOfWeek.of(dayNum);
			List<CalendarItem> items = getSortedItemsForDay(calendar, day);
			
			//check for workload
			long workload = items.stream().mapToLong(item -> item.getDuration().toHours()).sum();
			if(workload > calendar.getOptions().getWorkload()) return null;
			
			Duration activityDuration = activity.getDuration();
			
			if(LocalTime.of(06,00).plus(activityDuration).isBefore(getStartTimeForItem(items.get(0)))){ //after 6 and before first?
				return LocalDateTime.of(LocalDate.now(), getStartTimeForItem(items.get(0)).minus(activityDuration));
			}
			
			//between activities
			for(int i=1;i<items.size()-1;++i){
				LocalTime firstItemEnd = getEndTimeForItem(items.get(i));
				LocalTime secondItemStart = getStartTimeForItem(items.get(i+1));
				
				Duration durationBetween = Duration.between(firstItemEnd, secondItemStart);

				if(durationBetween.compareTo(activityDuration) >= 0){ // same or greater
					return LocalDateTime.of(LocalDate.now(), firstItemEnd);
				}
			}
			
			//after last activity
			if(getEndTimeForItem(items.get(items.size()-1)).plus(activityDuration).isBefore(LocalTime.MIDNIGHT)){ //after last and before 12?
				return LocalDateTime.of(LocalDate.now(), getEndTimeForItem(items.get(items.size()-1)));
			}
			
			
			//couldn't schedule in this day, try next
		}
		return null; //cant be scheduled
	}
	
	private List<CalendarItem> getSortedItemsForDay(IPlanCalendar calendar, DayOfWeek dofWeek){
		Stream<CalendarItem> items = getAllItemsFromCalendar(calendar);
		
		return items
				.filter(item -> item.getStartTime().getDayOfWeek().equals(dofWeek))
				.sorted((i1, i2)-> getStartTimeForItem(i1).compareTo(getStartTimeForItem(i2)))
				.collect(Collectors.toList());
	}
	
	private Stream<CalendarItem> getAllItemsFromCalendar(IPlanCalendar calendar) {
		return Stream.concat(calendar.getActivities().stream(), calendar.getCourses().stream());
	}

	private LocalTime getStartTimeForItem(CalendarItem item){
		if(item instanceof Course){
			Course course = (Course) item;
			return LocalTime.from(course.getStartTime().minus(course.getPreparationTime()).plusMinutes(1));
		} else {
			return LocalTime.from(item.getStartTime().plusMinutes(1));
		}
	}
	
	private LocalTime getEndTimeForItem(CalendarItem item){
		if(item instanceof Course){
			Course course = (Course) item;
			return LocalTime.from(course.getEndTime().plus(course.getReworkTime()).minusMinutes(1));
		} else {
			return LocalTime.from(item.getEndTime().minusMinutes(1));
		}
	}
	
	
	public boolean isNonOverlapping(IPlanCalendar calendar, CalendarItem item){
		LocalTime scheduledStart = getStartTimeForItem(item);
		LocalTime scheduledEnd = getEndTimeForItem(item);
		
		List<CalendarItem> items = getAllItemsFromCalendar(calendar).collect(Collectors.toList());
		
		for(CalendarItem alreadyScheduledItem:items){
			LocalTime alreadyScheduledStart = getStartTimeForItem(alreadyScheduledItem);
			LocalTime alreadyScheduledEnd = getEndTimeForItem(alreadyScheduledItem);
			
			if((alreadyScheduledStart.isBefore(scheduledStart) && alreadyScheduledEnd.isAfter(scheduledEnd))
					|| between(scheduledStart, scheduledEnd, alreadyScheduledStart)
					|| between(scheduledStart, scheduledEnd, alreadyScheduledEnd)) return false;
		}
		
		return true;
	}
	
	private boolean between(LocalTime start, LocalTime end, LocalTime test){
		return test.isAfter(start) && test.isBefore(end);
	}

}
