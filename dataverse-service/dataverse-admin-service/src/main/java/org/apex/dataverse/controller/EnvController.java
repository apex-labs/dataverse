package org.apex.dataverse.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apex.dataverse.enums.EnvEnum;
import org.apex.dataverse.enums.EnvModelEnum;
import org.apex.dataverse.util.ResultVO;
import org.apex.dataverse.vo.EnumInfoVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: EnvController
 * @Author: wwd
 * @TODO:
 * @Date: 2024/1/18 13:31
 */
@RestController
@RequestMapping("/env")
@Api(tags = "环境相关接口")
public class EnvController {

    @GetMapping("/listModel")
    @ApiOperation("获取环境模式")
    public ResultVO<List<EnumInfoVO>> listModel() {
        List<EnumInfoVO> enumInfoVOList = new ArrayList<>();
        for (EnvModelEnum envModelEnum : EnvModelEnum.values()) {
            EnumInfoVO enumInfoVO = new EnumInfoVO();
            enumInfoVO.setValue(envModelEnum.getValue());
            enumInfoVO.setDesc(envModelEnum.getDesc());
            enumInfoVOList.add(enumInfoVO);
        }

        return ResultVO.success(enumInfoVOList);
    }

    @GetMapping("/listEnv")
    @ApiOperation("获取环境")
    public ResultVO<List<EnumInfoVO>> listEnv() {
        List<EnumInfoVO> enumInfoVOList = new ArrayList<>();
        for (EnvEnum envEnum : EnvEnum.values()) {
            EnumInfoVO enumInfoVO = new EnumInfoVO();
            enumInfoVO.setValue(envEnum.getValue());
            enumInfoVO.setDesc(envEnum.getDesc());
            enumInfoVOList.add(enumInfoVO);
        }

        return ResultVO.success(enumInfoVOList);
    }
}
