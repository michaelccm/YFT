package com.sf.custom.propertybean;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

import com.teamcenter.rac.aif.AbstractAIFDialog;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemType;
import com.teamcenter.rac.kernel.TCComponentManager;
import com.teamcenter.rac.kernel.TCComponentType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;

@SuppressWarnings("serial")
public class SetUPGDialog extends AbstractAIFDialog {
	private TCSession session;
	private AbstractAIFUIApplication app;
	private JTextField jtf2;//企业代号
	private JTextField jtf3;//UPG号
	private JTextField jtf4;//年份后两位
	private JComboBox<String> jbox1;//车型代码
	private JComboBox<String> jbox2;//车型的年款代号
	private JComboBox<String> jbox3;//车型类别代号
	private JComboBox<String> jbox4;//底盘的年款代号
	private JComboBox<String> jbox5;//字母流水号
	private JComboBox<String> jbox6;//车型（取首字母）
	private JTextField jtf8;//CODE值
	private JTextField jtf9;//手输零件号
	private JTextField jtf10;//三位流水码
	private JTextField jtf11;//四位流水码
	//按钮
	private JButton jbtn;//选择UPG号
	private JButton jbtn1;//完成
	private JButton jbtn2;//关闭
	private JButton jbtn3;//指派三位流水码按钮
	private Container c;
	
	private Text textField;//item_id输入框
	
	private int mark = 0;//标记 用来区分创建的不同的零件类型
	
	private static final String BUEDLE_NAME = "com.xlm.custom.dialogs.query_locale";
	private static final ResourceBundle RESOURCE_BUDELE = ResourceBundle.getBundle(BUEDLE_NAME);
	
	String item_id = RESOURCE_BUDELE.getString("Item_id");
	String query_name = RESOURCE_BUDELE.getString("query_name");

	public SetUPGDialog(AbstractAIFUIApplication app, Frame frame, Text textField,int mark) {
		super(frame,false);
		this.textField=textField;
		this.app=app;
		this.mark = mark;
		session=(TCSession) app.getSession();
		showWindow();
	}
	
	private void showWindow() {
		if(mark==1){
			setTitle("设定UPG总成id");
		}else if(mark==2){
			setTitle("设定零组件id");
		}else if(mark==3){
			setTitle("设定EO零组件id");
		}
		setLayout(null);
		
		JPanel panel3 = new JPanel();//面板3
		panel3.setLayout(null);
		panel3.setBackground(Color.white);
		panel3.setBounds(5, 15, 500, 380);
		panel3.setBorder(BorderFactory.createEtchedBorder());
		
		JPanel jp1 = new JPanel();//*******自动组合框********
		if(mark==1){
			jp1 = getJpanel1();
		}else if(mark==2){
			jp1 = getJpanel2();
		}else if(mark==3){
			jp1 = getJpanel3();
		}
		
		JPanel jp2 = new JPanel();//*******手动输入框********
		jp2.setLayout(null);
		jp2.setBorder(BorderFactory.createTitledBorder("手动输入"));
		jp2.setBounds(10,300,480,70);
		jp2.setBackground(Color.WHITE);
		JLabel jl14 = new JLabel("手输零件号：");
		jl14.setBounds(20, 30, 100, 20);
		jtf9 = new JTextField();//手输零件号
		jtf9.setBounds(120, 30, 200, 20);
		jp2.add(jl14);
		jp2.add(jtf9);
		
		panel3.add(jp1);
		panel3.add(jp2);
		
		JPanel panel5 = new JPanel();
		panel5.setLayout(null);
		panel5.setBounds(5,400, 500, 50);
		jbtn1 = new JButton("完成(F)");
		jbtn1.setBounds(320, 10, 80, 30);
		jbtn2 = new JButton("关闭");
		jbtn2.setBounds(400, 10, 80, 30);
		panel5.add(jbtn1);
		panel5.add(jbtn2);
		
		c = getContentPane();
		c.add(panel3);
		c.add(panel5);
		c.setPreferredSize(new Dimension(500, 450));
		showDialog();
		dolistener();
	}

	private JPanel getJpanel3() {
		JPanel jp1 = new JPanel();
		jp1.setLayout(null);
		jp1.setBorder(BorderFactory.createTitledBorder("自动组合"));
		jp1.setBounds(10,40,480,250);
		jp1.setBackground(Color.WHITE);
		JLabel jl7 = new JLabel("年份：");
		jl7.setBounds(20, 30, 100, 20);
		String yearLast = new SimpleDateFormat("yy",Locale.CHINESE).format(Calendar.getInstance().getTime());
		jtf4 = new JTextField(yearLast);//年份
		jtf4.setBounds(120, 30, 200, 20);
		jp1.add(jl7);
		jp1.add(jtf4);
		JLabel jl9 = new JLabel("车型：");
		jl9.setBounds(20, 60, 100, 20);
		jbox6 = new JComboBox<String>();//车型
		jbox6.setBounds(120, 60, 200, 20);
		String[] values1 = GetPreference.GetVaiues(session, "NLM_CheXing_Value");
		for(int i=0;i<values1.length;i++){
			String[] split = values1[i].split(":");
			jbox6.addItem(split[0]);
		}
		jp1.add(jl9);
		jp1.add(jbox6);
		JLabel jl13 = new JLabel("四位流水码：");
		jl13.setBounds(20, 90, 100, 20);
		jtf11 = new JTextField();//CODE值
		jtf11.setBounds(120, 90, 200, 20);
		jp1.add(jl13);
		jp1.add(jtf11);
		return jp1;
	}

