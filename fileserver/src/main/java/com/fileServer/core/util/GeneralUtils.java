package com.fileServer.core.util;

import java.util.Random;

public class GeneralUtils {


	public static StringBuilder generateRandomString(int sizeToGenerate){
		char[] chars = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };
		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		
		for (int k = 0; k < sizeToGenerate; k++) {
		    char c = chars[random.nextInt(chars.length)];
		    sb.append(c);
		}
		return sb;
		
	}

}
