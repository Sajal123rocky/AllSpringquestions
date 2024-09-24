package com.nw.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/clientService")
public class clientController {
//	@GetMapping("/show")
//	public String showString() {
//		return "Hello Eureka Server1";
//	}
	
	@Autowired
	DiscoveryClient discoveryClient;
	
	@GetMapping("/getService")
	public String requestMapping() {
		List<ServiceInstance> services= discoveryClient.getInstances("productService");
		String res=null;
		if(!services.isEmpty())
		{
			ServiceInstance ins1= services.get(0);
			String url=ins1.getUri().toString();
			System.out.println("-----"+url);
			url=url+"/api/productService/show";
			RestTemplate restTemplate=new RestTemplate();
			res=restTemplate.getForObject(url, String.class);
			
		}
		return res;
		
	}
}