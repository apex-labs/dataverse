package org.apex.dataverse.netty.processor;

import org.apex.dataverse.msg.Request;
import org.apex.dataverse.msg.Response;

/**
 * @version : v1.0
 * @projectName : nexus-dtvs
 * @package : com.apex.dtvs.netty.processor
 * @className : DoRequest
 * @description :
 * @Author : Danny.Huo
 * @createDate : 2023/1/11 17:11
 * @updateUser :
 * @updateDate :
 * @updateRemark :
 */
public interface HandleRequest<I, O> {

    /**
     * 处理Request请求
     * @param request
     * @return
     */
    Response<O> handleRequest (Request<I> request, Response<O> response);
}
