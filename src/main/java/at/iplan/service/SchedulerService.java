package at.iplan.service;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.stereotype.Service;

import at.iplan.model.Activity;
import at.iplan.model.CalendarItem;
import at.iplan.model.Course;
import at.iplan.model.IPlanCalendar;

@Service
public class SchedulerService {

	public LocalDateTime getScheduledStartForActivity(IPlanCalendar calendar,
			Activity activity) {
		for (int dayNum = 1; dayNum <= 7 - calendar.getOptions()
				.getFreeDayCount(); ++dayNum) {
			DayOfWeek day = DayOfWeek.of(dayNum);
			LocalDate dayDate = LocalDate.now().with(ChronoField.DAY_OF_WEEK,
					dayNum);
			System.out.println("Looking at day 1 (" + day + ") of "
					+ (7 - calendar.getOptions().getFreeDayCount()));
			List<CalendarItem> items = getSortedItemsForDay(calendar, day);

			System.out.println("We have " + items.size() + " items.");

			// check for workload
			long newWorkloadMinutes = items.stream()
					.mapToLong(item -> item.getDuration().toMinutes()).sum() + activity.getDuration().toMinutes();
			
			long maxWorkloadMinutes = calendar.getOptions().getWorkload() * 60;
			
			System.out.println("Combined Workload for "+day+" would be "+newWorkloadMinutes/60+"h");
			if (calendar.getOptions().getWorkload() > -1 && newWorkloadMinutes > maxWorkloadMinutes){
				System.out.println("Aborted due to combined workload being "+newWorkloadMinutes+" max("+maxWorkloadMinutes+")");
				return null;
			}
				

			Duration activityDuration = activity.getDuration();

			if (items.isEmpty()) {
				System.out.println("Empty, adding monday at 9:00");
				return LocalDateTime.of(dayDate, LocalTime.of(9, 00)); // monday
																		// 9 o
																		// clock
			}

			System.out.print("1 Event, ");
			LocalTime startTime;
			if (LocalTime.of(7, 00).plus(activityDuration)
					.isBefore(getStartTimeForItem(items.get(0)))) {
				startTime = getStartTimeForItem(items.get(0)).minus(
						activityDuration).minusMinutes(1);
				System.out.println("Adding before, at " + startTime);

				return LocalDateTime.of(dayDate, startTime);
			}

			if (items.size() == 1) { // didnt fit before first so we just put it
										// after, if we just have one.
				startTime = getEndTimeForItem(items.get(0)).plusMinutes(1);
				System.out.println("Adding after, at " + startTime);
			}

			// between activities
			for (int i = 0; i < items.size() - 1; ++i) {

				LocalTime firstItemEnd = getEndTimeForItem(items.get(i));
				LocalTime secondItemStart = getStartTimeForItem(items
						.get(i + 1));

				System.out.println("Checking between " + firstItemEnd + " and "
						+ secondItemStart);
				Duration durationBetween = Duration.between(firstItemEnd,
						secondItemStart);

				if (durationBetween.compareTo(activityDuration) >= 0) { // same
																		// or
																		// greater
					return LocalDateTime.of(dayDate, firstItemEnd);
				}
			}
			
			CalendarItem lastItem = items.get(items.size() - 1);
			LocalTime lastItemEnd = getEndTimeForItem(lastItem);
			LocalDateTime lastItemEndDateTime = lastItem.getStartTime().withHour(lastItemEnd.getHour()).withMinute(lastItemEnd.getMinute());
			LocalDateTime latestAcceptableEnd = lastItemEndDateTime.withHour(22).withMinute(00);
			
			// after last activity
			if (lastItemEndDateTime.plus(activityDuration).isBefore(
					latestAcceptableEnd)) {
				return LocalDateTime.of(dayDate,lastItemEnd);
			} else {
				System.out.println("End After midnight, looking at next day!");
			}

			// couldn't schedule in this day, try next
		}
		return null; // cant be scheduled
	}

	private List<CalendarItem> getSortedItemsForDay(IPlanCalendar calendar,
			DayOfWeek dofWeek) {
		Stream<CalendarItem> items = getAllItemsFromCalendar(calendar);

		return items
				.filter(item -> item.getStartTime().getDayOfWeek()
						.equals(dofWeek))
				.sorted((i1, i2) -> getStartTimeForItem(i1).compareTo(
						getStartTimeForItem(i2))).collect(Collectors.toList());
	}

	private Stream<CalendarItem> getAllItemsFromCalendar(IPlanCalendar calendar) {
		return Stream.concat(calendar.getActivities().stream(), calendar
				.getCourses().stream());
	}

	private LocalTime getStartTimeForItem(CalendarItem item) {
		if (item instanceof Course) {
			Course course = (Course) item;
			return LocalTime.from(course.getStartTime().minus(
					course.getPreparationTime()));
		} else {
			return LocalTime.from(item.getStartTime());
		}
	}

	private LocalTime getEndTimeForItem(CalendarItem item) {
		if (item instanceof Course) {
			Course course = (Course) item;
			return LocalTime.from(course.getEndTime().plus(
					course.getReworkTime()));
		} else {
			return LocalTime.from(item.getEndTime());
		}
	}

	public boolean isNonOverlapping(IPlanCalendar calendar, CalendarItem item) {
		LocalTime scheduledStart = getStartTimeForItem(item);
		LocalTime scheduledEnd = getEndTimeForItem(item);

		List<CalendarItem> items = getAllItemsFromCalendar(calendar).collect(
				Collectors.toList());

		for (CalendarItem alreadyScheduledItem : items) {
			LocalTime alreadyScheduledStart = getStartTimeForItem(alreadyScheduledItem);
			LocalTime alreadyScheduledEnd = getEndTimeForItem(alreadyScheduledItem);

			if (sameDay(alreadyScheduledItem, item)
					&& ((alreadyScheduledStart.isBefore(scheduledStart) && alreadyScheduledEnd
							.isAfter(scheduledEnd))
							|| between(scheduledStart, scheduledEnd,
									alreadyScheduledStart) || between(
								scheduledStart, scheduledEnd,
								alreadyScheduledEnd))) {
				System.out.println("Overlap with "
						+ ReflectionToStringBuilder.toString(
								alreadyScheduledItem,
								ToStringStyle.MULTI_LINE_STYLE));
				return false;
			}
		}

		return true;
	}

	private boolean sameDay(CalendarItem alreadyScheduledItem, CalendarItem item) {
		return item.getStartTime().getDayOfYear() == alreadyScheduledItem
				.getStartTime().getDayOfYear();
	}

	private boolean between(LocalTime start, LocalTime end, LocalTime test) {
		return test.isAfter(start) && test.isBefore(end);
	}

}
