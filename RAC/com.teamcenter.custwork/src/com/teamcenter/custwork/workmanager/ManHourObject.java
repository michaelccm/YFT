package com.teamcenter.custwork.workmanager;

import java.lang.String;
import java.awt.Color;
import java.util.List;
import java.util.ArrayList;


public class ManHourObject extends Object {

   public final static Color toolButtColor      = new Color (242, 242, 242);

   public final static Color nColor = new Color (212, 237, 255);
   public final static Color oColor = new Color (255, 218, 188);
   public final static Color wColor = new Color (204, 204, 204);
   public final static Color hColor = new Color (210, 198, 250);

   public final static Color holidayBackColor      = new Color (210, 198, 250);
   public final static Color weekendBackColor      = new Color (204, 204, 204);
   public final static Color workRequiredBackColor = new Color (255, 255, 255);
   public final static Color noWorkingDayBackColor = new Color (204, 204, 204);

   public final static Color defaultBackColor      = new Color (255, 255, 255);
   public final static Color defaultForeColor      = new Color (0, 0, 0);

   public final static Color releasedStatusBackColor = new Color (205, 240, 205);     // Saved 
   public final static Color failedStatusBackColor   = new Color (255, 0, 0);         // Error
   public final static Color WorkingStatusBackColor  = new Color (255, 255, 255);     // same as working required color.
   public final static Color noProgramDayBackColor   = new Color (89, 89, 89);

   public final static Color selectedCellBackColor   = new Color (155, 194, 230);     // selected 
  
   public final static Color totalCellBackColor      = new Color (242, 242, 242);     // selected 
   
   public final static Color loggingBackColor_Red      = new Color (255, 0, 0);       // selected 
   public final static Color loggingBackColor_Yellow   = new Color (255, 255, 110);   // selected 
   public final static Color loggingBackColor_Green    = new Color (146, 208, 88);    // selected 


   public final static String nBillType = "Normal";
   public final static String oBillType = "Overtime";
   public final static String wBillType = "Weekend";
   public final static String hBillType = "Holiday";
   public final static String noBillType = "Non-Billable";
   
   String mhBillType = null;
   String tseStatus = null;
   String mhWorkingHours = null;
   String tseRef = null;
   String mheRef = null;
 

   public ManHourObject (String working_hours)
   {
	  super ();
	  
	  mhWorkingHours = working_hours;
   }
   
   public void setBillType (String bill_type)
   {
	   mhBillType =  bill_type;
   }
   
   public String getBillType ()
   {
	   return mhBillType;
   }
   
   public void setTseStatus (String tse_status)
   {
      tseStatus = tse_status;
   }
   
   public String getTseStatus()
   {
	   return tseStatus;
   }
   
   public String getWorkingHours ()
   {
	   return mhWorkingHours;
   }

   public String getTseRef ()
   {  
          return tseRef;
   }

   public void setTseRef (String tse_ref)
   {
        tseRef = tse_ref;
   }

   public String toString ()
   
   {
         return mhWorkingHours;
   }

   public void setMheRef (String mhe_ref)
   {
      mheRef = mhe_ref;
   }

   public String getMheRef ()
   {
      return mheRef;
   }

   public static boolean isMe (Object obj)
   {
      boolean me = false;

      if (obj == null)
         return me;

      String myType = obj.getClass().toString();
      if (myType.toString().equals ("class com.teamcenter.custwork.workmanager.ManHourObject"))
      {
         me = true;
      }

      return me;
   }


