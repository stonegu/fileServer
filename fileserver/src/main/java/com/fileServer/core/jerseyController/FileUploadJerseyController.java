package com.fileServer.core.jerseyController;

import java.io.*;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

@Path("/file")
public class FileUploadJerseyController {
	
	
	@POST
	@Path("/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadFile(
		@FormDataParam("file") InputStream uploadedInputStream,
		@FormDataParam("file") FormDataContentDisposition fileDetail) {
		
		Response.Status respStatus = Response.Status.OK;
		String output = null;

		if (fileDetail == null)
		{
			respStatus = Response.Status.INTERNAL_SERVER_ERROR;
		}else{
			String uploadedFileLocation = "/Users/Hubba/Downloads/HelloWorld/download/" + fileDetail.getFileName();
			 
			// save it
			try {
				writeToFile(uploadedInputStream, uploadedFileLocation);
				output = "File uploaded to : " + uploadedFileLocation;
			} catch (IOException e) {
				respStatus = Response.Status.INTERNAL_SERVER_ERROR;
				e.printStackTrace();
			}
			
		}
 
		//return Response.status(respStatus).build();
		return Response.status(respStatus).entity(output).build();
	}
 
	// save uploaded file to new location
	private void writeToFile(InputStream uploadedInputStream, String uploadedFileLocation) throws IOException {
 
		OutputStream out = new FileOutputStream(new File(uploadedFileLocation));
		int read = 0;
		byte[] bytes = new byte[1024];
 
		out = new FileOutputStream(new File(uploadedFileLocation));
		while ((read = uploadedInputStream.read(bytes)) != -1) {
			out.write(bytes, 0, read);
		}
		out.flush();
		out.close();
 
	}
 	
	
	

}
