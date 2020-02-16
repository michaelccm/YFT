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
 
package com.d6.services.strong.work;

import java.util.List;


import com.teamcenter.soa.client.Connection;
import com.teamcenter.soa.internal.client.Sender;
import com.teamcenter.soa.internal.client.model.PopulateModel;

 /**
  * @unpublished
  */
@SuppressWarnings("unchecked")
public class ManHourManagementRestBindingStub extends ManHourManagementService
{
    private Sender              restSender;
    private PopulateModel       modelManager;
    private Connection          localConnection;
    
    /**
     * Constructor
     * @param connection
     * @unpublished
     */
    public ManHourManagementRestBindingStub( Connection connection )
    {
        this.localConnection = connection;
        this.restSender  = connection.getSender();
        this.modelManager= (PopulateModel)this.localConnection.getModelManager();
        com.teamcenter.soa.client.model.StrongObjectFactory.init();
    }

    static com.teamcenter.schemas.soa._2006_03.base.ObjectFactory base_Factory = new 
           com.teamcenter.schemas.soa._2006_03.base.ObjectFactory();

    // each child interface has its own factory and methods for calling


    static com.d6.schemas.work._2012_02.manhourmanagement.ObjectFactory ManHourManagement_201202Factory = new 
           com.d6.schemas.work._2012_02.manhourmanagement.ObjectFactory();
    static final String MANHOURMANAGEMENT_201202_PORT_NAME          = "Work-2012-02-ManHourManagement";
    static final String MANHOURMANAGEMENT_201202_CONTEXT_PATH = "com.d6.schemas.work._2012_02.manhourmanagement:com.teamcenter.schemas.soa._2006_03.base";

    /**
     * @param local
     * @return wire
     * @unpublished 
     */
    public  static com.d6.schemas.work._2012_02.manhourmanagement.ManHourEntry toWire( com.d6.services.strong.work._2012_02.ManHourManagement.ManHourEntry local ) 
    {
 		com.d6.schemas.work._2012_02.manhourmanagement.ManHourEntry wire = null;
     	wire = ManHourManagement_201202Factory.createManHourEntry();

        wire.setMyUserName( local.myUserName ); //BASIC_ELEMENT_TO_WIRE
        wire.setMyYear( local.myYear ); //BASIC_ELEMENT_TO_WIRE
        wire.setMyMonth( local.myMonth ); //BASIC_ELEMENT_TO_WIRE
        wire.setMyDay( local.myDay ); //BASIC_ELEMENT_TO_WIRE
        wire.setMyDayOfWeek( local.myDayOfWeek ); //BASIC_ELEMENT_TO_WIRE
        wire.setMyHoliday( local.myHoliday ); //BASIC_ELEMENT_TO_WIRE
        wire.setWorkRequired( local.workRequired ); //BASIC_ELEMENT_TO_WIRE
        wire.setMyPrjName( local.myPrjName ); //BASIC_ELEMENT_TO_WIRE
        wire.setMyPrjId( local.myPrjId ); //BASIC_ELEMENT_TO_WIRE
        wire.setMyTaskType( local.myTaskType ); //BASIC_ELEMENT_TO_WIRE
        wire.setMyTaskTypeDval( local.myTaskTypeDval ); //BASIC_ELEMENT_TO_WIRE
        wire.setBillType( local.billType ); //BASIC_ELEMENT_TO_WIRE
        wire.setMyRefBR( local.myRefBR ); //BASIC_ELEMENT_TO_WIRE
        wire.setWorkingHours( local.workingHours ); //BASIC_ELEMENT_TO_WIRE
        wire.setMyRefTE( local.myRefTE ); //BASIC_ELEMENT_TO_WIRE
        wire.setTseStatus( local.tseStatus ); //BASIC_ELEMENT_TO_WIRE
        wire.setMyRefMHE( local.myRefMHE ); //BASIC_ELEMENT_TO_WIRE
        wire.setRow( new java.math.BigInteger(Integer.toString( local.row ))); //BIG_INGEGER_ELEMENT_TO_WIRE
        wire.setCol( new java.math.BigInteger(Integer.toString( local.col ))); //BIG_INGEGER_ELEMENT_TO_WIRE
        wire.setBillTypeInternal( local.billTypeInternal ); //BASIC_ELEMENT_TO_WIRE

		return wire;
	}
    /**
     * @param wire
     * @param modelManager
     * @return local
     * @unpublished 
     */
    public  static com.d6.services.strong.work._2012_02.ManHourManagement.ManHourEntry toLocal( com.d6.schemas.work._2012_02.manhourmanagement.ManHourEntry  wire , com.teamcenter.soa.internal.client.model.PopulateModel modelManager ) 
 		
