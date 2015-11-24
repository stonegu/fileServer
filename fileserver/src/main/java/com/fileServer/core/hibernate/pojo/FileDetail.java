package com.fileServer.core.hibernate.pojo;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;

@Entity
@Table(name="filedetail")
public class FileDetail implements Serializable {
	
	private static final long serialVersionUID = -3760152499738550186L;

	@Id
	@Column(name="id")
	@GeneratedValue
	private Long id;

	@Column(name="originalname")
	private String originalname;
	
	@Column(name="systemname")
	private String systemname;
	
	@Column(name="parentsysname")
	private String parentsysname;
	
	@Column(name="contenttype")
	private String contenttype;
	
	@Column(name="size")
	private Long size;

	@Column(name="location")
	private String location;
	
	@Column(name="createdate")
	private Date createdate;
	
	@Column(name="extrainfo")
	private String extrainfo;
    
	public FileDetail() {
		super();
	}

	public FileDetail(Long id, String originalname, String systemname, String parentsysname, String contenttype, Long size, String location, Date createdate, String extrainfo) {
		super();
		this.id = id;
		this.originalname = originalname;
		this.systemname = systemname;
		this.parentsysname = parentsysname;
		this.contenttype = contenttype;
		this.size = size;
		this.location = location;
		this.createdate = createdate;
		this.extrainfo = extrainfo;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOriginalname() {
		return originalname;
	}

	public void setOriginalname(String originalname) {
		this.originalname = originalname;
	}

	public String getSystemname() {
		return systemname;
	}

	public void setSystemname(String systemname) {
		this.systemname = systemname;
	}

	public String getParentsysname() {
		return parentsysname;
	}

	public void setParentsysname(String parentsysname) {
		this.parentsysname = parentsysname;
	}

	public String getContenttype() {
		return contenttype;
	}

	public void setContenttype(String contenttype) {
		this.contenttype = contenttype;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Date getCreatedate() {
		return createdate;
	}

	public void setCreatedate(Date createdate) {
		this.createdate = createdate;
	}

	public String getExtrainfo() {
		return extrainfo;
	}

	public void setExtrainfo(String extrainfo) {
		this.extrainfo = extrainfo;
	}

	@Override
    public String toString(){
        return new ToStringBuilder(this).append("id", id).append("originalname", originalname).append("parentsysname", parentsysname).append("location", location).toString();
    }

}
