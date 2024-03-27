package org.apex.dataverse.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author danny
 * @since 2024-01-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Datasource implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 数据源实例ID
     */
    @TableId(value = "datasource_id", type = IdType.AUTO)
    private Long datasourceId;

    /**
     * 所属数据空间编码，一个数据空间编码对应一个（Basic模式）或两个Dvs(DEV-PROD模式)，且编码相同
     */
    private String dvsCode;

    /**
     * 数据源父编码，同一编码在不同环境（DEV/PROD)中相同
     */
    private String datasourceCode;

    /**
     * 数据源类型ID
     */
    private Integer datasourceTypeId;

    /**
     * 数据源类型名称
     */
    private String datasourceTypeName;

    /**
     * 实例名称，命名规则可为datasource_name + env（BASIC，DEV，TEST，PROD）
     */
    private String datasourceName;

    /**
     * 数据源简称，缩写名称，字母数字下划线，非数字开头
     */
    private String datasourceAbbr;

    /**
     * 数据源读写权限，1：只读，2：读写
     */
    private Integer datasourceReadWrite;

    /**
     * 环境，0：BASIC、1：DEV、2：PROD
     */
    private Integer env;

    /**
     * 数据源链接类型，不同链接类型的配置在不同的表中，1：JDBC数据源，2：kafka数据源
     */
    private Integer connType;

    /**
     * 是否删除：0否，1：是
     */
    private Integer isDeleted;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建人所属租户ID
     */
    private Long tenantId;

    /**
     * 创建人所属租户名称【冗余】
     */
    private String tenantName;

    /**
     * 创建人所属部门ID
     */
    private Long deptId;

    /**
     * 创建人所属部门名称【冗余】
     */
    private String deptName;

    /**
     * 创建人ID
     */
    private Long userId;

    /**
     * 创建人名称【冗余】
     */
    private String userName;


}
