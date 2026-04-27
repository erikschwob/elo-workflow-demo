package de.erikschwob.elodemo.model;

public enum AclRight {
    READ(1),
    WRITE(2),
    DELETE(4),
    LIST(8),
    EDIT(16),
    PROCESS(32);

    private final int bit;

    AclRight(int bit) { this.bit = bit; }

    public int bit() { return bit; }

    public boolean isGranted(int mask) { return (mask & bit) != 0; }
}
