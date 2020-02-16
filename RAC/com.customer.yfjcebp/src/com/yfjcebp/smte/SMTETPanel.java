/*
#=============================================================================
#																			   
#			opyright (c) 2009 Origin Enterprise Solution LTD.		   
#																			   
#=============================================================================
# File name: SMTETPanel.java
# File description: 										   	
#=============================================================================
#	Date		Name		Action	Description of Change					   
#	2013-3-16	zhanggl  	Ini												   
#=============================================================================
 */
package com.yfjcebp.smte;


import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.common.Activator;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentDatasetType;
import com.teamcenter.rac.kernel.TCComponentListOfValues;
import com.teamcenter.rac.kernel.TCComponentListOfValuesType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.TCTypeService;
import com.teamcenter.rac.kernel.TCUserService;
import com.teamcenter.rac.kernel.tcservices.TcPropService;
import com.teamcenter.rac.services.ISessionService;
import com.teamcenter.rac.stylesheet.PropertyArray;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.rac.util.VerticalLayout;
import com.teamcenter.services.rac.core.DataManagementService;
import com.yfjcebp.importdata.utils.OriginUtil;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import org.apache.log4j.Logger;

public class SMTETPanel extends Panel
{
	
    private class ParameterTableEditor extends DefaultCellEditor
        implements MouseListener
    {
        @Override
		public void mouseClicked(MouseEvent mouseevent)
        {
            validateButtons();
        }

        @Override
		public void mousePressed(MouseEvent mouseevent)
        {
        }

        @Override
		public void mouseReleased(MouseEvent mouseevent)
        {
        }

        @Override
		public void mouseEntered(MouseEvent mouseevent)
        {
        }

        @Override
		public void mouseExited(MouseEvent mouseevent)
        {
        }

        public ParameterTableEditor(JTextField jtextfield)
        {
        	super(jtextfield);
            jtextfield.addKeyListener(new KeyAdapter() {

                @Override
				public void keyReleased(KeyEvent keyevent)
                {
                    String s = null;
                    if(isArgument)
                    {
                        if(parametersTable.getEditingColumn() == 0)
                            s = "specialCharSetForArgumentName";
                        else
                        if(parametersTable.getEditingColumn() == 2)
                            s = "specialCharSetForArgumentValue";
                    } else
                    if(parametersTable.getEditingColumn() == 0)
                        s = "specialCharSetForParameterName";
                    if(s != null && isContainSpecialChar(String.valueOf(keyevent.getKeyChar()), s))
                    {
                        JTextField jtextfield1 = (JTextField)keyevent.getSource();
                        if(jtextfield1.getText() != null && jtextfield1.getText().length() > 0)
                            if(jtextfield1.getText().length() == 1)
                                jtextfield1.setText("");
                            else
                                jtextfield1.setText(jtextfield1.getText().substring(0, jtextfield1.getText().length() - 1));
                        (new MessageBox((new StringBuilder()).append(registry.getString("invalidInputError.MESSAGE")).append(registry.getString(s)).toString(), (Throwable)null, registry.getString("invalidInputError.TITLE"), 1, true)).setVisible(true);
                    }
                }
            }
);
            jtextfield.addFocusListener(new FocusListener() {

                @Override
				public void focusLost(FocusEvent focusevent)
                {
                    stopCellEditing();
                }

                @Override
				public void focusGained(FocusEvent focusevent)
                {
                }
            }
);
        }
    }


    public SMTETPanel(boolean flag)
    {
        rowSelectionModel = null;
        isArgument = false;
        registry = Registry.getRegistry(this);
        validationRegistry = Registry.getRegistry("com.teamcenter.rac.validation.validationdata.validationdata");
        isArgument = flag;
        init();
    }

    public SMTETPanel()
    {
        rowSelectionModel = null;
        isArgument = false;
        registry = Registry.getRegistry(this);
        validationRegistry = Registry.getRegistry("com.teamcenter.rac.validation.validationdata.validationdata");
        isArgument = false;
        init();
    }
    
    public boolean isArgument()
    {
        return isArgument;
    }

    public void setBeanType(boolean flag)
    {
        isArgument = flag;
    }

    public JTable getParametersTable()
    {
        return parametersTable;
    }
    