    {
  		com.d6.services.strong.work._2012_02.ManHourManagement.ManHourEntry local = new com.d6.services.strong.work._2012_02.ManHourManagement.ManHourEntry();

        local.myUserName = wire.getMyUserName(); //BASIC_ELEMENT_TO_LOCAL
        local.myYear = wire.getMyYear(); //BASIC_ELEMENT_TO_LOCAL
        local.myMonth = wire.getMyMonth(); //BASIC_ELEMENT_TO_LOCAL
        local.myDay = wire.getMyDay(); //BASIC_ELEMENT_TO_LOCAL
        local.myDayOfWeek = wire.getMyDayOfWeek(); //BASIC_ELEMENT_TO_LOCAL
        local.myHoliday = wire.getMyHoliday(); //BASIC_ELEMENT_TO_LOCAL
        local.workRequired = wire.getWorkRequired(); //BASIC_ELEMENT_TO_LOCAL
        local.myPrjName = wire.getMyPrjName(); //BASIC_ELEMENT_TO_LOCAL
        local.myPrjId = wire.getMyPrjId(); //BASIC_ELEMENT_TO_LOCAL
        local.myTaskType = wire.getMyTaskType(); //BASIC_ELEMENT_TO_LOCAL
        local.myTaskTypeDval = wire.getMyTaskTypeDval(); //BASIC_ELEMENT_TO_LOCAL
        local.billType = wire.getBillType(); //BASIC_ELEMENT_TO_LOCAL
        local.myRefBR = wire.getMyRefBR(); //BASIC_ELEMENT_TO_LOCAL
        local.workingHours = wire.getWorkingHours(); //BASIC_ELEMENT_TO_LOCAL
        local.myRefTE = wire.getMyRefTE(); //BASIC_ELEMENT_TO_LOCAL
        local.tseStatus = wire.getTseStatus(); //BASIC_ELEMENT_TO_LOCAL
        local.myRefMHE = wire.getMyRefMHE(); //BASIC_ELEMENT_TO_LOCAL
        local.row = wire.getRow().intValue(); //BIG_INGEGER_ELEMENT_TO_LOCAL
        local.col = wire.getCol().intValue(); //BIG_INGEGER_ELEMENT_TO_LOCAL
        local.billTypeInternal = wire.getBillTypeInternal(); //BASIC_ELEMENT_TO_LOCAL


		return local;
	}

    /**
     * @param local
     * @return wire
     * @unpublished 
     */
    public  static com.d6.schemas.work._2012_02.manhourmanagement.ManHourInfo toWire( com.d6.services.strong.work._2012_02.ManHourManagement.ManHourInfo local ) 
    {
 		com.d6.schemas.work._2012_02.manhourmanagement.ManHourInfo wire = null;
     	wire = ManHourManagement_201202Factory.createManHourInfo();

        wire.setMyUserName( local.myUserName ); //BASIC_ELEMENT_TO_WIRE
        wire.setMyPosition( local.myPosition ); //BASIC_ELEMENT_TO_WIRE
        wire.setMyYear( local.myYear ); //BASIC_ELEMENT_TO_WIRE
        wire.setMyMonth( local.myMonth ); //BASIC_ELEMENT_TO_WIRE
        wire.setTotalDays( new java.math.BigInteger(Integer.toString( local.totalDays ))); //BIG_INGEGER_ELEMENT_TO_WIRE
         List wireInTheMonthTmp = wire.getTheMonthTmp();// VECTOR_TO_WIRE 
         for (int i = 0; i < local.theMonthTmp.length; i ++ )
         {
                     wireInTheMonthTmp.add(toWire(local.theMonthTmp[i])); // STRUCT_IN_VECTOR_TO_WIRE 

         }
         List wireInTheProgramSet = wire.getTheProgramSet();// VECTOR_TO_WIRE 
         for (int i = 0; i < local.theProgramSet.length; i ++ )
         {
                     wireInTheProgramSet.add(toWire(local.theProgramSet[i])); // STRUCT_IN_VECTOR_TO_WIRE 

         }
        wire.setIsHourlyBasedUser( local.isHourlyBasedUser ); //BASIC_ELEMENT_TO_WIRE
        wire.setIsManHourEditable( local.isManHourEditable ); //BASIC_ELEMENT_TO_WIRE
         List wireInTheBillTypeSet = wire.getTheBillTypeSet();// VECTOR_TO_WIRE 
         for (int i = 0; i < local.theBillTypeSet.length; i ++ )
         {
                     wireInTheBillTypeSet.add(toWire(local.theBillTypeSet[i])); // STRUCT_IN_VECTOR_TO_WIRE 

         }

		return wire;
	}
    /**
     * @param wire
     * @param modelManager
     * @return local
     * @unpublished 
     */
    public  static com.d6.services.strong.work._2012_02.ManHourManagement.ManHourInfo toLocal( com.d6.schemas.work._2012_02.manhourmanagement.ManHourInfo  wire , com.teamcenter.soa.internal.client.model.PopulateModel modelManager ) 
 		
