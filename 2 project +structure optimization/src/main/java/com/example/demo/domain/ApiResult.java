package com.example.demo.domain;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ApiResult<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private int status;

    private boolean success;

    private String msg;

    private T data;

    @JSONField(
            format = "yyyy-MM-dd HH:mm:ss"
    )
    @JsonFormat(
            pattern = "yyyy-MM-dd HH:mm:ss",
            timezone = "GMT+8"
    )
    private Date time;

    public ApiResult() {
        this.time = new Date();
    }

    public static ApiResult<Boolean> result(int rows) {
        return rows > 0 ? ok() : fail();
    }

    public static ApiResult<Boolean> result(boolean flag) {
        return flag ? ok() : fail();
    }

    public static ApiResult<Boolean> result(ResultEnum resultEnum) {
        return result(resultEnum, null);
    }

    public static <T> ApiResult<T> result(ResultEnum resultEnum, T data) {
        return result(resultEnum, (String)null, data);
    }

    public static <T> ApiResult<T> result(ResultEnum resultEnum, String message, T data) {
        boolean success = false;
        if (resultEnum.getCode() == ResultEnum.SUCCESS.getCode()) {
            success = true;
        }

        if (StringUtils.isBlank(message)) {
            message = resultEnum.getMessage();
        }

        return (ApiResult<T>) builder().status(resultEnum.getCode()).msg(message).data(data).success(success).time(new Date()).build();
    }

    public static ApiResult<Boolean> ok() {
        return ok(null);
    }

    public static <T> ApiResult<T> ok(T data) {
        return result(ResultEnum.SUCCESS, data);
    }

    public static <T> ApiResult<T> ok(T data, String message) {
        return result(ResultEnum.SUCCESS, message, data);
    }

    public static ApiResult<Map<String, Object>> okMap(String key, Object value) {
        Map<String, Object> map = new HashMap(1);
        map.put(key, value);
        return ok(map);
    }

    public static ApiResult<Boolean> fail(ResultEnum resultEnum) {
        return result(resultEnum, null);
    }

    public static ApiResult<String> fail(String message) {
        return result(ResultEnum.FAIL, message, null);
    }

    public static <T> ApiResult<T> fail(ResultEnum resultEnum, T data) {
        if (ResultEnum.SUCCESS == resultEnum) {
            throw new RuntimeException("失败结果状态码不能为" + ResultEnum.SUCCESS.getCode());
        } else {
            return result(resultEnum, data);
        }
    }

    public static ApiResult<String> fail(Integer errorCode, String message) {
        return (new ApiResult()).setSuccess(false).setStatus(errorCode).setMsg(message);
    }

    public static ApiResult<Map<String, Object>> fail(String key, Object value) {
        Map<String, Object> map = new HashMap(1);
        map.put(key, value);
        return result(ResultEnum.FAIL, map);
    }

    public static ApiResult<Boolean> fail() {
        return fail(ResultEnum.FAIL);
    }

    public static <T> ApiResultBuilder<T> builder() {
        return new ApiResultBuilder();
    }

    public int getStatus() {
        return this.status;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public String getMsg() {
        return this.msg;
    }

    public T getData() {
        return this.data;
    }

    public Date getTime() {
        return this.time;
    }

    public ApiResult<T> setStatus(final int status) {
        this.status = status;
        return this;
    }

    public ApiResult<T> setSuccess(final boolean success) {
        this.success = success;
        return this;
    }

    public ApiResult<T> setMsg(final String msg) {
        this.msg = msg;
        return this;
    }

    public ApiResult<T> setData(final T data) {
        this.data = data;
        return this;
    }

    @JsonFormat(
            pattern = "yyyy-MM-dd HH:mm:ss",
            timezone = "GMT+8"
    )
    public ApiResult<T> setTime(final Date time) {
        this.time = time;
        return this;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof ApiResult)) {
            return false;
        } else {
            ApiResult<?> other = (ApiResult)o;
            if (!other.canEqual(this)) {
                return false;
            } else if (this.getStatus() != other.getStatus()) {
                return false;
            } else if (this.isSuccess() != other.isSuccess()) {
                return false;
            } else {
                label52: {
                    Object this$msg = this.getMsg();
                    Object other$msg = other.getMsg();
                    if (this$msg == null) {
                        if (other$msg == null) {
                            break label52;
                        }
                    } else if (this$msg.equals(other$msg)) {
                        break label52;
                    }

                    return false;
                }

                Object this$data = this.getData();
                Object other$data = other.getData();
                if (this$data == null) {
                    if (other$data != null) {
                        return false;
                    }
                } else if (!this$data.equals(other$data)) {
                    return false;
                }

                Object this$time = this.getTime();
                Object other$time = other.getTime();
                if (this$time == null) {
                    if (other$time != null) {
                        return false;
                    }
                } else if (!this$time.equals(other$time)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof ApiResult;
    }

    @Override
    public int hashCode() {
        boolean PRIME = true;
        int result = 1;
        result = result * 59 + this.getStatus();
        result = result * 59 + (this.isSuccess() ? 79 : 97);
        Object $msg = this.getMsg();
        result = result * 59 + ($msg == null ? 43 : $msg.hashCode());
        Object $data = this.getData();
        result = result * 59 + ($data == null ? 43 : $data.hashCode());
        Object $time = this.getTime();
        result = result * 59 + ($time == null ? 43 : $time.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "ApiResult(status=" + this.getStatus() + ", success=" + this.isSuccess() + ", msg=" + this.getMsg() + ", data=" + this.getData() + ", time=" + this.getTime() + ")";
    }

    public ApiResult(final int status, final boolean success, final String msg, final T data, final Date time) {
        this.status = status;
        this.success = success;
        this.msg = msg;
        this.data = data;
        this.time = time;
    }

    public static class ApiResultBuilder<T> {
        private int status;
        private boolean success;private String msg;
        private T data;
        private Date time;

        ApiResultBuilder() {
        }

        public ApiResultBuilder<T> status(final int status) {
            this.status = status;
            return this;
        }

        public ApiResultBuilder<T> success(final boolean success) {
            this.success = success;
            return this;
        }

        public ApiResultBuilder<T> msg(final String msg) {
            this.msg = msg;
            return this;
        }

        public ApiResultBuilder<T> data(final T data) {
            this.data = data;
            return this;
        }

        @JsonFormat(
                pattern = "yyyy-MM-dd HH:mm:ss",
                timezone = "GMT+8"
        )
        public ApiResultBuilder<T> time(final Date time) {
            this.time = time;
            return this;
        }

        public ApiResult<T> build() {
            return new ApiResult(this.status, this.success, this.msg, this.data, this.time);
        }

        @Override
        public String toString() {
            return "ApiResult.ApiResultBuilder(status=" + this.status + ", success=" + this.success + ", msg=" + this.msg + ", data=" + this.data + ", time=" + this.time + ")";
        }
    }
}