    private void init()
    {
    	

    	String as[] = {"Requirement *", registry.getString("jci6_DesignReq.ColumnName"), 
    			"Detailed Info",registry.getString("jci6_DetailedInfo.ColumnName"),
    			registry.getString("jci6_Operation.ColumnName"),
    			registry.getString("jci6_ValidValue.ColumnName"),
    			registry.getString("jci6_MaxValue.ColumnName"),
    			registry.getString("jci6_MinValue.ColumnName"),
    			registry.getString("jci6_Type.ColumnName"),
    			registry.getString("jci6_Unit.ColumnName")};
    	
        for(int i = 0; i < as.length; i++)
            if(as[i] == null || as[i].length() <= 0)
                as[i] = " ";

        getLovValues();
        
        parametersTableModel = new DefaultTableModel(as, 0);
        parametersTable = new JTable(parametersTableModel);
        parametersTable.setSelectionMode(2);
        
        parametersTable.setPreferredScrollableViewportSize(new Dimension(700, 90));
        JScrollPane jscrollpane = new JScrollPane(parametersTable);
        
        class MouseAdapterListener extends java.awt.event.MouseMotionAdapter
        implements MouseListener {
		    int oldY = 0;
		    int newY = 0;
		    int row = 0;
		    int oldHeight = 0;
		    boolean drag = false;
		    int increase = 0;
		
		    public MouseAdapterListener() {
		
		    }
		
		    @Override
			public void mouseMoved(MouseEvent e) {
		        int onRow = parametersTable.rowAtPoint(e.getPoint());
		
		        int height = 0;
		        for (int i = 0; i <= onRow; i++) {
		            height = height + parametersTable.getRowHeight(i);
		        }
		
		        if (height - e.getY() < 3) {
		            drag = true;
		            parametersTable.setCursor(new Cursor(Cursor.N_RESIZE_CURSOR));
		        } else {
		            drag = false;
		            parametersTable.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		        }
		
		    }
		
		    @Override
			public void mouseDragged(MouseEvent e) {
		        if (drag) {
		            int value = oldHeight + e.getY() - oldY;
		            if (value < 30)
		            	parametersTable.setRowHeight(row, 30);
		            else                     
		            	parametersTable.setRowHeight(row, oldHeight + e.getY() - oldY);
		                           
		        }
		    }
		
		    @Override
			public void mousePressed(MouseEvent e) {
		        oldY = e.getY();
		        row = parametersTable.rowAtPoint(e.getPoint());
		        oldHeight = parametersTable.getRowHeight(row);

		    }
		
		    @Override
			public void mouseReleased(MouseEvent e) {
		        newY = e.getY();
		        parametersTable.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		    }
		
		    @Override
			public void mouseClicked(MouseEvent e) {
		    }
		
		    @Override
			public void mouseEntered(MouseEvent e) {
		    }
		
		    @Override
			public void mouseExited(MouseEvent e) {
		    }
		
		}
        
        MouseAdapterListener listener = new MouseAdapterListener();
        parametersTable.addMouseListener(listener);
        parametersTable.addMouseMotionListener(listener);
        initColumnSizes(parametersTable);
        ParameterTableEditor parametertableeditor = new ParameterTableEditor(new JTextField());
        parametersTable.setDefaultEditor(Object.class, parametertableeditor);
        parametersTable.addMouseListener(parametertableeditor);
        rowSelectionModel = parametersTable.getSelectionModel();
        setUpMathOperatorColumn(parametersTable.getColumnModel().getColumn(4), 4 );
        setUpMathOperatorColumn(parametersTable.getColumnModel().getColumn(8), 8);
        setUpMathOperatorColumn(parametersTable.getColumnModel().getColumn(9), 9);
        setUpTextaColumn(parametersTable.getColumnModel().getColumn(0), 0);
        setUpTextaColumn(parametersTable.getColumnModel().getColumn(1), 1);
        setUpTextaColumn(parametersTable.getColumnModel().getColumn(2), 2);
        setUpTextaColumn(parametersTable.getColumnModel().getColumn(3), 3);
        upButton = new JButton(validationRegistry.getImageIcon("upButton.ICON"));
        upButton.setMargin(new Insets(0, 0, 0, 0));
        upButton.setFocusPainted(false);
        upButton.setToolTipText(validationRegistry.getString("upButton.TIP"));
        upButton.setEnabled(false);
        upButton.addActionListener(new ActionListener() {

            @Override
			public void actionPerformed(ActionEvent actionevent)
            {
                processMoveRequest(-1);
            }
        }
);
        downButton = new JButton(validationRegistry.getImageIcon("downButton.ICON"));
        downButton.setMargin(new Insets(0, 0, 0, 0));
        downButton.setFocusPainted(false);
        downButton.setToolTipText(validationRegistry.getString("downButton.TIP"));
        downButton.setEnabled(false);
        downButton.addActionListener(new ActionListener() {

            @Override
			public void actionPerformed(ActionEvent actionevent)
            {
                processMoveRequest(1);
            }
        }
);
        removeButton = new JButton(validationRegistry.getImageIcon("removeButton.ICON"));
        removeButton.setMargin(new Insets(0, 0, 0, 0));
        removeButton.setFocusPainted(false);
        removeButton.setToolTipText(validationRegistry.getString("removeButton.TIP"));
        removeButton.setEnabled(false);
        removeButton.addActionListener(new ActionListener() {

            @Override
			public void actionPerformed(ActionEvent actionevent)
            {
                processRemoveRequest();
            }
        }
);
        addButton = new JButton(validationRegistry.getImageIcon("addButton.ICON"));
        addButton.setMargin(new Insets(0, 0, 0, 0));
        addButton.setFocusPainted(false);
        addButton.setToolTipText(validationRegistry.getString("addButton.TIP"));
        addButton.setEnabled(true);
        addButton.addActionListener(new ActionListener() {

            @Override
			public void actionPerformed(ActionEvent actionevent)
            {
                processAddRequest();
            }
        }
);
        editButtonsPanel = new JPanel(new VerticalLayout(2, 2, 2, 2, 2));
 
        editButtonsPanel.add("bottom", removeButton);
        editButtonsPanel.add("bottom", addButton);
        setLayout(new BorderLayout());
        add(jscrollpane, "Center");
        add(editButtonsPanel, "East");
    }

