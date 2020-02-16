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


package com.d6.services.rac.work._2012_02;

/**
 *
 */
 @SuppressWarnings("unchecked")
public interface ManHourManagement
{

    /**
     * man hour enty
     */
    public class ManHourEntry
    {
        /**
         * myUserName
         */
        public String myUserName = "";
        /**
         * the year for man hour.
         */
        public String myYear = "";
        /**
         * the month for man hour
         */
        public String myMonth = "";
        /**
         * the day of year
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
         * bill rate reference
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
         * tse status
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
     * basic man hour info.
     */
    public class ManHourInfo
    {
        /**
         * user name
         */
        public String myUserName = "";
        /**
         * myPosition
         */
        public String myPosition = "";
        /**
         * the year for man hour.
         */
        public String myYear = "";
        /**
         * the month for the man hour.
         */
        public String myMonth = "";
        /**
         * Total days for the give year and month.
         */
        public int totalDays;
        /**
         * the month template.
         */
        public com.d6.services.rac.work._2012_02.ManHourManagement.ManHourMonthTmp[] theMonthTmp = new com.d6.services.rac.work._2012_02.ManHourManagement.ManHourMonthTmp[0];
        /**
         * a set of program
         */
        public com.d6.services.rac.work._2012_02.ManHourManagement.ManHourProgram[] theProgramSet = new com.d6.services.rac.work._2012_02.ManHourManagement.ManHourProgram[0];
        /**
         * identify if the user is a hourly based user
         */
        public boolean isHourlyBasedUser;
        /**
         * identify if the man hour can be edited.
         */
        public boolean isManHourEditable;
        /**
         * theBillTypeSet
         */
        public com.d6.services.rac.work._2012_02.ManHourManagement.ManHourBillType[] theBillTypeSet = new com.d6.services.rac.work._2012_02.ManHourManagement.ManHourBillType[0];
    }


    /**
     * the month template for man hour.
     */
    public class ManHourMonthTmp
    {
        /**
         * the day of month.
         */
        public int dayOfMonth;
        /**
         * the day of week.
         */
        public String dayOfWeek = "";
        /**
         * is the day weekend
         */
        public boolean isWeekend;
        /**
         * is the day holiday
         */
        public boolean isHoliday;
        /**
         * the holiday name.
         */
        public String holidayName = "";
        /**
         * is the day required to work.
         */
        public boolean isWorkRequired;
    }


    /**
     * the program info for the man hour.
     */
    public class ManHourProgram
    {
        /**
         * Project ID
         */
        public String prjId = "";
        /**
         * Project Name
         */
        public String prjName = "";
        /**
         * task type
         */
        public String tskType = "";
        /**
         * tskTypeDval
         */
        public String tskTypeDval = "";
        /**
         * Schdeule Task Tag
         */
        public String stkTag = "";
        /**
         * start day in given month
         */
        public int startDay;
        /**
         * end day in given month
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
     * a set of man hour entries
     */
    public class ManHourEntrySet
    {
        /**
         * a sef of man hour entries
         */
        public com.d6.services.rac.work._2012_02.ManHourManagement.ManHourEntry[] mheSet = new com.d6.services.rac.work._2012_02.ManHourManagement.ManHourEntry[0];
    }


    /**
     * BillType
     */
    public class ManHourBillType
    {
        /**
         * bill rate ref.
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
     * get the man hour info for give year and month
     *
     * @param theUserName
     *        the user name
     *
     * @param theYear
     *        the given year
     *
     * @param theMonth
     *        the given month
     *
     * @return
     *         the man hour info
     *
     * @exception  com.teamcenter.schemas.soa._2006_03.exceptions.ServiceException
     *
     */
    public com.d6.services.rac.work._2012_02.ManHourManagement.ManHourInfo getManHourInfo ( String theUserName, String theYear, String theMonth )
    throws com.teamcenter.schemas.soa._2006_03.exceptions.ServiceException;


    /**
     * clear man hours
     *
     * @param theUser
     *        the user name
     *
     * @param theYear
     *        the year
     *
     * @param theMonth
     *        the month
     *
     * @return
     *         return string value
     *
     * @exception  com.teamcenter.schemas.soa._2006_03.exceptions.ServiceException
     *
     */
    public String clearManHours ( String theUser, String theYear, String theMonth )
    throws com.teamcenter.schemas.soa._2006_03.exceptions.ServiceException;


    /**
     * load man hour entires
     *
     * @param filter
     *        filter
     *
     * @return
     *         a set of man hour entries
     *
     * @exception  com.teamcenter.schemas.soa._2006_03.exceptions.ServiceException
     *
     */
    public com.d6.services.rac.work._2012_02.ManHourManagement.ManHourEntrySet load ( com.d6.services.rac.work._2012_02.ManHourManagement.ManHourEntry filter )
    throws com.teamcenter.schemas.soa._2006_03.exceptions.ServiceException;


    /**
     * save man hours
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
     * @param manHours
     *        manHours
     *
     * @param thePrograms
     *        the programs
     *
     * @return
     *         save man hours
     *
     * @exception  com.teamcenter.schemas.soa._2006_03.exceptions.ServiceException
     *
     */
    public com.d6.services.rac.work._2012_02.ManHourManagement.ManHourEntrySet save ( String theUserName, String theYear, String theMonth, com.d6.services.rac.work._2012_02.ManHourManagement.ManHourEntry[] manHours, com.d6.services.rac.work._2012_02.ManHourManagement.ManHourProgram[] thePrograms )
    throws com.teamcenter.schemas.soa._2006_03.exceptions.ServiceException;


    /**
     * revise
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
     *         man hour entry set
     *
     * @exception  com.teamcenter.schemas.soa._2006_03.exceptions.ServiceException
     *
     */
    public com.d6.services.rac.work._2012_02.ManHourManagement.ManHourEntrySet revise ( String username, String year, String month, com.d6.services.rac.work._2012_02.ManHourManagement.ManHourEntry[] manHours )
    throws com.teamcenter.schemas.soa._2006_03.exceptions.ServiceException;


    /**
     * mheTest
     *
     * @param userName
     *        userName
     *
     * @return
     *         test result
     *
     * @exception  com.teamcenter.schemas.soa._2006_03.exceptions.ServiceException
     *
     */
    public String mheTest ( String userName )
    throws com.teamcenter.schemas.soa._2006_03.exceptions.ServiceException;



}
