/*
#=============================================================================
#																			   
#			opyright (c) 2009 Origin Enterprise Solution LTD.		   
#																			   
#=============================================================================
# File name: ImportNonLaborActualDataDialog.java
# File description: 										   	
#=============================================================================
#	Date		Name		Action	Description of Change					   
#	2013-3-8	liqz  		Ini										   
#=============================================================================
*/
package com.yfjcebp.importdata.importnonlabor.actual;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.apache.log4j.Logger;

import com.teamcenter.rac.aif.AbstractAIFApplication;
import com.teamcenter.rac.aif.AbstractAIFDialog;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponentTcFile;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.ButtonLayout;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.PropertyLayout;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.rac.util.Utilities;
import com.teamcenter.rac.util.iButton;
import com.yfjcebp.importdata.importhyperion.ReadDataFromFileOperation;
import com.yfjcebp.importdata.utils.OriginUtil;

/**
 * @author liqz
 */
class ImportNonLaborActualDataDialog extends AbstractAIFDialog implements ActionListener{

	private static final long	serialVersionUID	= 1L;
	private TCSession session;
	private JRadioButton radImpActualPR;
	//private JRadioButton radImpActual;
	private JRadioButton radImpActualQAD;
	private iButton btnImport;
	private iButton btnCancel;
	private TCComponentTcFile tcfile;
	private HashMap<String,String> hmCostType = new HashMap<String,String>();
	private HashMap<String,String> hmMonthProperty = new HashMap<String,String>();
	private ArrayList<String> listCostTypeColNameRange = new ArrayList<String>();
	private int iActualCostTypeRow ;
	private int iActualCostPhaseTypeRow;
	private String strActualCostPhaseTypeColName;
	private int iActualDateRow;
	private String strActualDateColName;
	private int iActualStartRow;
	private String strActualProjectIdColName;
	private int iQADStartRow;
	
	/**
	 * m_PreferenceCostTpe::Excel
	 * */
	private String m_PreferenceCostTpe = "YFJC_CostType_Map"; 
	
	/**
	 * m_PreferenceMonthProperty::Excel
	 * */
	private String m_PreferenceMonthProperty = "YFJC_MonthProperty_Map";
	
	
	private String m_PreferenceActualCostTypeRowLocation ="YFJC_NonLabor_Actual_CostType_RowLocation";
	
	/**
	 * m_PreferenceActualCostPhaseTypeRowColLocation::ActualActual_PR
	 * */
	private String m_PreferenceActualCostPhaseTypeRowColLocation ="YFJC_NonLabor_Actual_CostPhaseType_RowColLocation";
	
	/**
	 * m_PreferenceActualProjectIdColName::ActualActual_PRID
	 * */
	private String m_PreferenceActualProjectIdColName = "YFJC_NonLabor_Actual_ProjectId_ColName";
	
	/**
	 * m_PreferenceActualDateRowColName::  ActualActualPR
	 * */
	private String m_PreferenceActualDateRowColName = "YFJC_NonLabor_Actual_Date_Row_ColName";
	
	/**
	 * m_PreferenceActualCostInfoRowStartWith::  ActualActualPR
	 * */
	private String m_PreferenceActualCostInfoRowStartWith ="YFJC_NonLabor_Actual_CostInfo_RowStartWith";
	
	/**
	 * m_PreferenceActualCostTypeRowColNameRange:: Actual
	 * */
	private String m_PreferenceActualCostTypeColNameRange ="YFJC_NonLabor_Actual_CostType_ColNameRange";
	
	/**
	 * m_PreferenceActualPRCostTypeColNameRange::ActualPR
	 * */
	private String m_PreferenceActualPRCostTypeColNameRange ="YFJC_NonLabor_ActualPR_CostType_ColNameRange";
	
	/**
	 * m_PreferenceQADCostInfoRowStartWith::  QAD
	 * */
	private String m_PreferenceQADCostInfoRowStartWith ="YFJC_NonLabor_QAD_CostInfo_RowStartWith";
	
	/**
	 * iImportType::ActualActual_PRQAD
	 * 1:Actual  data
	 * 2:Actual_PR data
	 * 3:QAD data
	 * */
	private int iImportType = 0;
	
