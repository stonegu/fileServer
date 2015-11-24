package com.fileServer.core.controller.component;

import java.io.Serializable;
import java.util.*;

import org.apache.commons.lang.StringUtils;

@Deprecated
public class WebSessionContainer implements Serializable{
	
	private static final long serialVersionUID = -3255308768164598142L;

	/**
	 * ************* foldersForFileUpload ************** 
	 * 
	 * GeneralSelectionType: 
	 * 		"key" is for folder name, 
	 * 		"value" is the number for how many files can be put, 
	 * 		"selected" is for the lock, true is locked, false is unloaded
	 */
	private Map<String, GeneralSelectionType> foldersForFileUpload;

	public Map<String, GeneralSelectionType> getFoldersForFileUpload() {
		return foldersForFileUpload;
	}

	public void setFoldersForFileUpload(Map<String, GeneralSelectionType> foldersForFileUpload) {
		this.foldersForFileUpload = foldersForFileUpload;
	}
	
	public void addFolder(String folderName, int availableSpots){
		if(foldersForFileUpload==null) foldersForFileUpload = new HashMap<String, GeneralSelectionType>();
		foldersForFileUpload.put(folderName, new GeneralSelectionType(folderName, Integer.toString(availableSpots), false));
	}
	
	public void removeFolder(String folderName){
		if(foldersForFileUpload!=null){
			foldersForFileUpload.remove(folderName);
		}
	}
	
	public boolean isLocked(String folderName){
		if(foldersForFileUpload!=null){
			return foldersForFileUpload.get(folderName).getSelected();
		}
		return false;
	}
	
	public int getAvailableSpotsNumber(String folderName){
		if(foldersForFileUpload!=null){
			return Integer.parseInt(foldersForFileUpload.get(folderName).getValue());
		}
		return 0;
	}
	
	public boolean setLock(String folderName, boolean lock){
		if(foldersForFileUpload!=null){
			foldersForFileUpload.get(folderName).setSelected(lock);
			return lock;
		}
		return false;
	}
	
	public int totalAvailableSpots(){
		int spots = 0;
		if(foldersForFileUpload!=null){
			for(Map.Entry<String, GeneralSelectionType> e : foldersForFileUpload.entrySet()){
				spots = spots + Integer.parseInt(e.getValue().getValue());
			}
		}
		return spots;
	}
	
	
	
	
	
}
