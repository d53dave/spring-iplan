package at.iplan.model;

import java.time.Duration;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import at.iplan.config.DurationDeserializer;
import at.iplan.config.DurationSerializer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class Statistics {
	@JsonDeserialize(using = DurationDeserializer.class)
	private static final Duration ONE_WEEK = Duration.ofDays(7);
	@JsonSerialize(using = DurationSerializer.class)
	@JsonDeserialize(using = DurationDeserializer.class)
	private Duration total = Duration.ZERO;
	@JsonSerialize(using = DurationSerializer.class)
	@JsonDeserialize(using = DurationDeserializer.class)
	private Duration courseWork = Duration.ZERO;
	@JsonSerialize(using = DurationSerializer.class)
	@JsonDeserialize(using = DurationDeserializer.class)
	private Duration freeTime = Duration.ZERO;
	@JsonSerialize(using = DurationSerializer.class)
	@JsonDeserialize(using = DurationDeserializer.class)
	private Duration preparation = Duration.ZERO;
	@JsonSerialize(using = DurationSerializer.class)
	@JsonDeserialize(using = DurationDeserializer.class)
	private Duration rework = Duration.ZERO;
	@JsonSerialize(using = DurationSerializer.class)
	@JsonDeserialize(using = DurationDeserializer.class)
	private Duration rest = Duration.ZERO;
	
	public Statistics(IPlanCalendar cal){
		for(Activity activity:cal.getActivities()){
			Duration d = activity.getDuration();
			total = total.plus(d);
			freeTime = freeTime.plus(d);
		}
		
		for(Course course: cal.getCourses()){
			Duration d = course.getDuration();
			courseWork = courseWork.plus(d);
			preparation = preparation.plus(course.getPreparationTime());
			rework = rework.plus(course.getReworkTime());
			total = total.plus(courseWork).plus(preparation).plus(rework);
		}
		
		rest = ONE_WEEK.minus(total);
		
		System.out.println("Updated statistics\n"+ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE));
	}

	public Duration getTotal() {
		return total;
	}

	public Duration getCourseWork() {
		return courseWork;
	}

	public Duration getFreeTime() {
		return freeTime;
	}

	public Duration getPreparation() {
		return preparation;
	}

	public Duration getRework() {
		return rework;
	}

	public Duration getRest() {
		return rest;
	}
	

	@Override
	public boolean equals(Object o){
		System.out.println("Options "+this+" equals called");
		return EqualsBuilder.reflectionEquals(this, o, false);
	}
	
	@Override
	public int hashCode(){
		return HashCodeBuilder.reflectionHashCode(this, false);
	}
}
