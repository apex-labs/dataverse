package org.apex.dataverse.utils;

import org.apex.dataverse.model.UserInfo;

/**
 * 获取用户信息
 */
public class NexusUserInfoUtils {


    /**
     * 封装用户信息
     * @return
     */
    public static UserInfo getUserInfo() {
        UserInfo userInfo = new UserInfo(1L, "创略科技开源团队", 1L, "apex", 1L, "创略科技", "it@chinapex.com");
        return userInfo;
    }
}
