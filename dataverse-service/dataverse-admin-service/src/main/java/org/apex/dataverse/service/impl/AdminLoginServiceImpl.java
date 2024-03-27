package org.apex.dataverse.service.impl;

import cn.hutool.jwt.JWT;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.apex.dataverse.entity.DatavsUser;
import org.apex.dataverse.exception.DtvsAdminException;
import org.apex.dataverse.param.UserLoginParam;
import org.apex.dataverse.service.IAdminLoginService;
import org.apex.dataverse.service.IDatavsUserService;
import org.apex.dataverse.utils.OAuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

/**
 * @ClassName: AdminLoginServiceImpl
 * @Author: wwd
 * @TODO:
 * @Date: 2024/3/20 10:46
 */
@Service
public class AdminLoginServiceImpl implements IAdminLoginService {

    @Autowired
    private IDatavsUserService datavsUserService;

    @Value("${security.encode.key:1234567812345678}")
    private String encodeKey;

    @Override
    public String userLogin(UserLoginParam userLoginParam) throws DtvsAdminException {
        validateUserLogin(userLoginParam);
        return UUID.randomUUID().toString();
    }

    private void validateUserLogin(UserLoginParam userLoginParam) throws DtvsAdminException {
        if (Objects.isNull(userLoginParam)) {
            throw new DtvsAdminException("参数为空");
        }
        if (StringUtils.isBlank(userLoginParam.getUsername())) {
            throw new DtvsAdminException("用户名为空");
        }
        if (StringUtils.isBlank(userLoginParam.getPassword())) {
            throw new DtvsAdminException("密码为空");
        }
        LambdaQueryWrapper<DatavsUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DatavsUser::getUserName, userLoginParam.getUsername());
        if (Objects.nonNull(userLoginParam.getLoginType())) {
            queryWrapper.eq(DatavsUser::getUserType, userLoginParam.getLoginType());
        }
        DatavsUser datavsUser = datavsUserService.getOne(queryWrapper);
        if (Objects.isNull(datavsUser)) {
            throw new DtvsAdminException("用户不存在");
        }
        String password = OAuthUtil.decryptPassword(encodeKey, userLoginParam.getPassword());
        if (!OAuthUtil.matchPassword(password, datavsUser.getPassword())) {
            throw new DtvsAdminException("输入密码错误");
        }

    }

    @Override
    public Boolean validateLoginToken(String token) {
        return null;
    }
}
