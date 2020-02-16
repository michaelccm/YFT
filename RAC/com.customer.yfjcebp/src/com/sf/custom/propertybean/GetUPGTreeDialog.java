package com.sf.custom.propertybean;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.teamcenter.rac.aif.AbstractAIFDialog;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;

@SuppressWarnings("serial")
public class GetUPGTreeDialog extends AbstractAIFDialog {
	private TCSession session;
	private JTree tree;
	private DefaultMutableTreeNode root;//树的根节点
	private Container c;
	private JList<String> jlist;
	private JTextField jt1;//输入框
	private JButton btn1;//确定
	private JButton btn2;//完成
	private JButton btn3;//关闭
	
	private ArrayList<String> upglist = new ArrayList<String>();//所有upg号集合
	
	private JTextField textField;
	
	public GetUPGTreeDialog(AbstractAIFUIApplication app, Frame desktop, JTextField jtf3) throws TCException, Exception {
		super(desktop,false);
		textField=jtf3;
		session = (TCSession) app.getSession();
		TCComponentDataset dataset = GetExlDataset.getDataset(session, "N9_UPG_NUM");//通过首选项获取exl数据集
		if(dataset!=null){
			//System.out.println(dataset.toString());
			String directory = System.getProperty("java.io.tmpdir");//计算机temp临时目录
			File file = GetExlDataset.downloadFileFromServer(session, dataset, directory);//将数据集下载到本地
			ArrayList<UPGPojo> upgList = getUPGList(file);
			file.delete();
			ArrayList<String> bigClassification = new ArrayList<String>();//大分类集合
			for(int i=0;i<upgList.size();i++){//获得大分类集合
				String big = upgList.get(i).getBigClassification();
				String upg = upgList.get(i).getUpg();
				if(!bigClassification.contains(big)){
					bigClassification.add(big);
				}
				if(!upglist.contains(upg)){
					upglist.add(upg);
				}
			}
			tree = getTree(bigClassification,upgList);
			jlist = new JList<>();
			showWindow(tree,jlist);
		}else{
			MessageBox.post("未找到UPG号清单！", "错误提示", MessageBox.WARNING);
		}
	}

