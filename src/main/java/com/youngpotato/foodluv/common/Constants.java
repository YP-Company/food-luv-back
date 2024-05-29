package com.youngpotato.foodluv.common;

public final class Constants {

    private Constants() {
        throw new UnsupportedOperationException("Constants should not be instantiated");
    }

    public static final String SYSTEM_STRING = "SYSTEM";

    public static final String JWT_HEADER_STRING = "Authorization";
    public static final String JWT_TOKEN_PREFIX = "Bearer";
    public static final String JWT_AUTHORITIES_KEY = "auth";
}
