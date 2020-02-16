package com.casic.handlers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
//import org.eclipse.ui.IPerspectiveDescriptor;
//import org.eclipse.ui.IWorkbenchPart;


import com.casic.handlers.DynamicHelpSelectionDialog;
import com.casic.handlers.DynamicHelpUtils;
import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.commands.open.OpenCommand;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentDatasetDefinition;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentForm;
import com.teamcenter.rac.kernel.TCComponentTask;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCComponentUserType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
//import com.teamcenter.rac.ui.common.RACUIUtil;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;
//import com.teamcenter.rac.util.UIUtilities;

public class DynamicHelpUtils
{
	static Registry reg = Registry.getRegistry(com.casic.handlers.DynamicHelpUtils.class);
	
	/**
	 * @return 
	 * return current task name
	 */
	public static String GetCurrentTaskName()
	{
		
		String taskName = null;
		AIFDesktop desk = AIFDesktop.getActiveDesktop();
		AbstractAIFUIApplication application = desk.getCurrentApplication();
		InterfaceAIFComponent[] selecomps = application.getTargetComponents();
		for (InterfaceAIFComponent selecomp : selecomps){
			if(selecomp instanceof TCComponentTask){
				TCComponentTask task = (TCComponentTask)selecomp;
				try {
					taskName = task.getName();
				} catch (TCException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				MessageBox.post(reg.getString("wrong_object_selection"), reg.getString("box_title"), MessageBox.ERROR);
			}
		}
		return taskName;
	}
	
	/**
	 * @return 
	 * return current template name
	 */
	public static String GetCurrentTemplateName()
	{
		String templateName = null;
		AIFDesktop desk = AIFDesktop.getActiveDesktop();
		AbstractAIFUIApplication application = desk.getCurrentApplication();
		InterfaceAIFComponent[] selecomps = application.getTargetComponents();
		for (InterfaceAIFComponent selecomp : selecomps){
			if(selecomp instanceof TCComponentTask){
				TCComponentTask task = (TCComponentTask)selecomp;
				try {
					templateName = task.getProcessDefinition().getName();
				} catch (TCException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				MessageBox.post(reg.getString("wrong_object_selection"), reg.getString("box_title"), MessageBox.ERROR);
			}
		}
		return templateName;
	}
	
	/**
	 * @constant
	 * User defined preference variable name,
	 * Site preference
	 * Multiple values 
	 */
	public final static String HelpPreferenceName = "YFJC_dynamic_help_mapping";
	
	/**
	 * Get team center preference variable name
	 * @param name : name of user defined preference variable, site preference, multiple string value
	 * 	in this class it should be HelpPreferenceName
	 * @return : return user defined preference string values
	 */
	public static String[] GetPreferenceValues(String name)
	{
		AIFDesktop desk = AIFDesktop.getActiveDesktop();
		AbstractAIFUIApplication application = desk.getCurrentApplication();
		TCSession tcsession = (TCSession) application.getSession();
		TCPreferenceService pref = tcsession.getPreferenceService();
		return pref.getStringArray(TCPreferenceService.TC_preference_site, name);
	}
	
	public final static String RuleSeperator = "=";
	public final static String WarningDialogTitle_PreferenceDefinitionIssue = reg.getString("warning_title_preference_issue");//"帮助首选项定义";
	public final static String WarningMessage_PreferenceDefinitionIssue = reg.getString("warning_msg_preference_issue");//"首选项\"%1$s\"中定义的\"%2$s\"规则格式有误，请联系管理员！";
	
	/**
	 * Get the TC help folder, it's a sub folder of TC Home folder.
	 * @param name : the name of help folder, it's a sub folder of TC Home folder.
	 * @return : return the object of help folder
	 * @throws TCException
	 */
	public static TCComponentFolder GetHelpFolder(String name)
			throws TCException
	{
		AIFDesktop desk = AIFDesktop.getActiveDesktop();
		AbstractAIFUIApplication application = desk.getCurrentApplication();
		TCSession tcsession=(TCSession)application.getSession();
		TCComponentUserType userType = (TCComponentUserType)tcsession.getTypeComponent("User");
		TCComponentUser infodba = userType.find("infodba");
		TCComponentFolder home = infodba.getHomeFolder();
		TCComponent[] comList = home.getRelatedComponents("contents");
		TCComponentFolder folder = null;
		for (int i = 0; i < comList.length; i++)
		{
			if (comList[i] instanceof TCComponentFolder)
			{
				TCComponentFolder temp_folder = (TCComponentFolder) comList[i];
				//System.out.println("Folder in Home   : " + temp_folder.toDisplayString());
				//System.out.println("Folder in Search : " + name);
				if(name.equals(temp_folder.toDisplayString()))
				{
					folder = temp_folder;
					break;
				}
			}
		}
		if(null == folder)
		{
			MessageBox.post(reg.getString("helpfolder_not_found"), reg.getString("box_title"), MessageBox.ERROR);
		}

		return folder;
	}
	
	/**
	 * 
	 * @param help_folder : object of TC help folder
	 * @param file_name : file name of pdf/word format help document
	 * @return : return a dataset if it contains the file_name
	 * if the file_name could not be found, it will return nulls
	 * @throws TCException
	 */
	public static TCComponent GetSpecDataset(TCComponentFolder help_folder, String file_name)
		throws TCException
	{
		TCComponent[] comps = help_folder.getRelatedComponents("contents");
		TCComponent dataset = null;
		for(TCComponent comp : comps)
		{
			// get all datasets
			if(comp instanceof TCComponentDataset)
			{
				TCComponentDataset temp_dataset = ((TCComponentDataset) comp).latest();			
				TCComponentDatasetDefinition dataset_definition = temp_dataset.getDatasetDefinitionComponent();		
				if(null == dataset_definition)
					continue;
				
				String[] namedrefs = dataset_definition.getNamedReferences();
				if(null == namedrefs)
					continue;
				
				// get all files of dataset to find the file specified
				for(String named_ref : dataset_definition.getNamedReferences())
				{
					String[] dataset_files = temp_dataset.getFileNames(named_ref);
					if(null == dataset_files)
					{
						continue;
					}
					
					for(String dataset_file : dataset_files)
					{
						//System.out.println("file in dataset : " + dataset_file);
						//System.out.println("file to find    : " + file_name);
						// file found 
						if(dataset_file.equalsIgnoreCase(file_name))
						{
							dataset = temp_dataset;
							break;
						}
					}
				
					if(null != dataset)
						break;
				}
				
				if(null != dataset)
					break;
			}
			
			//find the specific url object
			else if(comp instanceof TCComponentForm){
				TCComponentForm	 temp_form = ((TCComponentForm) comp);
				if (temp_form.getProperty("object_name").equals(file_name));
				{
					dataset = temp_form;
					break;
				}
			}
		}
		return dataset;
		
	}

	public static final String HelpDocumentName = "ProcessTemplate_HelpFolder";
	
	/**
	 * 
	 * @param parent : parent of error dialog
	 * @param filename : file name of help document
	 * @param rule : rule definition in preference
	 */
	public static void OpenDatasetFile(Shell parent, String filename,
			String rule)
	{
		try
		{
			TCComponentFolder help = DynamicHelpUtils.GetHelpFolder(HelpDocumentName);
			if(null == help)
				return;
			//System.out.println(help.toDisplayString());
			TCComponent dataset = DynamicHelpUtils.GetSpecDataset(help, filename);
			if(null == dataset)
			{
				MessageBox.post(reg.getString("helpfile_not_found_one")+" " + filename + " " + reg.getString("helpfile_not_found_two"), reg.getString("box_title"), MessageBox.ERROR);
				
			}
			else
			{
				
				//System.out.println(dataset.toStringLabel());
				//判断dataset是数据集还是url对象
				if(dataset instanceof TCComponentDataset)
				{
				try
				{
					((TCComponentDataset) dataset).openForView();
				}
				catch(IOException exp)
				{
					// do nothing....
				}
				}
				else if(dataset instanceof TCComponentForm)
				{
					try {
						TCSession se1 = dataset.getSession();
						String urlProperty = dataset.getProperty("url");
						OpenCommand.openWebLink( dataset.toString(), urlProperty);
					} catch (TCException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
				}
			}
		}
		catch(TCException exp)
		{
			
		}
	}
	
	public final static String ErrorDialogTitle_HelpDocNotFound = reg.getString("error_title_helpdoc_not_found");//"帮助文件找不到";
	public final static String ErrorMsg_HelpDocNotFound = reg.getString("error_msg_helpdoc_not_found");//"首选项\"%1$s\"中定义的\"%2$s\"规则找不到对应的帮助文件，请联系管理员！";

	public final static String ErrorDialogTitle_PreferenceDefinitionNotFound = reg.getString("error_title_preference_not_found");//"首选项";
	public final static String ErrorMsg_HelpPreferenceNotFound = reg.getString("error_msg_preference_not_found");//"首选项\"%1$s\"未定义";

	public final static String SelectHelpDocDialogTitle = reg.getString("select_helpdoc_title");//"选择帮助文件";

	/**
	 * Main workflow of show help
	 * @param parent : parent of all error/warning/input/output dialogs
	 */
	public static void ShowHelp(Shell parent)
	{
		String template_name = GetCurrentTemplateName();
		if(template_name == null){
			return;
		}
		String task_name = GetCurrentTaskName();
		if(task_name == null){
			return;
		}

		// get help prefix
		String help_prompt = String
				.format("%s~%s", template_name, task_name);
		
		//System.out.println("dynamic help prompt = " + help_prompt);
		
		// get preference
		String[] preference_values = GetPreferenceValues(HelpPreferenceName);
		//System.out.println("preference_values = " + (null == preference_values ? "null" : "not null"));
		if (null == preference_values || preference_values.length < 1)
		{
			MessageDialog.openError(parent,
					ErrorDialogTitle_PreferenceDefinitionNotFound, String
							.format(ErrorMsg_HelpPreferenceNotFound,
									HelpPreferenceName));
			//MessageBox.post(String.format(ErrorMsg_HelpPreferenceNotFound,HelpPreferenceName),
			//		ErrorDialogTitle_PreferenceDefinitionNotFound, MessageBox.ERROR);
			return;
		}

		// find rules start with help prompt prefix
		List<String> help_rules = new ArrayList<String>();
		List<String> help_docs = new ArrayList<String>();
		for (String help_rule : preference_values)
		{
			//System.out.println("Trying to match :" + help_rule);
			if (help_rule.startsWith(help_prompt))
			{
				//System.out.println("Matched rule = " + help_rule);
				String[] help_defs = help_rule.split(RuleSeperator);
				if (2 == help_defs.length)
				{
					help_rules.add(help_rule);
					help_docs.add(help_defs[1]);
					//System.out.println("Matched Document = " + help_defs[1]);
				}
				else
				{
					// show warning message, but the workflow will continue
					MessageDialog.openWarning(parent,
							WarningDialogTitle_PreferenceDefinitionIssue,
							String.format(
									WarningMessage_PreferenceDefinitionIssue,
									HelpPreferenceName, help_rule));
				}
			}
		}
		
		if(help_docs.size() < 1)
		{
			MessageBox.post(reg.getString("preference_not_match") + help_prompt, 
					reg.getString("box_title"), MessageBox.ERROR);
			return;
		}
		else if(help_docs.size() == 1) // show document directly if there is only one document can be show
		{
			//System.out.println("The help matched is =" + help_rules.get(0));
			OpenDatasetFile(parent, help_docs.get(0), help_rules.get(0));
		}
		else
		{
			// open selection dialog,
			DynamicHelpSelectionDialog dialog = new DynamicHelpSelectionDialog(parent,
				SelectHelpDocDialogTitle, help_docs);
			if (dialog.open() == Window.OK)
			{
				String doc_name = dialog.GetSelectedHelp();
				//System.out.println(doc_name);
				int index = help_docs.indexOf(doc_name);
				OpenDatasetFile(parent, doc_name, help_rules.get(index));
			}
		}
	}
	
	
	
}
	
