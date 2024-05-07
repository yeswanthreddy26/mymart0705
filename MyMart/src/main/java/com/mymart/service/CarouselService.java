package com.mymart.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mymart.model.Carousel;
import com.mymart.repository.CarouselRepository;

@Service
public class CarouselService {
	
		@Autowired
		 private CarouselRepository imageRepository;
		
		    public List<Carousel> getAllImages() {
		        return imageRepository.findAll();
		    }
		    
		   

	}