    {
  		com.d6.services.strong.work._2012_02.ManHourManagement.ManHourInfo local = new com.d6.services.strong.work._2012_02.ManHourManagement.ManHourInfo();

        local.myUserName = wire.getMyUserName(); //BASIC_ELEMENT_TO_LOCAL
        local.myPosition = wire.getMyPosition(); //BASIC_ELEMENT_TO_LOCAL
        local.myYear = wire.getMyYear(); //BASIC_ELEMENT_TO_LOCAL
        local.myMonth = wire.getMyMonth(); //BASIC_ELEMENT_TO_LOCAL
        local.totalDays = wire.getTotalDays().intValue(); //BIG_INGEGER_ELEMENT_TO_LOCAL
      List wireTheMonthTmp = wire.getTheMonthTmp();// VECTOR_TO_LOCAL
      local.theMonthTmp = new com.d6.services.strong.work._2012_02.ManHourManagement.ManHourMonthTmp[wireTheMonthTmp.size()];
      for (int i = 0; i < wireTheMonthTmp.size(); i ++ )
      {
          local.theMonthTmp[i] =  toLocal((com.d6.schemas.work._2012_02.manhourmanagement.ManHourMonthTmp)wireTheMonthTmp.get(i),modelManager); // STRUCT_IN_VECTOR_TO_LOCAL

      }
      List wireTheProgramSet = wire.getTheProgramSet();// VECTOR_TO_LOCAL
      local.theProgramSet = new com.d6.services.strong.work._2012_02.ManHourManagement.ManHourProgram[wireTheProgramSet.size()];
      for (int i = 0; i < wireTheProgramSet.size(); i ++ )
      {
          local.theProgramSet[i] =  toLocal((com.d6.schemas.work._2012_02.manhourmanagement.ManHourProgram)wireTheProgramSet.get(i),modelManager); // STRUCT_IN_VECTOR_TO_LOCAL

      }
        local.isHourlyBasedUser = wire.isIsHourlyBasedUser(); //BOOLEAN_ELEMENT_TO_LOCAL
        local.isManHourEditable = wire.isIsManHourEditable(); //BOOLEAN_ELEMENT_TO_LOCAL
      List wireTheBillTypeSet = wire.getTheBillTypeSet();// VECTOR_TO_LOCAL
      local.theBillTypeSet = new com.d6.services.strong.work._2012_02.ManHourManagement.ManHourBillType[wireTheBillTypeSet.size()];
      for (int i = 0; i < wireTheBillTypeSet.size(); i ++ )
      {
          local.theBillTypeSet[i] =  toLocal((com.d6.schemas.work._2012_02.manhourmanagement.ManHourBillType)wireTheBillTypeSet.get(i),modelManager); // STRUCT_IN_VECTOR_TO_LOCAL

      }


		return local;
	}

    /**
     * @param local
     * @return wire
     * @unpublished 
     */
    public  static com.d6.schemas.work._2012_02.manhourmanagement.ManHourMonthTmp toWire( com.d6.services.strong.work._2012_02.ManHourManagement.ManHourMonthTmp local ) 
    {
 		com.d6.schemas.work._2012_02.manhourmanagement.ManHourMonthTmp wire = null;
     	wire = ManHourManagement_201202Factory.createManHourMonthTmp();

        wire.setDayOfMonth( new java.math.BigInteger(Integer.toString( local.dayOfMonth ))); //BIG_INGEGER_ELEMENT_TO_WIRE
        wire.setDayOfWeek( local.dayOfWeek ); //BASIC_ELEMENT_TO_WIRE
        wire.setIsWeekend( local.isWeekend ); //BASIC_ELEMENT_TO_WIRE
        wire.setIsHoliday( local.isHoliday ); //BASIC_ELEMENT_TO_WIRE
        wire.setHolidayName( local.holidayName ); //BASIC_ELEMENT_TO_WIRE
        wire.setIsWorkRequired( local.isWorkRequired ); //BASIC_ELEMENT_TO_WIRE

		return wire;
	}
    /**
     * @param wire
     * @param modelManager
     * @return local
     * @unpublished 
     */
    public  static com.d6.services.strong.work._2012_02.ManHourManagement.ManHourMonthTmp toLocal( com.d6.schemas.work._2012_02.manhourmanagement.ManHourMonthTmp  wire , com.teamcenter.soa.internal.client.model.PopulateModel modelManager ) 
 		
    {
  		com.d6.services.strong.work._2012_02.ManHourManagement.ManHourMonthTmp local = new com.d6.services.strong.work._2012_02.ManHourManagement.ManHourMonthTmp();

        local.dayOfMonth = wire.getDayOfMonth().intValue(); //BIG_INGEGER_ELEMENT_TO_LOCAL
        local.dayOfWeek = wire.getDayOfWeek(); //BASIC_ELEMENT_TO_LOCAL
        local.isWeekend = wire.isIsWeekend(); //BOOLEAN_ELEMENT_TO_LOCAL
        local.isHoliday = wire.isIsHoliday(); //BOOLEAN_ELEMENT_TO_LOCAL
        local.holidayName = wire.getHolidayName(); //BASIC_ELEMENT_TO_LOCAL
        local.isWorkRequired = wire.isIsWorkRequired(); //BOOLEAN_ELEMENT_TO_LOCAL


		return local;
	}

