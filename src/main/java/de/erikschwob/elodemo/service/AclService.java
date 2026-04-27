package de.erikschwob.elodemo.service;

import de.erikschwob.elodemo.model.*;
import de.erikschwob.elodemo.repository.SordAclRepository;
import de.erikschwob.elodemo.repository.SordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional
public class AclService {

    private final SordAclRepository aclRepository;
    private final SordRepository sordRepository;

    public AclService(SordAclRepository aclRepository, SordRepository sordRepository) {
        this.aclRepository = aclRepository;
        this.sordRepository = sordRepository;
    }

    public SordAcl grant(Long sordId, String principal, int rightsMask, boolean inheritable) {
        Sord sord = sordRepository.findById(sordId)
            .orElseThrow(() -> new NoSuchElementException("Sord not found: " + sordId));
        Optional<SordAcl> existing = aclRepository.findBySordIdAndPrincipal(sordId, principal);
        if (existing.isPresent()) {
            SordAcl acl = existing.get();
            acl.setRightsMask(rightsMask);
            acl.setInheritable(inheritable);
            return acl;
        }
        return aclRepository.save(new SordAcl(sord, principal, rightsMask, inheritable));
    }

    public void revoke(Long sordId, String principal) {
        aclRepository.findBySordIdAndPrincipal(sordId, principal)
            .ifPresent(aclRepository::delete);
    }

    @Transactional(readOnly = true)
    public List<SordAcl> listAcl(Long sordId) {
        return aclRepository.findBySordId(sordId);
    }

    @Transactional(readOnly = true)
    public boolean hasRight(Long sordId, String principal, AclRight right) {
        int mask = resolveEffectiveMask(sordId, principal);
        return right.isGranted(mask);
    }

    @Transactional(readOnly = true)
    public void checkRight(Long sordId, String principal, AclRight right) {
        if (!hasRight(sordId, principal, right)) {
            throw new AccessDeniedException(principal, right.name(), sordId);
        }
    }

    private int resolveEffectiveMask(Long sordId, String principal) {
        Optional<SordAcl> direct = aclRepository.findBySordIdAndPrincipal(sordId, principal);
        if (direct.isPresent()) {
            return direct.get().getRightsMask();
        }
        Sord sord = sordRepository.findById(sordId).orElse(null);
        if (sord == null || sord.getParent() == null) {
            return 0;
        }
        Long parentId = sord.getParent().getId();
        Optional<SordAcl> parentAcl = aclRepository.findBySordIdAndPrincipal(parentId, principal);
        if (parentAcl.isPresent()) {
            return parentAcl.get().isInheritable() ? parentAcl.get().getRightsMask() : 0;
        }
        return resolveEffectiveMask(parentId, principal);
    }
}