	private static Logger logger = Logger.getLogger(ImportNonLaborActualDataHandler.class);
//	private Registry reg = Registry.getRegistry("com.yfjcebp.importdata.utils.utils");
	private static final String BURFLR_NAME = "com.yfjcebp.importdata.utils.utils_locale";
	private static final ResourceBundle reg = ResourceBundle.getBundle(BURFLR_NAME);
	private Registry registry = Registry.getRegistry(this);
	private String m_CostTypeLovName = "Billing Types";
	
	
	/**
	 * ImportNonLaborActualDataDialog::/
	 * @param AbstractAIFApplication 
	 * @param TCComponentTcFile tcfile 
	 * @param int 
	 * */
	public ImportNonLaborActualDataDialog(AbstractAIFApplication app,TCComponentTcFile ptcfile,int pimportType){
//		super(AIFUtility.getActiveDesktop(),false);
		session = (TCSession) app.getSession();
		iImportType = pimportType;
		tcfile = ptcfile;
		initUI();
	}
	
	/**
	 * initUI:: 
	 * */
	private void initUI(){
		setTitle(registry.getString("Dialog.Title"));	
		radImpActualPR = new JRadioButton(registry.getString("Import.ActualPR"));
		//radImpActual = new JRadioButton(registry.getString("Import.Actual"));
		radImpActualQAD = new JRadioButton(registry.getString("Import.ActualQAD"));
		
		if(iImportType == 1){
			//radImpActual.setSelected(true);
		}else if(iImportType == 2){
			radImpActualPR.setSelected(true);
		}else if(iImportType == 3){
			radImpActualQAD.setSelected(true);
		}
		
		btnImport = new iButton(registry.getString("Dialog.ImportBtnName"));
		btnCancel = new iButton(registry.getString("Dialog.CancelBtnName"));
		
		btnImport.addActionListener(this);
		btnCancel.addActionListener(this);
		
		ButtonGroup group = new ButtonGroup();
		group.add(radImpActualPR);
		//group.add(radImpActual);
		group.add(radImpActualQAD);
		
		JPanel paneRadio = new JPanel(new PropertyLayout(5,5,5,5,5,5));
		paneRadio.add("1.1.left.center",radImpActualPR);
		//paneRadio.add("2.1.left.center",radImpActual);
		//paneRadio.add("3.1.left.center",radImpActualQAD);
		paneRadio.add("2.1.left.center",radImpActualQAD);
		
		JPanel paneButton = new JPanel(new ButtonLayout());
		paneButton.add(btnImport);
		paneButton.add(btnCancel);
		
		JPanel paneMain = new JPanel(new BorderLayout());
		paneMain.add(BorderLayout.CENTER,paneRadio);
		paneMain.add(BorderLayout.SOUTH,paneButton);
		getContentPane().add(paneMain);
		
		setPreferredSize(new Dimension(500,250));
		centerToScreen();
		setVisible(true);
	}

	/* 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent evt) {
		if(evt.getSource() == btnImport){
			if(!initImportPublicPreferenceData()){
				return;
			}
			if(!initImportPreferenceData()){
				return;
			}
			disposeDialog();
			doImport();
		}else if(evt.getSource() == btnCancel){
			disposeDialog();
		}
	}
	
	
	/* 
	 * initImportPublicPreferenceData::init preference value
	 * @return boolean
	 */
	private boolean initImportPublicPreferenceData() {
		TCPreferenceService preference = session.getPreferenceService();
		//
		hmCostType = OriginUtil.getMapWithLOV(session, preference, m_PreferenceCostTpe,m_CostTypeLovName);
		if(hmCostType.size() == 0){
			return false;
		}
		
		if(logger.isDebugEnabled()){
			logger.debug("hmCostType = " + hmCostType);
		}
		
		//
		hmMonthProperty = OriginUtil.getPreferenceMap(preference, m_PreferenceMonthProperty);
		if(hmMonthProperty.size() == 0){
			return false;
		}
		return true;
	}
	
