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
public class DeployJob implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "deploy_job_id", type = IdType.AUTO)
    private Long deployJobId;

    private Long deployId;

    /**
     * 被加入调度中原作业编码，ETL作业或BDM作业
     */
    private String jobCode;

    /**
     * 作业类型，1：ETL Job, 2:BDM Job
     */
    private Boolean jobType;

    /**
     * 作业名称
     */
    private String jobName;

    /**
     * 调度节点编码
     */
    private String nodeCode;

    /**
     * 环境，0：BASIC、1：DEV、2：PROD
     */
    private Integer env;

    /**
     * 发布前ETL规则。json格式，来自table_extract/table_transform/table_load三表中的数据，按json格式组装。非ETL作业为空。
     */
    private String odlEtl;

    /**
     * 发布后ETL规则。json格式，来自table_extract/table_transform/table_load三表中的数据（页面修改后传递至后端的），按json格式组装。非ETL作业为空。
     */
    private String nowEtl;

    /**
     * 发布前的BDM作业的脚本，非BDM作业为空
     */
    private String oldBdmScript;

    /**
     * 发布后的BDM作业的脚本，非BDM作业为空
     */
    private String nowBdmScript;

    /**
     * 版本号，每发一次版本增加1
     */
    private Integer version;

    /**
     * 发布时间
     */
    private LocalDateTime createTime;


}
