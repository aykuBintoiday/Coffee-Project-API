package com.coffee.coffee_api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterReq(
    @NotBlank String fullName,
    @Email @NotBlank String email,
    @Size(min = 8) String password
) {}
