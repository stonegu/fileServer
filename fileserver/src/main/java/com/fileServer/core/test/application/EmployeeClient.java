package com.fileServer.core.test.application;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fileServer.core.test.pojo.Employee;
import com.fileServer.core.test.pojo.EmployeeList;


public class EmployeeClient {

	public static void main(String[] args) {
		RestTemplate restTemplate = getTemplate();
		
		listXML(restTemplate);
		
		postEmployee(restTemplate);
		
		updateEmployee(restTemplate);
		
//		listAtom(restTemplate);
		
		removeEmployee(restTemplate);
		
		listJson(restTemplate);
	}
	
	public static void listXML(RestTemplate rest) {
		HttpEntity<String> entity = prepareGet(MediaType.APPLICATION_XML);
		
		ResponseEntity<EmployeeList> response = rest.exchange(
				"http://localhost:8080/service/emps", 
				HttpMethod.GET, entity, EmployeeList.class);
		
		EmployeeList employees = response.getBody();
		for(Employee e : employees.getEmployees()) {
			System.out.println(e.getId() + ": " + e.getName());
		}
	}
	
//	public static void listAtom(RestTemplate rest) {
//		HttpEntity<String> entity = prepareGet(MediaType.APPLICATION_ATOM_XML);
//		
//		ResponseEntity<Feed> response = rest.exchange(
//				"http://localhost:8080/service/emps", 
//				HttpMethod.GET, entity, Feed.class);
//		
//		WireFeed atomFeed = response.getBody();
//		WireFeedOutput output = new WireFeedOutput();
//		try {
//			System.out.println(output.outputString(atomFeed));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
	public static void listJson(RestTemplate rest) {
		HttpEntity<String> entity = prepareGet(MediaType.APPLICATION_JSON);
		
		ResponseEntity<EmployeeList> response = rest.exchange(
				"http://localhost:8090/service/emps", 
				HttpMethod.GET, entity, EmployeeList.class);
		
		EmployeeList employees = response.getBody();
		for(Employee e : employees.getEmployees()) {
			System.out.println(e.getId() + ": " + e.getName());
		}
	}
	
	public static void postEmployee(RestTemplate rest) {
		Employee newEmp = new Employee(99, "guest", "guest@hubba.com");
		HttpEntity<Employee> entity = new HttpEntity<Employee>(newEmp);
		
		ResponseEntity<Employee> response = rest.postForEntity(
				"http://localhost:8090/service/emp", 
				entity, Employee.class);
		
		Employee e = response.getBody();
		System.out.println("New employee: " + e.getId() + ", " + e.getName());
	}
	
	public static void updateEmployee(RestTemplate rest) {
		Employee newEmp = new Employee(99, "guest99", "guest99@hubba.com");
		HttpEntity<Employee> entity = new HttpEntity<Employee>(newEmp);
			
		rest.put(
				"http://localhost:8090/service/emp/{id}", 
				entity, "99");
	}
	
	public static void removeEmployee(RestTemplate rest) {
		rest.delete("http://localhost:8090/service/emp/{id}", "99");
	}
	
	private static RestTemplate getTemplate() {
		ApplicationContext ctx = new FileSystemXmlApplicationContext(
			"src/main/webapp/WEB-INF/xml/servlet-context.xml");
		RestTemplate template = (RestTemplate) ctx.getBean("restTemplate");
		return template;
	}
	
	private static HttpEntity<String> prepareGet(MediaType type) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(type);
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		return entity;
	}
}
