package com.facebookMessenger.rest;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class CsrfController {

	 @GetMapping("/getCsrfToken")
	    public String getCsrfToken(HttpServletRequest request) {
	        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
	        if (csrfToken != null) {
	            return csrfToken.getToken();
	        }
	        return "CSRF token not found.";
	    }
}
