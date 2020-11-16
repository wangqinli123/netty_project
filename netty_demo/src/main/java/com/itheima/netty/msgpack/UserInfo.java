package com.itheima.netty.msgpack;

import org.msgpack.annotation.Message;

@Message
public class UserInfo{

	private String userName;
	private String age;
	
	public UserInfo() {
		
	}
	public UserInfo(String userName, String age) {
		super();
		this.userName = userName;
		this.age = age;
	}


	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getAge() {
		return age;
	}
	public void setAge(String age) {
		this.age = age;
	}
	@Override
	public String toString() {
		return "UserInfo [userName=" + userName + ", age=" + age + "]";
	}
	
	
	
	
}
