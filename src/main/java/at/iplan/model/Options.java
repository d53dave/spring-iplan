package at.iplan.model;

public class Options {

	private long freeDayCount;
	private long workload = Long.MAX_VALUE;

	public long getFreeDayCount() {
		return freeDayCount;
	}

	public void setFreeDayCount(long freeDayCount) {
		this.freeDayCount = freeDayCount;
	}

	public long getWorkload() {
		return workload;
	}

	public void setWorkload(long workload) {
		this.workload = workload;
	}
	
}