    /**
     * @param local
     * @return wire
     * @unpublished 
     */
    public  static com.d6.schemas.work._2012_02.manhourmanagement.ManHourProgram toWire( com.d6.services.strong.work._2012_02.ManHourManagement.ManHourProgram local ) 
    {
 		com.d6.schemas.work._2012_02.manhourmanagement.ManHourProgram wire = null;
     	wire = ManHourManagement_201202Factory.createManHourProgram();

        wire.setPrjId( local.prjId ); //BASIC_ELEMENT_TO_WIRE
        wire.setPrjName( local.prjName ); //BASIC_ELEMENT_TO_WIRE
        wire.setTskType( local.tskType ); //BASIC_ELEMENT_TO_WIRE
        wire.setTskTypeDval( local.tskTypeDval ); //BASIC_ELEMENT_TO_WIRE
        wire.setStkTag( local.stkTag ); //BASIC_ELEMENT_TO_WIRE
        wire.setStartDay( new java.math.BigInteger(Integer.toString( local.startDay ))); //BIG_INGEGER_ELEMENT_TO_WIRE
        wire.setEndDay( new java.math.BigInteger(Integer.toString( local.endDay ))); //BIG_INGEGER_ELEMENT_TO_WIRE
        wire.setPrjStartYear( new java.math.BigInteger(Integer.toString( local.prjStartYear ))); //BIG_INGEGER_ELEMENT_TO_WIRE
        wire.setPrjStartMonth( new java.math.BigInteger(Integer.toString( local.prjStartMonth ))); //BIG_INGEGER_ELEMENT_TO_WIRE
        wire.setPrjStartDay( new java.math.BigInteger(Integer.toString( local.prjStartDay ))); //BIG_INGEGER_ELEMENT_TO_WIRE
        wire.setPrjEndYear( new java.math.BigInteger(Integer.toString( local.prjEndYear ))); //BIG_INGEGER_ELEMENT_TO_WIRE
        wire.setPrjEndMonth( new java.math.BigInteger(Integer.toString( local.prjEndMonth ))); //BIG_INGEGER_ELEMENT_TO_WIRE
        wire.setPrjEndDay( new java.math.BigInteger(Integer.toString( local.prjEndDay ))); //BIG_INGEGER_ELEMENT_TO_WIRE
        wire.setSchTag( local.schTag ); //BASIC_ELEMENT_TO_WIRE

		return wire;
	}
    /**
     * @param wire
     * @param modelManager
     * @return local
     * @unpublished 
     */
    public  static com.d6.services.strong.work._2012_02.ManHourManagement.ManHourProgram toLocal( com.d6.schemas.work._2012_02.manhourmanagement.ManHourProgram  wire , com.teamcenter.soa.internal.client.model.PopulateModel modelManager ) 
 		
    {
  		com.d6.services.strong.work._2012_02.ManHourManagement.ManHourProgram local = new com.d6.services.strong.work._2012_02.ManHourManagement.ManHourProgram();

        local.prjId = wire.getPrjId(); //BASIC_ELEMENT_TO_LOCAL
        local.prjName = wire.getPrjName(); //BASIC_ELEMENT_TO_LOCAL
        local.tskType = wire.getTskType(); //BASIC_ELEMENT_TO_LOCAL
        local.tskTypeDval = wire.getTskTypeDval(); //BASIC_ELEMENT_TO_LOCAL
        local.stkTag = wire.getStkTag(); //BASIC_ELEMENT_TO_LOCAL
        local.startDay = wire.getStartDay().intValue(); //BIG_INGEGER_ELEMENT_TO_LOCAL
        local.endDay = wire.getEndDay().intValue(); //BIG_INGEGER_ELEMENT_TO_LOCAL
        local.prjStartYear = wire.getPrjStartYear().intValue(); //BIG_INGEGER_ELEMENT_TO_LOCAL
        local.prjStartMonth = wire.getPrjStartMonth().intValue(); //BIG_INGEGER_ELEMENT_TO_LOCAL
        local.prjStartDay = wire.getPrjStartDay().intValue(); //BIG_INGEGER_ELEMENT_TO_LOCAL
        local.prjEndYear = wire.getPrjEndYear().intValue(); //BIG_INGEGER_ELEMENT_TO_LOCAL
        local.prjEndMonth = wire.getPrjEndMonth().intValue(); //BIG_INGEGER_ELEMENT_TO_LOCAL
        local.prjEndDay = wire.getPrjEndDay().intValue(); //BIG_INGEGER_ELEMENT_TO_LOCAL
        local.schTag = wire.getSchTag(); //BASIC_ELEMENT_TO_LOCAL


		return local;
	}

    /**
     * @param local
     * @return wire
     * @unpublished 
     */
    public  static com.d6.schemas.work._2012_02.manhourmanagement.ManHourEntrySet toWire( com.d6.services.strong.work._2012_02.ManHourManagement.ManHourEntrySet local ) 
    {
 		com.d6.schemas.work._2012_02.manhourmanagement.ManHourEntrySet wire = null;
     	wire = ManHourManagement_201202Factory.createManHourEntrySet();

         List wireInMheSet = wire.getMheSet();// VECTOR_TO_WIRE 
         for (int i = 0; i < local.mheSet.length; i ++ )
         {
                     wireInMheSet.add(toWire(local.mheSet[i])); // STRUCT_IN_VECTOR_TO_WIRE 

         }

		return wire;
	}
    /**
     * @param wire
     * @param modelManager
     * @return local
     * @unpublished 
     */
    public  static com.d6.services.strong.work._2012_02.ManHourManagement.ManHourEntrySet toLocal( com.d6.schemas.work._2012_02.manhourmanagement.ManHourEntrySet  wire , com.teamcenter.soa.internal.client.model.PopulateModel modelManager ) 
 		
