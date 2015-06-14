package at.iplan.test;

import java.time.Duration;
import java.time.LocalDateTime;

import org.junit.Assert;
import org.junit.Test;

import at.iplan.model.Course;
import at.iplan.model.IPlanCalendar;
import at.iplan.service.SchedulerService;

public class SchedulerServiceTest {

	private SchedulerService schedulerService = new SchedulerService();

	@Test
	public void overlappingTest() {

		IPlanCalendar cal = new IPlanCalendar();

		Course course = new Course();
		course.setStartTime(LocalDateTime.now());
		course.setDuration(Duration.ofMinutes(60));

		Assert.assertTrue(schedulerService.isNonOverlapping(cal, course));

		cal.getCourses().add(course);

		Course newCourse = new Course();
		newCourse.setStartTime(LocalDateTime.now().plusMinutes(40));
		newCourse.setDuration(Duration.ofMinutes(60));

		Assert.assertFalse(schedulerService.isNonOverlapping(cal, newCourse));

		newCourse.setStartTime(LocalDateTime.now().plusMinutes(40));
		newCourse.setDuration(Duration.ofMinutes(10));

		Assert.assertFalse(schedulerService.isNonOverlapping(cal, newCourse));

		newCourse.setStartTime(LocalDateTime.now().minusMinutes(40));
		newCourse.setDuration(Duration.ofMinutes(50));

		Assert.assertFalse(schedulerService.isNonOverlapping(cal, newCourse));

		newCourse.setStartTime(LocalDateTime.now().minusMinutes(40));
		newCourse.setDuration(Duration.ofMinutes(120));

		Assert.assertFalse(schedulerService.isNonOverlapping(cal, newCourse));

		newCourse.setStartTime(LocalDateTime.now().minusMinutes(40));
		newCourse.setDuration(Duration.ofMinutes(20));

		Assert.assertTrue(schedulerService.isNonOverlapping(cal, newCourse));

	}

	@Test
	public void differentDaysTest() {
		IPlanCalendar cal = new IPlanCalendar();

		Course course = new Course();
		course.setStartTime(LocalDateTime.now());
		course.setDuration(Duration.ofMinutes(60));

		Assert.assertTrue(schedulerService.isNonOverlapping(cal, course));

		cal.getCourses().add(course);

		Course course2 = new Course();
		course2.setStartTime(LocalDateTime.now().plusDays(1));
		course2.setDuration(Duration.ofMinutes(60));

		Assert.assertTrue(schedulerService.isNonOverlapping(cal, course2));
	}
}
