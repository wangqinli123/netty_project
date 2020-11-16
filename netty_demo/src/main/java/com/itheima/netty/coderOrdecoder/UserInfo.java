package com.itheima.netty.coderOrdecoder;

import java.io.Serializable;
import java.nio.ByteBuffer;

public class UserInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String userName;
	private int userID;
	
	public UserInfo buildUserName(String userName){
		this.userName = userName;
		return this;
	}

	public UserInfo buildUserID(int userID){
		this.userID = userID;
		return this;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}
	
	
	/**
	 * 对二进制码流进行解码(测试编码后的码流大小)
	 * @return
	 */
	public byte[] codeC(){
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		byte[] value = this.userName.getBytes();
		buffer.putInt(value.length);
		buffer.put(value);
		buffer.putInt(this.userID);
		buffer.flip();
		value = null;
		byte[] result = new byte[buffer.remaining()];
		buffer.get(result);
		return result;
	}
	
	/**
	 * 对编码的性能测试
	 */
	public byte[] codeC(ByteBuffer buffer){
		
		buffer.clear();
		byte[] value = this.userName.getBytes();
		buffer.putInt(value.length);
		buffer.put(value);
		buffer.putInt(this.userID);
		buffer.flip();
		value = null;
		byte[] result = new byte[buffer.remaining()];
		buffer.get(result);
		return result;
	}
}