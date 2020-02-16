package com.teamcenter.rac.project;

import com.teamcenter.rac.kernel.TCComponentProject;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.rac.util.controls.ComplementingViewerFilter;
import com.teamcenter.rac.util.controls.SelectionViewerComposite;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class MySelectionViewerComposite
  implements ISelectionProvider
{
  private static final Logger logger = Logger.getLogger(MySelectionViewerComposite.class);
  private static Registry m_Reg = Registry.getRegistry(SelectionViewerComposite.class);
  private Composite m_leftViewerComposite;
  private Composite m_rightViewerComposite;
  private StructuredViewer m_leftSideViewer;
  private StructuredViewer m_rightSideViewer;
  private List<? extends Object> m_selectedObjects;
  private Button m_shiftToRight;
  private Button m_shiftToLeft;
  private Button m_shiftAllToRight;
  private Button m_shiftAllToLeft;
  private ListenerList m_selectionChangedListeners = new ListenerList();
  public int number = 0;

  public MySelectionViewerComposite(Composite paramComposite, ViewerConfigurator paramViewerConfigurator)
  {
	number = 0;
    if (paramViewerConfigurator == null)
      paramViewerConfigurator = new ViewerConfigurator();
    Composite localComposite1 = new Composite(paramComposite, 0);
    localComposite1.setLayout(new GridLayout(1, false));
    localComposite1.setLayoutData(new GridData(4, 4, true, true));
    localComposite1.setBackground(paramComposite.getBackground());
    Composite localComposite2 = new Composite(localComposite1, 0);
    localComposite2.setBackground(paramComposite.getBackground());
    localComposite2.setLayout(new GridLayout(3, false));
    localComposite2.setLayoutData(new GridData(4, 4, true, true));
    Composite localComposite3 = new Composite(localComposite2, 0);
    localComposite3.setLayout(new GridLayout(1, false));
    localComposite3.setLayoutData(new GridData(4, 4, true, true));
    localComposite3.setBackground(paramComposite.getBackground());
    Label localLabel1 = new Label(localComposite3, 16384);
    localLabel1.setBackground(paramComposite.getBackground());
    String str1 = paramViewerConfigurator.getLeftTitle();
    if (str1 != null)
      localLabel1.setText(str1);
    this.m_leftViewerComposite = new Composite(localComposite3, 0);
    this.m_leftViewerComposite.setBackground(paramComposite.getBackground());
    this.m_leftViewerComposite.setLayout(new FillLayout(256));
    this.m_leftViewerComposite.setLayoutData(new GridData(4, 4, true, true, 1, 1));
    Composite localComposite4 = new Composite(localComposite2, 0);
    localComposite4.setLayout(new GridLayout(1, false));
    localComposite4.setBackground(paramComposite.getBackground());
    this.m_shiftAllToRight = new Button(localComposite4, 0);
    this.m_shiftAllToRight.setImage(m_Reg.getImage("shiftAllToRight.ICON"));
    this.m_shiftAllToRight.setEnabled(false);
    String str2 = paramViewerConfigurator.getToolTipForShiftAllToRightButton();
    if (str2 != null)
      this.m_shiftAllToRight.setToolTipText(str2);
    this.m_shiftToRight = new Button(localComposite4, 0);
    this.m_shiftToRight.setImage(m_Reg.getImage("rightShif.ICON"));
    this.m_shiftToRight.setEnabled(false);
    this.m_shiftToRight.setData("ToRight");
    str2 = paramViewerConfigurator.getToolTipForRightShiftButton();
    if (str2 != null)
      this.m_shiftToRight.setToolTipText(str2);
    this.m_shiftToLeft = new Button(localComposite4, 0);
    this.m_shiftToLeft.setImage(m_Reg.getImage("leftShift.ICON"));
    this.m_shiftToLeft.setEnabled(false);
    this.m_shiftToLeft.setData("ToLeft");
    str2 = paramViewerConfigurator.getToolTipForLeftShiftButton();
    if (str2 != null)
      this.m_shiftToLeft.setToolTipText(str2);
    this.m_shiftAllToLeft = new Button(localComposite4, 0);
    this.m_shiftAllToLeft.setImage(m_Reg.getImage("shiftAllToLeft.ICON"));
    this.m_shiftAllToLeft.setEnabled(false);
    str2 = paramViewerConfigurator.getToolTipForShiftAllToLeftButton();
    if (str2 != null)
      this.m_shiftAllToLeft.setToolTipText(str2);
    this.m_shiftAllToRight.addSelectionListener(new SelectionAdapter()
    {
      public void widgetSelected(SelectionEvent paramAnonymousSelectionEvent)
      {
        IStructuredContentProvider localIStructuredContentProvider = (IStructuredContentProvider)MySelectionViewerComposite.this.m_leftSideViewer.getContentProvider();
        Object[] arrayOfObject = localIStructuredContentProvider.getElements(MySelectionViewerComposite.this.m_leftSideViewer.getInput());
        MySelectionViewerComposite.this.m_selectedObjects.clear();
        MySelectionViewerComposite.this.m_selectedObjects.addAll(new ArrayList(Arrays.asList(arrayOfObject)));
        MySelectionViewerComposite.this.m_leftSideViewer.refresh();
        MySelectionViewerComposite.this.m_rightSideViewer.refresh();
        MySelectionViewerComposite.this.updateButtons();
        MySelectionViewerComposite.this.fireSelectionChangedEvent(new StructuredSelection(arrayOfObject));
      }
    });
    this.m_shiftToRight.addSelectionListener(new SelectionAdapter()
    {
      public void widgetSelected(SelectionEvent paramAnonymousSelectionEvent)
      {
        IStructuredSelection localIStructuredSelection = (IStructuredSelection)MySelectionViewerComposite.this.m_leftSideViewer.getSelection();
        MySelectionViewerComposite.this.m_selectedObjects.addAll(localIStructuredSelection.toList());
        MySelectionViewerComposite.this.m_leftSideViewer.refresh();
        MySelectionViewerComposite.this.m_rightSideViewer.refresh();
        MySelectionViewerComposite.this.updateButtons();
        MySelectionViewerComposite.this.fireSelectionChangedEvent(localIStructuredSelection);
        number++;
      }
    });
    this.m_shiftToLeft.addSelectionListener(new SelectionAdapter()
    {
      public void widgetSelected(SelectionEvent paramAnonymousSelectionEvent)
      {
        IStructuredSelection localIStructuredSelection = (IStructuredSelection)MySelectionViewerComposite.this.m_rightSideViewer.getSelection();
        MySelectionViewerComposite.this.m_selectedObjects.removeAll(localIStructuredSelection.toList());
        MySelectionViewerComposite.this.m_leftSideViewer.refresh();
        MySelectionViewerComposite.this.m_rightSideViewer.refresh();
        MySelectionViewerComposite.this.updateButtons();
        MySelectionViewerComposite.this.fireSelectionChangedEvent(localIStructuredSelection);
        number--;
      }
    });
    this.m_shiftAllToLeft.addSelectionListener(new SelectionAdapter()
    {
      public void widgetSelected(SelectionEvent paramAnonymousSelectionEvent)
      {
    	  MySelectionViewerComposite.this.m_selectedObjects.clear();
    	  MySelectionViewerComposite.this.m_leftSideViewer.refresh();
    	  MySelectionViewerComposite.this.m_rightSideViewer.refresh();
    	  MySelectionViewerComposite.this.updateButtons();
    	  MySelectionViewerComposite.this.fireSelectionChangedEvent(StructuredSelection.EMPTY);
      }
    });
    Composite localComposite5 = new Composite(localComposite2, 0);
    localComposite5.setLayout(new GridLayout(1, false));
    localComposite5.setLayoutData(new GridData(4, 4, true, true));
    localComposite5.setBackground(paramComposite.getBackground());
    Label localLabel2 = new Label(localComposite5, 16384);
    localLabel2.setBackground(paramComposite.getBackground());
    str1 = paramViewerConfigurator.getRightTitle();
    if (str1 != null)
      localLabel2.setText(str1);
    this.m_rightViewerComposite = new Composite(localComposite5, 0);
    this.m_rightViewerComposite.setLayout(new FillLayout(256));
    this.m_rightViewerComposite.setLayoutData(new GridData(4, 4, true, true, 1, 1));
    this.m_rightViewerComposite.setBackground(paramComposite.getBackground());
  }

  public void setViewer(StructuredViewer paramStructuredViewer)
  {
    this.m_leftSideViewer = paramStructuredViewer;
    this.m_leftSideViewer.addSelectionChangedListener(new IC_SelectionChangedListener(this.m_shiftToRight,this.m_leftSideViewer,this));
    this.m_leftSideViewer.addFilter(new ComplementingViewerFilter(new IC_SelectedObjectFilter()));
    if (this.m_rightSideViewer == null)
      this.m_rightSideViewer = cloneViewer(paramStructuredViewer);
    if (this.m_rightSideViewer != null)
    {
      this.m_rightSideViewer.setContentProvider(paramStructuredViewer.getContentProvider());
      this.m_rightSideViewer.setLabelProvider(paramStructuredViewer.getLabelProvider());
      this.m_rightSideViewer.setSorter(paramStructuredViewer.getSorter());
      this.m_rightSideViewer.setComparator(paramStructuredViewer.getComparator());
      this.m_rightSideViewer.setInput(this.m_leftSideViewer.getInput());
      this.m_rightSideViewer.addSelectionChangedListener(new IC_SelectionChangedListener(this.m_shiftToLeft,this.m_leftSideViewer,this));
      this.m_rightSideViewer.addFilter(new IC_SelectedObjectFilter());
      this.m_rightViewerComposite.layout(true);
    }
  }

  public Composite getComposite()
  {
    return this.m_leftViewerComposite;
  }

  private StructuredViewer cloneViewer(StructuredViewer paramStructuredViewer)
  {
    StructuredViewer localStructuredViewer = null;
    Class localClass = paramStructuredViewer.getClass();
    Class[] arrayOfClass = new Class[1];
    arrayOfClass[0] = Composite.class;
    try
    {
      Constructor localConstructor = localClass.getConstructor(arrayOfClass);
      localStructuredViewer = (StructuredViewer)localConstructor.newInstance(new Object[] { this.m_rightViewerComposite });
    }
    catch (SecurityException localSecurityException)
    {
      logger.error(localSecurityException.getLocalizedMessage(), localSecurityException);
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      logger.error(localNoSuchMethodException.getLocalizedMessage(), localNoSuchMethodException);
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      logger.error(localIllegalArgumentException.getLocalizedMessage(), localIllegalArgumentException);
    }
    catch (InstantiationException localInstantiationException)
    {
      logger.error(localInstantiationException.getLocalizedMessage(), localInstantiationException);
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      logger.error(localIllegalAccessException.getLocalizedMessage(), localIllegalAccessException);
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      logger.error(localInvocationTargetException.getLocalizedMessage(), localInvocationTargetException);
    }
    return localStructuredViewer;
  }

  private void updateButtons()
  {
    if ((this.m_selectedObjects != null) && (this.m_leftSideViewer != null))
    {
      IStructuredContentProvider localIStructuredContentProvider = (IStructuredContentProvider)this.m_leftSideViewer.getContentProvider();
      Object[] arrayOfObject = localIStructuredContentProvider.getElements(this.m_leftSideViewer.getInput());
      boolean bool = (arrayOfObject != null) && (this.m_selectedObjects.size() != arrayOfObject.length);
      //this.m_shiftAllToRight.setEnabled(bool);
      //this.m_shiftAllToLeft.setEnabled(!this.m_selectedObjects.isEmpty());
      //yfjc
      this.m_shiftAllToRight.setEnabled(false);
      this.m_shiftAllToLeft.setEnabled(false);
    }
  }

  public void addSelectionChangedListener(ISelectionChangedListener paramISelectionChangedListener)
  {
    this.m_selectionChangedListeners.add(paramISelectionChangedListener);
  }

  public void removeSelectionChangedListener(ISelectionChangedListener paramISelectionChangedListener)
  {
    this.m_selectionChangedListeners.remove(paramISelectionChangedListener);
  }

  public ISelection getSelection()
  {
    StructuredSelection localStructuredSelection = new StructuredSelection(this.m_selectedObjects);
    return localStructuredSelection;
  }

  public TCComponentProject getMySelection()
  {
	TCComponentProject proj = null;
    StructuredSelection ss = new StructuredSelection(this.m_selectedObjects);
	  List list = ss.toList();
	  for (int i = 0; i < list.size(); i++) {
		if(list.get(i) instanceof TCComponentProject){
			proj = (TCComponentProject)list.get(i);
			System.out.println("get the project ");
		}
	}
    return proj;
  }
  public StructuredViewer getM_rightSideViewer() {
	return m_rightSideViewer;
}

public void setM_rightSideViewer(StructuredViewer m_rightSideViewer) {
	this.m_rightSideViewer = m_rightSideViewer;
}

public void setSelection(ISelection paramISelection)
  {
    if ((paramISelection instanceof StructuredSelection))
    {
      this.m_selectedObjects = new ArrayList();
      this.m_selectedObjects.addAll(((StructuredSelection)paramISelection).toList());
      if ((this.m_leftSideViewer != null) && (this.m_rightSideViewer != null))
      {
        this.m_leftSideViewer.refresh();
        this.m_rightSideViewer.refresh();
      }
      updateButtons();
    }
  }

  private void fireSelectionChangedEvent(IStructuredSelection paramIStructuredSelection)
  {
    Object[] arrayOfObject1 = this.m_selectionChangedListeners.getListeners();
    for (Object localObject : arrayOfObject1)
      ((ISelectionChangedListener)localObject).selectionChanged(new SelectionChangedEvent(this, paramIStructuredSelection));
  }

  private class IC_SelectedObjectFilter extends ViewerFilter
  {
    private IC_SelectedObjectFilter()
    {
    }

    public boolean select(Viewer paramViewer, Object paramObject1, Object paramObject2)
    {
      boolean bool = false;
      if ((MySelectionViewerComposite.this.m_selectedObjects != null) && (MySelectionViewerComposite.this.m_selectedObjects.contains(paramObject2)))
        bool = true;
      return bool;
    }
  }

  private static class IC_SelectionChangedListener
    implements ISelectionChangedListener
  {
    private Button m_associatedButton;
    private StructuredViewer sv;
    private MySelectionViewerComposite svc;

    public IC_SelectionChangedListener(Button paramButton,StructuredViewer sv,MySelectionViewerComposite svc)
    {
      this.m_associatedButton = paramButton;
      this.sv = sv;
      this.svc = svc;
    }

    public void selectionChanged(SelectionChangedEvent paramSelectionChangedEvent)
    {
      ISelection localISelection = paramSelectionChangedEvent.getSelection();
      Object[] objs = null;
      if(localISelection instanceof StructuredSelection){
    	  StructuredSelection ss = (StructuredSelection)localISelection;
    	  objs = ss.toArray();
      }
      if(this.m_associatedButton.getData().equals("ToRight")){
    	  //获取右边个数
          if(objs != null && objs.length ==1&& this.svc.number == 0){
        	  this.m_associatedButton.setEnabled(true);
          }
          else{
        	  this.m_associatedButton.setEnabled(false);
          }
      }
      else{
    	  if(objs != null && objs.length ==1)
        	  this.m_associatedButton.setEnabled(true);
          else
        	  this.m_associatedButton.setEnabled(false);
      }
    }
  }

  public static class ViewerConfigurator
  {
    public String getLeftTitle()
    {
      return null;
    }

    public String getRightTitle()
    {
      return null;
    }

    public String getToolTipForLeftShiftButton()
    {
      return null;
    }

    public String getToolTipForRightShiftButton()
    {
      return null;
    }

    public String getToolTipForShiftAllToLeftButton()
    {
      return null;
    }

    public String getToolTipForShiftAllToRightButton()
    {
      return null;
    }
  }
}