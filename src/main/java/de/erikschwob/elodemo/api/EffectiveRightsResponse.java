package de.erikschwob.elodemo.api;

public record EffectiveRightsResponse(String principal, String right, boolean granted) {}
