package org.apex.dataverse.advice;

import org.apex.dataverse.exception.DtvsManageException;
import org.apex.dataverse.util.ResultVO;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @ClassName ExceptionAdvice
 * @Description TODO
 * @Author wending.wang
 * @Date 2022/4/7 15:06
 * @Version 1.0.0
 **/
@ControllerAdvice()
@RestControllerAdvice()
public class ExceptionAdvice {

    @ExceptionHandler(value = DtvsManageException.class)
    public ResultVO sqlErrorHandler(DtvsManageException ex) {
        return handler(ex);
    }

    /**
     * 异常信息捕获，封装ResultVO
     *
     * @param e
     * @return
     */
    private ResultVO handler(DtvsManageException e) {
        ResultVO resultVO = new ResultVO(false);
        String msg = e.getMessage();
        resultVO.setErrorMsg(msg);
        resultVO.setCode(e.getErrorCode());
        resultVO.setResponseStatus(500);
        return resultVO;
    }
}
