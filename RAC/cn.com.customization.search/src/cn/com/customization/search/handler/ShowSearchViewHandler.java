package cn.com.customization.search.handler;

import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.explorer.common.TCComponentSearch;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.kernel.TCComponentQueryType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.search.views.SearchView;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;

public class ShowSearchViewHandler extends AbstractHandler
{
  public Object execute(ExecutionEvent event)
    throws ExecutionException
  {
    String labelName = getMenuCommandLabelName(event);
    System.out.println("labelName-->"+labelName);
    showSearchView(labelName);
    return null;
  }

  private String getMenuCommandLabelName(ExecutionEvent event) {
	    String labelName = "";
	    String eventTrigger = event.getTrigger().toString();
	    eventTrigger = eventTrigger.replace("Event {", "");
	    int length = eventTrigger.length();
	    eventTrigger = eventTrigger.substring(0, length - 2);
	    int startIndex = eventTrigger.indexOf("MenuItem {");
	    if (startIndex != -1) {
	      int endIndex = eventTrigger.indexOf("}");
	      int length2 = "MenuItem {".length();
	      labelName = eventTrigger.substring(startIndex + length2, endIndex);
	    }
	    return labelName;
  }

	  private void showSearchView(String queryName) {
	    try {
	      TCSession m_session = (TCSession)AIFUtility.getDefaultSession();
	      TCComponentQueryType querytype = null;
	      TCComponentQuery query = null;
	      querytype = (TCComponentQueryType)m_session.getTypeComponent("ImanQuery");
	      querytype.clearCache();
	      query = (TCComponentQuery)querytype.find(queryName);
	      if (query == null) {
	        return;
	      }
	      IWorkbenchWindow localIWorkbenchWindow = AIFUtility.getActiveDesktop().getDesktopWindow();
	      IWorkbenchPage workbenchPage = localIWorkbenchWindow.getActivePage();
	      workbenchPage.showView("com.teamcenter.rac.search.views.SearchView");
	      TCComponentSearch componentSearch = new TCComponentSearch(query);
	      SearchView searchView = (SearchView)workbenchPage.findView("com.teamcenter.rac.search.views.SearchView");
	      searchView.searchChanged(componentSearch);
	    } catch (PartInitException e) {
	      e.printStackTrace();
	    } catch (TCException e) {
	      e.printStackTrace();
	    }
	  }
}