    public void validateButtons()
    {
        if(parametersTable == null || !parametersTable.isEnabled())
        {
            addButton.setEnabled(false);
            upButton.setEnabled(false);
            downButton.setEnabled(false);
            removeButton.setEnabled(false);
        } else
        {
            addButton.setEnabled(true);
            if(parametersTable.getSelectedRowCount() <= 0 || parametersTable.getRowCount() <= 0)
            {
                upButton.setEnabled(false);
                downButton.setEnabled(false);
                removeButton.setEnabled(false);
            } else
            {
                upButton.setEnabled(true);
                downButton.setEnabled(true);
                removeButton.setEnabled(true);
            }
        }
        validate();
        repaint();
    }

    public void setEditButtonsVisible(boolean flag)
    {
        editButtonsPanel.setVisible(flag);
    }

    public void processMoveRequest(int i)
    {
        if(rowSelectionModel.isSelectionEmpty())
        {
            String s = registry.getString("nothingSelected.MESSAGE");
            String s1 = registry.getString("nothingSelected.MESSAGE.TITLE");
            MessageBox messagebox = new MessageBox(s, "", s1, 2, false);
            messagebox.setVisible(true);
        } else
        {
            int j = rowSelectionModel.getMinSelectionIndex();
            int k = rowSelectionModel.getMaxSelectionIndex();
            int l = parametersTableModel.getRowCount();
            stopEditing();
            if(i == -1)
            {
                for(int i1 = j; i1 <= k; i1++)
                {
                    int k1 = i1 + i;
                    if(rowSelectionModel.isSelectedIndex(i1) && i1 > 0)
                    {
                        parametersTableModel.moveRow(i1, i1, k1);
                        rowSelectionModel.removeSelectionInterval(i1, i1);
                        rowSelectionModel.addSelectionInterval(k1, k1);
                        parametersTable.scrollRectToVisible(parametersTable.getCellRect(k1, 0, true));
                    }
                }

            } else
            {
                for(int j1 = k; j1 >= j; j1--)
                {
                    int l1 = j1 + i;
                    if(rowSelectionModel.isSelectedIndex(j1) && j1 < l - 1)
                    {
                        parametersTableModel.moveRow(j1, j1, l1);
                        rowSelectionModel.removeSelectionInterval(j1, j1);
                        rowSelectionModel.addSelectionInterval(l1, l1);
                        parametersTable.scrollRectToVisible(parametersTable.getCellRect(l1, 0, true));
                    }
                }

            }
        }
        validateButtons();
    }

    public void processRemoveRequest()
    {
        if(rowSelectionModel.isSelectionEmpty())
        {
            String s = registry.getString("nothingSelected.MESSAGE");
            String s1 = registry.getString("nothingSelected.MESSAGE.TITLE");
            MessageBox messagebox = new MessageBox(s, "", s1, 2, false);
            messagebox.setVisible(true);
        } else
        {
            int i = rowSelectionModel.getMinSelectionIndex();
            int j = rowSelectionModel.getMaxSelectionIndex();
            stopEditing();
            for(int k = j; k >= i; k--)
            {
                if(rowSelectionModel.isSelectedIndex(k))
                    parametersTableModel.removeRow(k);
                int l = k;
                int i1 = parametersTableModel.getRowCount();
                if(l >= i1)
                    l--;
                if(l >= 0 && l < i1)
                {
                    rowSelectionModel.setSelectionInterval(l, l);
                    parametersTable.scrollRectToVisible(parametersTable.getCellRect(l, 0, true));
                }
            }

        }
        validateButtons();
    }

    public void processAddRequest()
    {
        Object aobj[] = {
            "", "", ""
        };
        if(aobj != null)
        {
            insertParameter(aobj);
            validateButtons();
        }
    }

    public void clearTable()
    {
        removeAllVParameters();
        validate();
        repaint();
    }

    public void addParameters(TCComponent tccomponent[])
    {
        if(tccomponent == null)
        {
            clearTable();
            return;
        }
        try
        {
        	String[] proNames = new String[]{ "jci6_DesignReq", "jci6_DetailedInfo", "jci6_Operation", 
        			"jci6_ValidValue", "jci6_MaxValue","jci6_MinValue", "jci6_Type", "jci6_Unit"};
        	
        	ISessionService isessionservice = Activator.getDefault().getSessionService();
            TCSession tcsession = (TCSession)isessionservice.getSession(TCSession.class.getName());
        	TcPropService.getProperties(tcsession, tccomponent, proNames);

        	
        	for(int jy = 0; jy < tccomponent.length; jy++){

        		if(tccomponent[jy].getType().equals("JCI6_DRTRevision")){

        			System.out.println( tccomponent[jy].getTCProperty("jci6_DesignReq").getStringValue());
	        		Object aobj[] = new Object[10];
		            aobj[0] = tccomponent[jy].getTCProperty("jci6_DesignReq").getStringValue();
		            aobj[1] = tccomponent[jy].getTCProperty("jci6_DesignReq").getDisplayValue();
		            aobj[2] = tccomponent[jy].getTCProperty("jci6_DetailedInfo").getStringValue();
		            aobj[3] = tccomponent[jy].getTCProperty("jci6_DetailedInfo").getDisplayValue();
		            aobj[4] = tccomponent[jy].getTCProperty("jci6_Operation").getStringValue();
		            aobj[5] = tccomponent[jy].getTCProperty("jci6_ValidValue").getStringValue();
		            aobj[6] = tccomponent[jy].getTCProperty("jci6_MaxValue");
		            aobj[7] = tccomponent[jy].getTCProperty("jci6_MinValue");
		            aobj[8] = tccomponent[jy].getTCProperty("jci6_Type").getDisplayableValue();
		            aobj[9] = tccomponent[jy].getTCProperty("jci6_Unit").getDisplayableValue();
		           
		            insertParameter(aobj);
        		}
        		
        	}

        }catch(TCException tcexception)
        {
            logger.error("Fail to read parameters", tcexception);
            clearTable();
        }catch (Exception e1) {
			e1.printStackTrace();
		}
    }

