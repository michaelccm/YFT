package com.teamcenter.custwork.toolkit.component;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import com.teamcenter.rac.util.combobox.iComboBox;

// ylong 
import com.teamcenter.custwork.workmanager.WorkManagerDialog;


public class WDatePanel extends JPanel implements ActionListener, ItemListener {

	private JButton prevBtt = null;
	private JButton nextBtt = null;
	private iComboBox yearComb = null;
	private iComboBox monthComb = null;
	private Calendar calendar = null;
	private String[] years = null;
	private String[] months = null;
	private int year1;
	private int month1;
	private Date date = null;
	
	// ylong
	private WorkManagerDialog wMgr = null;

    public void setWorkManager(WorkManagerDialog w_mgr)
    {
        wMgr = w_mgr;
    }

    
	public WDatePanel(Date date) {
		setLayout(new FlowLayout());
		setBorder(new TitledBorder(BorderFactory.createEtchedBorder(), ""));
		setBackground(Color.WHITE);
		this.date = date;
		initDatas();
		this.prevBtt = new JButton("Prev");
		this.prevBtt.setToolTipText("Next");
		this.prevBtt.addActionListener(this);
		add(this.prevBtt);
		this.yearComb = new iComboBox(this.years);
		this.yearComb.setSelectedItem(String.valueOf(this.year1));
		this.yearComb.setToolTipText("选择年份");
		this.yearComb.addActionListener(this);
		//this.yearComb.addItemListener(this);
		this.yearComb.setTextFieldLength(5);
		add(this.yearComb);
		this.monthComb = new iComboBox(this.months);
		this.monthComb.setSelectedItem(" " + formatDay(this.month1 + 1));
		this.monthComb.setToolTipText("选择月份");
		this.monthComb.addActionListener(this);
		//this.monthComb.addItemListener(this);
		this.monthComb.setTextFieldLength(3);
		add(this.monthComb);
		this.nextBtt = new JButton("Next");
		this.nextBtt.setToolTipText("Prev");
		this.nextBtt.addActionListener(this);

		this.prevBtt.setBackground(Color.WHITE);
		this.nextBtt.setBackground(Color.WHITE);
		this.prevBtt.setBorderPainted(false);
		this.nextBtt.setBorderPainted(false);

		this.yearComb.setBackground(Color.WHITE);
		this.monthComb.setBackground(Color.WHITE);

		add(this.nextBtt);

	}

	private Dimension getStartDimension(int width, int height) {
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		dim.width = (dim.width / 2 - (width / 2));
		dim.height = (dim.height / 2 - (height / 2));
		return dim;
	}

	private void initDatas() {
		this.calendar = Calendar.getInstance();
		if (date != null) {
			calendar.setTime(date);
		}

		this.year1 = this.calendar.get(1);
		this.month1 = this.calendar.get(2);
		this.years = new String[100];
		this.months = new String[12];

		for (int i = 0; i < this.months.length; ++i) {
			this.months[i] = " " + formatDay(i + 1);
		}
		int start = this.year1 - 50;
		for (int i = start; i < start + 100; ++i) {
			this.years[(i - start)] = String.valueOf(i);
		}

	}

	@Override
	public void itemStateChanged(ItemEvent e) {

		 wMgr.enReloadBtt();
		 /*
            if(e.getStateChange() == ItemEvent.SELECTED)
            {  
	       wMgr.enReloadBtt();
            }
         */
	}



	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		int year5 = 0;
		int month5 = 0;
		if ("Next".equals(command)) {
			this.calendar.add(2, 1);
			year5 = this.calendar.get(1);
			int maxYear = this.year1 + 49;
			if (year5 > maxYear) {
				this.calendar.add(2, -1);
				return;
			}
			month5 = this.calendar.get(2) + 1;
			this.yearComb.setSelectedItem(String.valueOf(year5));
			this.monthComb.setSelectedItem("" + formatDay(month5));
			
	//		wMgr.enReloadBtt();
		} else if ("Prev".equals(command)) {
			this.calendar.add(2, -1);
			year5 = this.calendar.get(1);
			int minYear = this.year1 - 50;
			if (year5 < minYear) {
				this.calendar.add(2, 1);

			}
			month5 = this.calendar.get(2) + 1;
			this.yearComb.setSelectedItem(String.valueOf(year5));
			this.monthComb.setSelectedItem("" + formatDay(month5));
			
		//	wMgr.enReloadBtt();
		}

		wMgr.enReloadBtt();
		
	}

	public Calendar getCalendar() {
		String yearStr = this.yearComb.getTextField().getText();
		String monthStr = this.monthComb.getTextField().getText();		
//		System.out.println("yearStr is " + yearStr);
//		System.out.println("monthStr is " + monthStr);
		SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyy/MM");	
		try {
			this.date = simpledateformat.parse(yearStr+"/"+monthStr);
			calendar.setTime(date);
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		return calendar;
	}

	private String formatDay(int day) {
		if (day < 10) {
			return "0" + day;
		}
		return String.valueOf(day);
	}

	public iComboBox getYearComb() {
		return yearComb;
	}

	public iComboBox getMonthComb() {
		return monthComb;
	}
	
	
}
