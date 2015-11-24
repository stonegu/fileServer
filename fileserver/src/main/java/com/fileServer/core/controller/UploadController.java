package com.fileServer.core.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tika.Tika;
import org.apache.tika.mime.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.fileServer.core.configuration.ApplicationConfiguration;
import com.fileServer.core.controller.component.ApiResponse;
import com.fileServer.core.controller.component.FileUploadResponse;
import com.fileServer.core.hibernate.pojo.FileDetail;
import com.fileServer.core.service.UploadService;
import com.fileServer.core.test.thread.callables.MyCallable;
import com.fileServer.core.util.GeneralUtils;
import com.fileServer.core.util.ImageThumbnailSize;
import com.fileServer.core.util.ImgIoFormatNames;
import com.fileServer.core.util.MathOperator;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.parser.PdfTextExtractor;


@Controller
public class UploadController {
	private static final Logger logger = LoggerFactory.getLogger(UploadController.class);
	
	@Autowired
    protected ApplicationConfiguration applicationConfig;
	
	@Autowired
	private UploadService uploadService;
	
	@RequestMapping(value = "/hello", method = RequestMethod.GET)
	public String hello(
			ModelMap model) {
		
		
		return "index";
	}
	
	@RequestMapping(value = "/storefilesInSingleThread", method = RequestMethod.POST)
	public @ResponseBody ApiResponse storeFilesInSingleThread(HttpServletRequest request, HttpServletResponse response) {
		ApiResponse res = null;
		
	    try
	    {
	    	// generate storage folder
			StringBuilder fileStorageFolder = new StringBuilder(applicationConfig.getFileStorageBaseUrl());
			if(fileStorageFolder.lastIndexOf("/")<(fileStorageFolder.length()-1)){
				fileStorageFolder.append("/");
			}
			// find where is the location to save the file
			String subfolder = uploadService.findAvailableSubfolder();
			if(StringUtils.isNotBlank(subfolder)){
				fileStorageFolder.append(subfolder.trim()).append("/");
			}else{
				fileStorageFolder.append("temp/");
			logger.error(new StringBuilder("All subfolders are full, the file is saved to \"").append(fileStorageFolder).append("\" folder.").toString());
			}
	    	
	    	// list of response will return back as json object
	    	List<FileUploadResponse> rs = new ArrayList<FileUploadResponse>();
	    	
	        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
	        Set set = multipartRequest.getFileMap().entrySet(); 
	        Iterator i = set.iterator(); 
	        while(i.hasNext()) { 
	            Map.Entry me = (Map.Entry)i.next(); 
	            String fileName = (String)me.getKey();
	            MultipartFile multipartFile = (MultipartFile)me.getValue();
	            FileUploadResponse fs = uploadService.writeToDisk(fileName, multipartFile, fileStorageFolder.toString());
	            if(fs!=null){
		            rs.add(fs);
	            }
	            
	        } 
	        if(rs.size()>0){
	        	res = new ApiResponse();
	        	res.setSuccess(true);
	        	res.setResponse1(rs);
	        }
	    }
	    catch(Exception ex)
	    {
	        ex.printStackTrace();
	    }
	    
	    return res;
	}
	
	
	