	private void dolistener() {
		if(btn1!=null){
			btn1.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent paramActionEvent) {
					String text = jt1.getText();
					if(upglist.contains(text)){
						DefaultListModel<String> dlm = new DefaultListModel<>();
						dlm.addElement(text);
						jlist.removeAll();
						jlist.setModel(dlm);
						Enumeration<?> e = root.depthFirstEnumeration();
						TreePath treePath = null;
						while(e.hasMoreElements()){
							DefaultMutableTreeNode nextElement = (DefaultMutableTreeNode) e.nextElement();
							String nm = (String) nextElement.getUserObject();
							if(nm.equals(text)){
								treePath = new TreePath(nextElement.getPath());
								
							}
						}
						tree.setSelectionPath(treePath);
						tree.scrollPathToVisible(treePath);
					}else{
						MessageBox.post("此UPG号不存在，请重新输入！", "输入有误", MessageBox.WARNING);
					}
				}
			});
		}
		
		if(btn2!=null){
			btn2.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent paramActionEvent) {
					String text = jt1.getText();
					if(upglist.contains(text)){
						disposeDialog();
						textField.setText(text);
					}else{
						MessageBox.post("此UPG号不存在，请重新输入！", "输入有误", MessageBox.WARNING);
					}
				}
			});
		}
		
		if(btn3!=null){
			btn3.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent paramActionEvent) {
					disposeDialog();
				}
			});
		}
		
		if(tree!=null){
			tree.addTreeSelectionListener(new TreeSelectionListener() {
				
				@Override
				public void valueChanged(TreeSelectionEvent e) {
					TreeSelectionModel selectionModel = tree.getSelectionModel();
					DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) selectionModel.getSelectionPath().getLastPathComponent();
					if(selectedNode.isLeaf()){
						String name = (String) selectedNode.getUserObject();
						DefaultListModel<String> dlm = new DefaultListModel<>();
						dlm.addElement(name);
						jlist.removeAll();
						jlist.setModel(dlm);
					}else{
						Enumeration<?> children = selectedNode.children();
						DefaultListModel<String> dlm = new DefaultListModel<>();
						while(children.hasMoreElements()){
							DefaultMutableTreeNode node = (DefaultMutableTreeNode) children.nextElement();
							if(node.isLeaf()){
								String name = (String) node.getUserObject();
								dlm.addElement(name);
							}
						}
						jlist.removeAll();
						jlist.setModel(dlm);
					}
					jlist.addListSelectionListener(new ListSelectionListener() {
						
						@Override
						public void valueChanged(ListSelectionEvent e) {
							String selectedValue = jlist.getSelectedValue();
							//System.out.println(selectedValue);
							jt1.setText(selectedValue);
						}
					});
				}
			});
		}
	}

	//弹出窗口
	private void showWindow(JTree tree1, JList<String> jlist) {
		setTitle("UPG号选择");
		setLayout(null);
		
		JPanel jpanel1 = new JPanel();
		jpanel1.setLayout(null);
		jpanel1.setBounds(0, 0, 510, 50);
		jpanel1.setBackground(Color.WHITE);
		JLabel jl1 = new JLabel("UPG号:");
		jl1.setBounds(100, 10, 80, 30);
		jt1 = new JTextField();
		jt1.setBounds(150, 15, 200, 20);
		btn1 = new JButton("搜索");
		btn1.setBounds(360, 15, 60, 20);
		jpanel1.add(jl1);
		jpanel1.add(jt1);
		jpanel1.add(btn1);
		
		JPanel jpanel2 = new JPanel();
		jpanel2.setLayout(null);
		jpanel2.setBounds(4, 60, 500, 400);
		jpanel2.setBorder(BorderFactory.createEtchedBorder());
		JScrollPane jsp1 = new JScrollPane(tree1);
		jsp1.setBounds(5, 5, 242, 390);
		jsp1.setBackground(Color.WHITE);
		JScrollPane jsp2 = new JScrollPane(jlist);
		jsp2.setBounds(250, 5, 245, 390);
		jpanel2.add(jsp1);
		jpanel2.add(jsp2);
		
		btn2 = new JButton("完成");
		btn2.setBounds(150, 465, 80, 30);
		btn3 = new JButton("关闭");
		btn3.setBounds(280, 465, 80, 30);
		
		c = getContentPane();
		c.setPreferredSize(new Dimension(500, 500));
		c.add(jpanel1);
		c.add(jpanel2);
		c.add(btn2);
		c.add(btn3);
		showDialog();
		dolistener();
	}

	//生成树
	private JTree getTree(ArrayList<String> bigClassification, ArrayList<UPGPojo> upgList){
		root = new DefaultMutableTreeNode("UPG分类");
		for(int i=0;i<bigClassification.size();i++){
			DefaultMutableTreeNode childNode1 = new DefaultMutableTreeNode(bigClassification.get(i));
			root.add(childNode1);
			ArrayList<String> smallClassification = new ArrayList<String>();
			for(int j=0;j<upgList.size();j++){
				String big = upgList.get(j).getBigClassification();
				if(big.equals(bigClassification.get(i))){
					String small = upgList.get(j).getSmallClassification();
					if(!smallClassification.contains(small)){
						smallClassification.add(small);
					}
				}
			}
			for(int k=0;k<smallClassification.size();k++){
				DefaultMutableTreeNode childNode2 = new DefaultMutableTreeNode(smallClassification.get(k));
				childNode1.add(childNode2);
				for(int l=0;l<upgList.size();l++){
					String s = upgList.get(l).getSmallClassification();
					if(s.equals(smallClassification.get(k))){
						String upg = upgList.get(l).getUpg();
						DefaultMutableTreeNode upgNode = new DefaultMutableTreeNode(upg);
						childNode2.add(upgNode);
					}
				}
			}
		}
		DefaultTreeModel treeModel = new DefaultTreeModel(root);
		return new JTree(treeModel);
	}
	
	//读取excel表格，获得UPG号集合
	private ArrayList<UPGPojo> getUPGList(File file) throws IOException {
		ArrayList<UPGPojo> upgList = new ArrayList<UPGPojo>();
		InputStream in = new FileInputStream(file);
		XSSFWorkbook wb = new XSSFWorkbook(in);
		XSSFSheet sheet = wb.getSheetAt(0);
		for(int i=1;i<sheet.getPhysicalNumberOfRows();i++){
			UPGPojo upg = new UPGPojo();
			XSSFRow row = sheet.getRow(i);
			XSSFCell cell1 = row.getCell(0);
			XSSFCell cell2 = row.getCell(1);
			XSSFCell cell3 = row.getCell(2);
			upg.setBigClassification(cell1.toString());
			upg.setSmallClassification(cell2.toString());
			upg.setUpg(cell3.toString());
			upgList.add(upg);
		}
		in.close();
		return upgList;
	}
}
