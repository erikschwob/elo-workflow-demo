package de.erikschwob.elodemo.service;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(String principal, String right, Long sordId) {
        super("Principal '" + principal + "' lacks " + right + " on sord " + sordId);
    }
}
