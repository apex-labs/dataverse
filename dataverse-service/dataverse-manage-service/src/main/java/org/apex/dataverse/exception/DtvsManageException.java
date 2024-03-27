package org.apex.dataverse.exception;

public class DtvsManageException extends Exception{

    /**
     * 异常编码
     */
    private String errorCode;

    /**
     * 异常数据
     */
    private Object errorData;

    /**
     * 异常显示参数
     */
    private Object[] messageArgs;

    public DtvsManageException(String errorData) {
        super(errorData);
        this.errorData = errorData;
    }

    public DtvsManageException(String errCode, Object... messageArgs) {
        super(errCode);
        this.messageArgs = messageArgs;
        this.errorCode = errCode;
    }


    public DtvsManageException(String errCode, String errMessage, Object errData) {
        super(errMessage);
        this.errorCode = errCode;
        this.errorData = errData;
    }

    public String getErrorCode() {
        return this.errorCode;
    }


    public Object getErrorData() {
        return this.errorData;
    }

    public Object[] getMessageArgs() {
        return messageArgs;
    }
}