	private JPanel getJpanel2() {
		JPanel jp1 = new JPanel();
		jp1.setLayout(null);
		jp1.setBorder(BorderFactory.createTitledBorder("自动组合"));
		jp1.setBounds(10,40,480,250);
		jp1.setBackground(Color.WHITE);
		JLabel jl7 = new JLabel("企业代号：");
		jl7.setBounds(20, 30, 100, 20);
		jtf2 = new JTextField(GetPreference.GetVaiue(session, "N9_UPG_MID"));//企业代号框
		jtf2.setBounds(120, 30, 200, 20);
		jp1.add(jl7);
		jp1.add(jtf2);
		JLabel jl8 = new JLabel("UPG号：");
		jl8.setBounds(20, 60, 100, 20);
		jtf3 = new JTextField();//UPG号
		jtf3.setBounds(120, 60, 200, 20);
		jbtn = new JButton("选择UPG号");
		jbtn.setBounds(325, 60, 100, 20);
		jp1.add(jl8);
		jp1.add(jtf3);
		jp1.add(jbtn);
		
		JLabel jl9 = new JLabel("选值：");
		jl9.setBounds(20, 90, 100, 20);
		jbox5 = new JComboBox<String>();//字母流水号
		jbox5.setBounds(120, 90, 200, 20);
		for(int i=0;i<26;i++){
			char ch = (char) (65+i);
			String ch_str = ch+"";
			jbox5.addItem(ch_str);
		}
		jp1.add(jl9);
		jp1.add(jbox5);
		
		JLabel jl13 = new JLabel("三位流水码：");
		jl13.setBounds(20, 120, 100, 20);
		jtf10 = new JTextField();//三位流水码
		jtf10.setBounds(120, 120, 200, 20);
		jbtn3 = new JButton("指派");
		jbtn3.setBounds(325, 120, 100, 20);
		jp1.add(jl13);
		jp1.add(jtf10);
		jp1.add(jbtn3);
		return jp1;
	}

	private JPanel getJpanel1() {
		JPanel jp1 = new JPanel();
		jp1.setLayout(null);
		jp1.setBorder(BorderFactory.createTitledBorder("自动组合"));
		jp1.setBounds(10,40,480,250);
		jp1.setBackground(Color.WHITE);
		JLabel jl7 = new JLabel("企业代号：");
		jl7.setBounds(20, 30, 100, 20);
		jtf2 = new JTextField(GetPreference.GetVaiue(session, "N9_UPG_MID"));//企业代号框
		jtf2.setBounds(120, 30, 200, 20);
		jp1.add(jl7);
		jp1.add(jtf2);
		JLabel jl8 = new JLabel("UPG号：");
		jl8.setBounds(20, 60, 100, 20);
		jtf3 = new JTextField();//UPG号
		jtf3.setBounds(120, 60, 200, 20);
		jbtn = new JButton("选择UPG号");
		jbtn.setBounds(325, 60, 100, 20);
		jp1.add(jl8);
		jp1.add(jtf3);
		jp1.add(jbtn);
		JLabel jl9 = new JLabel("车型代码：");
		jl9.setBounds(20, 90, 100, 20);
		jbox1 = new JComboBox<String>();//车型代码
		jbox1.setBounds(120, 90, 200, 20);
		String[] values1 = GetPreference.GetVaiues(session, "N9_UPG_CType");
		for(int i=0;i<values1.length;i++){
			jbox1.addItem(values1[i]);
		}
		jp1.add(jl9);
		jp1.add(jbox1);
		JLabel jl10 = new JLabel("车型的年款代号：");
		jl10.setBounds(20, 120, 100, 20);
		jbox2 = new JComboBox<String>();//车型的年款代号
		jbox2.setBounds(120, 120, 200, 20);
		for(int i=0;i<10;i++){
			jbox2.addItem(i+"");
		}
		jp1.add(jl10);
		jp1.add(jbox2);
		JLabel jl11 = new JLabel("车型类别代号：");
		jl11.setBounds(20, 150, 100, 20);
		jbox3 = new JComboBox<String>();//车型类别代号
		jbox3.setBounds(120, 150, 200, 20);
		String[] values2 = GetPreference.GetVaiues(session, "N9_UPG_BType");
		for(int i=0;i<values2.length;i++){
			jbox3.addItem(values2[i]);
		}
		jp1.add(jl11);
		jp1.add(jbox3);
		JLabel jl12 = new JLabel("底盘的年款代号：");
		jl12.setBounds(20, 180, 100, 20);
		jbox4 = new JComboBox<String>();//底盘的年款代号
		jbox4.setBounds(120, 180, 200, 20);
		for(int i=0;i<10;i++){
			jbox4.addItem(i+"");
		}
		jp1.add(jl12);
		jp1.add(jbox4);
		JLabel jl13 = new JLabel("CODE值：");
		jl13.setBounds(20, 210, 100, 20);
		jtf8 = new JTextField();//CODE值
		jtf8.setBounds(120, 210, 200, 20);
		jp1.add(jl13);
		jp1.add(jtf8);
		return jp1;
	}

