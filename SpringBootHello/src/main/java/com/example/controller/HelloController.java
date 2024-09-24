package com.example.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.Employee;

@RestController
public class HelloController {
	@GetMapping("/hello-world")
	public String helloService() {
		return "Hello World";
	}
	@GetMapping("/employee")
	public Employee getEmployee() {
		return new Employee(1,"Raj","Kumar");
	}
	
	@GetMapping("/employees")
	public List<Employee> getEmployees(){
		List<Employee> list=new ArrayList<>();
		list.add(new Employee(1,"Raj","Kumar"));
		list.add(new Employee(2,"John","Doe"));
		list.add(new Employee(3,"Raju","Rastogi"));
		list.add(new Employee(4,"Sumit","Goel"));
		return list;
	}
	
	@GetMapping("/employee/{id}/{firstName}/{lastName}")
	public Employee getEmployeePathVar(@PathVariable("id") int id,@PathVariable("firstName") String firstName,@PathVariable("lastName") String lastName) {
		return new Employee(id,firstName,lastName);
	}
	
	@GetMapping("/employee/query")
	public Employee getEmployeeRequestParam(@RequestParam(name="id") int id,@RequestParam(name="firstName") String firstName,@RequestParam(name="lastName") String lastName) {
		return new Employee(id,firstName,lastName);
	}
}
