package com.fileServer.core.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.image.ImageParser;
import org.apache.tika.sax.BodyContentHandler;
import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.helpers.DefaultHandler;

import com.fileServer.core.configuration.ApplicationConfiguration;
import com.fileServer.core.controller.component.FileUploadResponse;
import com.fileServer.core.hibernate.dao.UploadDao;
import com.fileServer.core.hibernate.dao.UploadDaoImpl;
import com.fileServer.core.hibernate.pojo.FileDetail;
import com.fileServer.core.hibernate.pojo.FileSubLocation;
import com.fileServer.core.util.ImageThumbnailSize;
import com.fileServer.core.util.ImgIoFormatNames;
import com.fileServer.core.util.MathOperator;

@Service
public class UploadServiceImpl implements UploadService{
	
    private static final Logger logger = LoggerFactory.getLogger(UploadServiceImpl.class);

	@Autowired
    protected ApplicationConfiguration applicationConfig;

	@Autowired
	UploadDao uploadDao;
	
	@Override
	public BufferedImage createThumbnail(BufferedImage img, int size, int originalWidth, int originalHeight) {
		if(originalWidth>originalHeight){
			if(size<originalWidth){
				return Scalr.resize(img, Scalr.Method.SPEED, Scalr.Mode.FIT_TO_WIDTH, size, size, Scalr.OP_ANTIALIAS);
			}
		}else{
			if(size<originalHeight){
				return Scalr.resize(img, Scalr.Method.SPEED, Scalr.Mode.FIT_TO_HEIGHT, size, size, Scalr.OP_ANTIALIAS);
			}
		}
		
		return img;
	}
	
	public void createThumbnails(final BufferedImage img, int[] sizes, final String thumbnailFolder, final int originalWidth, final int originalHeight){
		for(final int s : sizes){
			new Thread(new Runnable() {
				public void run() {
					writeImgToDisk(createThumbnail(img, s, originalWidth, originalHeight), ImgIoFormatNames.png, new StringBuilder(thumbnailFolder).append("/img_").append(s).toString());
				}
			}, new StringBuilder("writeImgToDisk_").append(s).toString()).start();				
		}
	}

	@Override
	@Transactional(readOnly=true)
	public List<String> findAllAvailableSubfolders() {
		return uploadDao.findAllAvailableSubfolders();
	}
	

	@Override
	@Transactional(readOnly=true)
	public String findAvailableSubfolder() {
		return uploadDao.findAvailableSubfolder();
	}

	@Override
	@Transactional
	public Long saveFileDetail(FileDetail fileDetail) {
		if(fileDetail!=null && StringUtils.isNotBlank(fileDetail.getContenttype()) && StringUtils.isNotBlank(fileDetail.getLocation())){
			return uploadDao.saveFileDetail(fileDetail);
		}
		return null;
	}

	@Override
	@Transactional
	public void updateSizeForSublocation(MathOperator op, int number, String subfolder) {
		if(op!=null && StringUtils.isNotBlank(subfolder)){
			uploadDao.updateSizeForSublocation(op, number, subfolder);
		}
	}

