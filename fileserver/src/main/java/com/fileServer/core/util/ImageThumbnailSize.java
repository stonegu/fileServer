package com.fileServer.core.util;

public enum ImageThumbnailSize {
	//{50, 200, 600}
	Original(-1),
	
	Fifty(50),
	Hundred(100),
	TwoHundred(200),
	SixHundred(600),
	;
	
	
	private int size;

	private ImageThumbnailSize(int size) {
		this.size = size;
	}

	public int getSize() {
		return size;
	}
}
