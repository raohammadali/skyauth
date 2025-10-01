package com.skynetauth.auth_service.utils;

import org.hashids.Hashids;
import org.springframework.stereotype.Component;

@Component
public class HashIdUtil {

    private final Hashids hashids;

    public HashIdUtil() {
        this.hashids = new Hashids("9VlqvY6RA4GJqqoDtV7RKhypGogOVB+v3BnpTYWOMglgFo3TWNOaJ5W9Zfw0YOeckqCdRoezuXTk+hqCLiZrHg==", 8);
    }

    public String encodeId(long id) {
        return hashids.encode(id);
    }

    public long decodeId(String hash) {
        long[] decoded = hashids.decode(hash);
        if (decoded.length == 0) {
            throw new IllegalArgumentException("Invalid hash ID");
        }
        return decoded[0];
    }
}

