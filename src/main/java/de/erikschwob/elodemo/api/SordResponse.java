package de.erikschwob.elodemo.api;

import de.erikschwob.elodemo.model.Sord;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

public record SordResponse(
    Long id,
    String shortDescription,
    String maskName,
    Long parentId,
    String nodeType,
    Map<String, String> fields,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static SordResponse from(Sord s) {
        return new SordResponse(
            s.getId(),
            s.getShortDescription(),
            s.getMask() != null ? s.getMask().getName() : null,
            s.getParent() != null ? s.getParent().getId() : null,
            s.getNodeType().name(),
            s.getFields().stream().collect(Collectors.toMap(
                f -> f.getFieldName(), f -> f.getValue() != null ? f.getValue() : ""
            )),
            s.getCreatedAt(),
            s.getUpdatedAt()
        );
    }
}
