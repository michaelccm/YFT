package com.teamcenter.custwork.toolkit;

import com.teamcenter.custwork.query.QueryProgressDialog;
import com.teamcenter.custwork.saves.SaveProgressDialog;
import com.teamcenter.custwork.toolkit.log.ILogsToolit;

public interface IWorkManagerTookit extends ILogsToolit{
	public void seach(QueryProgressDialog progressDialog);
	public void save(SaveProgressDialog progressDialog);
	public void saveAll(SaveProgressDialog progressDialog);
	public void revise(SaveProgressDialog progressDialog);

}
