package com.paipianwang.pat.common.web.exception;

import java.util.UUID;

/**
 * Web应用异常基类，所有Web应用异常都必须继承该异常
 * 
 * @author Jack
 * @version 2017-03-20
 */
public class WebException extends RuntimeException {

	private static final long serialVersionUID = -3693327393151496503L;

	/**
	 * 异常ID，用于表示某一异常实例，每一个异常实例都有一个唯一的异常ID
	 */
	protected String id;

	/**
	 * 异常信息，包含必要的上下文业务信息，用于打印日志
	 */
	protected String message;

	/**
	 * 代码异常码，由各具体异常实例化时自己定义
	 */
	protected String code;

	/**
	 * 异常类名
	 */
	protected String realClassName;

	public WebException(String message, String code) {
		super();
		this.id = UUID.randomUUID().toString().toUpperCase().replaceAll("-", "");
		this.message = message;
		this.code = code;
	}

	public String getId() {
		return id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getCode() {
		return code;
	}

}
