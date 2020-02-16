/*** Eclipse Class Decompiler plugin, copyright (c) 2012 Chao Chen (cnfree2000@hotmail.com) ***/
package com.teamcenter.rac.project.providers;

import com.teamcenter.rac.project.nodes.ProjectTeamContentNode;
import com.teamcenter.rac.viewer.provider.node.IContentNode;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;

public class ProjectTeamContentProvider implements ITreeContentProvider {
	private StructuredViewer m_viewer;

	public ProjectTeamContentProvider(StructuredViewer paramStructuredViewer) {
		Assert.isNotNull(paramStructuredViewer);
		this.m_viewer = paramStructuredViewer;
	}

	public void inputChanged(Viewer paramViewer, Object paramObject1,
			Object paramObject2) {
	}

	public void dispose() {
		this.m_viewer = null;
	}

	public Object[] getElements(Object paramObject) {
		return getChildren(paramObject);
	}

	public Object[] getChildren(Object paramObject) {
		if (paramObject instanceof ProjectTeamContentNode)
			return ((ProjectTeamContentNode) paramObject).getChildren(
					this.m_viewer, true);
		if (paramObject instanceof IContentNode)
			return ((IContentNode) paramObject)
					.getChildren(this.m_viewer, true);
		return new Object[0];
	}

	public Object[] getChildrenInUI(Object paramObject) {
		if (paramObject instanceof ProjectTeamContentNode)
			return ((ProjectTeamContentNode) paramObject).getChildrenInUI(
					this.m_viewer, true);
		return new Object[0];
	}

	public Object getParent(Object paramObject) {
		if (paramObject instanceof IContentNode)
			return ((IContentNode) paramObject).getParent();
		return null;
	}

	public boolean hasChildren(Object paramObject) {
		if (!(paramObject instanceof ProjectTeamContentNode))
			return ((paramObject instanceof IContentNode) ? ((IContentNode) paramObject)
					.hasChildren() : false);
		return ((ProjectTeamContentNode) paramObject).hasChildren();
	}
}