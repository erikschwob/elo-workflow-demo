package de.erikschwob.elodemo.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Map;

public record CreateSordRequest(
    @NotBlank @Size(max = 255) String shortDescription,
    Long maskId,
    Long parentId,
    Map<String, String> fields
) {}
