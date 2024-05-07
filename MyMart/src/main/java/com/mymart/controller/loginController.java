package com.mymart.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mymart.model.DropdownItem;
import com.mymart.model.NavLink;
import com.mymart.model.User;
import com.mymart.model.UserDto;
import com.mymart.repository.UserRepository;
import com.mymart.service.NavBarService;
import com.mymart.service.UserService;
import com.mymart.service.UserServiceImpl;

import jakarta.validation.Valid;

@Controller
public class loginController {
	
	@Autowired
	private UserServiceImpl serviceImpl; 
	@Autowired
	private final NavBarService service; 
	@Autowired
	UserRepository userrepo;
    public loginController(NavBarService service) {
        this.service = service;
    }

	@Autowired
	UserDetailsService userDetailsService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserServiceImpl userServiceimpl;
	@GetMapping("/registration")
	public String getRegistrationPage(@ModelAttribute("user") UserDto userDto) {
		return "login/register";
	}
	

	@PostMapping("/registration")
	public String saveUser(@ModelAttribute("user") @Valid UserDto userDto, BindingResult bindingResult, Model model,
			@RequestParam(name="g-recaptcha-response") String captcha) {
	    if (userServiceimpl.existsByEmail(userDto.getEmail())) {
	        model.addAttribute("message", "There is already an account registered with this email.");
	    } else if (!userDto.getPassword().equals(userDto.getConfirmPassword())) {
	        model.addAttribute("message", "Password and confirm password should be same");
	    } else {
	    	if (userServiceimpl.validateCaptcha(captcha)) {
	    		
	    		userService.save(userDto);
		        model.addAttribute("successMessage", "You have Registered Successfully!");
			}
	    	else if(!userServiceimpl.validateCaptcha(captcha)) {
	    		 model.addAttribute("message", "Please verify captcha");
	    		   
	    	}
	      }

	    return "login/register";
	}

	@GetMapping("/login")
	public String login() {
		
		return "login/login";
	}
	
	
	@GetMapping("user-page")
	public String userPage (Model model, Principal principal) {
		UserDetails userDetails = userDetailsService.loadUserByUsername(principal.getName());
		model.addAttribute("user", userDetails);
		//return "login/user";
		return "redirect:/";
	}
	
	

	@GetMapping("admin-page")
	public String adminPage (Model model, Principal principal) {
		UserDetails userDetails = userDetailsService.loadUserByUsername(principal.getName());
		
		 List<NavLink> allNavLinks = service.getAllNavLinks();
	        Map<NavLink, List<DropdownItem>> navbarWithDropdownData = service.getNavbarWithDropdownData();

	       

	        model.addAttribute("allNavLinks", allNavLinks);
	        model.addAttribute("navbarWithDropdownData", navbarWithDropdownData);
		
		
		model.addAttribute("user", userDetails);
		return "login/admin";
	}
	
	
	@GetMapping("/profiles")
	public String getUser(@RequestParam String name, Model model) {


		try {
			User user = userrepo.findByName(name);
			model.addAttribute("user", user);
			UserDto userDto = new UserDto();
			userDto.setName(user.getName());
			userDto.setEmail(user.getEmail());
			userDto.setContactNo(user.getContactNo());
		} catch (Exception e) {
			System.out.println("Exception is :" + e.getMessage());

		}

		return "user/myprofile";
	}

	@GetMapping("/edit")
	public String editUser(@RequestParam String name, Model model) {
		try {
			User user = userrepo.findByName(name); // Fetch user by ID
			model.addAttribute("user", user);
			UserDto userDto = new UserDto();
			model.addAttribute("userDto", userDto);

			// Decode the password
//			String decodedPassword = decodePassword(user.getPassword());
//
			// Set decoded password to userDto
//			userDto.setPassword(decodedPassword);

			userDto.setName(user.getName());

			userDto.setEmail(user.getEmail());
			userDto.setContactNo(user.getContactNo());
			// Pass any additional model attributes needed for editing
		} catch (Exception e) {
			System.out.println("Exception is :" + e.getMessage());
			// Handle error appropriately
		}
		return "user/editprofile"; // Return the edit profile HTML template
	}


	@PostMapping("/edit")
	public String saveUser(@ModelAttribute UserDto userDto) {
		// Here you can handle the form submission logic, such as updating the user's information
		// For example, you might fetch the user from the database and update its properties with the values from the userDto

		// Assuming you have a service layer to handle the business logic
		try {
			// Fetch the user from the database by its ID
			User user = userrepo.findById(userServiceimpl.getCurrentUserId()).get();

			// Update user details with the values from userDto



			user.setEmail(userDto.getEmail());
			user.setContactNo(userDto.getContactNo());

			// Save the updated user
			userrepo.save(user);
		} catch (Exception e) {
			// Handle exceptions, log errors, etc.
			// You might redirect to an error page or return to the edit page with an error message
			System.out.println("Exception occurred: " + e.getMessage());
			 // Assuming your edit profile page is named "editprofile"
		}

		// Redirect to some page after successful submission
		return "redirect:/login"; // Redirect to the admin page or any other page
	}


}

