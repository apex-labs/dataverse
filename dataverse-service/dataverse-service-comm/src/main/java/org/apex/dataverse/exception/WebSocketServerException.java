package org.apex.dataverse.exception;

/**
 * @version : v1.0
 * @projectName : nexus-dtvs
 * @package : com.apex.dtvs.exception
 * @className : WebSocketServerException
 * @description :
 * @Author : Danny.Huo
 * @createDate : 2023/1/11 11:54
 * @updateUser :
 * @updateDate :
 * @updateRemark :
 */
public class WebSocketServerException extends Throwable{

    public WebSocketServerException () {
        super();
    }

    public WebSocketServerException (String msg) {
        super(msg);
    }

    public WebSocketServerException (Exception e) {
        super(e);
    }
}
