package at.iplan.model;

import java.beans.Transient;
import java.time.Duration;
import java.time.LocalDateTime;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import at.iplan.config.DurationDeserializer;
import at.iplan.config.DurationSerializer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class Course extends CalendarItem{
	@JsonSerialize(using = DurationSerializer.class)
	@JsonDeserialize(using = DurationDeserializer.class)
	private Duration reworkTime = Duration.ZERO;
	
	@JsonSerialize(using = DurationSerializer.class)
	@JsonDeserialize(using = DurationDeserializer.class)
	private Duration preparationTime = Duration.ZERO;
	
	private Long priority = 0l;

	public Duration getReworkTime() {
		return reworkTime;
	}

	public void setReworkTime(Duration reworkTime) {
		this.reworkTime = reworkTime;
	}

	public Duration getPreparationTime() {
		return preparationTime;
	}

	public void setPreparationTime(Duration preparationTime) {
		this.preparationTime = preparationTime;
	}

	public Long getPriority() {
		return priority;
	}

	public void setPriority(Long priority) {
		this.priority = priority;
	}

	@JsonIgnore
	@Transient
	public LocalDateTime getStartTimeWithPreparation() {
		return getStartTime().minus(reworkTime);
	}

	@JsonIgnore
	@Transient
	public LocalDateTime getEndTimeWithRework() {
		return getStartTime().plus(getDuration()).plus(reworkTime);
	}
	
	@Override
	public String toString(){
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
	@Override
	public boolean equals(Object o){
		System.out.println("Course "+this+" equals called");
		return EqualsBuilder.reflectionEquals(this, o, "priority");
	}
	
	@Override
	public int hashCode(){
		return HashCodeBuilder.reflectionHashCode(this, false);
	}
	
}
