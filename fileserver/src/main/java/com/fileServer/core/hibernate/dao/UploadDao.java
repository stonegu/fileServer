package com.fileServer.core.hibernate.dao;

import java.util.*;

import com.fileServer.core.hibernate.pojo.FileDetail;
import com.fileServer.core.hibernate.pojo.FileSubLocation;
import com.fileServer.core.util.MathOperator;

public interface UploadDao {
	
	public List<String> findAllAvailableSubfolders();

	public String findAvailableSubfolder();
	
	public FileSubLocation findFileSubLocationByFoldername(String foldername);
	
	public FileDetail getFileDetailBySystemName(String fileSystemName);
	
	public Long saveFileDetail(FileDetail fileDetail);
	
	/**
	 * this method will guarantee that there will no duplicated folder saved into db 
	 * 
	 * @param loc
	 * @return
	 */
	public Long saveFileSubLocation(FileSubLocation loc);
	
	public void updateSizeForSublocation(MathOperator op, int number, String subfolder);

}
