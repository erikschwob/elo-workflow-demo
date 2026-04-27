package de.erikschwob.elodemo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "sord_acl", uniqueConstraints = {
    @UniqueConstraint(name = "uq_sord_acl_principal", columnNames = {"sord_id", "principal"})
})
public class SordAcl {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sord_id", nullable = false)
    private Sord sord;

    @Column(nullable = false, length = 100)
    private String principal;

    @Column(name = "rights_mask", nullable = false)
    private int rightsMask;

    @Column(name = "inheritable", nullable = false)
    private boolean inheritable = true;

    protected SordAcl() {}

    public SordAcl(Sord sord, String principal, int rightsMask, boolean inheritable) {
        this.sord = sord;
        this.principal = principal;
        this.rightsMask = rightsMask;
        this.inheritable = inheritable;
    }

    public Long getId() { return id; }
    public Sord getSord() { return sord; }
    public String getPrincipal() { return principal; }
    public int getRightsMask() { return rightsMask; }
    public void setRightsMask(int rightsMask) { this.rightsMask = rightsMask; }
    public boolean isInheritable() { return inheritable; }
    public void setInheritable(boolean inheritable) { this.inheritable = inheritable; }
}
