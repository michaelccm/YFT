package com.casic.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import com.teamcenter.rac.kernel.TCComponentBOMLine;

public class DynamicHelpHandler extends AbstractHandler
{
	/**
	 * The constructor.
	 */
	public DynamicHelpHandler()
	{
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		DynamicHelpUtils.ShowHelp(window.getShell());
		return null;
	}
}
