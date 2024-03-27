package org.apex.dataverse.model;

import lombok.Data;


@Data
public class UserInfo {

    private UserInfo() {
    }

    public UserInfo(Long userId, String userName, Long tenantId, String tenantName, Long deptId, String deptName, String email) {
        this.userId = userId;
        this.userName = userName;
        this.tenantId = tenantId;
        this.tenantName = tenantName;
        this.deptId = deptId;
        this.deptName = deptName;
        this.email = email;
    }

    public UserInfo(Long userId, String userName, Long tenantId, String tenantName, Long deptId, String deptName, String email, Isolation isolation) {
        this.userId = userId;
        this.userName = userName;
        this.tenantId = tenantId;
        this.tenantName = tenantName;
        this.deptId = deptId;
        this.deptName = deptName;
        this.email = email;
        this.isolation = isolation;
    }

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 租户code
     */
    private String tenantName;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 隔离信息
     */
    private Isolation isolation;
}
