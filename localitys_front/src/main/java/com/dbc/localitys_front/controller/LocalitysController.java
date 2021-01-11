package com.dbc.localitys_front.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import com.dbc.localitys_front.model.CitysDto;
import com.dbc.localitys_front.model.StateDto;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class LocalitysController {
	
	@Value("${api.localitys}")
	private String endpointLocalitys;
	
	private List<StateDto> states = new ArrayList<StateDto>();

	@SuppressWarnings("unchecked")
	@GetMapping("/")
	public ModelAndView inicio() {
		ModelAndView md = new ModelAndView();
		ObjectMapper mapper = new ObjectMapper();

		try {
			RestTemplate restTemplate = new RestTemplate();
			String url = endpointLocalitys + "api/v1/states";
			states = restTemplate.getForObject(url, List.class);
			StateDto statedDto = mapper.convertValue(states.get(1), StateDto.class);
			
			md.setViewName("index");
			md.addObject("states", states);
			md.addObject("stateDto", statedDto);
			md.addObject("citys", statedDto.getCitys());
			md.addObject("newCity", new CitysDto());
			
		}catch (RestClientException e) {
			HttpClientErrorException httpClientErro = (HttpClientErrorException) e;
			md.addObject("erro", httpClientErro.getResponseBodyAsString());
			md.setViewName("erro");
		}
		
		return md;
	}
	
	@GetMapping("/getDataState")
	public ModelAndView inicio(@RequestParam(name="idState") Long idState) {
		ModelAndView md = new ModelAndView();
		try {
			RestTemplate restTemplate = new RestTemplate();
			String url = endpointLocalitys + "api/v1/states/" + idState;
			ResponseEntity<StateDto> resp = restTemplate.getForEntity(url, StateDto.class);
			
			StateDto dto = resp.getBody();
			
			md.setViewName("index");
			md.addObject("states", states);
			
			md.addObject("stateDto", dto);
			md.addObject("citys", dto.getCitys());
			md.addObject("newCity", new CitysDto());
			
		}catch (RestClientException e) {
			HttpClientErrorException httpClientErro = (HttpClientErrorException) e;
			md.addObject("erro", httpClientErro.getResponseBodyAsString());
			md.setViewName("erro");
		}
		
		return md;
	}
	
	
	@PostMapping("/{idState}/salvarCidade")
	public ModelAndView saveCity(@PathVariable("idState") Long idState, CitysDto cityRequest) {
		ModelAndView md = new ModelAndView();
		try {
			RestTemplate restTemplate = new RestTemplate();
			
			String url = endpointLocalitys + "api/v1/states/"+idState+"/city";
			
			restTemplate.postForEntity(url, cityRequest, CitysDto.class);
			
			md.setViewName("redirect:/getDataState?idState="+idState);
			
		}catch (RestClientException e) {
			HttpClientErrorException httpClientErro = (HttpClientErrorException) e;
			md.addObject("erro", httpClientErro.getResponseBodyAsString());
			md.setViewName("erro");
		}
		
		return md;
	}
	
	@GetMapping("/excluirCidade/{idState}/citys/{idCity}")
	public ModelAndView deleteCity(@PathVariable("idState") Long idState, @PathVariable("idCity") Long idCity) {
		ModelAndView md = new ModelAndView();
		try {
			RestTemplate restTemplate = new RestTemplate();
			
			String url = endpointLocalitys + "api/v1/states/"+idState+"/city/" + idCity;
			restTemplate.delete(url);
			
			md.setViewName("redirect:/getDataState?idState="+idState);
			
		}catch (RestClientException e) {
			HttpClientErrorException httpClientErro = (HttpClientErrorException) e;
			md.addObject("erro", httpClientErro.getResponseBodyAsString());
			md.setViewName("erro");
		}
		
		return md;
	}
}
