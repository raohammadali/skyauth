package com.skynetauth.auth_service.utils;

import org.hashids.Hashids;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HashIdUtil {

    private Hashids hashids;
    private static final Logger logger = LoggerFactory.getLogger(HashIdUtil.class);

    @Value("${hashid.salt}")
    private String salt;

    @Value("${hashid.min-length}")
    private int minLength;

    // public HashIdUtil() {
    //     logger.info(salt);
    //     this.hashids = new Hashids(salt, minLength);
    // }

    public String encodeId(long id) {
        if (hashids == null) {
            hashids = new Hashids(salt, minLength);
            logger.info(salt);
        }
        return hashids.encode(id);
    }

    public long decodeId(String hash) {
        if (hashids == null) {
            hashids = new Hashids(salt, minLength);
            logger.info(salt);
        }
        long[] decoded = hashids.decode(hash);
        if (decoded.length == 0) {
            throw new IllegalArgumentException("Invalid hash ID");
        }
        return decoded[0];
    }
}