    {
  		com.d6.services.strong.work._2012_02.ManHourManagement.ManHourEntrySet local = new com.d6.services.strong.work._2012_02.ManHourManagement.ManHourEntrySet();

      List wireMheSet = wire.getMheSet();// VECTOR_TO_LOCAL
      local.mheSet = new com.d6.services.strong.work._2012_02.ManHourManagement.ManHourEntry[wireMheSet.size()];
      for (int i = 0; i < wireMheSet.size(); i ++ )
      {
          local.mheSet[i] =  toLocal((com.d6.schemas.work._2012_02.manhourmanagement.ManHourEntry)wireMheSet.get(i),modelManager); // STRUCT_IN_VECTOR_TO_LOCAL

      }


		return local;
	}

    /**
     * @param local
     * @return wire
     * @unpublished 
     */
    public  static com.d6.schemas.work._2012_02.manhourmanagement.ManHourBillType toWire( com.d6.services.strong.work._2012_02.ManHourManagement.ManHourBillType local ) 
    {
 		com.d6.schemas.work._2012_02.manhourmanagement.ManHourBillType wire = null;
     	wire = ManHourManagement_201202Factory.createManHourBillType();

        wire.setMyRefBR( local.myRefBR ); //BASIC_ELEMENT_TO_WIRE
        wire.setBillTypeInternal( local.billTypeInternal ); //BASIC_ELEMENT_TO_WIRE
        wire.setBillRateName( local.billRateName ); //BASIC_ELEMENT_TO_WIRE

		return wire;
	}
    /**
     * @param wire
     * @param modelManager
     * @return local
     * @unpublished 
     */
    public  static com.d6.services.strong.work._2012_02.ManHourManagement.ManHourBillType toLocal( com.d6.schemas.work._2012_02.manhourmanagement.ManHourBillType  wire , com.teamcenter.soa.internal.client.model.PopulateModel modelManager ) 
 		
    {
  		com.d6.services.strong.work._2012_02.ManHourManagement.ManHourBillType local = new com.d6.services.strong.work._2012_02.ManHourManagement.ManHourBillType();

        local.myRefBR = wire.getMyRefBR(); //BASIC_ELEMENT_TO_LOCAL
        local.billTypeInternal = wire.getBillTypeInternal(); //BASIC_ELEMENT_TO_LOCAL
        local.billRateName = wire.getBillRateName(); //BASIC_ELEMENT_TO_LOCAL


		return local;
	}
	
    @Override 
    public com.d6.services.strong.work._2012_02.ManHourManagement.ManHourInfo getManHourInfo( String theUserName, String theYear, String theMonth ) 
    
    throws com.teamcenter.schemas.soa._2006_03.exceptions.ServiceException
    {
       try
       {
        restSender.pushRequestId();
        com.teamcenter.soa.internal.common.Monitor.markStart("com.d6.services.strong.work.ManHourManagementRestBindingStub.getManHourInfo");
        com.teamcenter.soa.internal.common.Monitor.markStart("com.d6.services.strong.work.ManHourManagementRestBindingStub.getManHourInfo.localToWire");
		com.d6.schemas.work._2012_02.manhourmanagement.GetManHourInfoInput wireIn = null;
        wireIn = ManHourManagement_201202Factory.createGetManHourInfoInput();
        wireIn.setTheUserName( theUserName ); //BASIC_ELEMENT_TO_WIRE
        wireIn.setTheYear( theYear ); //BASIC_ELEMENT_TO_WIRE
        wireIn.setTheMonth( theMonth ); //BASIC_ELEMENT_TO_WIRE

        com.teamcenter.soa.internal.common.Monitor.markEnd  ("com.d6.services.strong.work.ManHourManagementRestBindingStub.getManHourInfo.localToWire");

        Object outObj = restSender.invoke2( MANHOURMANAGEMENT_201202_PORT_NAME, "getManHourInfo", wireIn, 
                        MANHOURMANAGEMENT_201202_CONTEXT_PATH, MANHOURMANAGEMENT_201202_CONTEXT_PATH);
		modelManager.lockModel();
		
        if(outObj instanceof com.teamcenter.schemas.soa._2006_03.exceptions.ServiceException)
           throw (com.teamcenter.schemas.soa._2006_03.exceptions.ServiceException)outObj;
        com.teamcenter.soa.internal.common.Monitor.markStart("com.d6.services.strong.work.ManHourManagementRestBindingStub.getManHourInfo.wireToLocal");

        com.d6.schemas.work._2012_02.manhourmanagement.ManHourInfo wireOut = 
       (com.d6.schemas.work._2012_02.manhourmanagement.ManHourInfo)outObj;
        com.d6.services.strong.work._2012_02.ManHourManagement.ManHourInfo localOut;
        
        localOut =  toLocal( wireOut,modelManager ); // STRUCT_RETURN

        if(!localConnection.getOption(Connection.OPT_CACHE_MODEL_OBJECTS).equals( "true" ))
      	{
        	localConnection.getClientDataModel().removeAllObjects(); 
        }
        com.teamcenter.soa.internal.common.Monitor.markEnd  ("com.d6.services.strong.work.ManHourManagementRestBindingStub.getManHourInfo.wireToLocal");
        com.teamcenter.soa.internal.common.Monitor.markEnd  ("com.d6.services.strong.work.ManHourManagementRestBindingStub.getManHourInfo");
         return localOut;
       }
       finally
       {
        restSender.popRequestId();
        modelManager.unlockModel();
       }
    }
	
