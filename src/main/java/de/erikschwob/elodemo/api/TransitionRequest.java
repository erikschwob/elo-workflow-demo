package de.erikschwob.elodemo.api;

import de.erikschwob.elodemo.model.WFStatus;
import jakarta.validation.constraints.NotNull;

public record TransitionRequest(
    @NotNull WFStatus targetStatus,
    String performedBy,
    String comment
) {}
