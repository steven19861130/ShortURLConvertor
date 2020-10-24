package com.steven.shorturldemo.service;

import com.steven.shorturldemo.bean.RedisProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import static com.steven.shorturldemo.util.Constants.*;

@Component(value = "urlMappingRedisService")
public class URLMappingRedisService {

    @Autowired
    private RedisProperties redisProperties;

    Logger log = LoggerFactory.getLogger(this.getClass());

    private String setURLScript;

    private Jedis jedis;

    public URLMappingRedisService() {
    }

    private void initJedis() {
        jedis = new Jedis(redisProperties.getHost(), redisProperties.getPort());
        jedis.auth(redisProperties.getPassword());
        jedis.connect();
    }

    public String getURL(String key) {
        try {
            initJedis();
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

    //
//    public int getIncrCount() {
//        try {
//            String incrCount = jedis.get(INCR_COUNT_KEY);
//            if(incrCount == null){
//                jedis.set(INCR_COUNT_KEY,String.valueOf(1));
//                return 1;
//            }
//            return Integer.parseInt(incrCount);
//        } catch (NumberFormatException nfe) {
//            log.error("Exception happen when get Increment Count", nfe);
//        } finally {
//            jedis.disconnect();
//        }
//        return -1;
//    }
}
