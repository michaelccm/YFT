package com.yfjcebp.projectmanager.budget;

import com.teamcenter.rac.aif.AbstractAIFDialog;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.PropertyLayout;
import com.teamcenter.rac.util.UIUtilities;
import com.teamcenter.rac.util.Utilities;
import com.yfjcebp.projectmanager.actual.ProjectActualPanel;
import com.yfjcebp.projectmanager.budget.ProjectBudgetPanel;
import com.yfjcebp.projectmanager.forecast.ProjectResourcePanel;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;


public class ProjectProgressBar extends AbstractAIFDialog implements
		ActionListener {
	private static final long serialVersionUID = 1L;
	private JProgressBar progressbar;
	private String showLable = null;
    private int amount;
    private ProjectBudgetPanel budgetPane;
    private ProjectActualPanel actualPane;
    private ProjectResourcePanel resourcePane;
    
    public boolean bool = true;
    
	public ProjectResourcePanel getResourcePane() {
		setModal(false);
		return resourcePane;
	}


	public void setResourcePane(ProjectResourcePanel resourcePane) {
		setModal(false);
		
		this.resourcePane = resourcePane;
	}


	public ProjectActualPanel getActualPane() {
		setModal(false);
		return actualPane;
	}


	public void setActualPane(ProjectActualPanel actualPane) {
		this.actualPane = actualPane;
	}


	public JProgressBar getProgressbar() {
		return progressbar;
	}


	public void setProgressbar(JProgressBar progressbar) {
		this.progressbar = progressbar;
	}
	private JLabel label;
	
	public JLabel getLabel() {
		return label;
	}


	public void setLabel(JLabel label) {
		this.label = label;
	}
	
	public ProjectBudgetPanel getBudgetPane() {
		return budgetPane;
	}


	public void setBudgetPane(ProjectBudgetPanel budgetPane) {
		this.budgetPane = budgetPane;
	}


	public ProjectProgressBar(ProjectBudgetPanel budgetPane,String showlable,int amount) {
		super(false);
		this.showLable = showlable;
		this.amount = amount;
		this.budgetPane = budgetPane;
	}
	
	public ProjectProgressBar(ProjectActualPanel actualPane,String showlable,int amount) {
		super(false);
		this.showLable = showlable;
		this.amount = amount;
		this.actualPane = actualPane;
	}

	public ProjectProgressBar(ProjectResourcePanel resourcePane,String showlable,int amount) {
		super(false);
		this.showLable = showlable;
		this.amount = amount;
		this.resourcePane = resourcePane;
		System.out.println("ProjectProgressBar showLable-->"+showLable);
	}


	public void initUI() {
		if (SwingUtilities.isEventDispatchThread()){
			System.out.println("is swing action");
			Container container =getContentPane();  //Utilities.getCurrentFrame();  
			JPanel mainPanel = new JPanel(new PropertyLayout());
			label = new JLabel(this.showLable, 0);
			progressbar = new JProgressBar();
			progressbar.setOrientation(0);
			progressbar.setMinimum(0);
			progressbar.setMaximum(amount);
			progressbar.setValue(0);
			progressbar.setPreferredSize(new Dimension(200, 15));
			progressbar.setBorderPainted(true);
			mainPanel.add("1.1.center", new JLabel(" "));
			mainPanel.add("2.1.center", this.label);
			mainPanel.add("3.1.center", this.progressbar);
			mainPanel.add("4.1.center", new JLabel(" "));
			container.add(mainPanel);
			System.out.println("pack progress bar");
			pack();
			System.out.println("pack done");
			// setLocation(500, 200);
			centerToScreen();
			ProgressBarThread thread = new ProgressBarThread(this,showLable,amount);
			thread.start();
			setVisible(true);
			//UIUtilities.enableSwingDialogForSWTModality(this);
		}else{
			/*Container container =getContentPane(); //Utilities.getCurrentFrame(); 
			JPanel mainPanel = new JPanel(new PropertyLayout());
			label = new JLabel(this.showLable, 0);
			progressbar = new JProgressBar();
			progressbar.setOrientation(0);
			progressbar.setMinimum(0);
			progressbar.setMaximum(amount);
			progressbar.setValue(0);
			progressbar.setPreferredSize(new Dimension(200, 15));
			progressbar.setBorderPainted(true);
			mainPanel.add("1.1.center", new JLabel(" "));
			mainPanel.add("2.1.center", this.label);
			mainPanel.add("3.1.center", this.progressbar);
			mainPanel.add("4.1.center", new JLabel(" "));
			container.add(mainPanel);
			pack();
			// setLocation(500, 200);
			centerToScreen();
			
			final ProgressBarThread thread = new ProgressBarThread(this,showLable,amount);
			thread.start();
			setVisible(true);*/
			
			System.out.println("is swt action");
		}
		
		
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.out.println("is WindowEvent");
				bool = false;
				ProjectProgressBar.this.setVisible(false);
				ProjectProgressBar.this.dispose();
			}
		});
		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		
	}

}


