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
 * 数据抽取转换加载作业，Extract Transform and Load
 * </p>
 *
 * @author danny
 * @since 2024-01-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class EtlJob implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ETL作业ID
     */
    @TableId(value = "etl_job_id", type = IdType.AUTO)
    private Long etlJobId;

    /**
     * ETL作业编码，同一作业在不同环境下etl_job_code相同
     */
    private String etlJobCode;

    /**
     * 数据空间编码，一个数据空间编码对应一个（Basic模式）或两个Dvs(DEV-PROD模式)，且编码相同
     */
    private String dvsCode;

    /**
     * 数据域编码
     */
    private String dataRegionCode;

    /**
     * ETL作业名称
     */
    private String etlJobName;

    /**
     * Etl作业分组编码
     */
    private String etlGroupCode;

    /**
     * 数据源父编码，同一编码在不同环境（DEV/PROD)中相同
     */
    private String datasourceCode;

    /**
     * 环境，0：BASIC、1：DEV、2：PROD
     */
    private Integer env;

    /**
     * ETL作业描述
     */
    private String description;

    /**
     * 定时规则，cron表达式
     */
    private String cron;

    /**
     * 作业生命周期/状态
     * 作业开发中（草稿）：11
     * 作业开发完成：12
     * 作业测试中：21
     * 作业测试完成(通过）：22
     * 调度编排中：31
     * 调度编排完成：32
     * 调度测试中：41
     * 调度测试完成(通过）：42
     * 已发布生产：51
     * 生产测试中：61
     * 生产测试完成(通过）：62
     * 已上线：71
     * 已下线：72
     */
    private Integer jobLifecycle;

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
