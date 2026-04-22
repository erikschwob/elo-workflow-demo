package de.erikschwob.elodemo.api;

import de.erikschwob.elodemo.model.WFStatus;
import de.erikschwob.elodemo.service.WorkflowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Workflow", description = "Document workflow management")
public class WorkflowController {

    private final WorkflowService workflowService;

    public WorkflowController(WorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    @PostMapping("/sords/{sordId}/workflow")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Start a workflow for a document",
               description = "Initiates a new workflow at INCOMING status")
    public WFNodeResponse startWorkflow(
            @PathVariable Long sordId,
            @Valid @RequestBody StartWorkflowRequest request) {
        return WFNodeResponse.from(
            workflowService.startWorkflow(sordId, request.assignee())
        );
    }

    @PutMapping("/workflow/{nodeId}/transition")
    @Operation(summary = "Transition workflow to next status",
               description = "Allowed transitions: INCOMING→REVIEW, REVIEW→APPROVAL, REVIEW→INCOMING, APPROVAL→ARCHIVE, APPROVAL→REVIEW")
    public WFNodeResponse transition(
            @PathVariable Long nodeId,
            @Valid @RequestBody TransitionRequest request) {
        return WFNodeResponse.from(
            workflowService.transition(
                nodeId,
                request.targetStatus(),
                request.performedBy(),
                request.comment()
            )
        );
    }

    @GetMapping("/workflow/{nodeId}")
    @Operation(summary = "Get workflow status and history")
    public WFNodeResponse getWorkflow(@PathVariable Long nodeId) {
        return WFNodeResponse.from(workflowService.getById(nodeId));
    }

    @GetMapping("/sords/{sordId}/workflow")
    @Operation(summary = "Get workflow for a specific document")
    public WFNodeResponse getWorkflowBySord(@PathVariable Long sordId) {
        return WFNodeResponse.from(workflowService.getBySordId(sordId));
    }

    @GetMapping("/workflow")
    @Operation(summary = "List workflows by status or assignee")
    public List<WFNodeResponse> list(
            @RequestParam(required = false) WFStatus status,
            @RequestParam(required = false) String assignee) {
        if (status != null) {
            return workflowService.getByStatus(status).stream()
                .map(WFNodeResponse::from).toList();
        }
        if (assignee != null) {
            return workflowService.getByAssignee(assignee).stream()
                .map(WFNodeResponse::from).toList();
        }
        return workflowService.getByStatus(WFStatus.INCOMING).stream()
            .map(WFNodeResponse::from).toList();
    }
}
