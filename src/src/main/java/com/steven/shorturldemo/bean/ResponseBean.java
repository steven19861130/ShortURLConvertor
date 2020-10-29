package com.steven.shorturldemo.bean;

public class ResponseBean {

    private String msg;

    private String code;

    private ResponseBean(Builder builder) {
        this.code = builder.code;
        this.msg = builder.msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public static class Builder<T> {

        private String msg;

        private String code;

        public Builder ok() {
            this.code = ResponseCode.OK.toResponse();
            return this;
        }

        public Builder error() {
            this.code = ResponseCode.FAIL.toResponse();
            return this;
        }

        public Builder message(String msg) {
            this.msg = msg;
            return this;
        }

        public ResponseBean build() {
            return new ResponseBean(this);
        }
    }
}
