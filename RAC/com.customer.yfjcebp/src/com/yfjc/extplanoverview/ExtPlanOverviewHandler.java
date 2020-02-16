package com.yfjc.extplanoverview;

import java.io.File;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentContextList;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.kernel.TCComponentQueryType;
import com.teamcenter.rac.kernel.TCComponentTcFile;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.TCTextService;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;
import com.yfjc.sdtinput.SDTMemberInputDialog;

/*
 * author:tyl
 * 2015/01/23
 * @LCC查询圈人计划表查询
 */
public class ExtPlanOverviewHandler extends AbstractHandler {

	TCSession session;
	private Registry registry = null;

	// String datasetName = "LCC Report Template.xlsx";
	// String parentFolderName = "EBP报表配置模板";
	private String YFJC_SearchExtDayHrReleased_query = "YFJC_SearchExtDayHrReleased";
	private String YFJC_SearchLCCForm_query = "YFJC_SearchLCCForm";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		session = (TCSession) AIFUtility.getCurrentApplication().getSession();
		registry = Registry.getRegistry(this);
		if (!isQueryExist(session, YFJC_SearchExtDayHrReleased_query)) {
			return null;
		}
		if (!isQueryExist(session, YFJC_SearchLCCForm_query)) {
			return null;
		}
		new Thread() {
			public void run() {
				File templateFile = getTemplate(registry.getString("reportTemplateDataset.name"), registry.getString("reportTemplateFolder.name"));
				if (templateFile == null) {
					MessageBox.post(registry.getString("messagebox.notFindTemplate"), "WARNING", MessageBox.WARNING);
					return;
				}
				// templateFile=new File("c:/LCC Report.xlsx");
				YearMonthDialog dialog = new YearMonthDialog(templateFile);
			}
		}.start();
		return null;
	}

	public File getTemplate(String datasetName, String parentFolderName) {
		File theFile = null;
		try {
			TCComponent[] comps = query(this.session, "General...", new String[] { "object_name" }, new String[] { parentFolderName });
			if (comps != null && comps.length > 0) {
				if (comps[0] instanceof TCComponentFolder) {
					TCComponentFolder parentFolder = (TCComponentFolder) comps[0];
					AIFComponentContext[] context1 = parentFolder.getChildren();
					for (int i = 0; i < context1.length; i++) {
						TCComponent comp1 = (TCComponent) context1[i].getComponent();
						if (comp1 instanceof TCComponentDataset) {
							TCComponentDataset ds = (TCComponentDataset) comp1;
							if (ds.getType().contains("MSExcel") && ds.getProperty("object_name").equals(datasetName)) {
								TCComponentTcFile[] tcfiles = ds.getTcFiles();
								if (tcfiles != null && tcfiles.length > 0) {
									theFile = tcfiles[0].getFmsFile();
								}
							}
						}
					}
				}
			}
		} catch (TCException e) {
			e.printStackTrace();
		}
		return theFile;
	}

	public boolean isQueryExist(TCSession session, String query_name) {
		boolean exist = false;
		try {
			TCComponentQueryType imancomponentquerytype = (TCComponentQueryType) session.getTypeComponent("ImanQuery");
			TCComponentQuery imancomponentquery = (TCComponentQuery) imancomponentquerytype.find(query_name);
			if (imancomponentquery != null) {
				exist = true;
			} else {
				MessageBox.post("query :" + query_name + "is not exist！", "WARNING", MessageBox.WARNING);
			}
		} catch (TCException e) {
			e.printStackTrace();
		}
		return exist;
	}

	public TCComponent[] query(TCSession session, String query_name, String[] arg1, String[] arg2) {
		TCComponentContextList imancomponentcontextlist = null;
		TCComponent[] component = null;
		try {
			TCComponentQueryType imancomponentquerytype = (TCComponentQueryType) session.getTypeComponent("ImanQuery");
			TCComponentQuery imancomponentquery = (TCComponentQuery) imancomponentquerytype.find(query_name);
			TCTextService imantextservice = session.getTextService();
			String[] queryAttribute = new String[arg1.length];
			for (int i = 0; i < arg1.length; ++i)
				queryAttribute[i] = imantextservice.getTextValue(arg1[i]);

			String[] queryValues = new String[arg2.length];
			for (int i = 0; i < arg2.length; ++i)
				queryValues[i] = arg2[i];

			imancomponentcontextlist = imancomponentquery.getExecuteResultsList(queryAttribute, queryValues);
			component = imancomponentcontextlist.toTCComponentArray();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return component;
	}

}
