package com.xt.interview.dto;

import java.time.LocalDateTime;

public interface AccountPageView {

    Long getId();
    String getUsername();
    String getName();
    String getGender();
    String getPhone();
    Integer getRoleLevel();
    Integer getStatus();
    Integer getIsSuper();
    Long getDepartmentId();
    String getDepartmentName();
    LocalDateTime getCreateTime();
    LocalDateTime getUpdateTime();

    default AccountDTO toDTO() {
        AccountDTO dto = new AccountDTO();
        dto.setId(getId());
        dto.setUsername(getUsername());
        dto.setName(getName());
        dto.setGender(getGender());
        dto.setPhone(getPhone());
        dto.setRoleLevel(getRoleLevel());
        dto.setStatus(getStatus());
        dto.setIsSuper(getIsSuper());
        dto.setDepartmentId(getDepartmentId());
        dto.setDepartmentName(getDepartmentName());
        dto.setCreateTime(getCreateTime());
        dto.setUpdateTime(getUpdateTime());
        return dto;
    }
}
