package com.fileServer.core.controller.component;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;

public class GeneralSelectionType implements Comparable<GeneralSelectionType>, Serializable {
    
	private static final long serialVersionUID = -3187311786150479950L;
	private String key;
	private String value;
    private Boolean selected;

	public GeneralSelectionType() {
	}

    public GeneralSelectionType(String key, String value, Boolean selected) {
        this.key = key;
        this.value = value;
        this.selected = selected;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

	@Override
	public int compareTo(GeneralSelectionType that) {
		if(StringUtils.isNotBlank(value)){
			return value.compareTo(that.value);
		}else{
			return -1;
		}
	}

    @Override
    public String toString(){
        return new ToStringBuilder(this).append("key", key).append("value", value).append("selected", selected).toString();
    }
}
