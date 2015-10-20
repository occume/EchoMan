package com.echoman.model;

public class Response {

	private int errCode;
	private String errInfo;
	private Object payload;
	
	public Response(){}
	
	public Response(int errCode, String errInfo) {
		this.errCode = errCode;
		this.errInfo = errInfo;
	}
	
	public static Response getOk(){
		return new Response(0, "ok");
	}
	
	public int getErrCode() {
		return errCode;
	}
	public void setErrCode(int errCode) {
		this.errCode = errCode;
	}
	public String getErrInfo() {
		return errInfo;
	}
	public void setErrInfo(String errInfo) {
		this.errInfo = errInfo;
	}
	public Object getPayload() {
		return payload;
	}
	public void setPayload(Object payload) {
		this.payload = payload;
	}

	@Override
	public String toString() {
		return "Response [errCode=" + errCode + ", errInfo=" + errInfo
				+ ", payload=" + payload + "]";
	}
}
