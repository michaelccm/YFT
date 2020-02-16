package com.yfjcebp.importdata.utils;

import javax.swing.*;

import com.teamcenter.rac.aif.AbstractAIFUIApplication;

import java.awt.*;
import java.net.*;

//������������

public class KelsenStartWindow extends JWindow implements Runnable {
	Thread splashThread; // ����������߳�

	JProgressBar progress; // �����
	private boolean stopbar=false;
	private String title;
	public int intTime=30;
	AbstractAIFUIApplication app;
	public KelsenStartWindow(AbstractAIFUIApplication app) {
		this.app=app;
		Container container = getContentPane(); // �õ�����
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)); // ���ù��
		URL url = getClass().getResource("login.jpg"); // ͼƬ��λ��
		if (url != null) {
			container.add(new JLabel(new ImageIcon(url)), BorderLayout.CENTER); // ����ͼƬ
		}
		progress = new JProgressBar(1, 100); // ʵ������
		progress.setStringPainted(true); // �������
		progress.setString("��ݼ�����,���Ժ�......"); // ������ʾ����
		progress.setBackground(Color.white); // ���ñ���ɫ
		container.add(progress, BorderLayout.SOUTH); // ���ӽ������������

		Dimension screen = getToolkit().getScreenSize(); // �õ���Ļ�ߴ�
		pack(); // ������Ӧ����ߴ�
		setLocation((screen.width - getSize().width) / 2,
				(screen.height - getSize().height) / 2); // ���ô���λ��
		setAlwaysOnTop(true);
	}

	
	
	
	public KelsenStartWindow() {
		this.app=app;
		
	}
	
	
	
	
	public KelsenStartWindow(int kintTime) {
		this.app=app;
		this.intTime=kintTime;
		Container container = getContentPane(); // �õ�����
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)); // ���ù��
		URL url = getClass().getResource("login.jpg"); // ͼƬ��λ��
		if (url != null) {
			container.add(new JLabel(new ImageIcon(url)), BorderLayout.CENTER); // ����ͼƬ
		}
		progress = new JProgressBar(1, 100); // ʵ������
		progress.setStringPainted(true); // �������
		//progress.setString("������ݼ�����,���Ժ�..."); // ������ʾ����
		progress.setString("�Ժ�..."); // ������ʾ����
		progress.setBackground(Color.white); // ���ñ���ɫ
		container.add(progress, BorderLayout.SOUTH); // ���ӽ������������

		Dimension screen = getToolkit().getScreenSize(); // �õ���Ļ�ߴ�
		pack(); // ������Ӧ����ߴ�
		setLocation((screen.width - getSize().width) / 2,
				(screen.height - getSize().height) / 2); // ���ô���λ��
	}
	
	public void initUI(){
		Container container = getContentPane(); // �õ�����
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)); // ���ù��
		URL url = getClass().getResource("login.jpg"); // ͼƬ��λ��
		if (url != null) {
			container.add(new JLabel(new ImageIcon(url)), BorderLayout.CENTER); // ����ͼƬ
		}
		progress = new JProgressBar(1, 100); // ʵ������
		progress.setStringPainted(true); // �������
		progress.setString(title); // ������ʾ����
		progress.setBackground(Color.white); // ���ñ���ɫ
		container.add(progress, BorderLayout.SOUTH); // ���ӽ������������

		Dimension screen = getToolkit().getScreenSize(); // �õ���Ļ�ߴ�
		pack(); // ������Ӧ����ߴ�
		setLocation((screen.width - getSize().width) / 2,
				(screen.height - getSize().height) / 2); // ���ô���λ��
		////System.out.println("xx=="+progress.getPreferredSize());
		setAlwaysOnTop(true);
	}
	
	public void setTitle(String str){
		this.title=str;
	}
	
	public void stopBar(){
		stopbar=true;
	}
	public void start() {
		this.toFront(); // ����ǰ����ʾ
		splashThread = new Thread(this); // ʵ���߳�
		splashThread.start(); // ��ʼ�����߳�
	}
	
	
	@Override
	public void run() {
		setVisible(true); // ��ʾ����
		try {
			for (int i = 0; i < i+1; i++) {
				Thread.sleep(intTime); // �߳�����
				int value=progress.getValue();
				if(value<100){
					progress.setValue(progress.getValue() + 1); // ���ý����ֵ
				}else{
					progress.setValue(0); // ���ý����ֵ
				}
				if(stopbar){
					dispose(); // �ͷŴ���
				}
				
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
	}

	public static void main(String[] args) {
		KelsenStartWindow splash = new KelsenStartWindow();
		splash.setTitle("kjjjjjjjj.......");
		splash.initUI();
		splash.start(); // ������������
	}
}