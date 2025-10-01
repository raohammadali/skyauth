package com.skynetauth.auth_service.utils;

import org.hashids.Hashids;
import org.springframework.stereotype.Component;

@Component
public class HashIdUtil {

    private final Hashids hashids;

    /**
     * Creates a HashIdUtil and initializes its internal Hashids instance with a fixed salt
     * and a minimum hash length of 8.
     */
    public HashIdUtil() {
        this.hashids = new Hashids("9VlqvY6RA4GJqqoDtV7RKhypGogOVB+v3BnpTYWOMglgFo3TWNOaJ5W9Zfw0YOeckqCdRoezuXTk+hqCLiZrHg==", 8);
    }

    /**
     * Encodes a numeric identifier into a Hashids string.
     *
     * @param id the numeric identifier to encode
     * @return the encoded hash string representing the given identifier
     */
    public String encodeId(long id) {
        return hashids.encode(id);
    }

    /**
     * Decode an encoded hash string to its original numeric ID.
     *
     * @param hash the encoded hash string produced by Hashids
     * @return the decoded numeric ID
     * @throws IllegalArgumentException if the hash cannot be decoded
     */
    public long decodeId(String hash) {
        long[] decoded = hashids.decode(hash);
        if (decoded.length == 0) {
            throw new IllegalArgumentException("Invalid hash ID");
        }
        return decoded[0];
    }
}

