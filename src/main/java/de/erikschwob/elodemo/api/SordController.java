package de.erikschwob.elodemo.api;

import de.erikschwob.elodemo.service.SordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sords")
@Tag(name = "Sords", description = "Document and folder management")
public class SordController {

    private final SordService sordService;

    public SordController(SordService sordService) {
        this.sordService = sordService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new document",
               description = "Creates a Sord (document object) with optional metadata fields")
    public SordResponse create(@Valid @RequestBody CreateSordRequest request) {
        return SordResponse.from(
            sordService.createDocument(
                request.shortDescription(),
                request.maskId(),
                request.parentId(),
                request.fields()
            )
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a document by ID")
    public SordResponse getById(@PathVariable Long id) {
        return SordResponse.from(sordService.getById(id));
    }

    @GetMapping
    @Operation(summary = "List root-level documents and folders")
    public List<SordResponse> getRoots() {
        return sordService.getRootNodes().stream().map(SordResponse::from).toList();
    }

    @GetMapping("/{id}/children")
    @Operation(summary = "List children of a folder")
    public List<SordResponse> getChildren(@PathVariable Long id) {
        return sordService.getChildren(id).stream().map(SordResponse::from).toList();
    }

    @GetMapping("/search")
    @Operation(summary = "Search documents by description")
    public List<SordResponse> search(@RequestParam String q) {
        return sordService.search(q).stream().map(SordResponse::from).toList();
    }
}
