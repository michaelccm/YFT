package com.yfjc.extplanoverview;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.teamcenter.rac.aif.AbstractAIFDialog;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.common.lov.LOVComboBox;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Painter;
import com.teamcenter.rac.util.Registry;

public class YearMonthDialog extends AbstractAIFDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Registry registry = null;
	LOVComboBox yearMonthLOVComboBox;
	TCSession session;

	public YearMonthDialog(	 File templateFile) {
		super(true);
		registry = Registry.getRegistry(this);
		session = (TCSession) AIFUtility.getCurrentApplication().getSession();
		initUI(templateFile);
	}

	public void initUI(	 final File templateFile) {
		this.setTitle(registry.getString("dialog.title"));
		JPanel panel1 = new JPanel(new FlowLayout());
		yearMonthLOVComboBox = new LOVComboBox( "JCI6_NormalHours") {
			public void paint(Graphics g) {
				super.paint(g);
				Painter.paintIsRequired(this, g);
			}
		};
		panel1.add(new JLabel(registry.getString("SelectYearMonthLabel.name")));
		panel1.add(yearMonthLOVComboBox);
		
		JButton nextButton=new JButton(registry.getString("NextButton.name"));
		nextButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				String yearMonth=yearMonthLOVComboBox.getSelectedString();
				if((yearMonth==null)||(yearMonth.trim().length()==0))
				{
					MessageBox.post(registry.getString("YearMonthMustSelected.info"),"INFO",MessageBox.ERROR);
					return ;
				}
				dispose();
				new ExtPlanOverviewReport(yearMonthLOVComboBox.getSelectedString(),templateFile);
			}
		}
		);
		JPanel panel2 = new JPanel(new BorderLayout());
		panel2.add(nextButton,BorderLayout.EAST);
		
		JPanel panel = new JPanel(new GridLayout(2,1));
		panel.add(panel1);
		panel.add(panel2);
		this.add(panel);
		pack();
		centerToScreen();
		setVisible(true);
	}

}
