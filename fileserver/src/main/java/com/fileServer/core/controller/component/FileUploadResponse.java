package com.fileServer.core.controller.component;

import java.io.Serializable;
import java.util.*;

import org.apache.commons.lang.builder.ToStringBuilder;

public class FileUploadResponse implements Serializable{
	
	private static final long serialVersionUID = -6249875596983856526L;

	private String originalName;
	private String generatedName;
	private String contentType;
	private String extraInfo;
	
	// size for image : 100X100, ...
	private String size;
	
	public FileUploadResponse() {
		super();
	}
	public FileUploadResponse(String originalName, String generatedName, String contentType, String location, String extraInfo) {
		super();
		this.originalName = originalName;
		this.generatedName = generatedName;
		this.contentType = contentType;
		this.extraInfo = extraInfo;
	}

	public String getOriginalName() {
		return originalName;
	}
	public void setOriginalName(String originalName) {
		this.originalName = originalName;
	}
	public String getGeneratedName() {
		return generatedName;
	}
	public void setGeneratedName(String generatedName) {
		this.generatedName = generatedName;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public String getExtraInfo() {
		return extraInfo;
	}
	public void setExtraInfo(String extraInfo) {
		this.extraInfo = extraInfo;
	}
	
	@Override
    public String toString(){
        return new ToStringBuilder(this).append("original name", originalName).append("generated name", generatedName).append("contentType", contentType).toString();
    }

}
