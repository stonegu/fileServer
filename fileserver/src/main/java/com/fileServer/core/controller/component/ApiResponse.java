package com.fileServer.core.controller.component;

import org.apache.commons.lang.builder.ToStringBuilder;

public class ApiResponse implements java.io.Serializable {

	private static final long serialVersionUID = -8044567896893645758L;
	
	private boolean success;
	private Object response1;
	private Object response2;
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public Object getResponse1() {
		return response1;
	}
	public void setResponse1(Object response1) {
		this.response1 = response1;
	}
	public Object getResponse2() {
		return response2;
	}
	public void setResponse2(Object response2) {
		this.response2 = response2;
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this).append("success", success).toString();	}
}