	/**
	 * tasks this controller will finish:
	 * 1. upload files in multi-thread way
	 * 2. create folder(s) if all folders are full, or not enough spots in available folders
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/storefilesInMultiThread", method = RequestMethod.POST)
	public @ResponseBody ApiResponse storefilesInMultiThread(HttpServletRequest request, HttpServletResponse response) {

		ApiResponse res = null;
		
    	long maxUploadSize = applicationConfig.getUploadFileMaxSizeInKB()*1024;
		
		try{
			// get maps of multipart files from request
	        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
	        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
	        // get the size of uploaded file
	        int totalfile = fileMap.size();
			
			// find all available folders in db
			List<String> availableFoldersInDb = uploadService.findAllAvailableSubfolders();
			
			// get base folder path
			StringBuilder fileStorageFolder = new StringBuilder(applicationConfig.getFileStorageBaseUrl());
			if(fileStorageFolder.lastIndexOf("/")<(fileStorageFolder.length()-1)){
				fileStorageFolder.append("/");
			}
			// create new 5 folders if available folders in DB < 2
			if(availableFoldersInDb==null || availableFoldersInDb.size()<2){
				if(availableFoldersInDb==null) availableFoldersInDb = new ArrayList<String>();
				// generate 5 unique folder names
				Set<String> newfolderNames = new HashSet<String>();
				do{
					newfolderNames.add(GeneralUtils.generateRandomString(18).toString());
				}while(newfolderNames.size()<5);
				// create folders and save to db
				for(String s : newfolderNames){
					// create a folder
					StringBuilder newFolderPath = new StringBuilder(fileStorageFolder).append(s);
					File folder = new File(newFolderPath.toString());
					boolean folderCreated = false;
					if(!folder.exists()){
						if(folder.mkdirs()){
							folderCreated = true;
						}else{
							logger.error(new StringBuilder("Folder \"").append(newFolderPath).append("\" creation is failed!").toString());
						}
						
					}
					// update table
					if(folderCreated){
						availableFoldersInDb.add(s);
						uploadService.saveSublocation(s, applicationConfig.getDefaultTotalFilesInFolder());
					}
				}
			}
			
			// now you have availableFoldersInDb and find a random folder to upload
			Random random = new Random();
			String uploadSubfolder = availableFoldersInDb.get(random.nextInt(availableFoldersInDb.size()));
			final StringBuilder destinationFolder = new StringBuilder(fileStorageFolder).append(uploadSubfolder.trim()).append("/");
			
	        
	    	List<FileUploadResponse> rs = new ArrayList<FileUploadResponse>();
	        // loop through uploaded files to save (using multi-thread)
            final int NTHREDS = 10;
//            ExecutorService executor = Executors.newFixedThreadPool(NTHREDS);
            ExecutorService executor = Executors.newCachedThreadPool();
	        for(Map.Entry<String, MultipartFile> e : fileMap.entrySet()){
	            final String fileName = (String)e.getKey();
	            final MultipartFile multipartFile = (MultipartFile)e.getValue();
	            
	            if(maxUploadSize>=multipartFile.getSize()){
		            Callable<FileUploadResponse> worker = new Callable(){
						@Override
						public FileUploadResponse call() throws Exception {
							return uploadService.writeToDisk(fileName, multipartFile, destinationFolder.toString());
						}
		            };

		            Future<FileUploadResponse> submit = executor.submit(worker);
		            if(submit!=null && submit.get()!=null){
			            rs.add(submit.get());
		            }
	            }
	        	
	        }
	        if(rs.size()>0){
	        	res = new ApiResponse();
	        	res.setSuccess(true);
	        	res.setResponse1(rs);
	        }
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	
	
//    @RequestMapping(method=RequestMethod.GET, value="/newEmployee/id/{id}/name/{name}")
//	public String postNewEmployee(@PathVariable("id") long userId, @PathVariable("name") String username, ModelMap model) {


	
	@RequestMapping(value = "/getimage/id/{id}/size/{size}", method = RequestMethod.GET, headers="Accept=image/jpeg, image/jpg, image/png, image/gif")
	public @ResponseBody byte[] getImage(@PathVariable("id") String id, @PathVariable("size") Integer size) {
		
		if(StringUtils.isNotBlank(id)){
			
			// find filedetail from table
			FileDetail fDetail = uploadService.getFileDetailBySystemName(id);
			
			if(fDetail!=null && fDetail.getContenttype().indexOf("image")>-1){
				StringBuilder fileLoc = new StringBuilder();
				String imagetype = null;
				
				// adjust the size, find closest size // {50, 100, 200, 600}
				ImageThumbnailSize imageSize = ImageThumbnailSize.Original;
				if(size>0){
					int diff = 100000000;
					final int size_holder = size.intValue();
					for(ImageThumbnailSize s : ImageThumbnailSize.values()){
						if(s!=ImageThumbnailSize.Original && Math.abs(s.getSize()-size_holder)<diff){
							diff = Math.abs(s.getSize()-size_holder);
							size = s.getSize();
						}
					}
					// get thumbnail file loc based on size
					fileLoc.append(fDetail.getLocation()).append("_thumbnail/img_").append(size);
					imagetype = ImgIoFormatNames.png.name();
					
				}else{
					// get original file loc
					fileLoc.append(fDetail.getLocation());
					imagetype = ImgIoFormatNames.fromType(fDetail.getContenttype()).name();
				}
				
				try {
					// Retrieve image from the classpath
					//InputStream is = this.getClass().getClassLoader().getResourceAsStream(fileLoc.toString());
					// check if file exist:
					File imageFile = new File(fileLoc.toString());
					if(imageFile.exists()){
						InputStream is = new FileInputStream(imageFile);
						
						
						// Prepare buffered image
						BufferedImage img = ImageIO.read(is);
						
						// Create a byte array output stream
						ByteArrayOutputStream bao = new ByteArrayOutputStream();
						
						// Write to output stream
						ImageIO.write(img, imagetype, bao);
						
						return bao.toByteArray();
						
					}
					
				
				} catch (IOException e) {
					logger.error(e.toString());
					throw new RuntimeException(e);
				}
			}
			
			
		}
		
		return null;
	}	

	@RequestMapping(value = "/getTxt/id/{id}", method = RequestMethod.GET, headers="Accept=text/plain", produces="text/plain; charset=utf-8")
	public @ResponseBody String getTxt(@PathVariable("id") String id) {
		if(StringUtils.isNotBlank(id)){
			
			// find filedetail from table
			FileDetail fDetail = uploadService.getFileDetailBySystemName(id);
			
			if(fDetail!=null && (fDetail.getContenttype().indexOf("text")>-1 || fDetail.getContenttype().indexOf("javascript")>-1)){
				String fileLoc = fDetail.getLocation();
				
				File file = new File(fileLoc);
				if(file.exists()){
					
					try {
						return FileUtils.readFileToString(file, "UTF-8");
					} catch (IOException e) {
						e.printStackTrace();
					}
					
				}
			}
		}
		
		return null;
		
	}
		
	@RequestMapping(value = "/getPdfInString/id/{id}/pages/{pages}", method = RequestMethod.GET, headers="Accept=text/plain")
	public @ResponseBody String getPdfInString(@PathVariable("id") String id, @PathVariable("pages") Integer pages) {
		
		FileDetail fDetail = uploadService.getFileDetailBySystemName(id);
		if(fDetail!=null && fDetail.getContenttype().indexOf("pdf")>-1){
			String fileLoc = fDetail.getLocation();
			
			File file = new File(fileLoc);
			if(file.exists()){
				InputStream in;
				try {
					in = FileUtils.openInputStream(file);
					PdfReader pdfReader = new PdfReader(in);
					int numberOfPages = pdfReader.getNumberOfPages();
					StringBuilder returnString = new StringBuilder();
					PdfTextExtractor textExtractor = new PdfTextExtractor(pdfReader);
					for(int i=1; i<=(pages<=numberOfPages?pages:numberOfPages); i++){
						returnString.append(textExtractor.getTextFromPage(i));
					}
					
					return returnString.toString();
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		
		
		return null;
	}
	

	/**
	 * @param id
	 * @param page which page
	 * @return
	 */
	@RequestMapping(value = "/getPdfInByte/id/{id}/page/{page}", method = RequestMethod.GET, headers="Accept=text/plain")
	public @ResponseBody byte[] getPdfInByte(@PathVariable("id") String id, @PathVariable("page") Integer page) {
		
		
		return null;
	}
		
	
	
	
}
