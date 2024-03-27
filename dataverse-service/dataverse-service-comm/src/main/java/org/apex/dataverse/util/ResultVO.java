package org.apex.dataverse.util;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName: ResultVO
 * @Author: wwd
 * @TODO:
 * @Date: 2024/1/7 16:04
 */
@Data
public class ResultVO<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("返回数据")
    private T data;

    @ApiModelProperty("操作标识")
    private boolean success;

    @ApiModelProperty("错误码")
    private String code = "";

    @ApiModelProperty(value = "消息提示信息")
    private String msg;

    @ApiModelProperty(value = "请求路径")
    private String requestUrl;

    @ApiModelProperty(value = "错误简短信息", hidden = true)
    private String errorMsg;

    @ApiModelProperty(value = "错误堆栈信息", hidden = true)
    private String[] errorDetails;

    @ApiModelProperty(value = "请求响应耗时")
    private long elapsed = 0L;

    @ApiModelProperty(value = "HTTP响应码", hidden = true)
    private int responseStatus = 200;

    public ResultVO() {
    }

    public ResultVO(T data) {
        this.data = data;
    }

    public static <T> ResultVO<T> success() {
        return success("操作成功", null);
    }

    public static <T> ResultVO<T> success(T data) {
        return success("操作成功", data);
    }

    public static <T> ResultVO<T> success(String msg, T data) {
        ResultVO<T> rs = new ResultVO<T>(data);
        rs.setSuccess(true);
        rs.setMsg(msg);
        return rs;
    }

    public static <T> ResultVO<T> failure() {
        return failure("error");
    }

    public static <T> ResultVO<T> failure(String msg) {
        return failure(msg, null);
    }

    public static <T> ResultVO<T> failure(String msg, T data) {
        ResultVO<T> rs = new ResultVO<T>(data);
        rs.setSuccess(false);
        rs.setMsg(msg);
        return rs;
    }
}
