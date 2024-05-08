package ru.edu.springdata.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@NotNull
public class AuthorDto {

    @NotBlank(message = "Author first name is required")
    private String firstName;

    @NotBlank(message = "Author last name is required")
    private String lastName;

    private String phone;

    @NotNull
    private AddressDto address;
}
