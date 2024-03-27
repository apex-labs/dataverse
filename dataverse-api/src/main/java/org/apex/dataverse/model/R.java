
package org.apex.dataverse.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.apex.dataverse.constants.CommonConstants;

import java.io.Serializable;

@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ApiModel(value = "响应信息主体")
public class R<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String HTTP_OK = "200";

	public static final String INTERNAL_SERVER_ERROR = "500";

	@Getter
	@Setter
	@ApiModelProperty(value = "返回标记：成功标记=0，失败标记=1")
	private int code;

    @Getter
    @Setter
    @ApiModelProperty(value = "返回code编码")
    private String errorCode;

	@Getter
	@Setter
	@ApiModelProperty(value = "返回信息")
	private String msg;

	@Getter
	@Setter
	@ApiModelProperty(value = "数据")
	private T data;

	public static <T> R<T> ok() {
		return restOkResult(null, CommonConstants.SUCCESS, null);
	}

	public static <T> R<T> ok(T data) {
		return restOkResult(data, CommonConstants.SUCCESS, null);
	}

	public static <T> R<T> ok(T data, String msg) {
		return restOkResult(data, CommonConstants.SUCCESS, msg);
	}

	public static <T> R<T> failed(String errorCode,String msg) {
		return restResult(null, CommonConstants.FAIL,errorCode, msg);
	}

	public static <T> R<T> failed() {
		return restFailedResult(null, CommonConstants.FAIL, null);
	}

	public static <T> R<T> failed(String msg) {
		return restFailedResult(null, CommonConstants.FAIL, msg);
	}

	public static <T> R<T> failed(T data) {
		return restFailedResult(data, CommonConstants.FAIL, null);
	}

	public static <T> R<T> failed(T data, String msg) {
		return restFailedResult(data, CommonConstants.FAIL, msg);
	}

	private static <T> R<T> restResult(T data, int code, String errorCode, String msg) {
		R<T> apiResult = new R<>();
		apiResult.setCode(code);
		apiResult.setErrorCode(errorCode);
		apiResult.setData(data);
		apiResult.setMsg(msg);
		return apiResult;
	}

    private static <T> R<T> restOkResult(T data, int code, String msg) {
        return restResult(data,code,HTTP_OK,msg);
    }

    private static <T> R<T> restFailedResult(T data, int code, String msg) {
        return restResult(data,code,INTERNAL_SERVER_ERROR,msg);
    }

}
