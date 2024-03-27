package org.apex.dataverse.port.entity;

    import java.time.LocalDateTime;
    import java.io.Serializable;
    import lombok.Data;
    import lombok.EqualsAndHashCode;
    import lombok.experimental.Accessors;

/**
* <p>
    * Port的实例
    * </p>
*
* @author danny
* @since 2023-12-20
*/
    @Data
        @EqualsAndHashCode(callSuper = false)
    @Accessors(chain = true)
    public class PortEvent implements Serializable {

    private static final long serialVersionUID = 1L;

            /**
            * Port的实例ID
            */
    private Long portEventId;

            /**
            * Port ID
            */
    private Long portId;

            /**
            * Port名称
            */
    private String portName;

            /**
            * 连接器所在主机名
            */
    private String hostname;

            /**
            * 连接器所在IP
            */
    private String ip;

            /**
            * 连接器绑定的端口号
            */
    private Integer port;

            /**
            * 事件，START, STOP, EXCEPTION
            */
    private String event;

    private String description;

            /**
            * 创建时间
            */
    private LocalDateTime createTime;


}
