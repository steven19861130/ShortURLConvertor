package com.steven.shorturldemo.controller;


import com.steven.shorturldemo.bean.ResponseBean;
import com.steven.shorturldemo.service.ShortURLConvertorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/rest")
public class ShortURLRestController {

    Logger logger = LoggerFactory.getLogger(ShortURLRestController.class);

    @Autowired
    ShortURLConvertorService shortURLConvertorService;

    @GetMapping("/shorturl/v1/getshorturl")
    public ResponseBean getShortURL(@RequestParam String longURL, @RequestParam String expire) {
        String shortURL = shortURLConvertorService.shortenURL(longURL,expire);
        if (!shortURL.isEmpty()) {
            return new ResponseBean.Builder<>().ok().message(shortURL).build();
        } else {
            return new ResponseBean.Builder<>().error().message("Convert long URL failed!!!").build();
        }
    }

    @GetMapping("/{shortURL}")
    public ModelAndView redirectToLongURL(@PathVariable String shortURL, HttpServletResponse response) {
        String originalURL = shortURLConvertorService.getLongURL(shortURL);
        if (originalURL != null) {
            logger.info("original URL:"+originalURL);
            ModelAndView modelAndView = new ModelAndView(new RedirectView(originalURL));
            modelAndView.setStatus(HttpStatus.MOVED_PERMANENTLY);
            return modelAndView;
        } else {
            logger.info("original URL:"+originalURL);
            try {
                response.sendRedirect("/error.html");
            } catch (IOException e) {
                logger.error("Exception happen when send redirect", e);
            }
        }
        return null;
    }
}
