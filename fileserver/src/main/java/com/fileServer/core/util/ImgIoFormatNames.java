package com.fileServer.core.util;

import org.apache.commons.lang.StringUtils;

public enum ImgIoFormatNames {
//	aaa(new String[]{"aa", "bb"}),
	jpg(new String[]{"image/jpeg"}), 
	bmp(new String[]{"image/bmp", "image/x-ms-bmp"}), 
	jpeg(new String[]{"image/jpeg"}), 
	wbmp(new String[]{"image/vnd.wap.wbmp"}), 
	png(new String[]{"image/png"}), 
	gif(new String[]{"image/gif"}),
	;
	
	
	private String[] contentTypes;

	private ImgIoFormatNames(String[] contentTypes) {
		this.contentTypes = contentTypes;
	}

	public String[] getContentTypes() {
		return contentTypes;
	}
	
	static public ImgIoFormatNames fromType(String contentType){
		if(StringUtils.isNotBlank(contentType)){
			for(ImgIoFormatNames i : ImgIoFormatNames.values()){
				
				if(i.getContentTypes()!=null && i.getContentTypes().length>0){
					for(String type : i.getContentTypes()){
						if(type.equalsIgnoreCase(contentType)){
							return i;
						}
					}
				}
				
			}
		}
		return null;
	}
	

}
