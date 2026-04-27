package de.erikschwob.elodemo.api;

import de.erikschwob.elodemo.model.AclRight;
import de.erikschwob.elodemo.model.SordAcl;

import java.util.Arrays;
import java.util.List;

public record AclEntryResponse(
    Long id,
    String principal,
    int rightsMask,
    List<String> rights,
    boolean inheritable
) {
    public static AclEntryResponse from(SordAcl acl) {
        List<String> granted = Arrays.stream(AclRight.values())
            .filter(r -> r.isGranted(acl.getRightsMask()))
            .map(Enum::name)
            .toList();
        return new AclEntryResponse(
            acl.getId(),
            acl.getPrincipal(),
            acl.getRightsMask(),
            granted,
            acl.isInheritable()
        );
    }
}
