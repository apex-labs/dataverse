package org.apex.dataverse.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @ClassName: SaveDvsPortParam
 * @Author: wwd
 * @TODO:
 * @Date: 2024/1/20 15:36
 */
@Data
@ApiModel
public class SaveDvsPortParam {

    @ApiModelProperty("连接器ID")
    private Long portId;

    @ApiModelProperty("编码，每个Port唯一")
    private String portCode;

    @ApiModelProperty("连接器名称")
    private String portName;

    @ApiModelProperty("向注册中心注册时的注册编码，唯一")
    private String registryCode;

    @ApiModelProperty("分组编码，当此编码不为空时，只有指定group_code的才可被对应code的driver链接。属于专属port")
    private String groupCode;

    @ApiModelProperty("分组名称【冗余】")
    private String groupName;

    @ApiModelProperty("连接器所在主机名")
    private String hostname;

    @ApiModelProperty("连接器所在IP")
    private String ip;

    @ApiModelProperty("连接器绑定的端口号")
    private Integer port;

    @ApiModelProperty("Driver的最大链接数")
    private Integer maxDriverConns;

    @ApiModelProperty("已链接的driver数量")
    private Integer connectedDrivers;

    @ApiModelProperty("连接器状态。BUILD, ONLINE, PAUSE, OFFLINE")
    private String state;

    @ApiModelProperty("最近心跳时间")
    private LocalDateTime lastHeartbeat;

    @ApiModelProperty("心跳频率，单位毫秒。 默认3000。")
    private Integer heartbeatHz;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("是否删除：0否，1：是")
    private Integer isDeleted;
}
