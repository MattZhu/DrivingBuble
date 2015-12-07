package com.dt.android.serverapi.message;


public class Response {
	
	private Integer status_code;

	private String msg;
	

	
	
	
	/**
	 * @return the status_code
	 */
	public Integer getStatus_code() {
		return status_code;
	}

	/**
	 * @param status_code the status_code to set
	 */
	public void setStatus_code(Integer status_code) {
		this.status_code = status_code;
	}

	
	
	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}



	@Override
	public String toString() {
		return "Response [status_code=" + status_code + ", msg=" + msg
				+ ",]";
	}

	
}
