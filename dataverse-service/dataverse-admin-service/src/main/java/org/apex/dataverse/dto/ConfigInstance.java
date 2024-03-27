package org.apex.dataverse.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>项目名称: me-router
 *
 * <p>文件名称:
 *
 * <p>描述:
 *
 * <p>创建时间: 2021/6/9 4:52 PM
 *
 * <p>公司信息: 创略科技
 *
 * @author Danny.Huo <br>
 * @version v1.0 <br>
 * @date 2021/6/9 4:52 PM <br>
 * @update [序号][日期YYYY-MM-DD] [更改人姓名][变更描述] <br>
 */
@Data
@ApiModel(value = "配置实例")
public class ConfigInstance implements Serializable {

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "配置ID")
    private String confId;

    @ApiModelProperty(value = "配置属性字段")
    private String confPropField;

    @ApiModelProperty(value = "配置属性名称")
    private String confPropName;

    @ApiModelProperty(value = "配置值")
    private String confValue;

    public ConfigInstance() {
    }

    public ConfigInstance(Long id, String confId, String confPropField, String confPropName, String confValue) {
        this.id = id;
        this.confId = confId;
        this.confPropField = confPropField;
        this.confPropName = confPropName;
        this.confValue = confValue;
    }
}
