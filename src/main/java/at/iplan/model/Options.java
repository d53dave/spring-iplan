package at.iplan.model;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.el.util.ReflectionUtil;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Options{
	
	private static final String sep = "!!!";
	
	private long freeDayCount;
	private long workload = -1;

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
	
	public static Options parseFromString(String s){
		String[] tokens = s.split(sep);
		
		if(tokens.length != 2){
			return null;
		}
		
		Options opt = new Options();
		opt.setFreeDayCount(Long.valueOf(tokens[0]));
		opt.setWorkload(Long.valueOf(tokens[1]));
		
		return opt;
	}
	
	@JsonIgnore
	public String getSerializedString(){
		return freeDayCount+sep+workload;
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
