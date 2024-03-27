package org.apex.dataverse.entity;

import java.time.LocalDateTime;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 数据空间成员表
 * </p>
 *
 * @author danny
 * @since 2024-01-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class DvsMember implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 成员ID
     */
    @TableId(value = "member_id", type = IdType.AUTO)
    private Long memberId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * Dvs父编码，同parent下Dvs在多个环境(dev/prod)下Dvs_code相同。
     */
    private String dvsCode;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 1:项目负责人，2：架构师，3：业务负责人，4：研发，5：数据分析师
     */
    private Boolean role;

    /**
     * 角色名
     */
    private String roleName;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}
