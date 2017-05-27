package com.codebrat.youtubedemo.model;

/**
 * Created by Shikhar on 26-05-2017.
 */

public class Model {
	private String videoId;
	private int time;
	private String comment;

	public Model(){
		//Empty Constructor
	}

	public Model(String videoId, int time, String comment){
		this.videoId = videoId;
		this.time = time;
		this.comment = comment;
	}

	public String getVideoId() {
		return videoId;
	}

	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
}
