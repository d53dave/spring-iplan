package at.iplan.model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Course extends CalendarItem{
	private Duration reworkTime = Duration.ZERO;
	private Duration preparationTime = Duration.ZERO;

	public Duration getReworkTime() {
		return reworkTime;
	}

	public void setReworkTime(Duration reworkTime) {
		this.reworkTime = reworkTime;
	}
	
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
	
}
