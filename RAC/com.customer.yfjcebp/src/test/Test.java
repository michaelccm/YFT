package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class Test {

	public static void main(String[] args) {
		
		
		System.out.println("ִ��main����");
		/*OracleBeen oracleBeen = new OracleBeen();
		//                 �ӿ�����                                                   �ӿ�Ψһʶ��ID              �ӿڴ��䷽                                               �ӿڽ��շ�                                                  �ӿڴ���ʱ��                                                    �ӿ���ɽ��                                                  ����ʱ��                                                   ��������                                                    ������Ϣ                                                       ��������ʶ
		//create table ����   (�ӿ�����     VARCHAR2(30),�ӿ�Ψһʶ��ID VARCHAR2(30),�ӿڴ��䷽   VARCHAR2(30),�ӿڽ��շ�    VARCHAR2(30),�ӿڴ���ʱ��     VARCHAR2(30),�ӿ���ɽ��     VARCHAR2(30),����ʱ��     VARCHAR2(30),��������     VARCHAR2(30),������ϢVARCHAR2  (130),��������ʶ   VARCHAR2(30))
		LogBeen log = oracleBeen.operation_database("select * from aaa where a = ?", new String[]{"a"});
		if(log!=null){
			System.out.println("�ӿڴ��䷽"+log.getInterface_transporter());
		}*/
		
		//File file = new File("d:\\�ҵ��ĵ�\\����\\��Ŀ\\����������Ŀ\\����������TCM��Ŀ�������ƻ�V1.0_20181030(1).xlsx");
		
		try {
//			savePic(new FileInputStream(file), file.getName());
			
//			File file2 = new File("d:\\�ҵ��ĵ�\\����\\������.xlsx");
//			FileInputStream fileInputStream = new FileInputStream(file2);
//			XSSFWorkbook xssfWorkbook = new XSSFWorkbook(fileInputStream);
//			fileInputStream.close();
//			Sheet sheet = xssfWorkbook.getSheet("aaa");
//			
//			System.out.println(sheet);
//			Row row = sheet.getRow(6);
//			Cell cell = row.getCell(4);
//			cell.setCellValue("aaa");
//			FileOutputStream fileOutputStream = new FileOutputStream(file2);
//			xssfWorkbook.write(fileOutputStream);
//			fileOutputStream.flush();
//			fileOutputStream.close();
			/*String path="d:\\�ҵ��ĵ�\\����\\test.xlsx";
	        
			XSSFWorkbook wb = new XSSFWorkbook();
	        Sheet sheet = wb.createSheet("��һ��sheetҳ");
	        
	        
	        FileOutputStream output = new FileOutputStream(path);
	        wb.write(output);
	        output.flush();
	        output.close();
	        File file2 = new File(path);*/
	        
	        
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void aaa(File file2) {
		// TODO Auto-generated method stub
		file2.delete();
		
	}

	public static  void savePic(InputStream inputStream, String fileName) {

        OutputStream os = null;
        try {
            String path = "c:\\";
            // 2�����浽��ʱ�ļ�
            // 1K�����ݻ���
            byte[] bs = new byte[1024];
            // ��ȡ�������ݳ���
            int len;
            // ������ļ������浽�����ļ�

            File tempFile = new File(path);
            if (!tempFile.exists()) {
                tempFile.mkdirs();
            }
            os = new FileOutputStream(tempFile.getPath() +fileName);
            // ��ʼ��ȡ
            while ((len = inputStream.read(bs)) != -1) {
                os.write(bs, 0, len);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // ��ϣ��ر���������
            try {
                os.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
