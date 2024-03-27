package org.apex.dataverse.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author danny
 * @since 2023-05-15
 */
@Data
public class DvsParentVO {

    @ApiModelProperty("数据空间父ID")
    private Long parentId;

    @ApiModelProperty("Dvs父编码，同parent下Dvs在多个环境(dev/prod)下Dvs_code相同。")
    private String dvsCode;

    @ApiModelProperty("数据空间名称，英文名称")
    private String parentName;

    @ApiModelProperty("数据空间别名，中文/显示名称")
    private String parentAlias;

    @ApiModelProperty("数据空间缩写，英文简称")
    private String parentAbbr;

    @ApiModelProperty("环境模式，1：BASIC、2：DEV-PROD")
    private Integer envMode;

    @ApiModelProperty("是否删除：0否，1：是")
    private Integer isDeleted;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

    @ApiModelProperty("创建人所属租户ID")
    private Long tenantId;

    @ApiModelProperty("创建人所属租户名称【冗余】")
    private String tenantName;

    @ApiModelProperty("创建人所属部门ID")
    private Long deptId;

    @ApiModelProperty("创建人所属部门名称【冗余】")
    private String deptName;

    @ApiModelProperty("创建人ID")
    private Long userId;

    @ApiModelProperty("创建人名称【冗余】")
    private String userName;

    @ApiModelProperty("不同环境下数据空间详情")
    private List<DvsVO> DvsVOList;

    @ApiModelProperty("数据空间下成员列表")
    private List<DvsMemberVO> DvsMemberVOList;

    @ApiModelProperty("数据空间 集成数量 开发数量 表数量展示")
    private DvsDetailCountVO DvsDetailCountVO;

    @ApiModelProperty("是否多存储区，0：单存储区，1：多存储区")
    private Integer multStorage;

    @ApiModelProperty("存储区盒子列表")
    private List<StorageBoxVO> storageBoxVOList;

}
