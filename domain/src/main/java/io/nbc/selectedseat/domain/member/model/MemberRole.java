package io.nbc.selectedseat.domain.member.model;

public enum MemberRole {

    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN");

    private String authority;

    MemberRole(String authority) {
        this.authority = authority;
    }

    public String getAuthority() {
        return this.authority;
    }
}
