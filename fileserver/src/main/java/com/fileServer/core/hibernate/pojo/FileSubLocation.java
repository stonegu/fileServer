package com.fileServer.core.hibernate.pojo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;

@Entity
@Table(name="filesublocation")
public class FileSubLocation implements Serializable {
	
	private static final long serialVersionUID = 9061956650301008456L;

	@Id
	@Column(name="id")
	@GeneratedValue
	private Long id;

	@Column(name="subfolder")
	private String subfolder;
	
	@Column(name="size")
	private Integer size;

	@Column(name="maxsize")
	private Integer maxsize;

	public FileSubLocation() {
		super();
	}

	public FileSubLocation(Long id, String subfolder, Integer size,
			Integer maxsize) {
		super();
		this.id = id;
		this.subfolder = subfolder;
		this.size = size;
		this.maxsize = maxsize;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSubfolder() {
		return subfolder;
	}

	public void setSubfolder(String subfolder) {
		this.subfolder = subfolder;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public Integer getMaxsize() {
		return maxsize;
	}

	public void setMaxsize(Integer maxsize) {
		this.maxsize = maxsize;
	}

	@Override
    public String toString(){
        return new ToStringBuilder(this).append("id", id).append("subfolder", subfolder).append("size", size).append("maxsize", maxsize).toString();
    }
}
