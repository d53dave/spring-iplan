package at.iplan.model;

import java.beans.Transient;
import java.time.Duration;
import java.time.LocalDateTime;

import at.iplan.config.DurationDeserializer;
import at.iplan.config.DurationSerializer;
import at.iplan.config.LocalDateTimeDeserializer;
import at.iplan.config.LocalDateTimeSerializer;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;


public class CalendarItem {
	
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	private LocalDateTime startTime;
	
	@JsonSerialize(using = DurationSerializer.class)
	@JsonDeserialize(using = DurationDeserializer.class)
	private Duration duration;
	
	private Long id;
	private String name;
	private String text;
	
	public LocalDateTime getStartTime() {
		return startTime;
	}
	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}
	public Duration getDuration() {
		return duration;
	}
	
	@JsonIgnore
	@Transient
	public LocalDateTime getEndTime(){
		return startTime.plus(duration);
	}

	
	public void setDuration(Duration duration) {
		this.duration = duration;
	}
	public Long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
}
