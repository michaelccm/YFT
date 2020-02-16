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
	private JTextField jtf2;//��ҵ����
	private JTextField jtf3;//UPG��
	private JTextField jtf4;//��ݺ���λ
	private JComboBox<String> jbox1;//���ʹ���
	private JComboBox<String> jbox2;//���͵�������
	private JComboBox<String> jbox3;//����������
	private JComboBox<String> jbox4;//���̵�������
	private JComboBox<String> jbox5;//��ĸ��ˮ��
	private JComboBox<String> jbox6;//���ͣ�ȡ����ĸ��
	private JTextField jtf8;//CODEֵ
	private JTextField jtf9;//���������
	private JTextField jtf10;//��λ��ˮ��
	private JTextField jtf11;//��λ��ˮ��
	//��ť
	private JButton jbtn;//ѡ��UPG��
	private JButton jbtn1;//���
	private JButton jbtn2;//�ر�
	private JButton jbtn3;//ָ����λ��ˮ�밴ť
	private Container c;
	
	private Text textField;//item_id�����
	
	private int mark = 0;//��� �������ִ����Ĳ�ͬ���������
	
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
			setTitle("�趨UPG�ܳ�id");
		}else if(mark==2){
			setTitle("�趨�����id");
		}else if(mark==3){
			setTitle("�趨EO�����id");
		}
		setLayout(null);
		
		JPanel panel3 = new JPanel();//���3
		panel3.setLayout(null);
		panel3.setBackground(Color.white);
		panel3.setBounds(5, 15, 500, 380);
		panel3.setBorder(BorderFactory.createEtchedBorder());
		
		JPanel jp1 = new JPanel();//*******�Զ���Ͽ�********
		if(mark==1){
			jp1 = getJpanel1();
		}else if(mark==2){
			jp1 = getJpanel2();
		}else if(mark==3){
			jp1 = getJpanel3();
		}
		
		JPanel jp2 = new JPanel();//*******�ֶ������********
		jp2.setLayout(null);
		jp2.setBorder(BorderFactory.createTitledBorder("�ֶ�����"));
		jp2.setBounds(10,300,480,70);
		jp2.setBackground(Color.WHITE);
		JLabel jl14 = new JLabel("��������ţ�");
		jl14.setBounds(20, 30, 100, 20);
		jtf9 = new JTextField();//���������
		jtf9.setBounds(120, 30, 200, 20);
		jp2.add(jl14);
		jp2.add(jtf9);
		
		panel3.add(jp1);
		panel3.add(jp2);
		
		JPanel panel5 = new JPanel();
		panel5.setLayout(null);
		panel5.setBounds(5,400, 500, 50);
		jbtn1 = new JButton("���(F)");
		jbtn1.setBounds(320, 10, 80, 30);
		jbtn2 = new JButton("�ر�");
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
		jp1.setBorder(BorderFactory.createTitledBorder("�Զ����"));
		jp1.setBounds(10,40,480,250);
		jp1.setBackground(Color.WHITE);
		JLabel jl7 = new JLabel("��ݣ�");
		jl7.setBounds(20, 30, 100, 20);
		String yearLast = new SimpleDateFormat("yy",Locale.CHINESE).format(Calendar.getInstance().getTime());
		jtf4 = new JTextField(yearLast);//���
		jtf4.setBounds(120, 30, 200, 20);
		jp1.add(jl7);
		jp1.add(jtf4);
		JLabel jl9 = new JLabel("���ͣ�");
		jl9.setBounds(20, 60, 100, 20);
		jbox6 = new JComboBox<String>();//����
		jbox6.setBounds(120, 60, 200, 20);
		String[] values1 = GetPreference.GetVaiues(session, "NLM_CheXing_Value");
		for(int i=0;i<values1.length;i++){
			String[] split = values1[i].split(":");
			jbox6.addItem(split[0]);
		}
		jp1.add(jl9);
		jp1.add(jbox6);
		JLabel jl13 = new JLabel("��λ��ˮ�룺");
		jl13.setBounds(20, 90, 100, 20);
		jtf11 = new JTextField();//CODEֵ
		jtf11.setBounds(120, 90, 200, 20);
		jp1.add(jl13);
		jp1.add(jtf11);
		return jp1;
	}

	private JPanel getJpanel2() {
		JPanel jp1 = new JPanel();
		jp1.setLayout(null);
		jp1.setBorder(BorderFactory.createTitledBorder("�Զ����"));
		jp1.setBounds(10,40,480,250);
		jp1.setBackground(Color.WHITE);
		JLabel jl7 = new JLabel("��ҵ���ţ�");
		jl7.setBounds(20, 30, 100, 20);
		jtf2 = new JTextField(GetPreference.GetVaiue(session, "N9_UPG_MID"));//��ҵ���ſ�
		jtf2.setBounds(120, 30, 200, 20);
		jp1.add(jl7);
		jp1.add(jtf2);
		JLabel jl8 = new JLabel("UPG�ţ�");
		jl8.setBounds(20, 60, 100, 20);
		jtf3 = new JTextField();//UPG��
		jtf3.setBounds(120, 60, 200, 20);
		jbtn = new JButton("ѡ��UPG��");
		jbtn.setBounds(325, 60, 100, 20);
		jp1.add(jl8);
		jp1.add(jtf3);
		jp1.add(jbtn);
		
		JLabel jl9 = new JLabel("ѡֵ��");
		jl9.setBounds(20, 90, 100, 20);
		jbox5 = new JComboBox<String>();//��ĸ��ˮ��
		jbox5.setBounds(120, 90, 200, 20);
		for(int i=0;i<26;i++){
			char ch = (char) (65+i);
			String ch_str = ch+"";
			jbox5.addItem(ch_str);
		}
		jp1.add(jl9);
		jp1.add(jbox5);
		
		JLabel jl13 = new JLabel("��λ��ˮ�룺");
		jl13.setBounds(20, 120, 100, 20);
		jtf10 = new JTextField();//��λ��ˮ��
		jtf10.setBounds(120, 120, 200, 20);
		jbtn3 = new JButton("ָ��");
		jbtn3.setBounds(325, 120, 100, 20);
		jp1.add(jl13);
		jp1.add(jtf10);
		jp1.add(jbtn3);
		return jp1;
	}

	private JPanel getJpanel1() {
		JPanel jp1 = new JPanel();
		jp1.setLayout(null);
		jp1.setBorder(BorderFactory.createTitledBorder("�Զ����"));
		jp1.setBounds(10,40,480,250);
		jp1.setBackground(Color.WHITE);
		JLabel jl7 = new JLabel("��ҵ���ţ�");
		jl7.setBounds(20, 30, 100, 20);
		jtf2 = new JTextField(GetPreference.GetVaiue(session, "N9_UPG_MID"));//��ҵ���ſ�
		jtf2.setBounds(120, 30, 200, 20);
		jp1.add(jl7);
		jp1.add(jtf2);
		JLabel jl8 = new JLabel("UPG�ţ�");
		jl8.setBounds(20, 60, 100, 20);
		jtf3 = new JTextField();//UPG��
		jtf3.setBounds(120, 60, 200, 20);
		jbtn = new JButton("ѡ��UPG��");
		jbtn.setBounds(325, 60, 100, 20);
		jp1.add(jl8);
		jp1.add(jtf3);
		jp1.add(jbtn);
		JLabel jl9 = new JLabel("���ʹ��룺");
		jl9.setBounds(20, 90, 100, 20);
		jbox1 = new JComboBox<String>();//���ʹ���
		jbox1.setBounds(120, 90, 200, 20);
		String[] values1 = GetPreference.GetVaiues(session, "N9_UPG_CType");
		for(int i=0;i<values1.length;i++){
			jbox1.addItem(values1[i]);
		}
		jp1.add(jl9);
		jp1.add(jbox1);
		JLabel jl10 = new JLabel("���͵������ţ�");
		jl10.setBounds(20, 120, 100, 20);
		jbox2 = new JComboBox<String>();//���͵�������
		jbox2.setBounds(120, 120, 200, 20);
		for(int i=0;i<10;i++){
			jbox2.addItem(i+"");
		}
		jp1.add(jl10);
		jp1.add(jbox2);
		JLabel jl11 = new JLabel("���������ţ�");
		jl11.setBounds(20, 150, 100, 20);
		jbox3 = new JComboBox<String>();//����������
		jbox3.setBounds(120, 150, 200, 20);
		String[] values2 = GetPreference.GetVaiues(session, "N9_UPG_BType");
		for(int i=0;i<values2.length;i++){
			jbox3.addItem(values2[i]);
		}
		jp1.add(jl11);
		jp1.add(jbox3);
		JLabel jl12 = new JLabel("���̵������ţ�");
		jl12.setBounds(20, 180, 100, 20);
		jbox4 = new JComboBox<String>();//���̵�������
		jbox4.setBounds(120, 180, 200, 20);
		for(int i=0;i<10;i++){
			jbox4.addItem(i+"");
		}
		jp1.add(jl12);
		jp1.add(jbox4);
		JLabel jl13 = new JLabel("CODEֵ��");
		jl13.setBounds(20, 210, 100, 20);
		jtf8 = new JTextField();//CODEֵ
		jtf8.setBounds(120, 210, 200, 20);
		jp1.add(jl13);
		jp1.add(jtf8);
		return jp1;
	}

	private void dolistener() {
		//ѡ��UPG�Ű�ť
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
		//��ɰ�ť
		if(jbtn1!=null){
			jbtn1.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					if(mark==1){
						final String text = jtf9.getText();
						if("".equals(text)||text==null){
							String s1 = jtf2.getText();//��ҵ����
							String s2 = jtf3.getText();//UPG��
							String s3 = jbox1.getSelectedItem().toString();
							String s4 = jbox2.getSelectedItem().toString();
							String s5 = jbox3.getSelectedItem().toString();
							String s6 = jbox4.getSelectedItem().toString();
							String s7 = jtf8.getText();
							if("".equals(s2)||"".equals(s7)){
								MessageBox.post("UPG�ź�codeֵ����Ϊ�գ�", "������ʾ", MessageBox.WARNING);
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
							String s1 = jtf2.getText();//��ҵ����
							String s2 = jtf3.getText();//UPG��
							String s3 = jbox5.getSelectedItem().toString();//��ĸ��ˮ��
							String s7 = jtf10.getText();//��λ������ˮ��
							if("".equals(s2)||"".equals(s7)){
								MessageBox.post("UPG�ź�������ˮ�Ų���Ϊ�գ�", "������ʾ", MessageBox.WARNING);
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
							String s1 = jtf4.getText();//��ݺ���λ
							String s2 = jtf11.getText();//��λ��ˮ��
							String s3 = jbox6.getSelectedItem().toString().charAt(0)+"";//���ͣ�ȡ����ĸ��
							if("".equals(s2)||"".equals(s1)){
								MessageBox.post("��ݺ���λ��ˮ�벻��Ϊ�գ�", "������ʾ", MessageBox.WARNING);
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
			//�رհ�ť
			jbtn2.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					disposeDialog();
				}
			});
		}
		if(jbtn3!=null){
			//ָ����λ��ˮ�밴ť
			jbtn3.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					String val_R = jtf2.getText();//��ҵ����
					String val_upg = jtf3.getText();//UPG��
					String val_ch = jbox5.getSelectedItem().toString();//ѡֵ
					String str = null;
					if("".equals(val_upg)||val_upg==null){
						MessageBox.post("����ѡ��UPG�ţ�", "��ʾ", MessageBox.WARNING);
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
								MessageBox.post("��ˮ�ű�����001-999֮�䣡", "��ʾ", MessageBox.WARNING);
							}
						}
					}
					jtf10.setText(str);
				}
			});
		}
	}
}
