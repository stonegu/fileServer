package com.fileServer.core.controller;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.fileServer.core.test.dao.EmployeeDS;
import com.fileServer.core.test.pojo.Employee;
import com.fileServer.core.test.pojo.EmployeeList;


@Controller
public class TestRestfulController {

	@Autowired
	private EmployeeDS employeeDS;
	
	@Autowired
	private Jaxb2Marshaller jaxb2Mashaller;
	
	private static final String XML_VIEW_NAME = "testEmployees";
	
	@RequestMapping(method=RequestMethod.GET, value="/employee/{id}")
	public ModelAndView getEmployee(@PathVariable String id) {
		Employee e = employeeDS.get(Long.parseLong(id));
		return new ModelAndView(XML_VIEW_NAME, "object", e);
	}
	
	@RequestMapping(method=RequestMethod.PUT, value="/employee/{id}")
	public ModelAndView updateEmployee(@RequestBody String body) {
		Source source = new StreamSource(new StringReader(body));
		Employee e = (Employee) jaxb2Mashaller.unmarshal(source);
		employeeDS.update(e);
		return new ModelAndView(XML_VIEW_NAME, "object", e);
	}
	
	@RequestMapping(method=RequestMethod.POST, value="/employee")
	public ModelAndView addEmployee(@RequestBody String body) {
		Source source = new StreamSource(new StringReader(body));
		Employee e = (Employee) jaxb2Mashaller.unmarshal(source);
		employeeDS.add(e);
		return new ModelAndView(XML_VIEW_NAME, "object", e);
	}
	
	@RequestMapping(method=RequestMethod.DELETE, value="/employee/{id}")
	public ModelAndView removeEmployee(@PathVariable String id) {
		employeeDS.remove(Long.parseLong(id));
		List<Employee> employees = employeeDS.getAll();
		EmployeeList list = new EmployeeList(employees);
		return new ModelAndView(XML_VIEW_NAME, "employees", list);
	}
	
	@RequestMapping(method=RequestMethod.GET, value="/employees")
	public ModelAndView getEmployees() {
		List<Employee> employees = employeeDS.getAll();
		EmployeeList list = new EmployeeList(employees);
		return new ModelAndView(XML_VIEW_NAME, "employees", list);
	}
	
	////////////////////////// @ResponseBody ////////////////////////
	
	@RequestMapping(method=RequestMethod.GET, value="/emp/{id}", headers="Accept=application/xml, application/json")
	public @ResponseBody Employee getEmp(@PathVariable String id) {
		Employee e = employeeDS.get(Long.parseLong(id));
		return e;
	}
	
	@RequestMapping(method=RequestMethod.GET, value="/emps", headers="Accept=application/xml, application/json")
	public @ResponseBody EmployeeList getAllEmp() {
		List<Employee> employees = employeeDS.getAll();
		EmployeeList list = new EmployeeList(employees);
		return list;
	}
	
//	@RequestMapping(method=RequestMethod.GET, value="/emps", headers="Accept=application/atom+xml")
//	public @ResponseBody Feed getEmpFeed() {
//		List<Employee> employees = employeeDS.getAll();
//		return AtomUtil.employeeFeed(employees, jaxb2Mashaller);
//	}
//	
	@RequestMapping(method=RequestMethod.POST, value="/emp")
	public @ResponseBody Employee addEmp(@RequestBody Employee e) {
		employeeDS.add(e);
		return e;
	}
	
	@RequestMapping(method=RequestMethod.PUT, value="/emp/{id}")
	public @ResponseBody Employee updateEmp(@RequestBody Employee e, @PathVariable String id) {
		employeeDS.update(e);
		return e;
	}
	
	@RequestMapping(method=RequestMethod.DELETE, value="/emp/{id}")
	public @ResponseBody void removeEmp(@PathVariable String id) {
		employeeDS.remove(Long.parseLong(id));
	}
	
	
	// test for image upload
	@RequestMapping(value = "/receiver1", method = RequestMethod.POST)
	public @ResponseBody Employee processUploadFile1(
	        @RequestParam(value = "imgName") String imgName,
	        @RequestParam(value = "img") String m
	) {

		Map<String, String> resultMap = new HashMap<String, String>();
		
		byte[] bytes = Base64.decodeBase64(m);
		
	    if (m == null) {
	    	resultMap.put("pass", "false");
	        System.out.println("Shit!... is null");
	    } else {
	    	resultMap.put("pass", "true");
	        System.out.println("Yes!... work done!");
	    }
	    
	    Employee e = new Employee(1l, "stone", "stone@test.com");
	    
	    return e;
	}	
	
	@RequestMapping(value = "/receiver2", method = RequestMethod.POST
//			, headers="Content-Type=multipart/form-data"
				)
	public @ResponseBody Employee processUploadFile2(
	        @RequestParam(value = "imgName") String imgName,
	        @RequestParam(value = "img") CommonsMultipartFile m
	) {

		Map<String, String> resultMap = new HashMap<String, String>();
		
	    if (m == null) {
	    	resultMap.put("pass", "false");
	        System.out.println("Shit!... is null");
	    } else {
	    	resultMap.put("pass", "true");
	        System.out.println("Yes!... work done!");
	    }
	    
	    Employee e = new Employee(1l, "stone", "stone@test.com");
	    
	    return e;
	}	
	
	@RequestMapping(value = "/receiver3", method = RequestMethod.POST
//			, headers="Content-Type=multipart/form-data"
				)
	public @ResponseBody Employee processUploadFile3(
	        @RequestParam(value = "imgName") String imgName,
	        @RequestParam(value = "img") InputStream m
	) {

		Map<String, String> resultMap = new HashMap<String, String>();
		
	    if (m == null) {
	    	resultMap.put("pass", "false");
	        System.out.println("Shit!... is null");
	    } else {
	    	resultMap.put("pass", "true");
	        System.out.println("Yes!... work done!");
	    }
	    
	    
	    Employee e = new Employee(1l, "stone", "stone@test.com");
	    
	    return e;
	}
	
	@RequestMapping(value = "/formpost", method = RequestMethod.POST)
	public @ResponseBody String formpost(){
		return "redirect:/index";
	}
	
	@RequestMapping(value = "/formpostmultipart", method = RequestMethod.POST)
	public @ResponseBody String formpostmultipart(
	        @RequestParam(value = "img") CommonsMultipartFile m
	){
		return "redirect:/index";
	}
	
	
	
}
