package com.example.demo.domain;

/**
 * @BelongsProject: blog
 * @BelongsPackage: com.example.blog.framework.constant
 * @Author: huang
 * @CreateTime: 2022-10-09  20:52
 * @Description: TODO
 * @Version: 1.0.0
 */
public enum ResultEnum {
    SUCCESS(200, "成功"),
    BAD_REQUEST(400, "错误请求"),
    UN_AUTHENTICATION(401, "没有身份验证"),
    FORBIDDEN(403, "没有访问权限"),
    NOT_FOUND(404, "请求不存在"),
    METHOD_NOT_ALLOWED(405, "方法禁用"),
    FAIL(500, "系统错误"),
    NOT_IMPLEMENTED(501, "尚未实施"),
    BAD_GATEWAY(502, "系统繁忙"),
    SERVICE_UNAVAILABLE(503, "服务不可用"),
    ;
    private Integer code;
    private String message;

    ResultEnum() {
    }

    ResultEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
