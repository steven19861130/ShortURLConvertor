package com.steven.shorturldemo.util;

import java.math.BigInteger;

public class Constants {

    public static final String URL_SET_SCRIPT =
            "redis.call('setex', KEYS[1], KEYS[2], KEYS[3]);" +
                    "redis.call('setex', KEYS[3], KEYS[2],KEYS[1]);";

}
