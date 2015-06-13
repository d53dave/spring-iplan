package at.iplan.model;

public class Activity extends CalendarItem {
 private boolean optional;

public boolean isOptional() {
	return optional;
}

public void setOptional(boolean optional) {
	this.optional = optional;
}
}
