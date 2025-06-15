package org.example.core.util;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class EncodingUtils {
    public static String encodeChinese(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));

    }

    public static String decodeChinese(String encodedValue) {
        return URLDecoder.decode(
            new String(Base64.getDecoder().decode(encodedValue), StandardCharsets.UTF_8)
        );
    }
}