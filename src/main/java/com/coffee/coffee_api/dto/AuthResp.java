package com.coffee.coffee_api.dto;

public record AuthResp(
    boolean ok, String token, Long id, String fullName, String email, String role
) {}
