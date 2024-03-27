package org.apex.dataverse.service;

import org.apex.dataverse.exception.DtvsAdminException;
import org.apex.dataverse.param.UserLoginParam;

public interface IAdminLoginService {

    /**
     * 验证用户登录
     * @param userLoginParam
     * @return
     */
    String userLogin(UserLoginParam userLoginParam) throws DtvsAdminException;

    /**
     * 校验token是否有效
     * @param token
     * @return
     */
    Boolean validateLoginToken(String token);
}
