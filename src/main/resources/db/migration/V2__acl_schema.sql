-- Per-Sord access control list, mirroring ELOs bitmask rights model
CREATE TABLE sord_acl (
    id           BIGSERIAL PRIMARY KEY,
    sord_id      BIGINT NOT NULL REFERENCES sord(id) ON DELETE CASCADE,
    principal    VARCHAR(100) NOT NULL,
    rights_mask  INTEGER NOT NULL DEFAULT 0,
    inheritable  BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT uq_sord_acl_principal UNIQUE (sord_id, principal)
);

CREATE INDEX idx_sord_acl_sord ON sord_acl(sord_id);
