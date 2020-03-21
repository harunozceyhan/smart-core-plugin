package com.smart.controller;

import org.springframework.stereotype.Controller;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.security.access.AccessDeniedException;

@Controller
public class SecurityErrorController implements ErrorController {

    @RequestMapping(value = "/error")
    public String handleError(HttpServletRequest request) throws Exception {
        if (request.getHeader("Authorization") == null) {
            throw new AccessDeniedException("Token Not Found");
        }
        throw (Exception) request.getAttribute("exception");
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }

}