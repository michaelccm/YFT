package com.casic.handlers;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

public class DynamicHelpSelectionDialog extends Dialog
{
	private String m_dialog_name;
	private List<String> m_help_items;
	private String m_selected_item;
	private java.util.List<Button> m_btns;

	public DynamicHelpSelectionDialog(Shell parent, String name, List<String> items)
	{
		super(parent);
		this.m_dialog_name = name;
		this.m_help_items = items;
	}
	
	@Override
	protected Control createDialogArea(Composite parent)
	{
		Composite container = (Composite)super.createDialogArea(parent);
		container.setLayout(new FillLayout());
		Group sc = new Group(container, SWT.VERTICAL|SWT.HORIZONTAL);
		sc.setText("Please choose help document:");
		
		final GridLayout gl = new GridLayout();
		gl.numColumns = 1;
		sc.setLayout(gl);
		CreateRadioButton(sc);
		return container;
	}
	
	@Override
	protected void configureShell(Shell new_shell)
	{
		super.configureShell(new_shell);
		new_shell.setText(this.m_dialog_name);
	}
	
	public String GetSelectedHelp()
	{
		return this.m_selected_item;
	}
	
	private void CreateRadioButton(Composite parent)
	{
		
		m_btns = new java.util.ArrayList<Button>();
		for(String help_item : m_help_items)
		{
			final Button btn = new Button(parent, SWT.RADIO);
			m_btns.add(btn);
			btn.setText(help_item);
			btn.addSelectionListener(new SelectionAdapter()
			{
				@Override
				public void widgetSelected(SelectionEvent e)
				{
					if(e.widget instanceof Button)
					{
						Button selected = (Button)e.widget;
						m_selected_item = selected.getText();
						for(int i = 0; i < m_btns.size(); ++i)
						{
							if(selected != m_btns.get(i))
								m_btns.get(i).setSelection(false);
							selected.setSelection(true);
						}
					}
				}
			});
		}
	}
}
