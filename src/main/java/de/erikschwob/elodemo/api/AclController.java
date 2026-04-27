package de.erikschwob.elodemo.api;

import de.erikschwob.elodemo.model.AclRight;
import de.erikschwob.elodemo.service.AclService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sords/{sordId}/acl")
@Tag(name = "ACL", description = "Per-Sord access control list management")
public class AclController {

    private final AclService aclService;

    public AclController(AclService aclService) {
        this.aclService = aclService;
    }

    @GetMapping
    @Operation(summary = "List all ACL entries for a Sord")
    public List<AclEntryResponse> list(@PathVariable Long sordId) {
        return aclService.listAcl(sordId).stream().map(AclEntryResponse::from).toList();
    }

    @PutMapping("/{principal}")
    @Operation(summary = "Grant or update rights for a principal on a Sord")
    public AclEntryResponse grant(
            @PathVariable Long sordId,
            @PathVariable String principal,
            @Valid @RequestBody GrantAclRequest request) {
        return AclEntryResponse.from(
            aclService.grant(sordId, principal, request.rightsMask(), request.inheritable())
        );
    }

    @DeleteMapping("/{principal}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Revoke all rights for a principal on a Sord")
    public void revoke(@PathVariable Long sordId, @PathVariable String principal) {
        aclService.revoke(sordId, principal);
    }

    @GetMapping("/check")
    @Operation(summary = "Check if a principal has a specific right (includes inherited)")
    public EffectiveRightsResponse check(
            @PathVariable Long sordId,
            @RequestParam String principal,
            @RequestParam AclRight right) {
        boolean granted = aclService.hasRight(sordId, principal, right);
        return new EffectiveRightsResponse(principal, right.name(), granted);
    }
}
