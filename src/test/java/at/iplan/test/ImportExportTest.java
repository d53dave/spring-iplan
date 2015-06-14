package at.iplan.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.List;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;

import com.google.common.collect.Sets;

import biweekly.Biweekly;
import biweekly.ICalendar;
import at.iplan.model.Activity;
import at.iplan.model.CalendarItem;
import at.iplan.model.Course;
import at.iplan.model.IPlanCalendar;
import at.iplan.model.Options;
import at.iplan.service.CalendarService;

public class ImportExportTest {

	private CalendarService calendarService = new CalendarService();

	@Test
	public void roundTripTest() throws IOException {
		IPlanCalendar cal = new IPlanCalendar();

		cal.setId(42l);
		calendarService.getCalendars().put(cal.getId(), cal);
		
		
		cal.setOptions(new Options());
		cal.getOptions().setWorkload(42);
		cal.getOptions().setFreeDayCount(2);
	
		Activity act = new Activity();
		act.setDuration(Duration.ofMinutes(45));
		act.setName("LOLNAME");
		act.setStartTime(LocalDateTime.now().withHour(15));
		act.setText("TEXTETETTEET");

		cal.addActivity(act);

		Activity act2 = new Activity();
		act2.setDuration(Duration.ofMinutes(45));
		act2.setName("LOLNAME");
		act2.setStartTime(LocalDateTime.now().withHour(15).with(ChronoField.DAY_OF_WEEK, 2));
		act2.setText("TEXTETETTEET");

		cal.addActivity(act2);

		Course c = new Course();
		c.setDuration(Duration.ofMinutes(45));
		c.setName("LOLNAMEcourse");
		c.setStartTime(LocalDateTime.now().withHour(14));
		c.setText("TEXTETETTEET");
		c.setReworkTime(Duration.ofHours(1));
		c.setPreparationTime(Duration.ofHours(2));

		cal.addCourse(c);

		Course c2 = new Course();
		c2.setDuration(Duration.ofMinutes(45));
		c2.setName("LOLNAMEcourse2");
		c2.setStartTime(LocalDateTime.now().withHour(15).with(ChronoField.DAY_OF_WEEK, 2));
		c2.setText("TEXTETETTEET");

		cal.addCourse(c2);

		File tempFile = File.createTempFile("iplan", "testFile");
		FileOutputStream out = new FileOutputStream(tempFile);

		ICalendar downloadable = calendarService.getDownloadableICalFile(cal
				.getId());
		Biweekly.write(downloadable).go(out);
		
		calendarService.getCalendars().clear(); //simulate server restart

		FileInputStream inStream = new FileInputStream(tempFile);
		String contents = getContentsFromStream(inStream);
		IPlanCalendar parsedCal = calendarService.parseFromICalFile(contents, cal.getId());

		Assert.assertEquals(cal.getOptions(), parsedCal.getOptions());
		Assert.assertEquals(cal.getStatistics(), parsedCal.getStatistics());
		for(int i = 0; i<cal.getCourses().size(); ++i){
			Assert.assertTrue(courseEquals(cal.getCourses().get(i), parsedCal.getCourses().get(i)));
		}
		for(int i = 0; i<cal.getActivities().size(); ++i){
			Assert.assertTrue(activityEquals(cal.getActivities().get(i), parsedCal.getActivities().get(i)));
		}
		Assert.assertThat(cal, new ReflectionEquals(parsedCal, "courses", "activities", "statistics", "options"));

	}
	
	private boolean activityEquals(Activity c1, Activity c2) {
		c1.setStartTime(LocalDateTime.from(c1.getStartTime().with(ChronoField.MILLI_OF_SECOND, 0).with(ChronoField.MICRO_OF_SECOND, 0).withNano(0)));
		c2.setStartTime(LocalDateTime.from(c2.getStartTime().with(ChronoField.MILLI_OF_SECOND, 0).with(ChronoField.MICRO_OF_SECOND, 0).withNano(0)));
		
		return c1.getDuration().equals(c2.getDuration()) && c1.getId().equals(c2.getId()) && c1.getName().equals(c2.getName())
				&& c1.getStartTime().equals(c2.getStartTime());
	}

	private boolean courseEquals(Course c1, Course c2){
		
		c1.setStartTime(LocalDateTime.from(c1.getStartTime().with(ChronoField.MILLI_OF_SECOND, 0).with(ChronoField.MICRO_OF_SECOND, 0).withNano(0)));
		c2.setStartTime(LocalDateTime.from(c2.getStartTime().with(ChronoField.MILLI_OF_SECOND, 0).with(ChronoField.MICRO_OF_SECOND, 0).withNano(0)));
		
		return c1.getDuration().equals(c2.getDuration()) && c1.getId().equals(c2.getId()) && c1.getName().equals(c2.getName())
				&& c1.getPreparationTime().equals(c2.getPreparationTime()) && c1.getPriority().equals(c2.getPriority())
				&& c1.getStartTime().equals(c2.getStartTime()) && c1.getReworkTime().equals(c2.getReworkTime());
	}
	

	private String getContentsFromStream(FileInputStream fis)
			throws IOException {
		StringBuilder builder = new StringBuilder();
		int ch;
		while ((ch = fis.read()) != -1) {
			builder.append((char) ch);
		}
		return builder.toString();
	}

}