    @Override 
    public String clearManHours( String theUser, String theYear, String theMonth ) 
    
    throws com.teamcenter.schemas.soa._2006_03.exceptions.ServiceException
    {
       try
       {
        restSender.pushRequestId();
        com.teamcenter.soa.internal.common.Monitor.markStart("com.d6.services.strong.work.ManHourManagementRestBindingStub.clearManHours");
        com.teamcenter.soa.internal.common.Monitor.markStart("com.d6.services.strong.work.ManHourManagementRestBindingStub.clearManHours.localToWire");
		com.d6.schemas.work._2012_02.manhourmanagement.ClearManHoursInput wireIn = null;
        wireIn = ManHourManagement_201202Factory.createClearManHoursInput();
        wireIn.setTheUser( theUser ); //BASIC_ELEMENT_TO_WIRE
        wireIn.setTheYear( theYear ); //BASIC_ELEMENT_TO_WIRE
        wireIn.setTheMonth( theMonth ); //BASIC_ELEMENT_TO_WIRE

        com.teamcenter.soa.internal.common.Monitor.markEnd  ("com.d6.services.strong.work.ManHourManagementRestBindingStub.clearManHours.localToWire");

        Object outObj = restSender.invoke2( MANHOURMANAGEMENT_201202_PORT_NAME, "clearManHours", wireIn, 
                        MANHOURMANAGEMENT_201202_CONTEXT_PATH, MANHOURMANAGEMENT_201202_CONTEXT_PATH);
		modelManager.lockModel();
		
        if(outObj instanceof com.teamcenter.schemas.soa._2006_03.exceptions.ServiceException)
           throw (com.teamcenter.schemas.soa._2006_03.exceptions.ServiceException)outObj;
        com.teamcenter.soa.internal.common.Monitor.markStart("com.d6.services.strong.work.ManHourManagementRestBindingStub.clearManHours.wireToLocal");

        com.d6.schemas.work._2012_02.manhourmanagement.ClearManHoursOutput wireOut = 
       (com.d6.schemas.work._2012_02.manhourmanagement.ClearManHoursOutput)outObj;
        String localOut;
        
        localOut = wireOut.getOut(); // BASIC_RETURN

        if(!localConnection.getOption(Connection.OPT_CACHE_MODEL_OBJECTS).equals( "true" ))
      	{
        	localConnection.getClientDataModel().removeAllObjects(); 
        }
        com.teamcenter.soa.internal.common.Monitor.markEnd  ("com.d6.services.strong.work.ManHourManagementRestBindingStub.clearManHours.wireToLocal");
        com.teamcenter.soa.internal.common.Monitor.markEnd  ("com.d6.services.strong.work.ManHourManagementRestBindingStub.clearManHours");
         return localOut;
       }
       finally
       {
        restSender.popRequestId();
        modelManager.unlockModel();
       }
    }
	
    @Override 
    public com.d6.services.strong.work._2012_02.ManHourManagement.ManHourEntrySet load( com.d6.services.strong.work._2012_02.ManHourManagement.ManHourEntry filter ) 
    
    throws com.teamcenter.schemas.soa._2006_03.exceptions.ServiceException
    {
       try
       {
        restSender.pushRequestId();
        com.teamcenter.soa.internal.common.Monitor.markStart("com.d6.services.strong.work.ManHourManagementRestBindingStub.load");
        com.teamcenter.soa.internal.common.Monitor.markStart("com.d6.services.strong.work.ManHourManagementRestBindingStub.load.localToWire");
		com.d6.schemas.work._2012_02.manhourmanagement.LoadInput wireIn = null;
        wireIn = ManHourManagement_201202Factory.createLoadInput();
        wireIn.setFilter( toWire( filter )); //STRUCT_ELEMENT_TO_WIRE

        com.teamcenter.soa.internal.common.Monitor.markEnd  ("com.d6.services.strong.work.ManHourManagementRestBindingStub.load.localToWire");

        Object outObj = restSender.invoke2( MANHOURMANAGEMENT_201202_PORT_NAME, "load", wireIn, 
                        MANHOURMANAGEMENT_201202_CONTEXT_PATH, MANHOURMANAGEMENT_201202_CONTEXT_PATH);
		modelManager.lockModel();
		
        if(outObj instanceof com.teamcenter.schemas.soa._2006_03.exceptions.ServiceException)
           throw (com.teamcenter.schemas.soa._2006_03.exceptions.ServiceException)outObj;
        com.teamcenter.soa.internal.common.Monitor.markStart("com.d6.services.strong.work.ManHourManagementRestBindingStub.load.wireToLocal");

        com.d6.schemas.work._2012_02.manhourmanagement.ManHourEntrySet wireOut = 
       (com.d6.schemas.work._2012_02.manhourmanagement.ManHourEntrySet)outObj;
        com.d6.services.strong.work._2012_02.ManHourManagement.ManHourEntrySet localOut;
        
        localOut =  toLocal( wireOut,modelManager ); // STRUCT_RETURN

        if(!localConnection.getOption(Connection.OPT_CACHE_MODEL_OBJECTS).equals( "true" ))
      	{
        	localConnection.getClientDataModel().removeAllObjects(); 
        }
        com.teamcenter.soa.internal.common.Monitor.markEnd  ("com.d6.services.strong.work.ManHourManagementRestBindingStub.load.wireToLocal");
        com.teamcenter.soa.internal.common.Monitor.markEnd  ("com.d6.services.strong.work.ManHourManagementRestBindingStub.load");
         return localOut;
       }
       finally
       {
        restSender.popRequestId();
        modelManager.unlockModel();
       }
    }
	
