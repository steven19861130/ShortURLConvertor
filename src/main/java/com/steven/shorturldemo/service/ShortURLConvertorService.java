package com.steven.shorturldemo.service;

import org.apache.commons.validator.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component(value = "shortURLConvertorService")
public class ShortURLConvertorService {

    // storage for generated keys
    private String domain; // Use this attribute to generate urls for a custom
    private char myChars[]; // This array is used for character to number
    private Random myRand; // Random object used to generate random integers
    private int shortURLLength; // the key length in URL defaults to 8

    @Autowired
    URLMappingRedisService urlMappingRedisService;

    //URL Validator
    UrlValidator urlValidator;

    // Default Constructor
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
        domain = "http://localhost:8080/rest";
        urlValidator = new UrlValidator();
    }

    public String shortenURL(String longURL, String expire) {
        StringBuilder shortURL = new StringBuilder();
        String unfiedLongURL = unifyURL(longURL);
        if (urlValidator.isValid(unfiedLongURL)) {
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

    // sanitizeURL
    // This method should take care various issues with a valid url
    // e.g. www.google.com,www.google.com/, http://www.google.com,
    // http://www.google.com/
    // all the above URL should point to same shortened URL
    // There could be several other cases like these.
    String sanitizeURL(String url) {
        if (url.substring(0, 7).equals("http://"))
            url = url.substring(7);

        if (url.substring(0, 8).equals("https://"))
            url = url.substring(8);

        if (url.charAt(url.length() - 1) == '/')
            url = url.substring(0, url.length() - 1);
        return url;
    }

    private String getShortURL(String longURL, String expire) {
        String shortURL;
        shortURL = generateShortURL();
        urlMappingRedisService.setShortURL(shortURL, longURL, expire);
        return shortURL;
    }

    private String generateShortURL() {
        String shortURL = "";
        for (int i = 0; i <= shortURLLength; i++) {
            shortURL += myChars[myRand.nextInt(62)];
        }
        return shortURL;
    }

}
