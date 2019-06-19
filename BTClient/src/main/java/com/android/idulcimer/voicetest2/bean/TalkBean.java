package com.android.idulcimer.voicetest2.bean;

/**
 * 提问/回答对象封装
 */
public class TalkBean {

	public TalkBean(String content, boolean isAsk, int imageId) {
		this.content = content;
		this.isAsk = isAsk;
		this.imageId = imageId;
	}

	public String content;
	public boolean isAsk;//标记是否是提问
	public int imageId = -1;//回答的图片
}