	/*
	 * initImportPreferenceData:: init preference value
	 * @return boolean
	 * */
	private boolean initImportPreferenceData(){
		TCPreferenceService preference = session.getPreferenceService();
		if(iImportType == 1 || iImportType == 2){
			//ActualActualPR
			String strActualCostTypeRow = OriginUtil.getPreferenceValue(preference,m_PreferenceActualCostTypeRowLocation);
			if(strActualCostTypeRow.length() == 0){
				return false;
			}
			iActualCostTypeRow = Integer.parseInt(strActualCostTypeRow);
			
			String strActualCostPhaseTypeRowCol = OriginUtil.getPreferenceValue(preference,m_PreferenceActualCostPhaseTypeRowColLocation);
			if(strActualCostPhaseTypeRowCol.length() == 0){
				return false;
			}
			strActualCostPhaseTypeRowCol = strActualCostPhaseTypeRowCol.replace("", ":");
			if(!strActualCostPhaseTypeRowCol.contains(":")){
				MessageBox.post(Utilities.getCurrentFrame(),reg.getString("PreferenceName") + m_PreferenceActualCostPhaseTypeRowColLocation + reg.getString("NoValidValue"),"Information",MessageBox.WARNING);
				return false;
			}
			iActualCostPhaseTypeRow = Integer.parseInt(strActualCostPhaseTypeRowCol.split(":")[0]);
			strActualCostPhaseTypeColName = strActualCostPhaseTypeRowCol.split(":")[1];
			
			//ActualActualPR(3:C)
			String strActualDateRowCol = OriginUtil.getPreferenceValue(preference,m_PreferenceActualDateRowColName);  
			strActualDateRowCol = strActualDateRowCol.replace("", ":");
			iActualDateRow = Integer.parseInt(strActualDateRowCol.split(":")[0]); 
			strActualDateColName = strActualDateRowCol.split(":")[1];
			
			//ActualActualPR
			String strActualStartRow = OriginUtil.getPreferenceValue(preference,m_PreferenceActualCostInfoRowStartWith);
			if(strActualStartRow.length() == 0){
				return false;
			}
			iActualStartRow = Integer.parseInt(strActualStartRow);
			
			//ActualActualPRProjectID
			strActualProjectIdColName = OriginUtil.getPreferenceValue(preference,m_PreferenceActualProjectIdColName);
			if(strActualProjectIdColName.length() == 0){
				return false;
			}
			
			//
			if(iImportType == 1){
				//Actual
				listCostTypeColNameRange = OriginUtil.getPreferenceList(preference,m_PreferenceActualCostTypeColNameRange);
				if(listCostTypeColNameRange.size() == 0){
					return false;
				}
			}else{
				//Actual
				listCostTypeColNameRange = OriginUtil.getPreferenceList(preference,m_PreferenceActualPRCostTypeColNameRange);
				if(listCostTypeColNameRange.size() == 0){
					return false;
				}
			}
		}else if(iImportType == 3){
			//QAD
			//
			String strQADStartRow = OriginUtil.getPreferenceValue(preference,m_PreferenceQADCostInfoRowStartWith);
			if(strQADStartRow.length() == 0){
				return false;
			}
			iQADStartRow = Integer.parseInt(strQADStartRow);
		}
		return true;
	}
	
	
	/* 
	 * doImport:: import data
	 */
	private void doImport() {
		String strPath = System.getenv("TEMP");
		File file = null;
		try {
			file = tcfile.getFile(strPath);
		} catch (TCException e) {
			e.printStackTrace();
		}
		if(file == null){
			return;
		}
		if(iImportType == 1 || iImportType == 2){
			session.queueOperation(new ReadDataFromFileOperation(iImportType,file,session,hmCostType,hmMonthProperty,
				listCostTypeColNameRange,strActualCostPhaseTypeColName,iActualCostPhaseTypeRow,iActualCostTypeRow,iActualDateRow,
				strActualDateColName,iActualStartRow,strActualProjectIdColName));
		}else if(iImportType == 3){
			session.queueOperation(new ReadDataFromFileOperation(iImportType,file,session,hmCostType,hmMonthProperty,iQADStartRow));
		}
	}

}