    @Override 
    public com.d6.services.strong.work._2012_02.ManHourManagement.ManHourEntrySet save( String theUserName, String theYear, String theMonth, com.d6.services.strong.work._2012_02.ManHourManagement.ManHourEntry[] manHours, com.d6.services.strong.work._2012_02.ManHourManagement.ManHourProgram[] thePrograms ) 
    
    throws com.teamcenter.schemas.soa._2006_03.exceptions.ServiceException
    {
       try
       {
        restSender.pushRequestId();
        com.teamcenter.soa.internal.common.Monitor.markStart("com.d6.services.strong.work.ManHourManagementRestBindingStub.save");
        com.teamcenter.soa.internal.common.Monitor.markStart("com.d6.services.strong.work.ManHourManagementRestBindingStub.save.localToWire");
		com.d6.schemas.work._2012_02.manhourmanagement.SaveInput wireIn = null;
        wireIn = ManHourManagement_201202Factory.createSaveInput();
        wireIn.setTheUserName( theUserName ); //BASIC_ELEMENT_TO_WIRE
        wireIn.setTheYear( theYear ); //BASIC_ELEMENT_TO_WIRE
        wireIn.setTheMonth( theMonth ); //BASIC_ELEMENT_TO_WIRE
         List wireInManHours = wireIn.getManHours();// VECTOR_TO_WIRE 
         for (int i = 0; i < manHours.length; i ++ )
         {
                     wireInManHours.add(toWire(manHours[i])); // STRUCT_IN_VECTOR_TO_WIRE 

         }
         List wireInThePrograms = wireIn.getThePrograms();// VECTOR_TO_WIRE 
         for (int i = 0; i < thePrograms.length; i ++ )
         {
                     wireInThePrograms.add(toWire(thePrograms[i])); // STRUCT_IN_VECTOR_TO_WIRE 

         }

        com.teamcenter.soa.internal.common.Monitor.markEnd  ("com.d6.services.strong.work.ManHourManagementRestBindingStub.save.localToWire");

        Object outObj = restSender.invoke2( MANHOURMANAGEMENT_201202_PORT_NAME, "save", wireIn, 
                        MANHOURMANAGEMENT_201202_CONTEXT_PATH, MANHOURMANAGEMENT_201202_CONTEXT_PATH);
		modelManager.lockModel();
		
        if(outObj instanceof com.teamcenter.schemas.soa._2006_03.exceptions.ServiceException)
           throw (com.teamcenter.schemas.soa._2006_03.exceptions.ServiceException)outObj;
        com.teamcenter.soa.internal.common.Monitor.markStart("com.d6.services.strong.work.ManHourManagementRestBindingStub.save.wireToLocal");

        com.d6.schemas.work._2012_02.manhourmanagement.ManHourEntrySet wireOut = 
       (com.d6.schemas.work._2012_02.manhourmanagement.ManHourEntrySet)outObj;
        com.d6.services.strong.work._2012_02.ManHourManagement.ManHourEntrySet localOut;
        
        localOut =  toLocal( wireOut,modelManager ); // STRUCT_RETURN

        if(!localConnection.getOption(Connection.OPT_CACHE_MODEL_OBJECTS).equals( "true" ))
      	{
        	localConnection.getClientDataModel().removeAllObjects(); 
        }
        com.teamcenter.soa.internal.common.Monitor.markEnd  ("com.d6.services.strong.work.ManHourManagementRestBindingStub.save.wireToLocal");
        com.teamcenter.soa.internal.common.Monitor.markEnd  ("com.d6.services.strong.work.ManHourManagementRestBindingStub.save");
         return localOut;
       }
       finally
       {
        restSender.popRequestId();
        modelManager.unlockModel();
       }
    }
	
    @Override 
    public com.d6.services.strong.work._2012_02.ManHourManagement.ManHourEntrySet revise( String username, String year, String month, com.d6.services.strong.work._2012_02.ManHourManagement.ManHourEntry[] manHours ) 
    
