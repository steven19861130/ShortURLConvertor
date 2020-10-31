package com.steven.shorturldemo.service;

import com.steven.shorturldemo.bean.EnvironmentProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;


import static com.steven.shorturldemo.util.Constants.*;

@Component(value = "urlMappingRedisService")
public class URLMappingRedisService {

    private final EnvironmentProperties environmentProperties;

    Logger log = LoggerFactory.getLogger(this.getClass());

    private String setURLScript;

    private Jedis jedis;

    private JedisPool jedisPool;

    public URLMappingRedisService(EnvironmentProperties environmentProperties) {
        this.environmentProperties = environmentProperties;
        initJedisPool();
    }

    private void initJedisPool() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(10);
        jedisPoolConfig.setMaxIdle(5);
        if (environmentProperties.getPassword().equals("default")) {
            jedisPool = new JedisPool(jedisPoolConfig, environmentProperties.getHost(), environmentProperties.getPort(), 2000);
        } else {
            jedisPool = new JedisPool(jedisPoolConfig, environmentProperties.getHost(), environmentProperties.getPort(), 2000, environmentProperties.getPassword(), true);
        }
    }


    public String getURL(String key) {
        try {
            jedis = jedisPool.getResource();
            return jedis.get(key);
        } catch (Exception e) {
            if (jedis != null) {
                jedis.disconnect();
                jedis = null;
            }
            log.error("Exception happen when get URL", e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
            jedis = null;
        }
        return null;
    }

    public Object setShortURL(String shortURL, String longURL, String expire) {
        try {
            jedis = jedisPool.getResource();
            if (expire.equals("-1")) {
                jedis.set(shortURL, longURL);
                jedis.set(longURL, shortURL);
                return 2;
            }

            if (setURLScript == null) {
                setURLScript = jedis.scriptLoad(URL_SET_SCRIPT);
            }
            return jedis.evalsha(setURLScript, 3, shortURL, expire, longURL);
        } catch (Exception e) {
            if (jedis != null) {
                jedis.disconnect();
                jedis = null;
            }
            log.error("Exception happen when get URL", e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
            jedis = null;
        }
        return null;
    }

    public boolean containsLongURL(String longURL) {
        try {
            jedis = jedisPool.getResource();
            return jedis.exists(longURL);
        } catch (Exception e) {
            if (jedis != null) {
                jedis.disconnect();
                jedis = null;
            }
            log.error("Exception happen when get URL", e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
            jedis = null;
        }
        return false;
    }

}