    public void addArguments(String as[])
    {
        if(as == null || as.length == 0)
            return;
        String as1[] = as;
        int i = as1.length;
        for(int j = 0; j < i; j++)
        {
            String s = as1[j];
            Object aobj[] = new Object[3];
            if(s.indexOf("=") > -1)
            {
                String as2[] = s.split("=");
                aobj[0] = as2[0];
                aobj[1] = "=";
                aobj[2] = as2.length <= 1 ? "" : ((Object) (as2[1]));
            } else
            if(s.indexOf(" ") > -1)
            {
                String as3[] = s.trim().split("\\s+");
                aobj[0] = as3[0];
                aobj[1] = " ";
                aobj[2] = as3.length <= 1 ? "" : ((Object) (as3[1]));
            } else
            {
                aobj[0] = s;
                aobj[1] = " ";
                aobj[2] = "";
            }
            parametersTableModel.addRow(aobj);
        }
    }

    public void removeAllVParameters()
    {
        int i = parametersTableModel.getRowCount();
        ListSelectionModel listselectionmodel = parametersTable.getSelectionModel();
        if(listselectionmodel != null && !listselectionmodel.isSelectionEmpty())
            listselectionmodel.clearSelection();
        if(i > 0)
            stopEditing();
        for(int j = i - 1; j >= 0; j--)
            parametersTableModel.removeRow(j);

    }

    private void initColumnSizes(JTable jtable)
    {
        for(int i = 0; i < 3; i++)
        {
            TableColumn tablecolumn = jtable.getColumnModel().getColumn(i);
            tablecolumn.sizeWidthToFit();
        }
      
    }

    public void insertParameter(Object aobj[])
    {
        int i = parametersTable.getSelectedRow();
        if(i == -1)
        {
            i = parametersTable.getRowCount();
            parametersTableModel.insertRow(i, aobj);
            ListSelectionModel listselectionmodel = parametersTable.getSelectionModel();
            listselectionmodel.setSelectionInterval(i, i);
        } else
        {
            ListSelectionModel listselectionmodel1 = parametersTable.getSelectionModel();
            i++;
            parametersTableModel.insertRow(i, aobj);
            listselectionmodel1.setSelectionInterval(i, i);
            parametersTable.scrollRectToVisible(parametersTable.getCellRect(i, 0, true));
        }
    }

    public void stopEditing()
    {
        TableCellEditor tablecelleditor = parametersTable.getCellEditor();
        if(tablecelleditor != null)
            tablecelleditor.stopCellEditing();
    }
   
