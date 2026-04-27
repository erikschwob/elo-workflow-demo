package de.erikschwob.elodemo.repository;

import de.erikschwob.elodemo.model.SordAcl;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SordAclRepository extends JpaRepository<SordAcl, Long> {
    List<SordAcl> findBySordId(Long sordId);
    Optional<SordAcl> findBySordIdAndPrincipal(Long sordId, String principal);
}
