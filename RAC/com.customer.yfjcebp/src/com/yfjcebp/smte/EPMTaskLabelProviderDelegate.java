package com.yfjcebp.smte;

import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentTask;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.providers.PropertiesLoader;
import com.teamcenter.rac.providers.delegates.property.NameTypeLabelProviderDelegate;
import com.teamcenter.rac.util.Registry;
import java.util.List;
import org.apache.log4j.Logger;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;


public class EPMTaskLabelProviderDelegate extends NameTypeLabelProviderDelegate
{
  private Font boldFont;
  private static String[] TASK_PROPERTIES = { "viewed_by_me", "job_name", "object_name", "late_flag", "due_date", "work_context", "has_surrogate" };

  public EPMTaskLabelProviderDelegate()
  {
    Font localFont = JFaceResources.getDefaultFont();
    FontData[] arrayOfFontData = localFont.getFontData();
    this.boldFont = new Font(Display.getDefault(), new FontData(arrayOfFontData[0].getName(), arrayOfFontData[0].getHeight(), 1));
  }

  public Font getFont(Object paramObject)
  {
    String str = getProperty();
    if (("object_string".equals(str)) || ("object_name".equals(str)))
    {
      TCComponent localTCComponent = getTCComponent(paramObject);
      if ((localTCComponent instanceof TCComponentTask))
      {
        preloadTaskProperties((TCComponentTask)localTCComponent);
        if (!isTaskViewedByMe((TCComponentTask)localTCComponent))
          return this.boldFont;
      }
    }
    return null;
  }

  public String getText(Object paramObject)
  {
    TCComponent localTCComponent = getTCComponent(paramObject);
    String str = getProperty();
    if (((str == null) || ("object_string".equals(str))) && ((localTCComponent instanceof TCComponentTask)))
      try
      {
        preloadTaskProperties((TCComponentTask)localTCComponent);
        return ((TCComponentTask)localTCComponent).toString2();
      }
      catch (TCException localTCException)
      {
        Logger.getLogger(EPMTaskLabelProviderDelegate.class).error(localTCException.getLocalizedMessage(), localTCException);
      }
    return super.getText(paramObject);
  }

  public Image getImage(Object paramObject)
  {
    String str = getProperty();
    if (("object_string".equals(str)) || ("object_name".equals(str)))
    {
      TCComponent localTCComponent = getTCComponent(paramObject);
      if ((localTCComponent instanceof TCComponentTask))
      {
        preloadTaskProperties((TCComponentTask)localTCComponent);
        return getComponentImage(localTCComponent);
      }
    }
    return null;
  }

  public Color getForeground(Object paramObject)
  {
    String str = getProperty();
    if (("object_string".equals(str)) || ("object_name".equals(str)))
    {
      TCComponent localTCComponent = getTCComponent(paramObject);
      if ((localTCComponent instanceof TCComponentTask))
      {
        preloadTaskProperties((TCComponentTask)localTCComponent);
        if (isTaskLate((TCComponentTask)localTCComponent))
          return Display.getDefault().getSystemColor(3);
        if (isDueDateSetOnTask((TCComponentTask)localTCComponent))
          return Display.getDefault().getSystemColor(6);
      }
    }
    return null;
  }

  public void dispose()
  {
    if (this.boldFont != null)
      this.boldFont.dispose();
  }

  protected boolean isTaskViewedByMe(TCComponentTask paramTCComponentTask)
  {
    try
    {
      return paramTCComponentTask.isViewedByMe();
    }
    catch (TCException localTCException)
    {
      Logger.getLogger(EPMTaskLabelProviderDelegate.class).error(localTCException.getLocalizedMessage(), localTCException);
    }
    return false;
  }

  protected boolean isTaskLate(TCComponentTask paramTCComponentTask)
  {
    try
    {
      return paramTCComponentTask.getLateFlag();
    }
    catch (TCException localTCException)
    {
      Logger.getLogger(EPMTaskLabelProviderDelegate.class).error(localTCException.getLocalizedMessage(), localTCException);
    }
    return false;
  }

  protected boolean isDueDateSetOnTask(TCComponentTask paramTCComponentTask)
  {
    try
    {
      return paramTCComponentTask.getDueDate() != null;
    }
    catch (TCException localTCException)
    {
      Logger.getLogger(EPMTaskLabelProviderDelegate.class).error(localTCException.getLocalizedMessage(), localTCException);
    }
    return false;
  }

  protected void appendTrailingImages(List<Image> paramList, Object paramObject)
  {
    super.appendTrailingImages(paramList, paramObject);
    TCComponent localTCComponent = getTCComponent(paramObject);
    if ((localTCComponent instanceof TCComponentTask))
    {
      TCComponentTask localTCComponentTask = (TCComponentTask)localTCComponent;
      preloadTaskProperties(localTCComponentTask);
      setStatusIcon(localTCComponentTask, paramList);
      if (hasSurrogate(localTCComponentTask))
      {
        Registry localRegistry = Registry.getRegistry("com.teamcenter.rac.common.common");
        Image localImage = localRegistry.getImage("surrogateTask.ICON");
        paramList.add(localImage);
      }
    }
  }

  private void setStatusIcon(TCComponentTask paramTCComponentTask, List<Image> paramList)
  {
    int i = 0;
    try
    {
      i = paramTCComponentTask.getTCProperty("fnd0MyTaskExecutionStatus").getIntValue();
    }
    catch (TCException localTCException)
    {
      Logger.getLogger(EPMTaskLabelProviderDelegate.class).error(localTCException.getLocalizedMessage(), localTCException);
      i = 2;
    }
    Registry localRegistry;
    Image localImage;
    if (i == 2)
    {
      localRegistry = Registry.getRegistry("com.teamcenter.rac.common.common");
      localImage = localRegistry.getImage("defaultError.ICON");
      paramList.add(localImage);
    }
    else if (i == 1)
    {
      localRegistry = Registry.getRegistry("com.teamcenter.rac.common.common");
      localImage = localRegistry.getImage("SRP0PrSrvRequest.ICON");
      paramList.add(localImage);
    }
  }

  protected boolean hasSurrogate(TCComponentTask paramTCComponentTask)
  {
    try
    {
      return paramTCComponentTask.getTCProperty("has_surrogate").getLogicalValue();
    }
    catch (TCException localTCException)
    {
      Logger.getLogger(EPMTaskLabelProviderDelegate.class).error(localTCException.getLocalizedMessage(), localTCException);
    }
    return false;
  }

  private void preloadTaskProperties(TCComponentTask paramTCComponentTask)
  {
    PropertiesLoader.getInstance().loadProperties(new Object[] { paramTCComponentTask }, TASK_PROPERTIES, true);
  }
}