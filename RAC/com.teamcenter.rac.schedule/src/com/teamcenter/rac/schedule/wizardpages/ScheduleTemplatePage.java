package com.teamcenter.rac.schedule.wizardpages;

import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.common.lov.view.controls.LOVDisplayer;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.kernel.TCComponentQueryType;
import com.teamcenter.rac.kernel.TCComponentTCCalendar;
import com.teamcenter.rac.kernel.TCComponentType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPropertyDescriptor;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.TCTextService;
import com.teamcenter.rac.schedule.commands.newschedule.NewScheduleHandler;
import com.teamcenter.rac.schedule.componentutils.CalendarHelper;
import com.teamcenter.rac.schedule.project.common.l10n.ClassType;
import com.teamcenter.rac.schedule.project.common.l10n.DisplayNameCache;
import com.teamcenter.rac.schedule.project.server.RACInterface.RACInterface;
import com.teamcenter.rac.schedule.project.sharedUtils.TimeZoneCalUtil;
import com.teamcenter.rac.schedule.scheduler.componentutils.PreferenceHelper;
import com.teamcenter.rac.schedule.scheduler.componentutils.ScheduleHelper;
import com.teamcenter.rac.ui.commands.RACUICommandsActivator;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.rac.util.UIUtilities;
import com.teamcenter.rac.util.wizard.extension.BaseExternalWizardPage;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.wb.swt.SWTResourceManager;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public class ScheduleTemplatePage extends BaseExternalWizardPage
  implements EventHandler, IPropertyChangeListener
{
  private static final Logger m_logger = Logger.getLogger("com.teamcenter.rac.schedule.wizardpages.properties.properties");
  private Registry r = Registry.getRegistry(this);
  private TCSession session;
  private Button templateCheckboxButton;
  private Button btnBackgroundCopy;
  private LOVDisplayer templateLovDisplayer;
  private DateTime shiftDateTime;
  private DateTime shiftTime;
  private DateTime start_dateTime;
  private DateTime startTime;
  private DateTime finish_dateTime;
  private DateTime finishTime;
  private LOVDisplayer tZLovDisplayer;
  private SchedulePageModel pageModel = NewScheduleHandler.getScheduleModel();
  private Label m_templateNameLbl;
  private Logger logger = Logger.getLogger(getClass());
  private DisplayNameCache nameCache = null;
  private String m_objectType = null;

  public ScheduleTemplatePage(String paramString1, String paramString2, String paramString3, ImageDescriptor paramImageDescriptor, int paramInt, String paramString4)
  {
    super(paramString1, paramString2, paramString3, paramImageDescriptor, paramInt);
    this.m_objectType = paramString4;
  }

  public void createControl(Composite paramComposite)
  {
    try
    {
      this.session = ((TCSession)RACUICommandsActivator.getDefault().getSession());
      this.nameCache = RACInterface.getDisplayNameCache(this.session);
    }
    catch (Exception localException1)
    {
      localException1.printStackTrace();
    }
    ScrolledComposite localScrolledComposite = new ScrolledComposite(paramComposite, 768);
    localScrolledComposite.setBackground(SWTResourceManager.getColor(1));
    Composite localComposite = new Composite(localScrolledComposite, 0);
    localComposite.setBackground(SWTResourceManager.getColor(1));
    localComposite.setBounds(61, 52, 526, 391);
    try
    {
      TCComponentType localTCComponentType1 = this.session.getTypeComponent("Schedule");
      TCPropertyDescriptor yfjc_localObject = localTCComponentType1.getPropertyDescriptor("fnd0TimeZone");
      this.tZLovDisplayer = new LOVDisplayer(localComposite, 0);
      this.tZLovDisplayer.initialize(null, (TCPropertyDescriptor)yfjc_localObject, null);
      this.tZLovDisplayer.setBackground(SWTResourceManager.getColor(1));
      this.tZLovDisplayer.setEnabled(true);
      this.tZLovDisplayer.setBounds(231, 287, 137, 28);
      this.tZLovDisplayer.addPropertyChangeListener(this);
    }
    catch (TCException localTCException1)
    {
      localTCException1.printStackTrace();
    }
    this.m_templateNameLbl = new Label(localComposite, 0);
    this.m_templateNameLbl.setBounds(373, 84, 150, 28);
    this.m_templateNameLbl.setBackground(SWTResourceManager.getColor(1));
    Label localLabel1 = new Label(localComposite, 0);
    localLabel1.setBackground(SWTResourceManager.getColor(1));
    localLabel1.setBounds(25, 190, 95, 20);
    localLabel1.setText(this.r.getString("ScheduleTemplatePage.Label.lblStartDate"));
    Object localObject = new Label(localComposite, 0);
    ((Label)localObject).setBackground(SWTResourceManager.getColor(1));
    ((Label)localObject).setBounds(25, 234, 95, 20);
    ((Label)localObject).setText(this.r.getString("ScheduleTemplatePage.Label.lblFinishDate"));
    Label localLabel2 = new Label(localComposite, 0);
    localLabel2.setBackground(SWTResourceManager.getColor(1));
    localLabel2.setBounds(25, 290, 87, 20);
    localLabel2.setText(this.r.getString("ScheduleTemplatePage.Label.lblTimeZone"));
    this.templateCheckboxButton = new Button(localComposite, 32);
    this.btnBackgroundCopy = new Button(localComposite, 32);
    this.shiftDateTime = new DateTime(localComposite, 36);
    this.shiftTime = new DateTime(localComposite, 132);
    this.shiftTime.setBounds(373, 122, 102, 28);
    this.shiftTime.setEnabled(false);
    this.start_dateTime = new DateTime(localComposite, 36);
    this.start_dateTime.setBounds(231, 182, 137, 28);
    this.startTime = new DateTime(localComposite, 1152);
    this.startTime.setBounds(374, 182, 102, 28);
    this.finish_dateTime = new DateTime(localComposite, 36);
    this.finish_dateTime.setBounds(231, 234, 137, 28);
    this.finish_dateTime.setMonth(this.start_dateTime.getMonth() + 3);
    this.finishTime = new DateTime(localComposite, 1152);
    this.finishTime.setBounds(374, 234, 102, 28);
    Label localLabel3 = new Label(localComposite, 0);
    localLabel3.setBackground(SWTResourceManager.getColor(1));
    localLabel3.setBounds(25, 86, 141, 20);
    localLabel3.setText(this.r.getString("ScheduleTemplatePage.Label.lblScheduleTemplate"));
    this.templateCheckboxButton.setText(this.r.getString("ScheduleTemplatePage.Label.templateCheckboxButton"));
    this.templateCheckboxButton.setBackground(SWTResourceManager.getColor(1));
    this.templateCheckboxButton.setBounds(10, 10, 424, 20);
    this.btnBackgroundCopy.setText(this.r.getString("ScheduleTemplatePage.Label.btnBackgroundCopy"));
    this.btnBackgroundCopy.setBackground(SWTResourceManager.getColor(1));
    this.btnBackgroundCopy.setSelection(false);
    this.btnBackgroundCopy.setBounds(10, 42, 401, 20);
    this.btnBackgroundCopy.setEnabled(false);
    if (this.templateCheckboxButton.getSelection())
    {
      this.btnBackgroundCopy.setSelection(true);
      this.btnBackgroundCopy.setEnabled(true);
    }
    Label localLabel4 = new Label(localComposite, 0);
    localLabel4.setBackground(SWTResourceManager.getColor(1));
    localLabel4.setBounds(25, 130, 100, 20);
    localLabel4.setText(this.r.getString("ScheduleTemplatePage.Label.shiftDateLbl"));
    this.shiftDateTime.setEnabled(false);
    this.shiftDateTime.setBounds(231, 122, 137, 28);
    try
    {
      TCComponentType localTCComponentType2 = this.session.getTypeComponent(this.m_objectType);
      TCPropertyDescriptor localTCPropertyDescriptor = localTCComponentType2.getPropertyDescriptor("fnd0ScheduleTemplate");
      this.templateLovDisplayer = new LOVDisplayer(localComposite, 0);
      this.templateLovDisplayer.initialize(null, localTCPropertyDescriptor, null);
      this.templateLovDisplayer.setBackground(SWTResourceManager.getColor(1));
      this.templateLovDisplayer.setBounds(231, 83, 137, 28);
      this.templateLovDisplayer.setEnabled(false);
      this.templateLovDisplayer.addPropertyChangeListener(this);
    }
    catch (TCException localTCException2)
    {
      localTCException2.printStackTrace();
    }
    this.templateCheckboxButton.addSelectionListener(new SelectionListener()
    {
      public void widgetSelected(SelectionEvent paramAnonymousSelectionEvent)
      {
        if (ScheduleTemplatePage.this.templateCheckboxButton.getSelection())
        {
          ScheduleTemplatePage.this.btnBackgroundCopy.setEnabled(true);
          ScheduleTemplatePage.this.templateLovDisplayer.setEnabled(true);
          ScheduleTemplatePage.this.shiftDateTime.setEnabled(true);
          ScheduleTemplatePage.this.shiftTime.setEnabled(true);
          ScheduleTemplatePage.this.start_dateTime.setEnabled(false);
          ScheduleTemplatePage.this.startTime.setEnabled(false);
          ScheduleTemplatePage.this.finish_dateTime.setEnabled(false);
          ScheduleTemplatePage.this.finishTime.setEnabled(false);
          ScheduleTemplatePage.this.tZLovDisplayer.setEnabled(false);
          ScheduleTemplatePage.this.pageModel.setScheduleTempleteCheckbox(ScheduleTemplatePage.this.templateCheckboxButton.getSelection());
          ScheduleTemplatePage.this.pageModel.setBackgroundCopyCheckbox(ScheduleTemplatePage.this.btnBackgroundCopy.getSelection());
        }
        else
        {
          ScheduleTemplatePage.this.btnBackgroundCopy.setSelection(false);
          ScheduleTemplatePage.this.btnBackgroundCopy.setEnabled(false);
          ScheduleTemplatePage.this.templateLovDisplayer.setEnabled(false);
          ScheduleTemplatePage.this.shiftDateTime.setEnabled(false);
          ScheduleTemplatePage.this.shiftTime.setEnabled(false);
          ScheduleTemplatePage.this.start_dateTime.setEnabled(true);
          ScheduleTemplatePage.this.startTime.setEnabled(true);
          ScheduleTemplatePage.this.finish_dateTime.setEnabled(true);
          ScheduleTemplatePage.this.finishTime.setEnabled(true);
          ScheduleTemplatePage.this.tZLovDisplayer.setEnabled(true);
          ScheduleTemplatePage.this.pageModel.setScheduleTempleteCheckbox(false);
          ScheduleTemplatePage.this.pageModel.setBackgroundCopyCheckbox(false);
          ScheduleTemplatePage.this.pageModel.setScheduleTemplate(null);
          ScheduleTemplatePage.this.m_templateNameLbl.setText("");
          ScheduleTemplatePage.this.m_templateNameLbl.setToolTipText(ScheduleTemplatePage.this.m_templateNameLbl.getText());
          ScheduleTemplatePage.this.templateLovDisplayer.clearSelection();
        }
      }

      public void widgetDefaultSelected(SelectionEvent paramAnonymousSelectionEvent)
      {
        if (ScheduleTemplatePage.this.templateCheckboxButton.getSelection())
        {
          ScheduleTemplatePage.this.btnBackgroundCopy.setEnabled(true);
          ScheduleTemplatePage.this.templateLovDisplayer.setEnabled(true);
          ScheduleTemplatePage.this.shiftDateTime.setEnabled(true);
          ScheduleTemplatePage.this.shiftTime.setEnabled(true);
        }
        else
        {
          ScheduleTemplatePage.this.btnBackgroundCopy.setSelection(false);
          ScheduleTemplatePage.this.btnBackgroundCopy.setEnabled(false);
          ScheduleTemplatePage.this.templateLovDisplayer.setEnabled(false);
          ScheduleTemplatePage.this.shiftDateTime.setEnabled(false);
          ScheduleTemplatePage.this.shiftTime.setEnabled(false);
          ScheduleTemplatePage.this.pageModel.setScheduleTempleteCheckbox(false);
          ScheduleTemplatePage.this.pageModel.setBackgroundCopyCheckbox(false);
          ScheduleTemplatePage.this.pageModel.setScheduleTemplate(null);
          ScheduleTemplatePage.this.m_templateNameLbl.setText("");
          ScheduleTemplatePage.this.m_templateNameLbl.setToolTipText(ScheduleTemplatePage.this.m_templateNameLbl.getText());
          ScheduleTemplatePage.this.templateLovDisplayer.clearSelection();
        }
      }
    });
    this.shiftDateTime.addSelectionListener(new SelectionListener()
    {
      public void widgetDefaultSelected(SelectionEvent paramAnonymousSelectionEvent)
      {
        Calendar localCalendar = Calendar.getInstance();
        localCalendar.set(ScheduleTemplatePage.this.shiftDateTime.getYear(), ScheduleTemplatePage.this.shiftDateTime.getMonth(), ScheduleTemplatePage.this.shiftDateTime.getDay(), ScheduleTemplatePage.this.shiftTime.getHours(), ScheduleTemplatePage.this.shiftTime.getMinutes(), ScheduleTemplatePage.this.shiftTime.getSeconds());
        localCalendar.getTimeInMillis();
        ScheduleTemplatePage.this.pageModel.setShiftDate(localCalendar);
      }

      public void widgetSelected(SelectionEvent paramAnonymousSelectionEvent)
      {
        Calendar localCalendar = Calendar.getInstance();
        localCalendar.set(ScheduleTemplatePage.this.shiftDateTime.getYear(), ScheduleTemplatePage.this.shiftDateTime.getMonth(), ScheduleTemplatePage.this.shiftDateTime.getDay(), ScheduleTemplatePage.this.shiftTime.getHours(), ScheduleTemplatePage.this.shiftTime.getMinutes(), ScheduleTemplatePage.this.shiftTime.getSeconds());
        localCalendar.getTimeInMillis();
        ScheduleTemplatePage.this.pageModel.setShiftDate(localCalendar);
      }
    });
    this.shiftTime.addSelectionListener(new SelectionListener()
    {
      public void widgetDefaultSelected(SelectionEvent paramAnonymousSelectionEvent)
      {
        Calendar localCalendar = Calendar.getInstance();
        localCalendar.set(5, ScheduleTemplatePage.this.shiftDateTime.getDay());
        localCalendar.set(2, ScheduleTemplatePage.this.shiftDateTime.getMonth());
        localCalendar.set(1, ScheduleTemplatePage.this.shiftDateTime.getYear());
        localCalendar.set(11, ScheduleTemplatePage.this.shiftTime.getHours());
        localCalendar.set(12, ScheduleTemplatePage.this.shiftTime.getMinutes());
        localCalendar.set(13, ScheduleTemplatePage.this.shiftTime.getSeconds());
        localCalendar.getTimeInMillis();
        ScheduleTemplatePage.this.pageModel.setShiftDate(localCalendar);
      }

      public void widgetSelected(SelectionEvent paramAnonymousSelectionEvent)
      {
        Calendar localCalendar = Calendar.getInstance();
        localCalendar.set(5, ScheduleTemplatePage.this.shiftDateTime.getDay());
        localCalendar.set(2, ScheduleTemplatePage.this.shiftDateTime.getMonth());
        localCalendar.set(1, ScheduleTemplatePage.this.shiftDateTime.getYear());
        localCalendar.set(11, ScheduleTemplatePage.this.shiftTime.getHours());
        localCalendar.set(12, ScheduleTemplatePage.this.shiftTime.getMinutes());
        localCalendar.set(13, ScheduleTemplatePage.this.shiftTime.getSeconds());
        localCalendar.getTimeInMillis();
        ScheduleTemplatePage.this.pageModel.setShiftDate(localCalendar);
      }
    });
    this.start_dateTime.addSelectionListener(new SelectionListener()
    {
      public void widgetSelected(SelectionEvent paramAnonymousSelectionEvent)
      {
        Calendar localCalendar = Calendar.getInstance();
        localCalendar.set(5, ScheduleTemplatePage.this.start_dateTime.getDay());
        localCalendar.set(2, ScheduleTemplatePage.this.start_dateTime.getMonth());
        localCalendar.set(1, ScheduleTemplatePage.this.start_dateTime.getYear());
        localCalendar.set(11, ScheduleTemplatePage.this.startTime.getHours());
        localCalendar.set(12, ScheduleTemplatePage.this.startTime.getMinutes());
        localCalendar.set(13, ScheduleTemplatePage.this.startTime.getSeconds());
        ScheduleTemplatePage.this.pageModel.setStartDate(localCalendar);
      }

      public void widgetDefaultSelected(SelectionEvent paramAnonymousSelectionEvent)
      {
        Calendar localCalendar = Calendar.getInstance();
        localCalendar.set(5, ScheduleTemplatePage.this.start_dateTime.getDay());
        localCalendar.set(2, ScheduleTemplatePage.this.start_dateTime.getMonth());
        localCalendar.set(1, ScheduleTemplatePage.this.start_dateTime.getYear());
        localCalendar.set(11, ScheduleTemplatePage.this.startTime.getHours());
        localCalendar.set(12, ScheduleTemplatePage.this.startTime.getMinutes());
        localCalendar.set(13, ScheduleTemplatePage.this.startTime.getSeconds());
        ScheduleTemplatePage.this.pageModel.setStartDate(localCalendar);
      }
    });
    this.finish_dateTime.addSelectionListener(new SelectionListener()
    {
      public void widgetDefaultSelected(SelectionEvent paramAnonymousSelectionEvent)
      {
        Calendar localCalendar = Calendar.getInstance();
        localCalendar.set(5, ScheduleTemplatePage.this.finish_dateTime.getDay());
        localCalendar.set(2, ScheduleTemplatePage.this.finish_dateTime.getMonth());
        localCalendar.set(1, ScheduleTemplatePage.this.finish_dateTime.getYear());
        localCalendar.set(11, ScheduleTemplatePage.this.finishTime.getHours());
        localCalendar.set(12, ScheduleTemplatePage.this.finishTime.getMinutes());
        localCalendar.set(13, ScheduleTemplatePage.this.finishTime.getSeconds());
        ScheduleTemplatePage.this.pageModel.setFinishDate(localCalendar);
      }

      public void widgetSelected(SelectionEvent paramAnonymousSelectionEvent)
      {
        Calendar localCalendar = Calendar.getInstance();
        localCalendar.set(5, ScheduleTemplatePage.this.finish_dateTime.getDay());
        localCalendar.set(2, ScheduleTemplatePage.this.finish_dateTime.getMonth());
        localCalendar.set(1, ScheduleTemplatePage.this.finish_dateTime.getYear());
        localCalendar.set(11, ScheduleTemplatePage.this.finishTime.getHours());
        localCalendar.set(12, ScheduleTemplatePage.this.finishTime.getMinutes());
        localCalendar.set(13, ScheduleTemplatePage.this.finishTime.getSeconds());
        ScheduleTemplatePage.this.pageModel.setFinishDate(localCalendar);
      }
    });
    this.startTime.addSelectionListener(new SelectionListener()
    {
      public void widgetDefaultSelected(SelectionEvent paramAnonymousSelectionEvent)
      {
        Calendar localCalendar = Calendar.getInstance();
        localCalendar.set(5, ScheduleTemplatePage.this.start_dateTime.getDay());
        localCalendar.set(2, ScheduleTemplatePage.this.start_dateTime.getMonth());
        localCalendar.set(1, ScheduleTemplatePage.this.start_dateTime.getYear());
        localCalendar.set(11, ScheduleTemplatePage.this.startTime.getHours());
        localCalendar.set(12, ScheduleTemplatePage.this.startTime.getMinutes());
        localCalendar.set(13, ScheduleTemplatePage.this.startTime.getSeconds());
        ScheduleTemplatePage.this.pageModel.setStartDate(localCalendar);
      }

      public void widgetSelected(SelectionEvent paramAnonymousSelectionEvent)
      {
        Calendar localCalendar = Calendar.getInstance();
        localCalendar.set(5, ScheduleTemplatePage.this.start_dateTime.getDay());
        localCalendar.set(2, ScheduleTemplatePage.this.start_dateTime.getMonth());
        localCalendar.set(1, ScheduleTemplatePage.this.start_dateTime.getYear());
        localCalendar.set(11, ScheduleTemplatePage.this.startTime.getHours());
        localCalendar.set(12, ScheduleTemplatePage.this.startTime.getMinutes());
        localCalendar.set(13, ScheduleTemplatePage.this.startTime.getSeconds());
        ScheduleTemplatePage.this.pageModel.setStartDate(localCalendar);
      }
    });
    this.finishTime.addSelectionListener(new SelectionListener()
    {
      public void widgetDefaultSelected(SelectionEvent paramAnonymousSelectionEvent)
      {
        Calendar localCalendar = Calendar.getInstance();
        localCalendar.set(5, ScheduleTemplatePage.this.finish_dateTime.getDay());
        localCalendar.set(2, ScheduleTemplatePage.this.finish_dateTime.getMonth());
        localCalendar.set(1, ScheduleTemplatePage.this.finish_dateTime.getYear());
        localCalendar.set(11, ScheduleTemplatePage.this.finishTime.getHours());
        localCalendar.set(12, ScheduleTemplatePage.this.finishTime.getMinutes());
        localCalendar.set(13, ScheduleTemplatePage.this.finishTime.getSeconds());
        ScheduleTemplatePage.this.pageModel.setFinishDate(localCalendar);
      }

      public void widgetSelected(SelectionEvent paramAnonymousSelectionEvent)
      {
        Calendar localCalendar = Calendar.getInstance();
        localCalendar.set(5, ScheduleTemplatePage.this.finish_dateTime.getDay());
        localCalendar.set(2, ScheduleTemplatePage.this.finish_dateTime.getMonth());
        localCalendar.set(1, ScheduleTemplatePage.this.finish_dateTime.getYear());
        localCalendar.set(11, ScheduleTemplatePage.this.finishTime.getHours());
        localCalendar.set(12, ScheduleTemplatePage.this.finishTime.getMinutes());
        localCalendar.set(13, ScheduleTemplatePage.this.finishTime.getSeconds());
        ScheduleTemplatePage.this.pageModel.setFinishDate(localCalendar);
      }
    });
    this.pageModel.setScheduleTempleteCheckbox(this.templateCheckboxButton.getSelection());
    if (this.templateCheckboxButton.getSelection())
    {
      this.pageModel.setBackgroundCopyCheckbox(this.btnBackgroundCopy.getSelection());
    }
    else
    {
      this.pageModel.setBackgroundCopyCheckbox(false);
      this.pageModel.setScheduleTemplate(null);
      this.m_templateNameLbl.setText("");
      this.m_templateNameLbl.setToolTipText(this.m_templateNameLbl.getText());
    }
    String str = this.pageModel.getTimeZone();
    try
    {
      this.tZLovDisplayer.setSelectedValue(str);
    }
    catch (Exception localException2)
    {
      this.logger.error("\nException::Error in setting timezone on LOV as the value of timezone from standard calendar was found to be : " + str);
    }
    this.btnBackgroundCopy.addSelectionListener(new SelectionListener()
    {
      public void widgetSelected(SelectionEvent paramAnonymousSelectionEvent)
      {
        ScheduleTemplatePage.this.pageModel.setBackgroundCopyCheckbox(ScheduleTemplatePage.this.btnBackgroundCopy.getSelection());
      }

      public void widgetDefaultSelected(SelectionEvent paramAnonymousSelectionEvent)
      {
        ScheduleTemplatePage.this.pageModel.setBackgroundCopyCheckbox(ScheduleTemplatePage.this.btnBackgroundCopy.getSelection());
      }
    });
    localScrolledComposite.setContent(localComposite);
    setControl(localScrolledComposite);
    initDates();
    if(NewScheduleHandler.commandName.contains("EBP")){
    	this.templateCheckboxButton.setEnabled(false);
    	this.templateCheckboxButton.setSelection(true);
        ScheduleTemplatePage.this.btnBackgroundCopy.setEnabled(true);
        ScheduleTemplatePage.this.templateLovDisplayer.setEnabled(true);
        ScheduleTemplatePage.this.shiftDateTime.setEnabled(true);
        ScheduleTemplatePage.this.shiftTime.setEnabled(true);
        ScheduleTemplatePage.this.start_dateTime.setEnabled(false);
        ScheduleTemplatePage.this.startTime.setEnabled(false);
        ScheduleTemplatePage.this.finish_dateTime.setEnabled(false);
        ScheduleTemplatePage.this.finishTime.setEnabled(false);
        ScheduleTemplatePage.this.tZLovDisplayer.setEnabled(false);
        ScheduleTemplatePage.this.pageModel.setScheduleTempleteCheckbox(ScheduleTemplatePage.this.templateCheckboxButton.getSelection());
        ScheduleTemplatePage.this.pageModel.setBackgroundCopyCheckbox(ScheduleTemplatePage.this.btnBackgroundCopy.getSelection());
    }
  }

  private void initDates()
  {
    Calendar localCalendar1 = this.pageModel.getFinishDate();
    Calendar localCalendar2 = this.pageModel.getStartDate();
    Calendar localCalendar3 = this.pageModel.getShiftDate();
    this.start_dateTime.setYear(localCalendar2.get(1));
    this.start_dateTime.setMonth(localCalendar2.get(2));
    this.start_dateTime.setDay(localCalendar2.get(5));
    this.startTime.setHours(localCalendar2.get(11));
    this.startTime.setMinutes(localCalendar2.get(12));
    this.startTime.setSeconds(0);
    this.finish_dateTime.setYear(localCalendar1.get(1));
    this.finish_dateTime.setMonth(localCalendar1.get(2));
    this.finish_dateTime.setDay(localCalendar1.get(5));
    this.finishTime.setHours(localCalendar1.get(11));
    this.finishTime.setMinutes(localCalendar1.get(12));
    this.finishTime.setSeconds(0);
    this.shiftDateTime.setYear(localCalendar3.get(1));
    this.shiftDateTime.setMonth(localCalendar3.get(2));
    this.shiftDateTime.setDay(localCalendar3.get(5));
    this.shiftTime.setHours(localCalendar3.get(11));
    this.shiftTime.setSeconds(0);
    this.shiftTime.setMinutes(localCalendar3.get(12));
  }

  public void dispose()
  {
    super.dispose();
  }

  public void handleEvent(Event paramEvent)
  {
  }

  public void reRender()
  {
    super.reRender();
  }

  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
  {
    String str = paramPropertyChangeEvent.getProperty();
    Object localObject1;
    Object localObject2;
    Object localObject3;
    Object localObject4;
    Object localObject5;
    if ("fnd0TimeZone".equals(str))
    {
      localObject1 = this.tZLovDisplayer.getSelectedValue();
      if ((localObject1 != null) && (localObject1 != ""))
      {
        localObject2 = String.valueOf(localObject1);
        if (!TimeZoneCalUtil.testTimeZoneID(String.valueOf(localObject1)))
        {
          localObject3 = this.r.getString("ScheduleTemplatePage.cal_tz_error.MSG");
          localObject3 = MessageFormat.format((String)localObject3, new Object[] { localObject2, this.nameCache.getAttrDisplayName(ClassType.TC_CALENDAR, "time_zone") });
          localObject4 = AIFUtility.getActiveDesktop().getDesktopWindow().getActivePage();
          localObject5 = ((IWorkbenchPage)localObject4).getActivePart().getSite().getShell();
          MessageDialog.openError((Shell)localObject5, this.r.getString("ScheduleTemplatePage.cal_tz_error.TITLE"), (String)localObject3);
        }
        else
        {
          localObject3 = TimeZone.getTimeZone((String)localObject2);
          if ((!((String)localObject2).equals(this.pageModel.getTimeZone())) && (!this.templateCheckboxButton.getSelection()))
          {
            localObject4 = AIFUtility.getActiveDesktop().getDesktopWindow().getActivePage();
            localObject5 = ((IWorkbenchPage)localObject4).getActivePart().getSite().getShell();
            boolean bool = MessageDialog.openConfirm((Shell)localObject5, this.r.getString("ScheduleTemplatePage.cal_tz_change.TITLE"), this.r.getString("ScheduleTemplatePage.cal_tz_change.MSG"));
            if (bool)
            {
              resetDatesforTimeZone((TimeZone)localObject3);
              this.pageModel.setTimeZone(String.valueOf(localObject1));
            }
            else
            {
              this.tZLovDisplayer.setSelectedValue(this.pageModel.getTimeZone());
            }
          }
        }
      }
    }
    else if ("fnd0ScheduleTemplate".equals(str))
    {
      TCComponent yfjc_localObject1 = null;
      localObject2 = this.templateLovDisplayer.getSelectedValue();
      if ((localObject2 != null) && (localObject2 != ""))
        try
        {
          localObject3 = this.session.getTextService();
          String yfjc_localObject4 = ((TCTextService)localObject3).getTextValue("item_id");
          localObject5 = new String[] { yfjc_localObject4 };
          String[] arrayOfString = { (String)localObject2 };
          TCComponentQueryType localTCComponentQueryType = (TCComponentQueryType)this.session.getTypeComponent("ImanQuery");
          TCComponentQuery localTCComponentQuery = (TCComponentQuery)localTCComponentQueryType.find("Schedules...");
          TCComponent[] arrayOfTCComponent = localTCComponentQuery.execute((String[])localObject5, arrayOfString);
          Object localObject6;
          if ((arrayOfTCComponent != null) && (arrayOfTCComponent.length == 1))
          {
            yfjc_localObject1 = arrayOfTCComponent[0];
            if (!yfjc_localObject1.getType().equals(this.m_objectType))
            {
              UIUtilities.setCurrentModalShell(null);
              localObject6 = new MessageBox("ScheduleTemplatePage.InvalidTemplateType.ERROR", this.r.getString("ScheduleTemplatePage.Error"), 1);
              ((MessageBox)localObject6).setModal(true);
              ((MessageBox)localObject6).setVisible(true);
              this.templateLovDisplayer.clearSelection();
              return;
            }
            if ((yfjc_localObject1.getIntProperty("schedule_type") == 1) || (yfjc_localObject1.getIntProperty("schedule_type") == 3))
            {
              UIUtilities.setCurrentModalShell(null);
              localObject6 = new MessageBox(this.r.getString("createScheduleFromMasterTemplate.MSG"), this.r.getString("createScheduleFromMasterTemplate.TITLE"), 2);
              ((MessageBox)localObject6).setModal(true);
              ((MessageBox)localObject6).setVisible(true);
            }
          }
          if (yfjc_localObject1 != null)
          {
            this.m_templateNameLbl.setText(yfjc_localObject1.toString());
            this.m_templateNameLbl.setToolTipText(yfjc_localObject1.toString());
          }
          this.pageModel.setScheduleTemplate(yfjc_localObject1);
          if ((yfjc_localObject1 != null) && (PreferenceHelper.useScheduleTemplateDate(this.session)))
          {
            localObject6 = ScheduleHelper.getStartDate(yfjc_localObject1);
            GregorianCalendar localGregorianCalendar = new GregorianCalendar();
            localGregorianCalendar.setTime((Date)localObject6);
            this.shiftDateTime.setDate(localGregorianCalendar.get(1), localGregorianCalendar.get(2), localGregorianCalendar.get(5));
            this.shiftTime.setHours(localGregorianCalendar.get(11));
            this.shiftTime.setSeconds(0);
            this.shiftTime.setMinutes(localGregorianCalendar.get(12));
            this.pageModel.setShiftDate(localGregorianCalendar);
          }
        }
        catch (TCException localTCException)
        {
          this.logger.error("Exception::performCreate", localTCException);
        }
    }
  }

  private void resetDatesforTimeZone(TimeZone paramTimeZone)
  {
    TCComponentTCCalendar localTCComponentTCCalendar = CalendarHelper.getDefaultBaseCalendar(this.session);
    Date localDate1 = this.pageModel.getStartDate().getTime();
    Date localDate2 = this.pageModel.getFinishDate().getTime();
    Calendar localCalendar1 = Calendar.getInstance();
    Calendar localCalendar2 = Calendar.getInstance();
    localCalendar1.setTimeInMillis(localDate1.getTime());
    localCalendar2.setTimeInMillis(localDate2.getTime());
    Calendar localCalendar3 = CalendarHelper.setToStartOfDay(this.session, localTCComponentTCCalendar, localCalendar1, paramTimeZone);
    GregorianCalendar localGregorianCalendar;
    if (localCalendar3 != null)
    {
      localDate1 = localCalendar3.getTime();
      GregorianCalendar localObject = new GregorianCalendar();
      ((Calendar)localObject).setTime(localDate1);
      this.pageModel.setStartDate((Calendar)localObject);
      localGregorianCalendar = new GregorianCalendar();
      localGregorianCalendar.setTime(localDate1);
      this.pageModel.setShiftDate(localGregorianCalendar);
    }
    Object localObject = CalendarHelper.setToEndOfDay(this.session, localTCComponentTCCalendar, localCalendar2, paramTimeZone);
    if (localObject != null)
    {
      localDate2 = ((Calendar)localObject).getTime();
      localGregorianCalendar = new GregorianCalendar();
      localGregorianCalendar.setTime(localDate2);
      this.pageModel.setFinishDate(localGregorianCalendar);
    }
    initDates();
  }

  public void validatePage()
  {
    boolean bool = this.pageModel.validatePage();
    if (bool)
      super.validatePage();
  }
}