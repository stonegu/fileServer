package com.fileServer.core.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class ApplicationConfiguration {
	/**
	 * Loaded config file.
	 */
	private Properties config;


	@Autowired private ApplicationContext appContext;

	@PostConstruct
	protected void init() {
		try {
			Resource res = appContext.getResource("/WEB-INF/classes/GlobalConfiguration.properties");
			if (res==null) throw new IllegalArgumentException("Cannot find configuration file: /WEB-INF/classes/GlobalConfiguration.properties");
			InputStream in = res.getInputStream();
			Reader r = new InputStreamReader(in,"UTF-8");
			Properties props = new Properties();
			props.load(r);
			r.close();

			config = props;
		} catch (java.io.IOException e) {
			throw new IllegalArgumentException("Cannot read GlobalConfiguration.propertires", e);
		}
	} // init

	// use this for testing - pass it the filename of the properties file
	public void initFromFile (File filename) {
		try {
			InputStream in =new FileInputStream(filename);
			Reader r = new InputStreamReader(in,"UTF-8");
			Properties props = new Properties();
			props.load(r);
			r.close();

			config = props;
		} catch (java.io.IOException e) {
			throw new IllegalArgumentException("Cannot read GlobalConfiguration.propertires", e);
		}
	}


    public Map<String, String> getMailConfigInfoMap(){
        Map<String, String> mailConfigInfoMap = new HashMap<String, String>();

        mailConfigInfoMap.put("gmail.smtp.host", config.getProperty("gmail.smtp.host"));
        mailConfigInfoMap.put("gmail.smtp.auth", config.getProperty("gmail.smtp.auth"));
        mailConfigInfoMap.put("gmail.smtp.starttls.enable", config.getProperty("gmail.smtp.starttls.enable"));
        mailConfigInfoMap.put("gmail.smtp.socketFactory.port", config.getProperty("gmail.smtp.socketFactory.port"));
        mailConfigInfoMap.put("gmail.smtp.socketFactory.class", config.getProperty("gmail.smtp.socketFactory.class"));
        mailConfigInfoMap.put("gmail.smtp.socketFactory.fallback", config.getProperty("gmail.smtp.socketFactory.fallback"));
        mailConfigInfoMap.put("gmail.account.username", config.getProperty("gmail.account.username"));
        mailConfigInfoMap.put("gmail.account.password", config.getProperty("gmail.account.password"));
        mailConfigInfoMap.put("gmail.account", config.getProperty("gmail.account"));
        mailConfigInfoMap.put("mail.test", config.getProperty("mail.test"));
        mailConfigInfoMap.put("mail.test.receiver", config.getProperty("mail.test.receiver"));

        return mailConfigInfoMap;
    }

    public String getHostName(){
    	return config.getProperty("hostname");
    }
    
    public String getFileStorageBaseUrl(){
    	return config.getProperty("fileStorageBaseUrl");
    }
    
    public int getDefaultTotalFilesInFolder(){
    	return Integer.parseInt(config.getProperty("defaultTotalFilesInFolder"));
    }

    public long getUploadFileMaxSizeInKB(){
    	return Long.parseLong(config.getProperty("upload.file.maxsize"));
    }
}