	@Override
	@Transactional
	public FileUploadResponse writeToDisk(String filename, MultipartFile multipartFile, String fileStorageFolder) {
		
		FileUploadResponse res = null;
		
	    try {

			// get all file information:
			String fileOriginalName = multipartFile.getOriginalFilename();
			long fileSize = multipartFile.getSize();
			String fileContentTypeFromHttp = multipartFile.getContentType();
			
			Tika tika = new Tika();
			MediaType mediaType = MediaType.parse(tika.detect(multipartFile.getBytes()));
			String fileContentTypeFromTika = mediaType.toString();
			
			String fileContentType = null;
			// 1. fileContentTypeFromTika can't detect text, css, javascript, all treat as text
			// 2. avoid type mismatch: change the extension for the file, for example, change jpg extension to gif.
			if(StringUtils.isBlank(fileContentTypeFromTika)){
				fileContentType = fileContentTypeFromHttp;
			}else if(fileContentTypeFromTika.equals("application/octet-stream")){ // Tika will fall back to application/octet-stream if it doesn't know what a file is.
				fileContentType = fileContentTypeFromHttp;
			}else if(fileContentTypeFromTika.indexOf("text")<0){ 
				fileContentType = fileContentTypeFromTika;
			}else{
				fileContentType = fileContentTypeFromHttp;
			}
			
			
			// for metadata
			// from http://svn.apache.org/repos/asf/tika/trunk/tika-parsers/src/test/java/org/apache/tika/parser/image/ImageParserTest.java
			
	        StringBuilder extraInfo = new StringBuilder();
	        
	        Metadata metadata = new Metadata();
	        if(fileContentType.indexOf("image")>-1){
				Parser parser = new ImageParser();
		        metadata.set(Metadata.CONTENT_TYPE, fileContentType);
		        
		        try {
			        parser.parse(multipartFile.getInputStream(), new DefaultHandler(), metadata, new ParseContext());			
		        	if(metadata!=null){
		        		extraInfo.append("width:").append(metadata.get("width")).append(", height:").append(metadata.get("height"));
		        	}
				} catch (Exception e) {
					res = new FileUploadResponse();
					res.setOriginalName(filename);
					res.setExtraInfo(e.toString());
					return res;
				}
		        
	        }else{
				AutoDetectParser autoparser = new AutoDetectParser();
				autoparser.parse(multipartFile.getInputStream(), new BodyContentHandler(-1), metadata);
	        }
			
			// save file
			// create a folder if necessary:
			File folder = new File(fileStorageFolder.toString());
			boolean folderExist = false;
			if(!folder.exists()){
				if(folder.mkdirs()){
					folderExist = true;
				}else{
					logger.error(new StringBuilder("Folder \"").append(fileStorageFolder).append("\" creation is failed!").toString());
				}
				
			}else{
				folderExist = true;
			}
			
			if(folderExist){
				// create a uniqe uuid name for the file
				String fileUniqeName = UUID.randomUUID().toString();
				// rename the uploaded file to this uniqe name
				StringBuilder fileStorageUrl = new StringBuilder();
				fileStorageUrl.append(fileStorageFolder).append(fileUniqeName);

				// write file
				FileOutputStream fos = new FileOutputStream(fileStorageUrl.toString());
				fos.write(multipartFile.getBytes());
				fos.close();
				
				// further process based on different contenttype
				if(fileContentType.indexOf("image")>-1){
					// create _thumbnail folder
					File thumbnailFolder = new File(new StringBuilder(fileStorageUrl).append("_thumbnail").toString());
					if(!thumbnailFolder.exists()){
						if(thumbnailFolder.mkdir()){
							// resize image to diffent sizes :
							BufferedImage originalImg = ImageIO.read(multipartFile.getInputStream());
							int[] sizes = {ImageThumbnailSize.Fifty.getSize(), ImageThumbnailSize.Hundred.getSize(), ImageThumbnailSize.TwoHundred.getSize(), ImageThumbnailSize.SixHundred.getSize()};
							createThumbnails(
								originalImg, 
								sizes, 
								thumbnailFolder.toString(), 
								metadata!=null?(NumberUtils.isNumber(metadata.get("width"))?Integer.parseInt(metadata.get("width")):-1):-1,
								metadata!=null?(NumberUtils.isNumber(metadata.get("height"))?Integer.parseInt(metadata.get("height")):-1):-1
							);
							
						}else{
							logger.error(new StringBuilder("thumbnail folder \"").append(thumbnailFolder).append("\" creation is failed!").toString());
						}
					}
					
				}
				
				// create response
				res = new FileUploadResponse();
				res.setContentType(fileContentType);
				res.setGeneratedName(fileUniqeName);
				res.setOriginalName(fileOriginalName);
				res.setExtraInfo("Upload succeeded!");
				
				// save all file information to fileDetail table
				FileDetail fileDetail = new FileDetail(null, fileOriginalName, fileUniqeName, null, fileContentType, fileSize, fileStorageUrl.toString(), new Date(), extraInfo.toString());
				Long fileId = saveFileDetail(fileDetail);
				// update filesublocation table for size 
				StringBuilder subfolder = new StringBuilder();
				subfolder.append(fileStorageFolder.substring(fileStorageFolder.lastIndexOf("/", fileStorageFolder.length()-2)+1, fileStorageFolder.length()-1));
				updateSizeForSublocation(MathOperator.ADD, 1, subfolder.toString());
				
			}
				
				
			
	    } catch (Exception ex) {
	        ex.printStackTrace();
	    }
	    
	    return res;
	}
	
	@Override
	public void writeImgToDisk(BufferedImage thumbnail, ImgIoFormatNames imgType, String location){
		try {
			ImageIO.write(thumbnail, imgType.name(), new File(location));
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(new StringBuffer("image writing \"").append(location).append("\" is failed").toString());
		}
	}

	@Override
	@Transactional
	public Long saveSublocation(String location, int maxSpot) {
		if(StringUtils.isNotBlank(location)){
			FileSubLocation loc = new FileSubLocation(null, location, 0, maxSpot);
			return uploadDao.saveFileSubLocation(loc);
		}
		return null;
	}

	@Override
	@Transactional(readOnly=true)
	public FileDetail getFileDetailBySystemName(String fileSystemName) {
		if(StringUtils.isNotBlank(fileSystemName)){
			return uploadDao.getFileDetailBySystemName(fileSystemName);
		}
		return null;
	}

	

}
