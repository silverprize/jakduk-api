package com.jakduk.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.LocaleResolver;

import com.jakduk.model.db.BoardFree;
import com.jakduk.service.CommonService;
import com.jakduk.service.HomeService;
import com.jakduk.service.UserService;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;

@Controller
@RequestMapping("/")
public class HomeController {
	
	@Autowired
	private CommonService commonService;
	
	@Autowired
	private HomeService homeService;
	
	@Autowired
	private UserService userService;
	
	@Resource
	LocaleResolver localeResolver;
	
	@Resource
	MessageSource messageSource;
	
	@RequestMapping
	public String root() {
		
		return "redirect:home";
	}
	
	@RequestMapping(value = "/home")
	public String home(Model model,
			HttpServletRequest request) {
		
		Locale locale = localeResolver.resolveLocale(request);
		homeService.getHome(model, locale);
		
		return "home/home";
	}
	
	@RequestMapping(value = "/home/jumbotron", method = RequestMethod.GET)
	public String jumbotron(HttpServletRequest request, HttpServletResponse response,			
			@RequestParam(required = false) String lang,
			Model model) {
		
		Locale locale = localeResolver.resolveLocale(request);
		String language = commonService.getLanguageCode(locale, lang);
		
		homeService.getJumbotron(model, language);
		
		return "home/jumbotron";
	}
	
	@RequestMapping(value = "/check/user/email")
	public void checkEmail(Model model,
			@RequestParam(required = true) String email) {
		
		Boolean existEmail = userService.existEmail(email);
		
		model.addAttribute("existEmail", existEmail);
	}
	
	@RequestMapping(value = "/check/user/username")
	public void checkUsername(Model model,
			@RequestParam(required = true) String username) {
		
		Boolean existUsername = userService.existUsernameOnWrite(username);
		
		model.addAttribute("existUsername", existUsername);
	}
	
	@RequestMapping(value = "/check/user/update/username")
	public void checkUpdateUsername(Model model,
			@RequestParam(required = true) String username) {
		
		Boolean existUsername = userService.existUsernameOnUpdate(username);
		
		model.addAttribute("existUsername", existUsername);
	}
	
	@RequestMapping(value = "/check/oauth/update/username")
	public void checkOAuthUpdateUsername(Model model,
			@RequestParam(required = true) String username) {
		
		Boolean existUsername = userService.existOAuthUsernameOnUpdate(username);
		
		model.addAttribute("existUsername", existUsername);
	}
	
	@RequestMapping(value = "/home/board/latest", method = RequestMethod.GET)
	public String boardLatest(Model model) {
		
		homeService.getBoardLatest(model);
		
		return "home/board/latest";
	}
	
	@RequestMapping(value = "/home/user/latest", method = RequestMethod.GET)
	public String userLatest(Model model) {
		
		homeService.getUserLatest(model);
		
		return "home/user/latest";
	}
	
	@RequestMapping(value = "/home/data/latest", method = RequestMethod.GET)
	public String dataLatest(Model model) {
		
		homeService.getBoardLatest(model);
		homeService.getUserLatest(model);
		homeService.getGalleryLatest(model);
		
		return "home/user/latest";
	}
	
	@RequestMapping(value = "/error/{code}", method = RequestMethod.GET)
	public String error(Model model, @PathVariable String code) {
		
		model.addAttribute("code", code);
		
		return "access/error";
	}
	
	@RequestMapping(value = "/rss")
	public void rss(Model model
			, HttpServletRequest request, HttpServletResponse response) {
		
		Locale locale = localeResolver.resolveLocale(request);
		Integer status = homeService.getRss(response, locale, messageSource);
	}		

}
