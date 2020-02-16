/* 
 @<COPYRIGHT>@
 ==================================================
 Copyright 2012
 Siemens Product Lifecycle Management Software Inc.
 All Rights Reserved.
 ==================================================
 @<COPYRIGHT>@

 ==================================================

   Auto-generated source from service interface.
                 DO NOT EDIT

 ==================================================
*/


package com.d9.services.strong.work._2018_06;

/**
 *
 */
 @SuppressWarnings("unchecked")
public interface ManHourManageService
{

    /**
     * ManHourBillType
     */
    public class ManHourBillType
    {
        /**
         * myRefBR
         */
        public String myRefBR = "";
        /**
         * billTypeInternal
         */
        public String billTypeInternal = "";
        /**
         * billRateName
         */
        public String billRateName = "";
    }


    /**
     * ManHourEntry
     */
    public class ManHourEntry
    {
        /**
         * myUserName,
         */
        public String myUserName = "";
        /**
         * myYear
         */
        public String myYear = "";
        /**
         * myMonth
         */
        public String myMonth = "";
        /**
         * myDay
         */
        public String myDay = "";
        /**
         * myDayOfWeek
         */
        public String myDayOfWeek = "";
        /**
         * myHoliday
         */
        public String myHoliday = "";
        /**
         * workRequired
         */
        public String workRequired = "";
        /**
         * myPrjName
         */
        public String myPrjName = "";
        /**
         * myPrjId
         */
        public String myPrjId = "";
        /**
         * myTaskType
         */
        public String myTaskType = "";
        /**
         * myTaskTypeDval
         */
        public String myTaskTypeDval = "";
        /**
         * billType
         */
        public String billType = "";
        /**
         * myRefBR
         */
        public String myRefBR = "";
        /**
         * workingHours
         */
        public String workingHours = "";
        /**
         * myRefTE
         */
        public String myRefTE = "";
        /**
         * tseStatus
         */
        public String tseStatus = "";
        /**
         * myRefMHE
         */
        public String myRefMHE = "";
        /**
         * row
         */
        public int row;
        /**
         * col
         */
        public int col;
        /**
         * billTypeInternal
         */
        public String billTypeInternal = "";
    }


    /**
     * ManHourEntrySet
     */
    public class ManHourEntrySet
    {
        /**
         * mheSet
         */
        public com.d9.services.strong.work._2018_06.ManHourManageService.ManHourEntry[] mheSet = new com.d9.services.strong.work._2018_06.ManHourManageService.ManHourEntry[0];
    }


    /**
     * ManHourInfo
     */
    public class ManHourInfo
    {
        /**
         * myUserName
         */
        public String myUserName = "";
        /**
         * myPosition
         */
        public String myPosition = "";
        /**
         * myYear
         */
        public String myYear = "";
        /**
         * myMonth
         */
        public String myMonth = "";
        /**
         * totalDays
         */
        public int totalDays;
        /**
         * theMonthTmp
         */
        public com.d9.services.strong.work._2018_06.ManHourManageService.ManHourMonthTmp[] theMonthTmp = new com.d9.services.strong.work._2018_06.ManHourManageService.ManHourMonthTmp[0];
        /**
         * theProgramSet
         */
        public com.d9.services.strong.work._2018_06.ManHourManageService.ManHourProgram[] theProgramSet = new com.d9.services.strong.work._2018_06.ManHourManageService.ManHourProgram[0];
        /**
         * isHourlyBasedUser
         */
        public boolean isHourlyBasedUser;
        /**
         * isManHourEditable
         */
        public boolean isManHourEditable;
        /**
         * theBillTypeSet
         */
        public com.d9.services.strong.work._2018_06.ManHourManageService.ManHourBillType[] theBillTypeSet = new com.d9.services.strong.work._2018_06.ManHourManageService.ManHourBillType[0];
    }


    /**
     * ManHourMonthTmp
     */
    public class ManHourMonthTmp
    {
        /**
         * dayOfMonth
         */
        public int dayOfMonth;
        /**
         * dayOfWeek
         */
        public String dayOfWeek = "";
        /**
         * isWeekend
         */
        public boolean isWeekend;
        /**
         * isHoliday
         */
        public boolean isHoliday;
        /**
         * holidayName
         */
        public String holidayName = "";
        /**
         * isWorkRequired
         */
        public boolean isWorkRequired;
    }


    /**
     * ManHourProgram
     */
    public class ManHourProgram
    {
        /**
         * prjId
         */
        public String prjId = "";
        /**
         * prjName
         */
        public String prjName = "";
        /**
         * tskType
         */
        public String tskType = "";
        /**
         * tskTypeDval
         */
        public String tskTypeDval = "";
        /**
         * stkTag
         */
        public String stkTag = "";
        /**
         * startDay
         */
        public int startDay;
        /**
         * endDay
         */
        public int endDay;
        /**
         * prjStartYear
         */
        public int prjStartYear;
        /**
         * prjStartMonth
         */
        public int prjStartMonth;
        /**
         * prjStartDay
         */
        public int prjStartDay;
        /**
         * prjEndYear
         */
        public int prjEndYear;
        /**
         * prjEndMonth
         */
        public int prjEndMonth;
        /**
         * prjEndDay
         */
        public int prjEndDay;
        /**
         * schTag
         */
        public String schTag = "";
    }



    

    /**
     * .
     *
     * @param theUser
     *        theUser
     *
     * @param theAction
     *        theAction
     *
     * @param thePara
     *        thePara
     *
     * @return
     *
     */
    public String clearManHoursOP ( String theUser, String theAction, String thePara );


    /**
     * .
     *
     * @param theUserName
     *        theUserName
     *
     * @param theYear
     *        theYear
     *
     * @param theMonth
     *        theMonth
     *
     * @return
     *
     */
    public com.d9.services.strong.work._2018_06.ManHourManageService.ManHourInfo getManHourInfoOP ( String theUserName, String theYear, String theMonth );


    /**
     * .
     *
     * @param manHourFilter
     *        manHourFilter
     *
     * @return
     *
     */
    public com.d9.services.strong.work._2018_06.ManHourManageService.ManHourEntrySet loadOP ( com.d9.services.strong.work._2018_06.ManHourManageService.ManHourEntry manHourFilter );


    /**
     * .
     *
     * @param theUser
     *        theUser
     *
     * @return
     *
     */
    public String mheTest ( String theUser );


    /**
     * .
     *
     * @param username
     *        username
     *
     * @param year
     *        year
     *
     * @param month
     *        month
     *
     * @param manHours
     *        manHours
     *
     * @return
     *
     */
    public com.d9.services.strong.work._2018_06.ManHourManageService.ManHourEntrySet reviseOP ( String username, String year, String month, com.d9.services.strong.work._2018_06.ManHourManageService.ManHourEntry[] manHours );


    /**
     * .
     *
     * @param username
     *        username
     *
     * @param year
     *        year
     *
     * @param month
     *        month
     *
     * @param manHours
     *        manHours
     *
     * @param programs
     *        programs
     *
     * @return
     *
     */
    public com.d9.services.strong.work._2018_06.ManHourManageService.ManHourEntrySet saveOP ( String username, String year, String month, com.d9.services.strong.work._2018_06.ManHourManageService.ManHourEntry[] manHours, com.d9.services.strong.work._2018_06.ManHourManageService.ManHourProgram[] programs );



}