   public static List<ManHourObject> getMheObjectList (ManHourObject mhe_obj)
   {
      List <ManHourObject> mheList = null;
      
      if (mhe_obj == null)
      {
        return mheList;
      }

      mheList = new ArrayList <ManHourObject> ();
      String workingHours = mhe_obj.getWorkingHours();
      if(workingHours==null){
    	  workingHours="";
      }
      String[] wrkHrsSet = workingHours.split ("\\:",-1);
      
      
      String billType = mhe_obj.getBillType();
      if(billType==null){
    	  billType="";
      }
      String[] btStrSet = billType.split ("\\:",-1);
      
      String tseStatus2 = mhe_obj.getTseStatus();
      if(tseStatus2==null){
    	  tseStatus2="";
      }
      String[] tseStatusSet = tseStatus2.split ("\\:",-1);
      
      String tseRef2 = mhe_obj.getTseRef();
      if(tseRef2==null){
    	  tseRef2="";
      }
      String[] tseRefSet = tseRef2.split ("\\:",-1);
      
      String mheRef2 = mhe_obj.getMheRef();
      if(mheRef2==null){
    	  mheRef2="";
      }
      String[] mheRefSet = mheRef2.split ("\\:",-1);

      if (wrkHrsSet.length == 2)
      {
         if (wrkHrsSet[0] != null && wrkHrsSet[0].length() !=0)
         {
            ManHourObject mhe0= new ManHourObject (wrkHrsSet[0]);
	    mhe0.setBillType (btStrSet[0]);
	    mhe0.setTseStatus (tseStatusSet[0]);
	    mhe0.setTseRef (tseRefSet[0]);
	    mhe0.setMheRef (mheRefSet[0]);
	    mheList.add (mhe0);

            ManHourObject mhe1= new ManHourObject (wrkHrsSet[1]);
	    mhe1.setBillType (btStrSet[1]);
	    mhe1.setTseStatus (tseStatusSet[1]);
	    mhe1.setTseRef (tseRefSet[1]);
	    mhe1.setMheRef (mheRefSet[1]);
            mheList.add (mhe1);
         }
	 else
         {
            ManHourObject mhe1= new ManHourObject (wrkHrsSet[1]);
	    mhe1.setBillType (btStrSet[1]);
	    mhe1.setTseStatus (tseStatusSet[1]);
	    mhe1.setTseRef (tseRefSet[1]);
	    mhe1.setMheRef (mheRefSet[1]);
            mheList.add (mhe1);
         }
      }
      else if (wrkHrsSet.length == 1)
      {
         mheList.add (mhe_obj);
      }

      return mheList;
   }


