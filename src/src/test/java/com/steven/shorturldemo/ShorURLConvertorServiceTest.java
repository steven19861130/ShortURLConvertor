package com.steven.shorturldemo;

import com.steven.shorturldemo.service.ShortURLConvertorService;
import com.steven.shorturldemo.service.URLMappingRedisService;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.HashMap;
import java.util.Map;

import static com.steven.shorturldemo.util.TestConstants.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class ShorURLConvertorServiceTest {

    @Autowired
    private ShortURLConvertorService shortURLConvertorService;

    @MockBean
    private URLMappingRedisService urlMappingRedisService;


    private Map<String, String> urlMap = new HashMap<>();

    @BeforeEach
    public void initMockAll(){
        when(urlMappingRedisService.setShortURL(anyString(), anyString(), anyString())).thenAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                String shortURL = invocation.getArguments()[0].toString();
                String longURL = invocation.getArguments()[1].toString();
                urlMap.put(shortURL, longURL);
                urlMap.put(longURL, shortURL);
                return null;
            }
        });

        when(urlMappingRedisService.getURL(anyString())).thenAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                String longURL = invocation.getArguments()[0].toString();
                return urlMap.get(longURL);
            }
        });

        when(urlMappingRedisService.containsLongURL(anyString())).thenAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                String longURL = invocation.getArguments()[0].toString();
                return urlMap.containsKey(longURL);
            }
        });
    }

    @Test
    public void giveSameLongURLWhenConvertThenReturnSameShortURL() {
        String shortURLFirst = shortURLConvertorService.shortenURL(TEST_URL_HTTP_1, URL_EXPIRE_SECOND);
        String shortURLSecond = shortURLConvertorService.shortenURL(TEST_URL_HTTP_1, URL_EXPIRE_SECOND);
        Assert.assertEquals(shortURLFirst, shortURLSecond);
    }

    @Test
    public void giveDifferentLongURLWhenConvertThenReturnDifferentShortURL() {
        String shortURLFirst = shortURLConvertorService.shortenURL(TEST_URL_HTTP_2, URL_EXPIRE_SECOND);
        String shortURLSecond = shortURLConvertorService.shortenURL(TEST_URL_HTTP_1, URL_EXPIRE_SECOND);
        Assert.assertNotEquals(shortURLFirst, shortURLSecond);
    }

    @Test
    public void givenShortenURLWhenConvertThenReturnLongURL() {
        String shortURL = shortURLConvertorService.shortenURL(TEST_URL_HTTP_2, URL_EXPIRE_SECOND);
        String longURL = shortURLConvertorService.getLongURL(shortURL);
        Assert.assertEquals(TEST_URL_HTTP_2, longURL);
    }

    @Test
    public void givenInvalidURLWhenConvertThenReturnOriginalURL(){
        String shortURL = shortURLConvertorService.shortenURL(TEST_INVALID_URL, URL_EXPIRE_SECOND);
        Assert.assertEquals(TEST_INVALID_URL,TEST_INVALID_URL);

    }

}
