package de.erikschwob.elodemo.service;

import de.erikschwob.elodemo.model.AclRight;
import de.erikschwob.elodemo.model.NodeType;
import de.erikschwob.elodemo.model.Sord;
import de.erikschwob.elodemo.repository.SordRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AclServiceTest {

    @Autowired AclService aclService;
    @Autowired SordRepository sordRepository;

    @Test
    @DisplayName("grant gives the principal the requested right")
    void grant_givesRight() {
        Sord sord = sordRepository.save(new Sord("Test doc", null, NodeType.DOCUMENT));
        aclService.grant(sord.getId(), "user:alice", AclRight.READ.bit(), true);

        assertThat(aclService.hasRight(sord.getId(), "user:alice", AclRight.READ)).isTrue();
        assertThat(aclService.hasRight(sord.getId(), "user:alice", AclRight.DELETE)).isFalse();
    }

    @Test
    @DisplayName("combined bitmask grants multiple rights at once")
    void grant_combinedMask() {
        Sord sord = sordRepository.save(new Sord("Test doc", null, NodeType.DOCUMENT));
        int mask = AclRight.READ.bit() | AclRight.LIST.bit() | AclRight.EDIT.bit();
        aclService.grant(sord.getId(), "user:alice", mask, true);

        assertThat(aclService.hasRight(sord.getId(), "user:alice", AclRight.READ)).isTrue();
        assertThat(aclService.hasRight(sord.getId(), "user:alice", AclRight.LIST)).isTrue();
        assertThat(aclService.hasRight(sord.getId(), "user:alice", AclRight.EDIT)).isTrue();
        assertThat(aclService.hasRight(sord.getId(), "user:alice", AclRight.WRITE)).isFalse();
    }

    @Test
    @DisplayName("revoke removes all rights for the principal")
    void revoke_removesRight() {
        Sord sord = sordRepository.save(new Sord("Test doc", null, NodeType.DOCUMENT));
        aclService.grant(sord.getId(), "user:bob", AclRight.WRITE.bit(), true);
        aclService.revoke(sord.getId(), "user:bob");

        assertThat(aclService.hasRight(sord.getId(), "user:bob", AclRight.WRITE)).isFalse();
    }

    @Test
    @DisplayName("checkRight throws AccessDeniedException when right is missing")
    void checkRight_throwsWhenDenied() {
        Sord sord = sordRepository.save(new Sord("Restricted", null, NodeType.DOCUMENT));

        assertThatThrownBy(() -> aclService.checkRight(sord.getId(), "user:eve", AclRight.READ))
            .isInstanceOf(AccessDeniedException.class)
            .hasMessageContaining("user:eve")
            .hasMessageContaining("READ");
    }

    @Test
    @DisplayName("inherited ACL propagates from parent folder to child document")
    void grant_inheritedFromParent() {
        Sord folder = sordRepository.save(new Sord("Folder", null, NodeType.FOLDER));
        Sord child = new Sord("Child doc", null, NodeType.DOCUMENT);
        child.setParent(folder);
        child = sordRepository.save(child);

        aclService.grant(folder.getId(), "user:carol", AclRight.READ.bit() | AclRight.LIST.bit(), true);

        assertThat(aclService.hasRight(child.getId(), "user:carol", AclRight.READ)).isTrue();
        assertThat(aclService.hasRight(child.getId(), "user:carol", AclRight.LIST)).isTrue();
        assertThat(aclService.hasRight(child.getId(), "user:carol", AclRight.DELETE)).isFalse();
    }

    @Test
    @DisplayName("non-inheritable ACL does not propagate to children")
    void grant_nonInheritableStopsAtParent() {
        Sord folder = sordRepository.save(new Sord("Folder", null, NodeType.FOLDER));
        Sord child = new Sord("Child doc", null, NodeType.DOCUMENT);
        child.setParent(folder);
        child = sordRepository.save(child);

        aclService.grant(folder.getId(), "user:dave", AclRight.READ.bit(), false);

        assertThat(aclService.hasRight(child.getId(), "user:dave", AclRight.READ)).isFalse();
    }

    @Test
    @DisplayName("direct ACL on child overrides inherited parent ACL")
    void grant_directOverridesInherited() {
        Sord folder = sordRepository.save(new Sord("Folder", null, NodeType.FOLDER));
        Sord child = new Sord("Child doc", null, NodeType.DOCUMENT);
        child.setParent(folder);
        child = sordRepository.save(child);

        aclService.grant(folder.getId(), "user:frank", AclRight.READ.bit() | AclRight.WRITE.bit(), true);
        aclService.grant(child.getId(), "user:frank", AclRight.READ.bit(), true);

        assertThat(aclService.hasRight(child.getId(), "user:frank", AclRight.READ)).isTrue();
        assertThat(aclService.hasRight(child.getId(), "user:frank", AclRight.WRITE)).isFalse();
    }

    @Test
    @DisplayName("updating an existing ACL entry replaces the rights mask")
    void grant_updateExistingEntry() {
        Sord sord = sordRepository.save(new Sord("Test doc", null, NodeType.DOCUMENT));
        aclService.grant(sord.getId(), "user:grace", AclRight.READ.bit(), true);
        aclService.grant(sord.getId(), "user:grace", AclRight.READ.bit() | AclRight.WRITE.bit(), true);

        assertThat(aclService.hasRight(sord.getId(), "user:grace", AclRight.WRITE)).isTrue();
    }

    @Test
    @DisplayName("listAcl returns all entries for the sord")
    void listAcl_returnsAllEntries() {
        Sord sord = sordRepository.save(new Sord("Shared doc", null, NodeType.DOCUMENT));
        aclService.grant(sord.getId(), "user:alice", AclRight.READ.bit(), true);
        aclService.grant(sord.getId(), "group:finance", AclRight.READ.bit() | AclRight.WRITE.bit(), false);

        assertThat(aclService.listAcl(sord.getId())).hasSize(2);
    }
}
