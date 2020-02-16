package com.teamcenter.custwork.export;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.BorderLineStyle;
import jxl.format.UnderlineStyle;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class ExcelWriteTool {
	public static void writeExcel(String sheettile,File fileExcel,
			Object[] cloumnNames,ArrayList arrayList, ExportProgressDialog processDialog) throws IOException, WriteException{	
		WritableWorkbook wbook = Workbook.createWorkbook(fileExcel);
		wbook.createSheet(sheettile,0);    
		try {			
			WritableSheet writableSheet = wbook.getSheet(0);
			writableSheet.getSettings().setShowGridLines(false);
			Object[] titles = cloumnNames;
			WritableFont font = new WritableFont(WritableFont.COURIER,14,WritableFont.BOLD,false,UnderlineStyle.NO_UNDERLINE,jxl.format.Colour.BLACK);
			WritableCellFormat wcfFC= new WritableCellFormat(font);  //用于设置单元格的样式,包括字体和颜色等.        
			wcfFC.setBorder(jxl.format.Border.TOP, BorderLineStyle.THIN);
			wcfFC.setBorder(jxl.format.Border.RIGHT, BorderLineStyle.THIN);
			wcfFC.setBorder(jxl.format.Border.LEFT, BorderLineStyle.THIN);
			wcfFC.setBorder(jxl.format.Border.BOTTOM, BorderLineStyle.THIN);			
			//wcfFC.setBackground(Colour.);  //设置单元格颜色
			wcfFC.setAlignment(Alignment.LEFT);	
			WritableFont font2 = new WritableFont(WritableFont.COURIER,10,WritableFont.NO_BOLD,false,UnderlineStyle.NO_UNDERLINE,jxl.format.Colour.BLACK);
			WritableCellFormat wcfFC2= new WritableCellFormat(font2);  //用于设置单元格的样式,包括字体和颜色等.        
			wcfFC2.setBorder(jxl.format.Border.TOP, BorderLineStyle.THIN);
			wcfFC2.setBorder(jxl.format.Border.RIGHT, BorderLineStyle.THIN);
			wcfFC2.setBorder(jxl.format.Border.LEFT, BorderLineStyle.THIN);
			wcfFC2.setBorder(jxl.format.Border.BOTTOM, BorderLineStyle.THIN);
			wcfFC2.setAlignment(Alignment.LEFT);
			for(int i=0;i<titles.length;i++){
				Label l = new Label(i,0,(String) titles[i]);
				l.setCellFormat(wcfFC);     //设置单元格的样式/格式
				System.out.println(i + "i" + titles[i]);
				writableSheet.setColumnView(i,titles[i].toString().length()*4);
				writableSheet.addCell(l);
			}
			if(arrayList != null && arrayList.size()>0){
				int iCount = arrayList.size();
				for(int n=0;n<arrayList.size();n++){			
					HashMap<?, ?>  hashMap = (HashMap<?, ?>) arrayList.get(n);
					processDialog.setProgressBarState("正在写入数据" , iCount+n);
					if(cloumnNames != null && cloumnNames.length>0){
						for(int k=0;k<titles.length;k++){
							Object obj = hashMap.get(titles[k]);
							String str = obj != null?obj.toString():"";						
							Label labelValue = new Label(k,n+1,str);
							labelValue.setCellFormat(wcfFC2);  	
							writableSheet.addCell(labelValue);
							if(str != null && str.length()>4){
								writableSheet.setColumnView(k, str.length()*4);								
							}
							
						}
					}
				}	
			}
			wbook.write(); 
		} catch (WriteException e) {
			e.printStackTrace();
		}finally{
			wbook.write(); 
			wbook.close();
		}	
		openExcelReport(fileExcel);
	}
	
	public static void openExcelReport(File file) throws IOException{
		System.out.println("File:" + file);
		//String as1[] = { "cmd.exe", "/c", "start", file };	
		//Runtime.getRuntime().exec(as1);
		Desktop.getDesktop().open(file);
	}

	public static void writeExcel(String sheettile,File fileExcel,
			Object[] cloumnNames,ArrayList arrayList, ExportProgressDialog processDialog,
			HashMap headGroupMap) throws IOException, WriteException {
		WritableWorkbook wbook = Workbook.createWorkbook(fileExcel);
		wbook.createSheet(sheettile,0);    
		try {			
			WritableSheet writableSheet = wbook.getSheet(0);
			writableSheet.getSettings().setShowGridLines(false);
			Object[] titles = cloumnNames;
			WritableFont font = new WritableFont(WritableFont.COURIER,12,
					WritableFont.BOLD,false,UnderlineStyle.NO_UNDERLINE,jxl.format.Colour.BLACK);
			WritableCellFormat wcfFC= new WritableCellFormat(font);  //用于设置单元格的样式,包括字体和颜色等.    
			wcfFC.setAlignment(Alignment.CENTRE);//把水平对齐方式指定为居中 
			wcfFC.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);//把垂直对齐方式指定为居中 
			wcfFC.setBorder(jxl.format.Border.TOP, BorderLineStyle.THIN);
			wcfFC.setBorder(jxl.format.Border.RIGHT, BorderLineStyle.THIN);
			wcfFC.setBorder(jxl.format.Border.LEFT, BorderLineStyle.THIN);
			wcfFC.setBorder(jxl.format.Border.BOTTOM, BorderLineStyle.THIN);			
			//wcfFC.setBackground(Colour.);  //设置单元格颜色
			wcfFC.setAlignment(Alignment.LEFT);	
			WritableFont font2 = new WritableFont(WritableFont.COURIER,10,
					WritableFont.NO_BOLD,false,UnderlineStyle.NO_UNDERLINE,jxl.format.Colour.BLACK);
			WritableCellFormat wcfFC2= new WritableCellFormat(font2);  //用于设置单元格的样式,包括字体和颜色等.        
			wcfFC2.setBorder(jxl.format.Border.TOP, BorderLineStyle.THIN);
			wcfFC2.setBorder(jxl.format.Border.RIGHT, BorderLineStyle.THIN);
			wcfFC2.setBorder(jxl.format.Border.LEFT, BorderLineStyle.THIN);
			wcfFC2.setBorder(jxl.format.Border.BOTTOM, BorderLineStyle.THIN);
			wcfFC2.setAlignment(Alignment.LEFT);
			for(int i=0;i<titles.length;i++){				
				Object obj = headGroupMap.get(i);				
				if(obj != null){					
					Label l1 = new Label(i,0,(String) obj);
					l1.setCellFormat(wcfFC);     //设置单元格的样式/格式
				//	System.out.println(i + "i" + obj);
					writableSheet.setColumnView(i,obj.toString().length()*4);
					writableSheet.addCell(l1);
					writableSheet.mergeCells(i,	0, i+1, 0);	
					
					Label l = new Label(i,1,"耗时");
					l.setCellFormat(wcfFC);     //设置单元格的样式/格式
					System.out.println(i + "i" + titles[i]);
					writableSheet.setColumnView(i,titles[i].toString().length()*4);
					writableSheet.addCell(l);
					
				}else{
					obj = headGroupMap.get(i-1);
					if(obj == null){
						Label l = new Label(i,0,(String) titles[i]);
						l.setCellFormat(wcfFC);     //设置单元格的样式/格式
						System.out.println(i + "i" + titles[i]);
						writableSheet.setColumnView(i,titles[i].toString().length()*4);
						writableSheet.addCell(l);
						writableSheet.mergeCells(i,	0, i, 1);	
					}else{
						Label l = new Label(i,1,"超时");
						l.setCellFormat(wcfFC);     //设置单元格的样式/格式
						System.out.println(i + "i" + titles[i]);
						writableSheet.setColumnView(i,titles[i].toString().length()*4);
						writableSheet.addCell(l);
					}
				}
			}
			if(arrayList != null && arrayList.size()>0){
				int iCount = arrayList.size();
				for(int n=0;n<arrayList.size();n++){			
					HashMap<?, ?>  hashMap = (HashMap<?, ?>) arrayList.get(n);
					processDialog.setProgressBarState("正在写入数据" , iCount+n);
					if(cloumnNames != null && cloumnNames.length>0){
						for(int k=0;k<titles.length;k++){
							Object obj = hashMap.get(titles[k]+Integer.toString(k));
							String str = obj != null?obj.toString():"";						
							Label labelValue = new Label(k,n+2,str);
							labelValue.setCellFormat(wcfFC2);  	
							writableSheet.addCell(labelValue);
							if(str != null && str.length()>4){
								writableSheet.setColumnView(k, str.length()*2);								
							}							
						}
					}
				}	
			}
			wbook.write(); 
		} catch (WriteException e) {
			e.printStackTrace();
		}finally{
			wbook.write(); 
			wbook.close();
		}	
		openExcelReport(fileExcel);
		
	}
	
	
	public static void writeExcel2(String sheettile,File fileExcel,
			Object[] cloumnNames,ArrayList arrayList, ExportProgressDialog processDialog,
			HashMap headGroupMap) throws IOException, WriteException {
		WritableWorkbook wbook = Workbook.createWorkbook(fileExcel);
		wbook.createSheet(sheettile,0);    
		try {			
			WritableSheet writableSheet = wbook.getSheet(0);
			writableSheet.getSettings().setShowGridLines(false);
			Object[] titles = cloumnNames;
			WritableFont font = new WritableFont(
					WritableFont.COURIER,12,WritableFont.BOLD,false,
					UnderlineStyle.NO_UNDERLINE,jxl.format.Colour.BLACK);
			WritableCellFormat wcfFC= new WritableCellFormat(font);  //用于设置单元格的样式,包括字体和颜色等.    
			wcfFC.setAlignment(Alignment.CENTRE);//把水平对齐方式指定为居中 
			wcfFC.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);//把垂直对齐方式指定为居中 
			wcfFC.setBorder(jxl.format.Border.TOP, BorderLineStyle.THIN);
			wcfFC.setBorder(jxl.format.Border.RIGHT, BorderLineStyle.THIN);
			wcfFC.setBorder(jxl.format.Border.LEFT, BorderLineStyle.THIN);
			wcfFC.setBorder(jxl.format.Border.BOTTOM, BorderLineStyle.THIN);			
			//wcfFC.setBackground(Colour.);  //设置单元格颜色
			wcfFC.setAlignment(Alignment.LEFT);	
			WritableFont font2 = new WritableFont(
					WritableFont.COURIER,10,WritableFont.NO_BOLD,false,
					UnderlineStyle.NO_UNDERLINE,jxl.format.Colour.BLACK);
			WritableCellFormat wcfFC2= new WritableCellFormat(font2);  //用于设置单元格的样式,包括字体和颜色等.        
			wcfFC2.setBorder(jxl.format.Border.TOP, BorderLineStyle.THIN);
			wcfFC2.setBorder(jxl.format.Border.RIGHT, BorderLineStyle.THIN);
			wcfFC2.setBorder(jxl.format.Border.LEFT, BorderLineStyle.THIN);
			wcfFC2.setBorder(jxl.format.Border.BOTTOM, BorderLineStyle.THIN);
			wcfFC2.setAlignment(Alignment.LEFT);
			for(int i=0;i<titles.length;i++){				
				Object obj = headGroupMap.get(i);				
				if(obj != null){					
					Label l1 = new Label(i,0,(String) obj);
					l1.setCellFormat(wcfFC);     //设置单元格的样式/格式
					System.out.println(i + "i" + obj);
					writableSheet.setColumnView(i,obj.toString().length()*4);
					writableSheet.addCell(l1);
					writableSheet.mergeCells(i,	0, i+3, 0);	
					
					Label l = new Label(i,1,"用户");
					l.setCellFormat(wcfFC);     //设置单元格的样式/格式
					System.out.println(i + "i" + titles[i]);
					writableSheet.setColumnView(i,titles[i].toString().length()*4);
					writableSheet.addCell(l);
					
				}else{
					
					if(headGroupMap.get(i-1) != null){
						Label l = new Label(i,1,"角色");
						l.setCellFormat(wcfFC);     //设置单元格的样式/格式
						System.out.println(i + "i" + titles[i]);
						writableSheet.setColumnView(i,titles[i].toString().length()*4);
						writableSheet.addCell(l);
					}else if(headGroupMap.get(i-2) != null){
						Label l = new Label(i,1,"耗时");
						l.setCellFormat(wcfFC);     //设置单元格的样式/格式
						System.out.println(i + "i" + titles[i]);
						writableSheet.setColumnView(i,titles[i].toString().length()*4);
						writableSheet.addCell(l);
					}else if(headGroupMap.get(i-3) != null){
						Label l = new Label(i,1,"超时");
						l.setCellFormat(wcfFC);     //设置单元格的样式/格式
						System.out.println(i + "i" + titles[i]);
						writableSheet.setColumnView(i,titles[i].toString().length()*4);
						writableSheet.addCell(l);
					}else{
						Label l = new Label(i,0,(String) titles[i]);
						l.setCellFormat(wcfFC);     //设置单元格的样式/格式
						System.out.println(i + "i" + titles[i]);
						writableSheet.setColumnView(i,titles[i].toString().length()*4);
						writableSheet.addCell(l);
						writableSheet.mergeCells(i,	0, i, 1);	
					}
				}
			}
			if(arrayList != null && arrayList.size()>0){
				int iCount = arrayList.size();
				for(int n=0;n<arrayList.size();n++){			
					HashMap<?, ?>  hashMap = (HashMap<?, ?>) arrayList.get(n);
					processDialog.setProgressBarState("正在写入数据" , iCount+n);
					if(cloumnNames != null && cloumnNames.length>0){
						for(int k=0;k<titles.length;k++){
							Object obj = hashMap.get(titles[k]+Integer.toString(k));
							String str = obj != null?obj.toString():"";						
							Label labelValue = new Label(k,n+2,str);
							labelValue.setCellFormat(wcfFC2);  	
							writableSheet.addCell(labelValue);
							if(str != null && str.length()>4){
								writableSheet.setColumnView(k, str.length()*2);								
							}							
						}
					}
				}	
			}
			wbook.write(); 
		} catch (WriteException e) {
			e.printStackTrace();
		}finally{
			wbook.write(); 
			wbook.close();
		}	
		openExcelReport(fileExcel);
		
	}
	
	
}
