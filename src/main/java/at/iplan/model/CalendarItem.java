package at.iplan.model;

import java.time.Duration;
import java.time.LocalDateTime;


public class CalendarItem {
	
	private LocalDateTime startTime;
	private Duration duration;
	private long id;
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
	public LocalDateTime getEndTime(){
		return startTime.plusSeconds(duration.getSeconds());
	}
	public void setDuration(Duration duration) {
		this.duration = duration;
	}
	public long getId() {
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
