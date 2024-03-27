package org.apex.dataverse.netty.server;

import org.apex.dataverse.exception.WebSocketServerException;

/**
 * @version : v1.0
 * @projectName : nexus-dtvs
 * @package : com.apex.dtvs.netty.server
 * @className : Server
 * @description :
 * @Author : Danny.Huo
 * @createDate : 2023/1/11 11:19
 * @updateUser :
 * @updateDate :
 * @updateRemark :
 */
public interface NettyServer extends Runnable {

    /**
     * Json对象开始符号
     */
    String JSON_START_CHAR = "{";

    /**
     * Json对象结束符号
     */
    String JSON_END_CHAR = "}";

    /**
     * 启动Server
     *
     * @throws WebSocketServerException
     * @throws InterruptedException
     */
    void startServer()
            throws WebSocketServerException, InterruptedException;

    /**
     * 处理请求
     */
    void openHandleRequest();

    /**
     * 处理响应
     */
    void openHandleResponse();
}
