package com.yfjcebp.importdata.importextrate;

import java.io.File;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;
import com.yfjcebp.importdata.utils.OriginUtil;

public class ImportExtRateHandler extends AbstractHandler{

	private TCSession session;
	private AbstractAIFUIApplication app;
	private File excelFile;
	
	
	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		app = AIFUtility.getCurrentApplication();
		session = (TCSession) app.getSession();
		InterfaceAIFComponent component = app.getTargetComponent();
		excelFile = checkImportFile((TCComponent)component);
		if (excelFile == null) {
			return null;
		}
		session.setStatus("import ExtRate data£¬waiting...");
		session.queueOperation(new ImportExtRateOperation(session, excelFile, (TCComponentDataset)component));
		session.setReadyStatus();
		return null;
	}
	
	
	private File checkImportFile(TCComponent component) {
		excelFile = OriginUtil.checkImportFile(component);
		return excelFile;
	}

}