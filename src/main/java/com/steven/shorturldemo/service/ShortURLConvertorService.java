package com.steven.shorturldemo.service;

import com.steven.shorturldemo.bean.EnvironmentProperties;
import org.apache.commons.validator.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component(value = "shortURLConvertorService")
public class ShortURLConvertorService {

    // storage for generated keys
    private char myChars[];
    private Random myRand;
    private int shortURLLength;

    @Autowired
    URLMappingRedisService urlMappingRedisService;

    @Autowired
    EnvironmentProperties environmentProperties;

    UrlValidator urlValidator;

    public ShortURLConvertorService() {
        myRand = new Random();
        shortURLLength = 8;
        myChars = new char[62];
        for (int i = 0; i < 62; i++) {
            int j = 0;
            if (i < 10) {
                j = i + 48;
            } else if (i > 9 && i <= 35) {
                j = i + 55;
            } else {
                j = i + 61;
            }
            myChars[i] = (char) j;
        }
        urlValidator = new UrlValidator();
    }

    public String shortenURL(String longURL, String expire) {
        StringBuilder shortURL = new StringBuilder();
        String unfiedLongURL = unifyURL(longURL);
        if (urlValidator.isValid(unfiedLongURL)) {
            String domain = "http://"+environmentProperties.getDomain()+"/rest";
            if (urlMappingRedisService.containsLongURL(unfiedLongURL)) {
                shortURL.append(domain).append("/").append(urlMappingRedisService.getURL(unfiedLongURL));
            } else {
                shortURL.append(domain).append("/").append(getShortURL(unfiedLongURL,expire));
            }
        }
        // add http part
        return shortURL.toString();
    }

    private String unifyURL(String longURL) {
        if(longURL.startsWith("http://") || longURL.startsWith("https://")){
            return longURL;
        }else{
            return "http://"+longURL;
        }
    }

    public String getLongURL(String shortURL) {
        String generatedShortURL = shortURL.substring(shortURL.lastIndexOf('/')+1, shortURL.length());
        return urlMappingRedisService.getURL(generatedShortURL);

    }

    private String getShortURL(String longURL, String expire) {
        String shortURL;
        shortURL = generateShortURL();
        urlMappingRedisService.setShortURL(shortURL, longURL, expire);
        return shortURL;
    }

    /**
     * Using a-z/A-Z/0-9 construct 62 digits as char array to random generate short URL satisfy
     * config length. ex www.google.com -> f7FG9Nbo
     * Assume random algorithm make each char in this array pick up even, then each character is
     * 1/62 possibility. We set 8 for URL length then duplicate probability will be (1/62)^8 =
     * 5*(1/10^15) and it can be treated as IMPOSSIBLE.
     * @return
     */
    private String generateShortURL() {
        String shortURL = "";
        for (int i = 0; i <= shortURLLength; i++) {
            shortURL += myChars[myRand.nextInt(62)];
        }
        return shortURL;
    }

}
