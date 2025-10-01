package com.skynetauth.auth_service.Enum;

public enum CustomHttpStatus {
    S_LOGIN(200),
    S_SIGNUP(201),
    S_LOGOUT(202),
    S_DIST(203),
    S_ROLE(204),
    S_UPDATE(205),
    S_FETCH_U(206),

    E_INVALID_INPUT(400),
    E_UNAUTHORIZED(401),
    E_INVALID_ROLE_ID(402),
    E_INVALID_PERMISSION_ID(403),
    E_U_NOT_FOUND(404),
    E_INVALID_DISTRIBUTION_ID(405),
    E_INVALID_USER_ID(406),
    E_USERNAME_NOT_FOUND(407),
    E_INVALID_TOKEN(408),
    F_LOGIN(409),

    SERVER_ERROR(500),
    EMAIL_ALR_USED(502);

    private final int value;

    CustomHttpStatus(final int newValue) {
        value = newValue;
    }

    public int value() {
        return value;
    }
}
