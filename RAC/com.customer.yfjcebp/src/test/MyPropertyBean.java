//package test;
//
//
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.Map;
//
//import org.eclipse.core.databinding.observable.value.IObservableValue;
//import org.eclipse.jface.databinding.swt.ISWTObservableValue;
//import org.eclipse.jface.databinding.swt.WidgetProperties;
//import org.eclipse.swt.SWT;
//import org.eclipse.swt.layout.GridData;
//import org.eclipse.swt.layout.GridLayout;
//import org.eclipse.swt.widgets.Composite;
//import org.eclipse.swt.widgets.Dialog;
//import org.eclipse.swt.widgets.Label;
//import org.eclipse.swt.widgets.Text;
//import org.eclipse.ui.forms.widgets.FormToolkit;
//
//import com.teamcenter.rac.aifrcp.AIFUtility;
//import com.teamcenter.rac.kernel.TCComponent;
//import com.teamcenter.rac.kernel.TCProperty;
//import com.teamcenter.rac.kernel.TCPropertyDescriptor;
//import com.teamcenter.rac.kernel.TCSession;
//import com.teamcenter.rac.viewer.stylesheet.beans.AbstractPropertyBean;
//import com.teamcenter.rac.viewer.stylesheet.beans.TextfieldPropertyBean;
//
//public class MyPropertyBean extends AbstractPropertyBean{
//     protected AbstractPropertyBean m_propBean;
//     private Composite m_localComposite;
//     protected boolean m_validationNeeded;
//    
//     TCSession session;
//     protected Label myLabel;
//     protected Text myText01;
//     protected Text myText02;
//     public MyPropertyBean(FormToolkit paramFormToolkit, Composite paramComposite, boolean paramBoolean, Map paramMap) {
//          System.out.println("创建MyPropertyBean");
//          //获取session
//          this.session=(TCSession) AIFUtility.getCurrentApplication().getSession();
//          //this.m_propBean = new TextfieldPropertyBean(paramFormToolkit, paramComposite, paramBoolean, paramMap);
//          this.m_localComposite=new Composite(paramComposite, 0);
//          add_myLabel();
//          setLayout(3);
//          add_text();
//          //setControl(this.m_propBean.getControl());
//         
//     }
//    
//    
//    
//     @Override
//     protected void bindValues(TCProperty paramTCProperty) {
//          /* if (this.m_dataBeanViewModel != null)
//              {
//                ISWTObservableValue localISWTObservableValue = WidgetProperties.text(24).observe(myText01);
//                IObservableValue localIObservableValue = BeansObservables.observeValue(this.m_dataBeanViewModel, "value");
//                this.m_ctx.bindValue(localISWTObservableValue, localIObservableValue, this.m_targetToModelStrategy, this.m_modelToTargetStrategy);
//                if (paramTCProperty != null)
//                  if (paramTCProperty.getNullVerdict())
//                    this.m_dataBeanViewModel.setValue("null");
//                  else
//                    this.m_dataBeanViewModel.setValue(paramTCProperty.getPropertyValue());
//              }*/
//     }
//    
//     private void add_myLabel() {
//          System.out.println("添加label");
//          this.myLabel=new Label(this.m_localComposite, SWT.NULL);
//          this.myLabel.setLayoutData(new GridData(SWT.FILL, SWT.NULL, true, false));
//          this.myLabel.setText("");
//     }
//    
//     private void add_text() {
//          System.out.println("添加文本框");
//          //添加文本框
//          this.myText01=new Text(this.m_localComposite, SWT.BORDER);
//          this.myText01.setLayoutData(new GridData(SWT.NULL, SWT.NULL, false, false));
//          //获取日期
//          Date date = new Date();
//          String format_date = new SimpleDateFormat("yyyy-DD-dd").format(date);
//          this.myText01.setText(format_date);
//         
//          this.myText02=new Text(this.m_localComposite, SWT.BORDER);
//          this.myText02.setLayoutData(new GridData(SWT.NULL, SWT.NULL, false, false));
//          //获取当前用户名
//          String userName = this.session.getUserName();
//          this.myText02.setText(userName);
//     }
//    
//     //初始化m_localComposite
//     protected void setLayout(int paramInt) {
//         if (this.m_localComposite != null)
//         {
//           GridLayout localGridLayout = new GridLayout(paramInt, false);
//           localGridLayout.marginHeight = 0;
//           localGridLayout.marginWidth = 0;
//           this.m_localComposite.setLayout(localGridLayout);
//           GridData localGridData = new GridData(0, 0, false, false);
//           this.m_localComposite.setLayoutData(localGridData);
//         }
//       }
//    
//     @Override
//     public boolean isPropertyModified(TCProperty arg0) throws Exception {
//          // TODO Auto-generated method stub
//          return false;
//     }
//    
//     //将创建的文本框中的值返回到OTB中
//     @Override
//     public Object getEditableValue() {
//          // TODO Auto-generated method stub
//          return this.myText01.getText()+this.myText02.getText();
//     }
//
//    
//
//     @Override
//     public void load(TCProperty arg0) throws Exception {
//          /*load(arg0.getPropertyDescriptor());
//         this.m_propBean.load(arg0);
//         setDirty(false);*/
//         
//     }
//
//     @Override
//     public void load(TCPropertyDescriptor paramTCPropertyDescriptor) throws Exception {
//          /*this.m_propBean.setProperty(getProperty());
//         this.descriptor = paramTCPropertyDescriptor;
//
//         this.m_propBean.load(paramTCPropertyDescriptor);*/
//         
//     }
//
//     @Override
//     public void setModifiable(boolean arg0) {
//          // TODO Auto-generated method stub
//         
//     }
//
//     @Override
//     public void setUIFValue(Object arg0) {
//          System.out.println("test==="+arg0);
//         
//     }
//
//
//
//     @Override
//     public TCProperty getPropertyToSave(TCProperty paramTCProperty) throws Exception {
//          /*TCProperty localTCProperty = this.m_propBean.getPropertyToSave(paramTCProperty);
//         return localTCProperty;*/
//          return null;
//     }
//
//    
//
//}