    throws com.teamcenter.schemas.soa._2006_03.exceptions.ServiceException
    {
       try
       {
        restSender.pushRequestId();
        com.teamcenter.soa.internal.common.Monitor.markStart("com.d6.services.strong.work.ManHourManagementRestBindingStub.revise");
        com.teamcenter.soa.internal.common.Monitor.markStart("com.d6.services.strong.work.ManHourManagementRestBindingStub.revise.localToWire");
		com.d6.schemas.work._2012_02.manhourmanagement.ReviseInput wireIn = null;
        wireIn = ManHourManagement_201202Factory.createReviseInput();
        wireIn.setUsername( username ); //BASIC_ELEMENT_TO_WIRE
        wireIn.setYear( year ); //BASIC_ELEMENT_TO_WIRE
        wireIn.setMonth( month ); //BASIC_ELEMENT_TO_WIRE
         List wireInManHours = wireIn.getManHours();// VECTOR_TO_WIRE 
         for (int i = 0; i < manHours.length; i ++ )
         {
                     wireInManHours.add(toWire(manHours[i])); // STRUCT_IN_VECTOR_TO_WIRE 

         }

        com.teamcenter.soa.internal.common.Monitor.markEnd  ("com.d6.services.strong.work.ManHourManagementRestBindingStub.revise.localToWire");

        Object outObj = restSender.invoke2( MANHOURMANAGEMENT_201202_PORT_NAME, "revise", wireIn, 
                        MANHOURMANAGEMENT_201202_CONTEXT_PATH, MANHOURMANAGEMENT_201202_CONTEXT_PATH);
		modelManager.lockModel();
		
        if(outObj instanceof com.teamcenter.schemas.soa._2006_03.exceptions.ServiceException)
           throw (com.teamcenter.schemas.soa._2006_03.exceptions.ServiceException)outObj;
        com.teamcenter.soa.internal.common.Monitor.markStart("com.d6.services.strong.work.ManHourManagementRestBindingStub.revise.wireToLocal");

        com.d6.schemas.work._2012_02.manhourmanagement.ManHourEntrySet wireOut = 
       (com.d6.schemas.work._2012_02.manhourmanagement.ManHourEntrySet)outObj;
        com.d6.services.strong.work._2012_02.ManHourManagement.ManHourEntrySet localOut;
        
        localOut =  toLocal( wireOut,modelManager ); // STRUCT_RETURN

        if(!localConnection.getOption(Connection.OPT_CACHE_MODEL_OBJECTS).equals( "true" ))
      	{
        	localConnection.getClientDataModel().removeAllObjects(); 
        }
        com.teamcenter.soa.internal.common.Monitor.markEnd  ("com.d6.services.strong.work.ManHourManagementRestBindingStub.revise.wireToLocal");
        com.teamcenter.soa.internal.common.Monitor.markEnd  ("com.d6.services.strong.work.ManHourManagementRestBindingStub.revise");
         return localOut;
       }
       finally
       {
        restSender.popRequestId();
        modelManager.unlockModel();
       }
    }
	
    @Override 
    public String mheTest( String userName ) 
    
    throws com.teamcenter.schemas.soa._2006_03.exceptions.ServiceException
    {
       try
       {
        restSender.pushRequestId();
        com.teamcenter.soa.internal.common.Monitor.markStart("com.d6.services.strong.work.ManHourManagementRestBindingStub.mheTest");
        com.teamcenter.soa.internal.common.Monitor.markStart("com.d6.services.strong.work.ManHourManagementRestBindingStub.mheTest.localToWire");
		com.d6.schemas.work._2012_02.manhourmanagement.MheTestInput wireIn = null;
        wireIn = ManHourManagement_201202Factory.createMheTestInput();
        wireIn.setUserName( userName ); //BASIC_ELEMENT_TO_WIRE

        com.teamcenter.soa.internal.common.Monitor.markEnd  ("com.d6.services.strong.work.ManHourManagementRestBindingStub.mheTest.localToWire");

        Object outObj = restSender.invoke2( MANHOURMANAGEMENT_201202_PORT_NAME, "mheTest", wireIn, 
                        MANHOURMANAGEMENT_201202_CONTEXT_PATH, MANHOURMANAGEMENT_201202_CONTEXT_PATH);
		modelManager.lockModel();
		
        if(outObj instanceof com.teamcenter.schemas.soa._2006_03.exceptions.ServiceException)
           throw (com.teamcenter.schemas.soa._2006_03.exceptions.ServiceException)outObj;
        com.teamcenter.soa.internal.common.Monitor.markStart("com.d6.services.strong.work.ManHourManagementRestBindingStub.mheTest.wireToLocal");

        com.d6.schemas.work._2012_02.manhourmanagement.MheTestOutput wireOut = 
       (com.d6.schemas.work._2012_02.manhourmanagement.MheTestOutput)outObj;
        String localOut;
        
        localOut = wireOut.getOut(); // BASIC_RETURN

        if(!localConnection.getOption(Connection.OPT_CACHE_MODEL_OBJECTS).equals( "true" ))
      	{
        	localConnection.getClientDataModel().removeAllObjects(); 
        }
        com.teamcenter.soa.internal.common.Monitor.markEnd  ("com.d6.services.strong.work.ManHourManagementRestBindingStub.mheTest.wireToLocal");
        com.teamcenter.soa.internal.common.Monitor.markEnd  ("com.d6.services.strong.work.ManHourManagementRestBindingStub.mheTest");
         return localOut;
       }
       finally
       {
        restSender.popRequestId();
        modelManager.unlockModel();
       }
    }


}
