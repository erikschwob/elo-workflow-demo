package de.erikschwob.elodemo.api;

import de.erikschwob.elodemo.model.WFNode;
import de.erikschwob.elodemo.model.WFTransition;
import java.time.LocalDateTime;
import java.util.List;

public record WFNodeResponse(
    Long id,
    Long sordId,
    String sordDescription,
    String status,
    String assignee,
    List<TransitionRecord> transitions,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public record TransitionRecord(
        String fromStatus, String toStatus,
        String performedBy, String comment,
        LocalDateTime transitionedAt
    ) {
        static TransitionRecord from(WFTransition t) {
            return new TransitionRecord(
                t.getFromStatus().name(), t.getToStatus().name(),
                t.getPerformedBy(), t.getComment(), t.getTransitionedAt()
            );
        }
    }

    public static WFNodeResponse from(WFNode n) {
        return new WFNodeResponse(
            n.getId(),
            n.getSord().getId(),
            n.getSord().getShortDescription(),
            n.getStatus().name(),
            n.getAssignee(),
            n.getTransitions().stream().map(TransitionRecord::from).toList(),
            n.getCreatedAt(),
            n.getUpdatedAt()
        );
    }
}
