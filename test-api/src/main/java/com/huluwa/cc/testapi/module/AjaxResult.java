package com.huluwa.cc.testapi.module;

import java.util.List;

public class AjaxResult { 
private String code = "0";
	
	private String message;

	private Integer count;

	private List data;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
		


	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public List getData() {
		return data;
	}

	public void setData(List data) {
		this.data = data;
	}

	public AjaxResult errro(String msg){
		this.setMessage(msg);
		this.setCode("1");
		return this;
	}

	public AjaxResult success(String msg){
		this.setMessage(msg);
		return this;
	}
	
}
