/*
 #=============================================================================
 #																			   
 #			opyright (c) 2009 Origin Enterprise Solution LTD.		   
 #																			   
 #=============================================================================
 # File name: ImportHyperionDataHandler.java
 # File description: 										   	
 #=============================================================================
 #	Date		Name		Action	Description of Change					   
 #	2013-3-1	liuc  		Ini		错误信息界面								   
 #=============================================================================
 */

package com.yfjcebp.importdata.utils;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.teamcenter.rac.aif.AbstractAIFApplication;
import com.teamcenter.rac.aif.AbstractAIFDialog;
import com.teamcenter.rac.util.ButtonLayout;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.rac.util.iTextArea;


public class MessageDialog extends AbstractAIFDialog {
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private File logFile;
	private  Registry reg = Registry.getRegistry(MessageDialog.class);
	
	public MessageDialog(AbstractAIFApplication app,File logFile){
		super(true);
		this.logFile=logFile;
		initDialog();
	}
	
	public MessageDialog(String log){
		initStringDialog(log);
	}
	
	public MessageDialog(File logFile){
		this.logFile=logFile;
		initDialog();
	}
	
	private void initDialog() {
		JPanel parentPanel = new JPanel(new BorderLayout());
		iTextArea ita=new iTextArea();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(logFile));
			String line = reader.readLine();
			while(line!=null){
				ita.append(line+"\r\n");
				line = reader.readLine();
			}
			
			reader.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		JPanel jp=new JPanel(new ButtonLayout());
		JButton okBtn=new JButton(reg.getString("MessageDialog.okBtnName"));
		JButton cancelBtn=new JButton(reg.getString("MessageDialog.cancelBtnName"));
		
		okBtn.addActionListener(new ActionListener() {// 给确定按钮添加监听器
			@Override
			public void actionPerformed(ActionEvent event) {
				dispose();
			
			}
		});
		cancelBtn.addActionListener(new ActionListener() {// 给取消按钮添加监听器
			@Override
			public void actionPerformed(ActionEvent event) {
				dispose();
			}
		});
		jp.add(okBtn);
		jp.add(cancelBtn);
		
		JScrollPane jsp=new JScrollPane(ita);
		jsp.setPreferredSize(new Dimension(400,300));
		parentPanel.add(BorderLayout.CENTER,jsp);
		parentPanel.add(BorderLayout.SOUTH,jp);
		
		setTitle(reg.getString("MessageDialog.title"));
		add(parentPanel);
		pack();
		centerToScreen();
		setVisible(true);	
	}
	
	 private void initStringDialog(String line) {
			JPanel parentPanel = new JPanel(new BorderLayout());
			iTextArea ita = new iTextArea();
			ita.append(line+"\r\n");
			JPanel jp=new JPanel(new ButtonLayout());
			JButton okBtn=new JButton(reg.getString("MessageDialog.okBtnName"));
			JButton cancelBtn=new JButton(reg.getString("MessageDialog.cancelBtnName"));
			
			okBtn.addActionListener(new ActionListener() {// 给确定按钮添加监听器
				@Override
				public void actionPerformed(ActionEvent event) {
					dispose();
				
				}
			});
			cancelBtn.addActionListener(new ActionListener() {// 给取消按钮添加监听器
				@Override
				public void actionPerformed(ActionEvent event) {
					dispose();
				}
			});
			jp.add(okBtn);
			jp.add(cancelBtn);
			
			ita.setLineWrap(true);
			JScrollPane jsp=new JScrollPane(ita);
			jsp.setPreferredSize(new Dimension(400,300));
			parentPanel.add(BorderLayout.CENTER,jsp);
			parentPanel.add(BorderLayout.SOUTH,jp);
			
			setTitle(reg.getString("MessageDialog.title"));
			add(parentPanel);
			pack();
			centerToScreen();
			setVisible(true);	
		}

	

}
