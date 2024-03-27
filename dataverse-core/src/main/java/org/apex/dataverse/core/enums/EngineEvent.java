package org.apex.dataverse.core.enums;

import lombok.Getter;

/**
 * 引擎事件
 * @author : Danny.Huo
 * @date : 2023/1/12 14:10
 * @version : v1.0
 * @since : 0.1.0
 */
@Getter
public enum EngineEvent {

    /**
     * 心跳命令
     */
    HEART_BEAT ((byte) 0),

    /**
     * 引擎注册
     */
    ENGINE_REGISTER ((byte) 1),

    /**
     * 注销引擎
     */
    ENGINE_UN_REGISTER ((byte) 2),

    /**
     * 接收到指令
     */
    ENGINE_ACC_CMD ((byte) 3),

    /**
     * 执行指令
     */
    ENGINE_EXE_CMD ((byte) 4),

    /**
     * 执行指令异常结束
     */
    ENGINE_EXE_CMD_ERROR ((byte) 5),

    /**
     * 成功指令执行结束
     */
    ENGINE_STOP_CMD ((byte) 6);

    private final Byte code;

    EngineEvent(Byte code) {
        this.code = code;
    }

}
