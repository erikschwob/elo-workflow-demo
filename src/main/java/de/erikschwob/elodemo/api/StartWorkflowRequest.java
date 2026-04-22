package de.erikschwob.elodemo.api;

import jakarta.validation.constraints.NotBlank;

public record StartWorkflowRequest(
    @NotBlank String assignee
) {}