   public static ManHourObject mergeMHEObject0 (ManHourObject old_value, ManHourObject value)
   {
      String btStr = value.getBillType ();
      String wrkHrs = value.getWorkingHours();
      String status = value.getTseStatus();
      String tseref = value.getTseRef ();
      String mheref = value.getMheRef ();

      if (old_value == null)
      {
         if (wrkHrs == null || wrkHrs.length() == 0)
         {
            return null;
         }
         else
         {
            if (btStr.equals (ManHourObject.nBillType))
            {
               return value;
            }
            else
            {
               String wrkHrs_new = ":" + wrkHrs;
	       String btStr_new = ":" + btStr;
	       String status_new = ":" + status;
	       String tseref_new = ":" + tseref;
	       String mheref_new = ":" + mheref;

               ManHourObject value_new = new ManHourObject(wrkHrs_new);
               value_new.setBillType(btStr_new);
               value_new.setTseStatus(status_new);
	       value_new.setTseRef (tseref_new);
               value_new.setMheRef (mheref_new);
	       return value_new;
            }
         }
      }
      else
      {
         String [] btStrSet_old = old_value.getBillType().split("\\:");
         String [] wrkHrsSet_old = old_value.getWorkingHours().split("\\:");
         String [] tseStatusSet_old = old_value.getTseStatus().split("\\:");
         String [] tseRefSet_old = old_value.getTseRef().split("\\:");
         String [] mheRefSet_old = old_value.getMheRef().split("\\:");

         if (btStrSet_old.length == 2)     // yyy:YYY  or :YYY
         {
            String wrkHrs0_old = wrkHrsSet_old[0];
            String wrkHrs1_old = wrkHrsSet_old[1];
	    String btStr0_old = btStrSet_old[0];
            String btStr1_old = btStrSet_old[1];
            String tseStatus0_old = tseStatusSet_old[0];
            String tseStatus1_old = tseStatusSet_old[1];
            String tseRef0_old = tseRefSet_old[0];
            String tseRef1_old = tseRefSet_old[1];
            String mheRef0_old = mheRefSet_old[0];
            String mheRef1_old = mheRefSet_old[1];

            if (btStr.equals (ManHourObject.nBillType))     //XXX
            {
               if (btStr0_old == null || btStr0_old.length() == 0)   //:YYY
               {
                  if (wrkHrs == null || wrkHrs.length() == 0)  //000
                  {
                     String wrkHrs_new = ":" + wrkHrs1_old;
                     String btStr_new = ":" + btStr1_old;
                     String tseStatus_new = ":" + tseStatus1_old;
                     String tseRef_new = ":" + tseRef1_old;
                     String mheRef_new = ":" + mheRef1_old;

                     ManHourObject value_new = new ManHourObject (wrkHrs_new);
                     value_new.setBillType (btStr_new);
                     value_new.setTseStatus(tseStatus_new);
                     value_new.setTseRef(tseRef_new);
                     value_new.setMheRef(mheRef_new);
                     return value_new; 
                  }
                  else  //XXX
                  {
                     String wrkHrs_new = wrkHrs + ":" + wrkHrs1_old;
                     String btStr_new = btStr +  ":" + btStr1_old;
	             String tseStatus_new = status + ":" + tseStatus1_old;
	             String tseRef_new = tseref + ":" + tseRef1_old;
	             String mheRef_new = mheref + ":" + mheRef1_old;

	             ManHourObject value_new = new ManHourObject (wrkHrs_new);
   	             value_new.setBillType (btStr_new);
	             value_new.setTseStatus(tseStatus_new);
	             value_new.setTseRef(tseRef_new);
	             value_new.setMheRef(mheRef_new);
	             return value_new; 
                  }
               }
	       else    // yyy:YYY
               {
	          if (wrkHrs == null || wrkHrs.length() == 0)   //000
                  {
	             String wrkHrs_new = ":" + wrkHrs1_old;
	             String btStr_new = ":" + btStr1_old;
	             String tseStatus_new = ":" + tseStatus1_old;
	             String tseRef_new = ":" + tseRef1_old;
	             String mheRef_new = ":" + mheRef1_old;

	             ManHourObject value_new = new ManHourObject (wrkHrs_new);
	             value_new.setBillType (btStr_new);
	             value_new.setTseStatus(tseStatus_new);
	             value_new.setTseRef(tseRef_new);
	             value_new.setMheRef(mheRef_new);
	             return value_new; 
	          }
                  else //XXX
                  {
                     String wrkHrs_new = wrkHrs + ":" + wrkHrs1_old;
	             String btStr_new = btStr +  ":" + btStr1_old;
	             String tseStatus_new = status + ":" + tseStatus1_old;
	             String tseRef_new = tseref + ":" + tseRef1_old;
	             String mheRef_new = mheref + ":" + mheRef1_old;

	             ManHourObject value_new = new ManHourObject (wrkHrs_new);
                     value_new.setBillType (btStr_new);
	             value_new.setTseStatus(tseStatus_new);
	             value_new.setTseRef(tseRef_new);
	             value_new.setMheRef(mheRef_new);
	             return value_new; 
                  }
               }
            }
            else    // :XXX
            {
               if (btStr0_old == null || btStr0_old.length() == 0)   //:YYY
               {
	          if (wrkHrs == null || wrkHrs.length() == 0)    //:000
                  {
   	             return null;
	          }
	          else
                  {
                     String wrkHrs_new = wrkHrs0_old + ":" + wrkHrs;
	             String btStr_new = btStr0_old + ":" + btStr;
	             String tseStatus_new = tseStatus0_old + ":" + status;
	             String tseRef_new = tseRef0_old + ":" + tseref;
	             String mheRef_new = mheRef0_old + ":" + mheref;

	             ManHourObject value_new = new ManHourObject (wrkHrs_new);
	             value_new.setBillType (btStr_new);
	             value_new.setTseStatus(tseStatus_new);
	             value_new.setTseRef(tseRef_new);
	             value_new.setMheRef(mheRef_new);
	             return value_new; 
                  }
               }
               else  //yyy:YYY
               {
	          if (wrkHrs == null || wrkHrs.length() == 0)  //:000
                  {
	             String wrkHrs_new = wrkHrs0_old;
                     String btStr_new = btStr0_old;
	             String tseStatus_new = tseStatus0_old;
	             String tseRef_new = tseRef0_old;
	             String mheRef_new = mheRef0_old;

	             ManHourObject value_new = new ManHourObject (wrkHrs_new);
	             value_new.setBillType (btStr_new);
	             value_new.setTseStatus(tseStatus_new);
	             value_new.setTseRef(tseRef_new);
	             value_new.setMheRef(mheRef_new);
   	             return value_new; 
	          }
	          else //:XXX
                  {
                     String wrkHrs_new = wrkHrs0_old + ":" + wrkHrs;
	             String btStr_new = btStr0_old + ":" + btStr;
	             String tseStatus_new = tseStatus0_old + ":" + status;
	             String tseRef_new = tseRef0_old + ":" + tseref;
	             String mheRef_new = mheRef0_old + ":" + mheref;

	             ManHourObject value_new = new ManHourObject (wrkHrs_new);
	             value_new.setBillType (btStr_new);
	             value_new.setTseStatus(tseStatus_new);
	             value_new.setTseRef(tseRef_new);
	             value_new.setMheRef(mheRef_new);
	             return value_new; 
                  }
               } 
            }
         }                 
         else  // set length =1  //yyy
         {
            String wrkHrs0_old = wrkHrsSet_old[0];
            String btStr0_old = btStrSet_old[0];
            String tseStatus0_old = tseStatusSet_old[0];
	    String tseRef0_old = tseRefSet_old[0];
	    String mheRef0_old = mheRefSet_old[0];

	    if (btStr.equals (ManHourObject.nBillType))  //XXX
            {
	       if (wrkHrs == null || wrkHrs.length() == 0) //000
               {
	          return null;
	       }
	       else //XXX
               {
                  String wrkHrs_new = wrkHrs;
	          String btStr_new = btStr;
	          String tseStatus_new = status;
	          String tseRef_new = tseref;
	          String mheRef_new = mheref;

	          ManHourObject value_new = new ManHourObject (wrkHrs_new);
	          value_new.setBillType (btStr_new);
	          value_new.setTseStatus(tseStatus_new);
	          value_new.setTseRef(tseRef_new);
	          value_new.setMheRef(mheRef_new);
	          return value_new; 
               }
            }
            else //:XXX
            {
              if (wrkHrs == null || wrkHrs.length() == 0)  //:000
              {
	         return old_value;
	      }
	      else //:XXX
              {
                 String wrkHrs_new = wrkHrs0_old + ":" + wrkHrs;
	         String btStr_new = btStr0_old + ":" + btStr;
	         String tseStatus_new = tseStatus0_old + ":" + status;
	         String tseRef_new = tseRef0_old + ":" + tseref;
	         String mheRef_new = mheRef0_old + ":" + mheref;

	         ManHourObject value_new = new ManHourObject (wrkHrs_new);
	         value_new.setBillType (btStr_new);
	         value_new.setTseStatus(tseStatus_new);
	         value_new.setTseRef(tseRef_new);
	         value_new.setMheRef(mheRef_new);
	         return value_new; 
              }
           }
        }
     }
  }



   /*
    * mergeMHEObject 
    */
  public static ManHourObject mergeMHEObject(ManHourObject old_value, ManHourObject value)
  {
     if (value == null)
     {
        return null;
     }

     ManHourObject mhValue = (ManHourObject) value;
     String btStr = mhValue.getBillType ();
     String wrkHrs = mhValue.getWorkingHours();
     if (btStr.equals (ManHourObject.wBillType) || btStr.equals (ManHourObject.hBillType))
     {
        if (wrkHrs != null && wrkHrs.length() != 0)
        {
           return value;
        }
        else
        {
           return null;
        }
     }

     if (old_value == null)
     {
        return mergeMHEObject0 (null, mhValue);
     }

     ManHourObject mhValue_old = (ManHourObject) old_value;
     String wrkHours_old = mhValue_old.getWorkingHours();
     String btStr_old = mhValue_old.getBillType ();
     if (btStr_old.equals (ManHourObject.wBillType) || btStr_old.equals (ManHourObject.hBillType))
     {
        return mergeMHEObject0 (null, mhValue);
     }

     return mergeMHEObject0 (mhValue_old, mhValue);
  }


}
