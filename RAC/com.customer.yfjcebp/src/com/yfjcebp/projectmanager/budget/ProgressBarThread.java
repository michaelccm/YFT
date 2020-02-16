package com.yfjcebp.projectmanager.budget;

import java.awt.EventQueue;

import javax.swing.SwingUtilities;


public class ProgressBarThread extends Thread{

	private ProjectProgressBar bar;
    public int amount;
    private String showLable;
 
    private Runnable run;
	public ProgressBarThread(ProjectProgressBar bar,String showLable,int amount) {
		this.bar = bar;
		this.amount = amount;
		this.showLable = showLable;
		System.out.println("ProgressBarThread showLable-->"+showLable);
		
//		run=new Runnable() {
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				
//			}
//		};
	}

	@Override
	public void run() {
		System.out.println("ProgressBarThread-->run");
		//����д����������ʱ����������
		
		//SwingUtilities.invokeLater(run);
		//EventQueue.invokeLater(run);
		
		if(ProgressBarThread.this.bar.getBudgetPane() != null)
		{
			ProgressBarThread.this.bar.getBudgetPane().fillOriginalData(ProgressBarThread.this,ProgressBarThread.this.amount);
			System.out.println("===Budget bar2.dispose()==");
			ProgressBarThread.this.bar.disposeDialog();
			//ProgressBarThread.this.bar.setVisible(false);
			return;
		}else if(ProgressBarThread.this.bar.getActualPane() != null)
		{
			ProgressBarThread.this.bar.getActualPane().fillOriginalData(ProgressBarThread.this, ProgressBarThread.this.amount);
			//ProgressBarThread.this.bar.disposeDialog();
			//ProgressBarThread.this.bar.setVisible(false);
			System.out.println("===Actual bar2.dispose()==");
			return;
		
		}else if(ProgressBarThread.this.bar.getResourcePane()!= null)
		{
			ProgressBarThread.this.bar.getResourcePane().fillOriginalData(ProgressBarThread.this, ProgressBarThread.this.amount);
			ProgressBarThread.this.bar.disposeDialog();
			//ProgressBarThread.this.bar.setVisible(false);
			System.out.println("===Resource bar2.dispose()==");
			return;
		}
	}
	
	//���ý�����ֵ
	public void setProgressValue(final int value){
		bar.getProgressbar().setValue(value);
		bar.getLabel().setText(showLable+" "+value/(amount/100)+"%");
	}
	
	//�õ���������ǰֵ
	public int getProgressValue(){
		return bar.getProgressbar().getValue();
	}
	
	public void close(){
		//timer.stop();
		if(bar!=null){
			//bar.setVisible(false);
			bar.dispose();
		}
		
	}
}
