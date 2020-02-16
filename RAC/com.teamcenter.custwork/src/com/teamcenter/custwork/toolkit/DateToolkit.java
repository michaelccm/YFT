package com.teamcenter.custwork.toolkit;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.teamcenter.custwork.toolkit.log.ILogsToolit;
public class DateToolkit {
	/**
	 * �����������ڵĹ�����
	 * @param dtFrom ��ʼʱ��
	 * @param dtTo ����ʱ��
	 * @param holidayFrom �����տ�ʼʱ��
	 * @param holidayTo �����ս���ʱ��
	 * @return �����յ�����
	 */
	public static int getWorkDate(Date dtFrom,Date dtTo,Date holidayFrom,Date holidayTo)
	{
		if(dtFrom.after(dtTo))
		{
			Date dt3=dtFrom;
			dtFrom=dtTo;
			dtTo=dt3;
		}
		int count=0;		
		long returncount=((((dtTo.getTime()-dtFrom.getTime())/1000)/60)/60)/24;
		for(int i=0;i<returncount;i++)
		{
		    Calendar cal=Calendar.getInstance();
		    cal.setTime(dtFrom);
		    if(cal.get(Calendar.DAY_OF_WEEK)!=Calendar.SATURDAY && cal.get(Calendar.DAY_OF_WEEK)!=Calendar.SUNDAY)
		    {
		    	if(holidayFrom.after(cal.getTime())||holidayTo.before(cal.getTime()))		    			    		
		    		count++;
		    	
		    }
		    cal.add(Calendar.DAY_OF_MONTH, 1);
		    dtFrom=cal.getTime();
		    cal=null;
		    	
		}
		return count;
	}
	 
	
	public static String getWeekOfDate( Calendar cal) {
        String[] weekDays = {"Sun", "Mon", "Tues", "Wed", "Thur", "Fri", "Sat"};
      //  Calendar cal = Calendar.getInstance();
     //   cal.setTime(dt);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
	}
	 
	 
	public static int getWorkDate(String start,String end) throws ParseException{
        //����ʱ���ʽ
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd 00:00:00");
    	DateFormat dateFormat3 = new SimpleDateFormat("yyyy/MM/dd 23:59:00");
        Date dateFrom = dateFormat.parse(start);
        Date dateTo =  dateFormat3.parse(end);
        System.out.println("dateFrom:" +dateFrom );
        System.out.println("dateTo:" +dateTo );
        //��ʼ����    
        int workdays = 0;
         while(dateFrom.before(dateTo)){
             Calendar cal = Calendar.getInstance();          
             //��������
            cal.setTime(dateFrom);
            System.out.println(cal.get(Calendar.DAY_OF_WEEK));
            if((cal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) && (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY)){
                //���бȽ�
            	  workdays++;
            }       
            //���ڼ�1
            cal.add(Calendar.DAY_OF_MONTH,1);
            dateFrom = cal.getTime();
            cal=null;
        }
         System.out.println("����������:" + workdays);
         return workdays;
    }
	
	public static double getWorkHours(Date dateFrom,Date dateTo,ILogsToolit lit) throws ParseException{
		double lastHous = 0;
		double  hous = 0;		
		DateFormat dateFormat1 = new SimpleDateFormat("yyyy/MM/dd 00:00:00");
		DateFormat dateFormat3 = new SimpleDateFormat("yyyy/MM/dd 23:59:00");
		int days = getWorkDate(dateFormat1.format(dateFrom),dateFormat3.format(dateTo));
		lit.writeInforToFile("�������չ�����϶�ǣ� " + days);
		if(!isSATURDAYOrSATURDAY(dateTo)){
			days = days+1;
		}
		hous = days*7.5;	
		lit.writeInforToFile("����Сʱ�ǣ�" + hous);
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:dd");
		int currh = Integer.parseInt(dateFormat.format(dateFrom).substring(0, 2));
		int currhTo = Integer.parseInt(dateFormat.format(dateTo).substring(0, 2));	
		int currm = Integer.parseInt(dateFormat.format(dateFrom).substring(3,5));
		int currmTo = Integer.parseInt(dateFormat.format(dateTo).substring(3,5));
		double addMin = 0;
		double subMin = 0;
		if(!isSATURDAYOrSATURDAY(dateFrom)){			
			if(currh>9 && currh<12){
				subMin = (currh-9)*60+currm;			
			}else if(currh == 9){
				subMin = currm;
			}else if(currh == 12){
				subMin = 3*60;
			}else if(currh == 13){
				subMin = 3*60+currm;
			}else if(currh>13 && currh<17){
				subMin = 3*60+(currh-13)*60+currm;
			}else if(currh == 17){		
				if(currm>30){
					subMin = 7.5*60;
				}else{
					subMin = 7*60+currm;
				}
			}else if(currh>17){
				subMin = 7.5*60;
			}
		}else{
			lit.writeInforToFile("��ʼʱ��Ϊ�ǹ����գ����ý��м���");
		}
		if(!isSATURDAYOrSATURDAY(dateTo)){
			if(currhTo>9 && currhTo<12){
				addMin = 7.5*60 - (currhTo-9)*60-currmTo;
			}else if(currhTo == 9){
				addMin = 7.5*60 - currmTo;
			}else if(currhTo ==12){
				addMin = 4.5*60;
			}else if(currhTo == 13){
				addMin = 4.5*60-currmTo;
			}else if(currhTo>13 && currhTo<17){
				addMin = 4.5*60-(currhTo-13)*60-currmTo;
			}else if(currhTo == 17){
				if(currmTo<30){				
					addMin = 30-currmTo;
				}			
			}else if(currhTo<9){
				addMin = 7.5*60;
			}
		}else{
			lit.writeInforToFile("����ʱ��Ϊ�ǹ����գ����ý��м���");
		}
		
		lit.writeInforToFile("hous:" + hous);
		lit.writeInforToFile("curr:" + currh);
		lit.writeInforToFile("currTo:" + currhTo);
		lit.writeInforToFile("currm:" + currm);
		lit.writeInforToFile("currmTo:" + currmTo);
		lit.writeInforToFile("hous:" + hous);
		lit.writeInforToFile("addMin:" + addMin);
		lit.writeInforToFile("subMin:" + subMin);		
		double h = hous*60-addMin-subMin;
		lit.writeInforToFile("ʱ����Ϊ:" + h + "   ����");
		BigDecimal decimal = new BigDecimal(h);
		BigDecimal b2 = new BigDecimal(60);
		MathContext mc = new MathContext(4, RoundingMode.HALF_DOWN);
		lastHous = decimal.divide(b2,mc).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();	
		return lastHous;
	}
	
	private static boolean isSATURDAYOrSATURDAY(Date date){
		boolean falg = false;
		 Calendar cal = Calendar.getInstance();          
		 cal.setTime(date);
		 if((cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)  ||
				 (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)){
			 falg = true;
		 }
		return falg;
	}
	
	
	public static String getWorkDateStr(double h){
		StringBuffer buffer = new StringBuffer();
		if(h>0){
			int l = (int) (h*60);
			int day = l/450;
			int hour = (l%450)/60;
			int min = (l%450)%60;
			buffer.append(day).append("��");
			buffer.append(hour).append("Сʱ");
			buffer.append(min).append("��");
		}else{
			buffer.append("0��0Сʱ0��");
		}
		return buffer.toString();
	}
}