	private void dolistener() {
		//选择UPG号按钮
		if(jbtn!=null){
			jbtn.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						new GetUPGTreeDialog(app,app.getDesktop(),jtf3);
					} catch (TCException e1) {
						e1.printStackTrace();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			});
		}
		//完成按钮
		if(jbtn1!=null){
			jbtn1.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					if(mark==1){
						final String text = jtf9.getText();
						if("".equals(text)||text==null){
							String s1 = jtf2.getText();//企业代号
							String s2 = jtf3.getText();//UPG号
							String s3 = jbox1.getSelectedItem().toString();
							String s4 = jbox2.getSelectedItem().toString();
							String s5 = jbox3.getSelectedItem().toString();
							String s6 = jbox4.getSelectedItem().toString();
							String s7 = jtf8.getText();
							if("".equals(s2)||"".equals(s7)){
								MessageBox.post("UPG号和code值不能为空！", "错误提示", MessageBox.WARNING);
							}else{
								disposeDialog();
								final String s = s1+s2+s3+s4+s5+s6+s7;
								Display.getDefault().asyncExec(new Runnable() {
									
									@Override
									public void run() {
										textField.setText(s);
									}
								});
							}
						}else{
							disposeDialog();
							Display.getDefault().asyncExec(new Runnable() {
								
								@Override
								public void run() {
									textField.setText(text);
								}
							});
						}
					}else if(mark==2){
						final String text = jtf9.getText();
						if("".equals(text)||text==null){
							String s1 = jtf2.getText();//企业代号
							String s2 = jtf3.getText();//UPG号
							String s3 = jbox5.getSelectedItem().toString();//字母流水号
							String s7 = jtf10.getText();//三位数字流水号
							if("".equals(s2)||"".equals(s7)){
								MessageBox.post("UPG号和数字流水号不能为空！", "错误提示", MessageBox.WARNING);
							}else{
								disposeDialog();
								final String s = s1+s2+s3+s7;
								Display.getDefault().asyncExec(new Runnable() {
									
									@Override
									public void run() {
										textField.setText(s);
									}
								});
							}
						}else{
							disposeDialog();
							Display.getDefault().asyncExec(new Runnable() {
								
								@Override
								public void run() {
									textField.setText(text);
								}
							});
						}
					}else if(mark==3){
						final String text = jtf9.getText();
						if("".equals(text)||text==null){
							String s1 = jtf4.getText();//年份后两位
							String s2 = jtf11.getText();//四位流水码
							String s3 = jbox6.getSelectedItem().toString().charAt(0)+"";//车型（取首字母）
							if("".equals(s2)||"".equals(s1)){
								MessageBox.post("年份和四位流水码不能为空！", "错误提示", MessageBox.WARNING);
							}else{
								disposeDialog();
								final String s = s3+s1+s2;
								Display.getDefault().asyncExec(new Runnable() {
									
									@Override
									public void run() {
										textField.setText(s);
									}
								});
							}
						}else{
							disposeDialog();
							Display.getDefault().asyncExec(new Runnable() {
								
								@Override
								public void run() {
									textField.setText(text);
								}
							});
						}
					}
				}
			});
		}
		if(jbtn2!=null){
			//关闭按钮
			jbtn2.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					disposeDialog();
				}
			});
		}
		if(jbtn3!=null){
			//指派三位流水码按钮
			jbtn3.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					String val_R = jtf2.getText();//企业代号
					String val_upg = jtf3.getText();//UPG号
					String val_ch = jbox5.getSelectedItem().toString();//选值
					String str = null;
					if("".equals(val_upg)||val_upg==null){
						MessageBox.post("请先选择UPG号！", "提示", MessageBox.WARNING);
					}else{
						for(int i=1;i<=1000;i++){
							if(i<1000){
								str = String.format("%03d", i); 
								String s = val_R+val_upg+val_ch+str;
								try {
									TCComponent[] search = session.search(query_name, new String[] {item_id}, new String[]{s});
									if(search.length>0){
										continue;
									}else{
										break;
									}
								} catch (Exception e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							}else{
								MessageBox.post("流水号必须在001-999之间！", "提示", MessageBox.WARNING);
							}
						}
					}
					jtf10.setText(str);
				}
			});
		}
	}
}
