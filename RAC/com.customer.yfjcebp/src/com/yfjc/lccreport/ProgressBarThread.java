package com.yfjc.lccreport;



public class ProgressBarThread extends Thread {

	private ProgressBar bar;

	private String title;

	public ProgressBarThread(String title, String message) {
		this.title = title;
		bar = new ProgressBar(message);
	}

	@Override
	public void run() {
		bar.setTitle(title);
		bar.initUI();
	}

	public void stopBar() {
		bar.setBool(true);
	}
	
	public void setBarMsg(String str)
	{
		bar.setTitle(str);
	}
}