    public TCComponent[]getOrcreateParameterComp(TCComponent[] tccomponent2)
    {
    	// save botton
    	PropertyArray array = new PropertyArray();
    	System.out.println("-----------getOrcreateParameterComp----------");
        int i = parametersTableModel.getRowCount();

        if(i == 0)
            return null;
       
        stopEditing();
        
        ArrayList<TCComponent> newObjectList = new ArrayList<TCComponent>();
        String []key = {"jci6_DesignReq"};
        ISessionService isessionservice = Activator.getDefault().getSessionService();
        
        TCSession tcsession = null;
		try {
			tcsession = (TCSession)isessionservice.getSession(TCSession.class.getName());
			TCUserService service = tcsession.getUserService();
			for(int j = 0; j < i; j++)
	        {
			    if(String.valueOf(parametersTableModel.getValueAt(j, 0)).equals("")){
		    		MessageBox.post("Requirement is required ","error",4);
		    		return null;
		    	}
			    if(String.valueOf(parametersTableModel.getValueAt(j, 1)).equals("")){
		    		MessageBox.post("要求属性是必填属性","error",4);
		    		return null;
		    	}
			    if(String.valueOf(parametersTableModel.getValueAt(j, 5)).equals("")){
		    		MessageBox.post("合格值属性是必填属性","error",4);
		    		return null;
		    	}
			    if(String.valueOf(parametersTableModel.getValueAt(j, 8)).equals("")){
		    		MessageBox.post("类型属性是必填属性","error",4);
		    		return null;
			    }
//			    if(!String.valueOf(parametersTableModel.getValueAt(j, 6)).equals("")&&
//			    		!isDouble(String.valueOf(parametersTableModel.getValueAt(j, 6)))){
//		    		MessageBox.post("","error",4);
//		    		return null;
//		    	}
//		    	if(!String.valueOf(parametersTableModel.getValueAt(j, 7)).equals("")&&
//		    			!isDouble(String.valueOf(parametersTableModel.getValueAt(j, 7)))){
//		    		MessageBox.post("","error",4);
//		    		return null;
//		    	}
	        }
			
	        for(int j = 0; j < i; j++)
	        {
	        	String []keyValue = {(String)parametersTableModel.getValueAt(j, 1)};
			    InterfaceAIFComponent[] propValueList = OriginUtil.searchComponentsCollection(
					    tcsession, "YFJC_Search_JCI6_DRT", key ,keyValue);
			    System.out.println("propValueList.length = " + propValueList.length);
			    
		    	String[] proNames = new String[]{ "jci6_DesignReq", "jci6_DetailedInfo", "jci6_Operation", 
	        			"jci6_ValidValue", "jci6_MaxValue","jci6_MinValue", "jci6_Type", "jci6_Unit"};
		    	String proValues1[] = new String[4];
		    	String proValues[] = new String[10];
		    	proValues[0] = String.valueOf(parametersTableModel.getValueAt(j, 0));
		    	proValues[1] = String.valueOf(parametersTableModel.getValueAt(j, 2));
		    	proValues[2] = String.valueOf(parametersTableModel.getValueAt(j, 4));
		    	proValues[3] = String.valueOf(parametersTableModel.getValueAt(j, 5));
		    	proValues[4] = String.valueOf(parametersTableModel.getValueAt(j, 6));
		    	proValues[5] = String.valueOf(parametersTableModel.getValueAt(j, 7));
		    	proValues[6] = typeLov.get(String.valueOf(parametersTableModel.getValueAt(j, 8))).toString();
		    	proValues[7] = unitLov.get(String.valueOf(parametersTableModel.getValueAt(j, 9))).toString();
		    	proValues[8] = String.valueOf(parametersTableModel.getValueAt(j, 1));;
		    	proValues[9] = String.valueOf(parametersTableModel.getValueAt(j, 3));
		    	proValues1[0] = String.valueOf(parametersTableModel.getValueAt(j, 8));
		    	proValues1[1] = String.valueOf(parametersTableModel.getValueAt(j, 9));
		    	proValues1[2] = String.valueOf(parametersTableModel.getValueAt(j, 1));
		    	proValues1[3] = String.valueOf(parametersTableModel.getValueAt(j, 3));
	        	System.out.println("proValues[8]==========="+proValues[8]);
	        	System.out.println("proValues[9]==========="+proValues[9]);
	        	
	        	
	        	//创建一个dataset文件
	        	String fileName=UUID.randomUUID().toString();	
	    		String fileStr = System.getenv("temp") +"/" + fileName+".txt";
	    		System.out.println("ds文件： fileStr = " + fileStr);
	    		File file = new File(fileStr);
	    		file.createNewFile();
	    		List list = new ArrayList();
	    		list.add(proValues1[2]);
	    		list.add(proValues1[3]);
	    		PrintWriter pw = new PrintWriter(new FileOutputStream(file), false);	
	    		for(int x=0;x<list.size();x++){
	    			
	    			pw.println(list.get(x).toString());

	    		}
	    		pw.flush();
	    		pw.close();
	    		TCTypeService service1 = tcsession.getTypeService();
	    		TCComponentDatasetType datasetType = (TCComponentDatasetType) service1.getTypeComponent("Dataset");
	    		TCComponentDataset tcd = datasetType.create(fileName, "description","Text");
	    		String[] arrTargetName = { fileStr };
	    		String[] type={"Text"}; //excel
	    		tcd.setFiles(arrTargetName,type);
	    		String Uid = tcd.getUid().toString();
	    		
	    		
	    		//-------------------------------------------------------
	    		TcPropService.getProperties(tcsession, tccomponent2, proNames);
		    	if(tccomponent2.length == 0){
	        		proValue = null;
		    	}else{
		    		
		    		if(j >= tccomponent2.length){
		    			
		    			proValue = null;

		    		}else{
				    	if(tccomponent2[j].getType().equals("JCI6_DRTRevision")){
		        			proValue = new String[10];
		        			proValue1 = new String[3];
			        		proValue[0] = tccomponent2[j].getTCProperty("jci6_DesignReq").getStringValue();
					    	proValue[1] = tccomponent2[j].getTCProperty("jci6_DesignReq").getDisplayValue();
					    	proValue[2] = tccomponent2[j].getTCProperty("jci6_DetailedInfo").getStringValue();
					    	proValue[3] = tccomponent2[j].getTCProperty("jci6_DetailedInfo").getDisplayValue();
					    	proValue[4] = tccomponent2[j].getTCProperty("jci6_Operation").getStringValue();
					    	proValue[5] = tccomponent2[j].getTCProperty("jci6_ValidValue").getStringValue();
					    	proValue[6] = String.valueOf(tccomponent2[j].getTCProperty("jci6_MaxValue").getDoubleValue());
					    	proValue[7] = String.valueOf(tccomponent2[j].getTCProperty("jci6_MinValue").getDoubleValue());
					    	proValue[8] = typeLov.get(tccomponent2[j].getTCProperty("jci6_Type").getDisplayValue()).toString();
					    	proValue[9] = unitLov.get(tccomponent2[j].getTCProperty("jci6_Unit").getDisplayValue()).toString();
					    	proValue1[0] = tccomponent2[j].getTCProperty("jci6_Type").getDisplayValue();
					    	proValue1[1] = tccomponent2[j].getTCProperty("jci6_Unit").getDisplayValue();
					    	
		        		}
				    	
		    		}
		    	}
		    	
		    		if(proValue == null || proValues[0] != proValue[0] || proValues[1] != proValue[2]
		    				|| proValues[2] != proValue[4] || proValues[3] != proValue[5]
		    				|| Double.parseDouble(proValues[4]) != Double.parseDouble(proValue[6]) 
		    				|| Double.parseDouble(proValues[5]) != Double.parseDouble(proValue[7])
	        				|| !proValue1[0].equals(proValues1[0]) || !proValue1[1].equals(proValues1[1])
		    				|| proValues1[2] != proValue[1] || proValues1[3] != proValue[3]){
		    			if(propValueList.length == 0 )
					    {
					    	Object objInput[] = new Object[4];
							objInput[0] = "JCI6_DRT";
							objInput[1] = proNames;
							objInput[2] = proValues;
							objInput[3] = Uid;
							TCComponent tccomponent = (TCComponent)service.call("create_object", objInput);
					    	System.out.println("---------------------------------create");
		
							if(tccomponent != null)
							{
			                    newObjectList.add( tccomponent );
							}
					    }else{
					    	Object objInput[] = new Object[4];
							objInput[0] = propValueList[0];
							objInput[1] = proNames;
							objInput[2] = proValues;
							objInput[3] = Uid;
							TCComponent tccomponent = (TCComponent)service.call("revise_object", objInput);
					    	System.out.println("---------------------------------revise");
		
							if(tccomponent != null)
							{
	
			                    newObjectList.add( tccomponent );
							}
					    }
		    			
		    		}else{
		    			 
		    			 System.out.println("---------------------------------add-----");
		    			 newObjectList.add( tccomponent2[j] );
		    			
		    			}
			    	
	    	}
		    
		    
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(newObjectList.size() > 0)
		{
			comp = new TCComponent[newObjectList.size()];
			newObjectList.toArray(comp);
			return comp;
		}
		else
			return null;
    }
   

    public String[] getArgumentArray(boolean flag)
    {
        int i = parametersTableModel.getRowCount();
        if(i == 0)
            return null;
        String as[] = new String[i];
        if(flag)
            stopEditing();
        for(int j = 0; j < i; j++)
        {
            StringBuffer stringbuffer = new StringBuffer("");
            String s = (String)parametersTableModel.getValueAt(j, 1);
            if(s == null || s.length() == 0)
                s = " ";
            stringbuffer.append((String)parametersTableModel.getValueAt(j, 0));
            stringbuffer.append(s);
            stringbuffer.append((String)parametersTableModel.getValueAt(j, 2));
            as[j] = stringbuffer.toString();
        }

        return as;
    }

    public boolean isValidValue()
    {
    	System.out.println("-----------isValidValue----------");
        boolean flag = true;
        int i = parametersTableModel.getRowCount();
        Object obj = null;
        Object obj1 = null;
        Object obj2 = null;
        for(int j = 0; j < i; j++)
        {
            String s = (String)parametersTableModel.getValueAt(j, 0);
            String s1 = (String)parametersTableModel.getValueAt(j, 1);
            String s2 = (String)parametersTableModel.getValueAt(j, 2);
            if(s == null || s.trim().length() <= 0)
                return false;
            if(!isArgument)
            {
                if(isContainSpecialChar(s, "specialCharSetForParameterName") || s1 == null || s1.trim().length() <= 0 || s2 == null || s2.length() <= 0)
                    return false;
                continue;
            }
            if(isContainSpecialChar(s, "specialCharSetForArgumentName"))
                return false;
            if((s1 == null || s1.trim().length() <= 0) && s2 != null && s2.trim().length() > 0)
                return false;
            if(s1 != null && s1.trim().length() > 0 && (s2 == null || s2.length() <= 0 || isContainSpecialChar(s2, "specialCharSetForArgumentValue")))
                return false;
        }

        return flag;
    }
    public void setUpTextaColumn(TableColumn tablecolumn,int column){
    	JTextArea jt = new JTextArea();
    	jt.setLineWrap(true);
    	jt.setWrapStyleWord(true);
    	jt.requestFocus();
    	jt.selectAll();
    	//JTextAreaеL

    	jt.setBorder(BorderFactory.createEmptyBorder(1, 1, 0, 0)); 
    	 tablecolumn.setCellEditor(new PRSCellEditor(jt));
         DefaultTableCellRenderer defaulttablecellrenderer = new DefaultTableCellRenderer();
         defaulttablecellrenderer.setToolTipText(registry.getString("combo.TIP"));
         tablecolumn.setCellRenderer(defaulttablecellrenderer);
         TableCellRenderer tablecellrenderer = tablecolumn.getHeaderRenderer();
         if(tablecellrenderer instanceof DefaultTableCellRenderer)
             ((DefaultTableCellRenderer)tablecellrenderer).setToolTipText(registry.getString("choice.TIP"));
    	
    }
    public void setUpMathOperatorColumn(TableColumn tablecolumn, int column)
    {
    	JComboBox jcombobox = new JComboBox();
        if(!isArgument && column == 4)
        {
        	 Iterator iterator = operationLov.keySet().iterator();                
             while (iterator.hasNext()) {    
              Object key = iterator.next(); 
              jcombobox.addItem(operationLov.get(key));
             }
        } else if(column == 8)
        {
        	Iterator iterator = typeLov.keySet().iterator();                
            while (iterator.hasNext()) {    
             Object key = iterator.next(); 
             jcombobox.addItem(key.toString());

            }
        }else if(column == 9){
        	Iterator iterator = unitLov.keySet().iterator();                
            while (iterator.hasNext()) {    
             Object key = iterator.next(); 
             jcombobox.addItem(key.toString());

            }
        }
        tablecolumn.setCellEditor(new DefaultCellEditor(jcombobox));
        DefaultTableCellRenderer defaulttablecellrenderer = new DefaultTableCellRenderer();
        defaulttablecellrenderer.setToolTipText(registry.getString("combo.TIP"));
        tablecolumn.setCellRenderer(defaulttablecellrenderer);
        TableCellRenderer tablecellrenderer = tablecolumn.getHeaderRenderer();
        if(tablecellrenderer instanceof DefaultTableCellRenderer)
            ((DefaultTableCellRenderer)tablecellrenderer).setToolTipText(registry.getString("choice.TIP"));
    }

    public static boolean isStringArrayModified(String as[], String as1[])
    {
        if(as == null || as1 == null)
        {
            int i = as1 == null ? 0 : as1.length;
            int k = as == null ? 0 : as.length;
            if(logger.isDebugEnabled())
                logger.debug((new StringBuilder()).append("ValidationDataParametersPanel:isValueModified(): returning: ").append(i != k).append(" originalValues ").append(as).append(" values ").append(as1).toString());
            return i != k;
        }
        int j = as1.length;
        int l = as.length;
        if(j != l)
        {
            if(logger.isDebugEnabled())
                logger.debug((new StringBuilder()).append("PropertyArray:isValueModified(): newSize=").append(j).append("   oldSize=").append(l).toString());
            return true;
        }
        for(int i1 = 0; i1 < j; i1++)
            if((as[i1] != null || as1[i1] != null) && (as[i1] != null ? !as[i1].equals(as1[i1]) : as1[i1] != null))
            {
                if(logger.isDebugEnabled())
                    logger.debug((new StringBuilder()).append("PropertyArray:isValueModified(): originalValues[i]=").append(as[i1]).append("   values[i]=").append(as1[i1]).append("   i=").append(i1).toString());
                return true;
            }

        return false;
    }

    public boolean isDataModified(TCComponent tccomponent)
    {
    	System.out.println("------------isDataModified");
        boolean flag = false;
        if(isArgument)
        {
            String as[] = null;
			try {
				as = tccomponent.getTCProperty("validation_arguments").getStringValueArray();
			} catch (TCException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            String as1[] = getArgumentArray(false);
            return isStringArrayModified(as, as1);
        }
        try
        {
        	System.out.println("tccomponent.getType() = "+tccomponent.getType());
            TCComponent []tccomponent1 = tccomponent.getReferenceListProperty("jci6_SMTET");

            int i = parametersTableModel.getRowCount();

            if(tccomponent1 != null)
            {
                if(i == 0 || i != tccomponent1.length)
                {
                    flag = true;
                } else
                {
                	for(int j = 0; j < tccomponent1.length; j++){
                		String[] proNames = new String[]{ "jci6_DesignReq", "jci6_DetailedInfo", "jci6_Operation", 
        	        			"jci6_ValidValue", "jci6_MaxValue","jci6_MinValue", "jci6_Type", "jci6_Unit"};
        	        	
        		    	String tableValues[] = new String[10];
        		    	tableValues[0] = String.valueOf(parametersTableModel.getValueAt(j, 0));
        		    	tableValues[1] = String.valueOf(parametersTableModel.getValueAt(j, 1));
        		    	tableValues[2] = String.valueOf(parametersTableModel.getValueAt(j, 2));
        		    	tableValues[3] = String.valueOf(parametersTableModel.getValueAt(j, 3));
        		    	tableValues[4] = String.valueOf(parametersTableModel.getValueAt(j, 4));
        		    	tableValues[5] = String.valueOf(parametersTableModel.getValueAt(j, 5));
        		    	tableValues[6] = String.valueOf(parametersTableModel.getValueAt(j, 6));
        		    	tableValues[7] = String.valueOf(parametersTableModel.getValueAt(j, 7));
        		    	tableValues[8] = String.valueOf(parametersTableModel.getValueAt(j, 8));
        		    	tableValues[9] = String.valueOf(parametersTableModel.getValueAt(j, 9));
        		    	
        		    	String proValues[] = new String[10];
        		    	proValues[0] = tccomponent1[j].getTCProperty("jci6_DesignReq").getStringValue();
        		    	proValues[1] = tccomponent1[j].getTCProperty("jci6_DesignReq").getDisplayValue();
        		    	proValues[2] = tccomponent1[j].getTCProperty("jci6_DetailedInfo").getStringValue();
        		    	proValues[3] = tccomponent1[j].getTCProperty("jci6_DetailedInfo").getDisplayValue();
        		    	proValues[4] = tccomponent1[j].getTCProperty("jci6_Operation").getStringValue();
        		    	proValues[5] = tccomponent1[j].getTCProperty("jci6_ValidValue").getStringValue();
        		    	proValues[6] = String.valueOf(tccomponent1[j].getTCProperty("jci6_MaxValue").getDoubleValue());
        		    	proValues[7] = String.valueOf(tccomponent1[j].getTCProperty("jci6_MinValue").getDoubleValue());
        		    	proValues[8] = tccomponent1[j].getTCProperty("jci6_Type").getDisplayValue();
        		    	proValues[9] = tccomponent1[j].getTCProperty("jci6_Unit").getDisplayValue();
        		    	
        		    	for(int k = 0; k < 10; k++ ){

        		    		if( k == 6 || k == 7 ){
        		    			if(Double.parseDouble(tableValues[k]) != Double.parseDouble(proValues[k])){
        		    				flag = true;
            		    			return flag;
        		    			}
        		    		}else
        		    		if( !tableValues[k].equals(proValues[k])){
        		    			flag = true;
        		    			return flag;
        		    		}
        		    	}
                	}
                }
            } else
            if(i != 0)
                flag = true;
        }
        catch(TCException tcexception)
        {
            flag = false;
            logger.error(registry.getString("propertiesReadERROR.MESSAGE"), tcexception);
        }
        return flag;
    }

    public void setAllEnabled(boolean flag)
    {
        setEnabled(flag);
        parametersTable.setEnabled(flag);
        validateButtons();
    }

    public boolean isContainSpecialChar(String s, String s1)
    {
        if(s == null || s.length() == 0)
            return false;
        boolean flag = false;
        char ac[] = registry.getString(s1, ".`:;<>\\?\\\"|*='~!@#$%^&").toCharArray();
        char ac1[] = ac;
        int i = ac1.length;
        int j = 0;
        do
        {
            if(j >= i)
                break;
            char c = ac1[j];
            if(s.indexOf(c) > -1)
            {
                flag = true;
                break;
            }
            j++;
        } while(true);
        return flag;
    }

  	private void getLovValues() {
  		operationLov.clear();
  		try {
  			ISessionService isessionservice = Activator.getDefault().getSessionService();
            TCSession tcsession = (TCSession)isessionservice.getSession(TCSession.class.getName());
            DataManagementService datamanagementservice = DataManagementService.getService(tcsession);
            
  			TCComponentListOfValuesType dtype = (TCComponentListOfValuesType) tcsession.getTypeComponent("ListOfValues");
  			TCComponentListOfValues[] tmpcom;
  			tmpcom = dtype.find("JCI6_Operation");
  			if(tmpcom.length > 0 ){
  				String[] code = tmpcom[0].getListOfValues().getStringListOfValues();
  				int cnt = code.length;
  				for (int i = 0; i < cnt; i++) {
  					operationLov.put(code[i], code[i]);
  				}
  			}
  			tmpcom = dtype.find("JCI6_SMTEUnit");
  			if(tmpcom.length > 0 ){
  				String[] code = tmpcom[0].getListOfValues().getStringListOfValues();
  				String[] codeDis = tmpcom[0].getListOfValues().getLOVDisplayValues();
  				int cnt = code.length;
  				for (int i = 0; i < cnt; i++) {
  					unitLov.put(codeDis[i], code[i]);
  				}
  			}
  			tmpcom = dtype.find("JCI6_Type");
  			if(tmpcom.length > 0 ){
  				String[] code = tmpcom[0].getListOfValues().getStringListOfValues();
  				String[] codeDis = tmpcom[0].getListOfValues().getLOVDisplayValues();
  				int cnt = code.length;
  				for (int i = 0; i < cnt; i++) {
  					typeLov.put(codeDis[i], code[i]);
  				}
  			}
  		} catch (TCException e) {
  			e.printStackTrace();
  		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
  	}

  	public boolean isDouble(String str){
		try{ 
			double b = Double.parseDouble(str);
			return true;
		}catch(Exception e){
			return false;
		}
	}
  	
  	
  	private HashMap operationLov = new HashMap<String, String>();
  	private HashMap typeLov = new HashMap<String, String>();
  	private HashMap unitLov = new HashMap<String, String>();
  	
    private ListSelectionModel rowSelectionModel;
    private DefaultTableModel parametersTableModel;
    private JTable parametersTable;
    private JButton upButton;
    private JButton downButton;
    private JButton removeButton;
    private JButton addButton;
    private JPanel editButtonsPanel;
    private Registry registry;
    private Registry validationRegistry;
    private boolean isArgument;
	TCComponent[] comp;
	String proValue[];
	String proValue1[];
    private static final Logger logger = Logger.getLogger(SMTETPanel.class);

}


/*
	DECOMPILATION REPORT

	Decompiled from: D:\Siemens\Teamcenter\OTW9\rac\plugins\com.teamcenter.rac.common_9000.1.0.jar
	Total time: 125 ms
	Jad reported messages/errors:
Couldn't resolve all exception handlers in method isDataModified
	Exit status: 0
	Caught exceptions:
*/