package com.fileServer.core.service;

import java.awt.image.BufferedImage;
import java.util.*;

import org.springframework.web.multipart.MultipartFile;

import com.fileServer.core.controller.component.FileUploadResponse;
import com.fileServer.core.hibernate.pojo.FileDetail;
import com.fileServer.core.hibernate.pojo.FileSubLocation;
import com.fileServer.core.util.ImgIoFormatNames;
import com.fileServer.core.util.MathOperator;

public interface UploadService {

	public BufferedImage createThumbnail(BufferedImage img, int size, int originalWidth, int originalHeight);
	
	public List<String> findAllAvailableSubfolders();
	
	public String findAvailableSubfolder();
	
	public FileDetail getFileDetailBySystemName(String fileSystemName);
	
	public Long saveFileDetail(FileDetail fileDetail);
	
	public Long saveSublocation(String location, int maxSpot);
	
	public void updateSizeForSublocation(MathOperator op, int number, String subfolder);
	
	/**
	 * @param filename
	 * @param multipartFile
	 * @return
	 */
	public FileUploadResponse writeToDisk(String filename, MultipartFile multipartFile, String storageFolder);
	
	/**
	 * @param thumbnail
	 * @param imgType "png", "gif", "jpg", ... in ImgIoFormatNames.java
	 * @param location
	 */
	public void writeImgToDisk(BufferedImage thumbnail, ImgIoFormatNames imgType, String location);
	
}
