package ru.edu.springdata.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddressDto {

    @NotBlank(message = "City in address is required")
    private String city;

    @NotBlank(message = "Street in address is required")
    private String street;
}
