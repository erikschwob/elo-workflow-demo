package de.erikschwob.elodemo.api;

import jakarta.validation.constraints.Min;

public record GrantAclRequest(@Min(0) int rightsMask, boolean inheritable) {}
