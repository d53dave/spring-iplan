package at.iplan.model;

import java.beans.Transient;
import java.time.Duration;
import java.time.LocalDateTime;

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
	
	private Long priority;

	public Duration getReworkTime() {
		return reworkTime;
	}

	public void setReworkTime(Duration reworkTime) {
		this.reworkTime = reworkTime;
	}
	
	@JsonIgnore
	@Transient
	@Override
	public LocalDateTime getEndTime(){
		return getStartTime().plus(getDuration().plus(reworkTime));
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
	
}
