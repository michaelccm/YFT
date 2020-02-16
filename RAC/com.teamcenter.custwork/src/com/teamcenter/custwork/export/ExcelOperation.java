package com.teamcenter.custwork.export;

import java.io.File;
import java.io.IOException;

import jxl.write.WriteException;

public interface ExcelOperation {
	public void exportExcel(File file,ExportProgressDialog processDialog)throws Exception;
}
