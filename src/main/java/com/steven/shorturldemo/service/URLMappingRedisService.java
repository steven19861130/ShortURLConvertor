package com.steven.shorturldemo.service;

import com.steven.shorturldemo.bean.EnvironmentProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import static com.steven.shorturldemo.util.Constants.*;

@Component(value = "urlMappingRedisService")
public class URLMappingRedisService {

    @Autowired
    private EnvironmentProperties redisProperties;

    Logger log = LoggerFactory.getLogger(this.getClass());

    private String setURLScript;

    private Jedis jedis;

    public URLMappingRedisService() {
    }

    private void initJedis() {
        jedis = new Jedis(redisProperties.getHost(), redisProperties.getPort(),2000,2000,true);
        log.info("redis auth: "+redisProperties.getPassword());
        jedis.auth(redisProperties.getPassword());
        jedis.connect();
    }

    public String getURL(String key) {
        try {
            initJedis();
            log.info("Get redis url:"+key);
            return jedis.get(key);
        } catch (Exception e) {
            log.error("Exception happen when get URL", e);
        } finally {
            jedis.disconnect();
            jedis = null;
        }
        return null;
    }

    public Object setShortURL(String shortURL, String longURL, String expire) {
        try {
            initJedis();
            if(expire.equals("-1")){
                jedis.set(shortURL,longURL);
                jedis.set(longURL,shortURL);
                return 2;
            }

            if (setURLScript == null) {
                setURLScript = jedis.scriptLoad(URL_SET_SCRIPT);
            }
            return jedis.evalsha(setURLScript, 3, shortURL, expire, longURL);
        } catch (Exception e) {
            log.error("Exception happen when set URL", e);
        } finally {
            jedis.disconnect();
            jedis = null;
        }
        return null;
    }

    public boolean containsLongURL(String longURL) {
        try {
            initJedis();
            return jedis.exists(longURL);
        } catch (Exception e) {
            log.error("Exception happen when set URL", e);
        } finally {
            jedis.disconnect();
            jedis = null;
        }
        return false;
    }

}
