package com.xt.interview.dto;

public class AccountPageQuery {

    private String username;
    private Integer status;
    private Integer roleLevel;
    private Integer page = 0;
    private Integer size = 10;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Integer getRoleLevel() { return roleLevel; }
    public void setRoleLevel(Integer roleLevel) { this.roleLevel = roleLevel; }

    public Integer getPage() { return page == null ? 0 : page; }
    public void setPage(Integer page) { this.page = page; }

    public Integer getSize() { return size == null ? 10 : size; }
    public void setSize(Integer size) { this.size = size; }

    public String getUsernamePattern() {
        if (username == null || username.isBlank()) return null;
        return "%" + username.trim().replace("!", "!!").replace("%", "!%").replace("_", "!_") + "%";
    }
}
