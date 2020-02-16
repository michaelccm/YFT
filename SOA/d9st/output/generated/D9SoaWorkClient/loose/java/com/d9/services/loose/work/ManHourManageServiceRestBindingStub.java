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
 
package com.d9.services.loose.work;

import java.util.List;


import com.teamcenter.soa.client.Connection;
import com.teamcenter.soa.internal.client.Sender;
import com.teamcenter.soa.internal.client.model.PopulateModel;

 /**
  * @unpublished
  */
@SuppressWarnings("unchecked")
public class ManHourManageServiceRestBindingStub extends ManHourManageServiceService
{
    private Sender              restSender;
    private PopulateModel       modelManager;
    private Connection          localConnection;
    
    /**
     * Constructor
     * @param connection
     * @unpublished
     */
    public ManHourManageServiceRestBindingStub( Connection connection )
    {
        this.localConnection = connection;
        this.restSender  = connection.getSender();
        this.modelManager= (PopulateModel)this.localConnection.getModelManager();
        
    }

    static com.teamcenter.schemas.soa._2006_03.base.ObjectFactory base_Factory = new 
           com.teamcenter.schemas.soa._2006_03.base.ObjectFactory();

    // each child interface has its own factory and methods for calling


    static com.d9.schemas.work._2018_06.manhourmanageservice.ObjectFactory ManHourManageService_201806Factory = new 
           com.d9.schemas.work._2018_06.manhourmanageservice.ObjectFactory();
    static final String MANHOURMANAGESERVICE_201806_PORT_NAME          = "Work-2018-06-ManHourManageService";
    static final String MANHOURMANAGESERVICE_201806_CONTEXT_PATH = "com.d9.schemas.work._2018_06.manhourmanageservice:com.teamcenter.schemas.soa._2006_03.base";

    /**
     * @param local
     * @return wire
     * @unpublished 
     */
    public  static com.d9.schemas.work._2018_06.manhourmanageservice.ManHourBillType toWire( com.d9.services.loose.work._2018_06.ManHourManageService.ManHourBillType local ) 
    {
 		com.d9.schemas.work._2018_06.manhourmanageservice.ManHourBillType wire = null;
     	wire = ManHourManageService_201806Factory.createManHourBillType();

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
    public  static com.d9.services.loose.work._2018_06.ManHourManageService.ManHourBillType toLocal( com.d9.schemas.work._2018_06.manhourmanageservice.ManHourBillType  wire , com.teamcenter.soa.internal.client.model.PopulateModel modelManager ) 
 		
    {
  		com.d9.services.loose.work._2018_06.ManHourManageService.ManHourBillType local = new com.d9.services.loose.work._2018_06.ManHourManageService.ManHourBillType();

        local.myRefBR = wire.getMyRefBR(); //BASIC_ELEMENT_TO_LOCAL
        local.billTypeInternal = wire.getBillTypeInternal(); //BASIC_ELEMENT_TO_LOCAL
        local.billRateName = wire.getBillRateName(); //BASIC_ELEMENT_TO_LOCAL


		return local;
	}

    /**
     * @param local
     * @return wire
     * @unpublished 
     */
    public  static com.d9.schemas.work._2018_06.manhourmanageservice.ManHourEntry toWire( com.d9.services.loose.work._2018_06.ManHourManageService.ManHourEntry local ) 
    {
 		com.d9.schemas.work._2018_06.manhourmanageservice.ManHourEntry wire = null;
     	wire = ManHourManageService_201806Factory.createManHourEntry();

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
    public  static com.d9.services.loose.work._2018_06.ManHourManageService.ManHourEntry toLocal( com.d9.schemas.work._2018_06.manhourmanageservice.ManHourEntry  wire , com.teamcenter.soa.internal.client.model.PopulateModel modelManager ) 
 		
    {
  		com.d9.services.loose.work._2018_06.ManHourManageService.ManHourEntry local = new com.d9.services.loose.work._2018_06.ManHourManageService.ManHourEntry();

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
    public  static com.d9.schemas.work._2018_06.manhourmanageservice.ManHourEntrySet toWire( com.d9.services.loose.work._2018_06.ManHourManageService.ManHourEntrySet local ) 
    {
 		com.d9.schemas.work._2018_06.manhourmanageservice.ManHourEntrySet wire = null;
     	wire = ManHourManageService_201806Factory.createManHourEntrySet();

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
    public  static com.d9.services.loose.work._2018_06.ManHourManageService.ManHourEntrySet toLocal( com.d9.schemas.work._2018_06.manhourmanageservice.ManHourEntrySet  wire , com.teamcenter.soa.internal.client.model.PopulateModel modelManager ) 
 		
    {
  		com.d9.services.loose.work._2018_06.ManHourManageService.ManHourEntrySet local = new com.d9.services.loose.work._2018_06.ManHourManageService.ManHourEntrySet();

      List wireMheSet = wire.getMheSet();// VECTOR_TO_LOCAL
      local.mheSet = new com.d9.services.loose.work._2018_06.ManHourManageService.ManHourEntry[wireMheSet.size()];
      for (int i = 0; i < wireMheSet.size(); i ++ )
      {
          local.mheSet[i] =  toLocal((com.d9.schemas.work._2018_06.manhourmanageservice.ManHourEntry)wireMheSet.get(i),modelManager); // STRUCT_IN_VECTOR_TO_LOCAL

      }


		return local;
	}

    /**
     * @param local
     * @return wire
     * @unpublished 
     */
    public  static com.d9.schemas.work._2018_06.manhourmanageservice.ManHourInfo toWire( com.d9.services.loose.work._2018_06.ManHourManageService.ManHourInfo local ) 
    {
 		com.d9.schemas.work._2018_06.manhourmanageservice.ManHourInfo wire = null;
     	wire = ManHourManageService_201806Factory.createManHourInfo();

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
    public  static com.d9.services.loose.work._2018_06.ManHourManageService.ManHourInfo toLocal( com.d9.schemas.work._2018_06.manhourmanageservice.ManHourInfo  wire , com.teamcenter.soa.internal.client.model.PopulateModel modelManager ) 
 		
    {
  		com.d9.services.loose.work._2018_06.ManHourManageService.ManHourInfo local = new com.d9.services.loose.work._2018_06.ManHourManageService.ManHourInfo();

        local.myUserName = wire.getMyUserName(); //BASIC_ELEMENT_TO_LOCAL
        local.myPosition = wire.getMyPosition(); //BASIC_ELEMENT_TO_LOCAL
        local.myYear = wire.getMyYear(); //BASIC_ELEMENT_TO_LOCAL
        local.myMonth = wire.getMyMonth(); //BASIC_ELEMENT_TO_LOCAL
        local.totalDays = wire.getTotalDays().intValue(); //BIG_INGEGER_ELEMENT_TO_LOCAL
      List wireTheMonthTmp = wire.getTheMonthTmp();// VECTOR_TO_LOCAL
      local.theMonthTmp = new com.d9.services.loose.work._2018_06.ManHourManageService.ManHourMonthTmp[wireTheMonthTmp.size()];
      for (int i = 0; i < wireTheMonthTmp.size(); i ++ )
      {
          local.theMonthTmp[i] =  toLocal((com.d9.schemas.work._2018_06.manhourmanageservice.ManHourMonthTmp)wireTheMonthTmp.get(i),modelManager); // STRUCT_IN_VECTOR_TO_LOCAL

      }
      List wireTheProgramSet = wire.getTheProgramSet();// VECTOR_TO_LOCAL
      local.theProgramSet = new com.d9.services.loose.work._2018_06.ManHourManageService.ManHourProgram[wireTheProgramSet.size()];
      for (int i = 0; i < wireTheProgramSet.size(); i ++ )
      {
          local.theProgramSet[i] =  toLocal((com.d9.schemas.work._2018_06.manhourmanageservice.ManHourProgram)wireTheProgramSet.get(i),modelManager); // STRUCT_IN_VECTOR_TO_LOCAL

      }
        local.isHourlyBasedUser = wire.isIsHourlyBasedUser(); //BOOLEAN_ELEMENT_TO_LOCAL
        local.isManHourEditable = wire.isIsManHourEditable(); //BOOLEAN_ELEMENT_TO_LOCAL
      List wireTheBillTypeSet = wire.getTheBillTypeSet();// VECTOR_TO_LOCAL
      local.theBillTypeSet = new com.d9.services.loose.work._2018_06.ManHourManageService.ManHourBillType[wireTheBillTypeSet.size()];
      for (int i = 0; i < wireTheBillTypeSet.size(); i ++ )
      {
          local.theBillTypeSet[i] =  toLocal((com.d9.schemas.work._2018_06.manhourmanageservice.ManHourBillType)wireTheBillTypeSet.get(i),modelManager); // STRUCT_IN_VECTOR_TO_LOCAL

      }


		return local;
	}

    /**
     * @param local
     * @return wire
     * @unpublished 
     */
    public  static com.d9.schemas.work._2018_06.manhourmanageservice.ManHourMonthTmp toWire( com.d9.services.loose.work._2018_06.ManHourManageService.ManHourMonthTmp local ) 
    {
 		com.d9.schemas.work._2018_06.manhourmanageservice.ManHourMonthTmp wire = null;
     	wire = ManHourManageService_201806Factory.createManHourMonthTmp();

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
    public  static com.d9.services.loose.work._2018_06.ManHourManageService.ManHourMonthTmp toLocal( com.d9.schemas.work._2018_06.manhourmanageservice.ManHourMonthTmp  wire , com.teamcenter.soa.internal.client.model.PopulateModel modelManager ) 
 		
    {
  		com.d9.services.loose.work._2018_06.ManHourManageService.ManHourMonthTmp local = new com.d9.services.loose.work._2018_06.ManHourManageService.ManHourMonthTmp();

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
    public  static com.d9.schemas.work._2018_06.manhourmanageservice.ManHourProgram toWire( com.d9.services.loose.work._2018_06.ManHourManageService.ManHourProgram local ) 
    {
 		com.d9.schemas.work._2018_06.manhourmanageservice.ManHourProgram wire = null;
     	wire = ManHourManageService_201806Factory.createManHourProgram();

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
    public  static com.d9.services.loose.work._2018_06.ManHourManageService.ManHourProgram toLocal( com.d9.schemas.work._2018_06.manhourmanageservice.ManHourProgram  wire , com.teamcenter.soa.internal.client.model.PopulateModel modelManager ) 
 		
    {
  		com.d9.services.loose.work._2018_06.ManHourManageService.ManHourProgram local = new com.d9.services.loose.work._2018_06.ManHourManageService.ManHourProgram();

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
	
    @Override 
    public String clearManHoursOP( String theUser, String theAction, String thePara ) 
    
    {
       try
       {
        restSender.pushRequestId();
        com.teamcenter.soa.internal.common.Monitor.markStart("com.d9.services.loose.work.ManHourManageServiceRestBindingStub.clearManHoursOP");
        com.teamcenter.soa.internal.common.Monitor.markStart("com.d9.services.loose.work.ManHourManageServiceRestBindingStub.clearManHoursOP.localToWire");
		com.d9.schemas.work._2018_06.manhourmanageservice.ClearManHoursOPInput wireIn = null;
        wireIn = ManHourManageService_201806Factory.createClearManHoursOPInput();
        wireIn.setTheUser( theUser ); //BASIC_ELEMENT_TO_WIRE
        wireIn.setTheAction( theAction ); //BASIC_ELEMENT_TO_WIRE
        wireIn.setThePara( thePara ); //BASIC_ELEMENT_TO_WIRE

        com.teamcenter.soa.internal.common.Monitor.markEnd  ("com.d9.services.loose.work.ManHourManageServiceRestBindingStub.clearManHoursOP.localToWire");

        Object outObj = restSender.invoke2( MANHOURMANAGESERVICE_201806_PORT_NAME, "clearManHoursOP", wireIn, 
                        MANHOURMANAGESERVICE_201806_CONTEXT_PATH, MANHOURMANAGESERVICE_201806_CONTEXT_PATH);
		modelManager.lockModel();
		

        com.teamcenter.soa.internal.common.Monitor.markStart("com.d9.services.loose.work.ManHourManageServiceRestBindingStub.clearManHoursOP.wireToLocal");

        com.d9.schemas.work._2018_06.manhourmanageservice.ClearManHoursOPOutput wireOut = 
       (com.d9.schemas.work._2018_06.manhourmanageservice.ClearManHoursOPOutput)outObj;
        String localOut;
        
        localOut = wireOut.getOut(); // BASIC_RETURN

        if(!localConnection.getOption(Connection.OPT_CACHE_MODEL_OBJECTS).equals( "true" ))
      	{
        	localConnection.getClientDataModel().removeAllObjects(); 
        }
        com.teamcenter.soa.internal.common.Monitor.markEnd  ("com.d9.services.loose.work.ManHourManageServiceRestBindingStub.clearManHoursOP.wireToLocal");
        com.teamcenter.soa.internal.common.Monitor.markEnd  ("com.d9.services.loose.work.ManHourManageServiceRestBindingStub.clearManHoursOP");
         return localOut;
       }
       finally
       {
        restSender.popRequestId();
        modelManager.unlockModel();
       }
    }
	
    @Override 
    public com.d9.services.loose.work._2018_06.ManHourManageService.ManHourInfo getManHourInfoOP( String theUserName, String theYear, String theMonth ) 
    
    {
       try
       {
        restSender.pushRequestId();
        com.teamcenter.soa.internal.common.Monitor.markStart("com.d9.services.loose.work.ManHourManageServiceRestBindingStub.getManHourInfoOP");
        com.teamcenter.soa.internal.common.Monitor.markStart("com.d9.services.loose.work.ManHourManageServiceRestBindingStub.getManHourInfoOP.localToWire");
		com.d9.schemas.work._2018_06.manhourmanageservice.GetManHourInfoOPInput wireIn = null;
        wireIn = ManHourManageService_201806Factory.createGetManHourInfoOPInput();
        wireIn.setTheUserName( theUserName ); //BASIC_ELEMENT_TO_WIRE
        wireIn.setTheYear( theYear ); //BASIC_ELEMENT_TO_WIRE
        wireIn.setTheMonth( theMonth ); //BASIC_ELEMENT_TO_WIRE

        com.teamcenter.soa.internal.common.Monitor.markEnd  ("com.d9.services.loose.work.ManHourManageServiceRestBindingStub.getManHourInfoOP.localToWire");

        Object outObj = restSender.invoke2( MANHOURMANAGESERVICE_201806_PORT_NAME, "getManHourInfoOP", wireIn, 
                        MANHOURMANAGESERVICE_201806_CONTEXT_PATH, MANHOURMANAGESERVICE_201806_CONTEXT_PATH);
		modelManager.lockModel();
		

        com.teamcenter.soa.internal.common.Monitor.markStart("com.d9.services.loose.work.ManHourManageServiceRestBindingStub.getManHourInfoOP.wireToLocal");

        com.d9.schemas.work._2018_06.manhourmanageservice.ManHourInfo wireOut = 
       (com.d9.schemas.work._2018_06.manhourmanageservice.ManHourInfo)outObj;
        com.d9.services.loose.work._2018_06.ManHourManageService.ManHourInfo localOut;
        
        localOut =  toLocal( wireOut,modelManager ); // STRUCT_RETURN

        if(!localConnection.getOption(Connection.OPT_CACHE_MODEL_OBJECTS).equals( "true" ))
      	{
        	localConnection.getClientDataModel().removeAllObjects(); 
        }
        com.teamcenter.soa.internal.common.Monitor.markEnd  ("com.d9.services.loose.work.ManHourManageServiceRestBindingStub.getManHourInfoOP.wireToLocal");
        com.teamcenter.soa.internal.common.Monitor.markEnd  ("com.d9.services.loose.work.ManHourManageServiceRestBindingStub.getManHourInfoOP");
         return localOut;
       }
       finally
       {
        restSender.popRequestId();
        modelManager.unlockModel();
       }
    }
	
    @Override 
    public com.d9.services.loose.work._2018_06.ManHourManageService.ManHourEntrySet loadOP( com.d9.services.loose.work._2018_06.ManHourManageService.ManHourEntry manHourFilter ) 
    
    {
       try
       {
        restSender.pushRequestId();
        com.teamcenter.soa.internal.common.Monitor.markStart("com.d9.services.loose.work.ManHourManageServiceRestBindingStub.loadOP");
        com.teamcenter.soa.internal.common.Monitor.markStart("com.d9.services.loose.work.ManHourManageServiceRestBindingStub.loadOP.localToWire");
		com.d9.schemas.work._2018_06.manhourmanageservice.LoadOPInput wireIn = null;
        wireIn = ManHourManageService_201806Factory.createLoadOPInput();
        wireIn.setManHourFilter( toWire( manHourFilter )); //STRUCT_ELEMENT_TO_WIRE

        com.teamcenter.soa.internal.common.Monitor.markEnd  ("com.d9.services.loose.work.ManHourManageServiceRestBindingStub.loadOP.localToWire");

        Object outObj = restSender.invoke2( MANHOURMANAGESERVICE_201806_PORT_NAME, "loadOP", wireIn, 
                        MANHOURMANAGESERVICE_201806_CONTEXT_PATH, MANHOURMANAGESERVICE_201806_CONTEXT_PATH);
		modelManager.lockModel();
		

        com.teamcenter.soa.internal.common.Monitor.markStart("com.d9.services.loose.work.ManHourManageServiceRestBindingStub.loadOP.wireToLocal");

        com.d9.schemas.work._2018_06.manhourmanageservice.ManHourEntrySet wireOut = 
       (com.d9.schemas.work._2018_06.manhourmanageservice.ManHourEntrySet)outObj;
        com.d9.services.loose.work._2018_06.ManHourManageService.ManHourEntrySet localOut;
        
        localOut =  toLocal( wireOut,modelManager ); // STRUCT_RETURN

        if(!localConnection.getOption(Connection.OPT_CACHE_MODEL_OBJECTS).equals( "true" ))
      	{
        	localConnection.getClientDataModel().removeAllObjects(); 
        }
        com.teamcenter.soa.internal.common.Monitor.markEnd  ("com.d9.services.loose.work.ManHourManageServiceRestBindingStub.loadOP.wireToLocal");
        com.teamcenter.soa.internal.common.Monitor.markEnd  ("com.d9.services.loose.work.ManHourManageServiceRestBindingStub.loadOP");
         return localOut;
       }
       finally
       {
        restSender.popRequestId();
        modelManager.unlockModel();
       }
    }
	
    @Override 
    public String mheTest( String theUser ) 
    
    {
       try
       {
        restSender.pushRequestId();
        com.teamcenter.soa.internal.common.Monitor.markStart("com.d9.services.loose.work.ManHourManageServiceRestBindingStub.mheTest");
        com.teamcenter.soa.internal.common.Monitor.markStart("com.d9.services.loose.work.ManHourManageServiceRestBindingStub.mheTest.localToWire");
		com.d9.schemas.work._2018_06.manhourmanageservice.MheTestInput wireIn = null;
        wireIn = ManHourManageService_201806Factory.createMheTestInput();
        wireIn.setTheUser( theUser ); //BASIC_ELEMENT_TO_WIRE

        com.teamcenter.soa.internal.common.Monitor.markEnd  ("com.d9.services.loose.work.ManHourManageServiceRestBindingStub.mheTest.localToWire");

        Object outObj = restSender.invoke2( MANHOURMANAGESERVICE_201806_PORT_NAME, "mheTest", wireIn, 
                        MANHOURMANAGESERVICE_201806_CONTEXT_PATH, MANHOURMANAGESERVICE_201806_CONTEXT_PATH);
		modelManager.lockModel();
		

        com.teamcenter.soa.internal.common.Monitor.markStart("com.d9.services.loose.work.ManHourManageServiceRestBindingStub.mheTest.wireToLocal");

        com.d9.schemas.work._2018_06.manhourmanageservice.MheTestOutput wireOut = 
       (com.d9.schemas.work._2018_06.manhourmanageservice.MheTestOutput)outObj;
        String localOut;
        
        localOut = wireOut.getOut(); // BASIC_RETURN

        if(!localConnection.getOption(Connection.OPT_CACHE_MODEL_OBJECTS).equals( "true" ))
      	{
        	localConnection.getClientDataModel().removeAllObjects(); 
        }
        com.teamcenter.soa.internal.common.Monitor.markEnd  ("com.d9.services.loose.work.ManHourManageServiceRestBindingStub.mheTest.wireToLocal");
        com.teamcenter.soa.internal.common.Monitor.markEnd  ("com.d9.services.loose.work.ManHourManageServiceRestBindingStub.mheTest");
         return localOut;
       }
       finally
       {
        restSender.popRequestId();
        modelManager.unlockModel();
       }
    }
	
    @Override 
    public com.d9.services.loose.work._2018_06.ManHourManageService.ManHourEntrySet reviseOP( String username, String year, String month, com.d9.services.loose.work._2018_06.ManHourManageService.ManHourEntry[] manHours ) 
    
    {
       try
       {
        restSender.pushRequestId();
        com.teamcenter.soa.internal.common.Monitor.markStart("com.d9.services.loose.work.ManHourManageServiceRestBindingStub.reviseOP");
        com.teamcenter.soa.internal.common.Monitor.markStart("com.d9.services.loose.work.ManHourManageServiceRestBindingStub.reviseOP.localToWire");
		com.d9.schemas.work._2018_06.manhourmanageservice.ReviseOPInput wireIn = null;
        wireIn = ManHourManageService_201806Factory.createReviseOPInput();
        wireIn.setUsername( username ); //BASIC_ELEMENT_TO_WIRE
        wireIn.setYear( year ); //BASIC_ELEMENT_TO_WIRE
        wireIn.setMonth( month ); //BASIC_ELEMENT_TO_WIRE
         List wireInManHours = wireIn.getManHours();// VECTOR_TO_WIRE 
         for (int i = 0; i < manHours.length; i ++ )
         {
                     wireInManHours.add(toWire(manHours[i])); // STRUCT_IN_VECTOR_TO_WIRE 

         }

        com.teamcenter.soa.internal.common.Monitor.markEnd  ("com.d9.services.loose.work.ManHourManageServiceRestBindingStub.reviseOP.localToWire");

        Object outObj = restSender.invoke2( MANHOURMANAGESERVICE_201806_PORT_NAME, "reviseOP", wireIn, 
                        MANHOURMANAGESERVICE_201806_CONTEXT_PATH, MANHOURMANAGESERVICE_201806_CONTEXT_PATH);
		modelManager.lockModel();
		

        com.teamcenter.soa.internal.common.Monitor.markStart("com.d9.services.loose.work.ManHourManageServiceRestBindingStub.reviseOP.wireToLocal");

        com.d9.schemas.work._2018_06.manhourmanageservice.ManHourEntrySet wireOut = 
       (com.d9.schemas.work._2018_06.manhourmanageservice.ManHourEntrySet)outObj;
        com.d9.services.loose.work._2018_06.ManHourManageService.ManHourEntrySet localOut;
        
        localOut =  toLocal( wireOut,modelManager ); // STRUCT_RETURN

        if(!localConnection.getOption(Connection.OPT_CACHE_MODEL_OBJECTS).equals( "true" ))
      	{
        	localConnection.getClientDataModel().removeAllObjects(); 
        }
        com.teamcenter.soa.internal.common.Monitor.markEnd  ("com.d9.services.loose.work.ManHourManageServiceRestBindingStub.reviseOP.wireToLocal");
        com.teamcenter.soa.internal.common.Monitor.markEnd  ("com.d9.services.loose.work.ManHourManageServiceRestBindingStub.reviseOP");
         return localOut;
       }
       finally
       {
        restSender.popRequestId();
        modelManager.unlockModel();
       }
    }
	
    @Override 
    public com.d9.services.loose.work._2018_06.ManHourManageService.ManHourEntrySet saveOP( String username, String year, String month, com.d9.services.loose.work._2018_06.ManHourManageService.ManHourEntry[] manHours, com.d9.services.loose.work._2018_06.ManHourManageService.ManHourProgram[] programs ) 
    
    {
       try
       {
        restSender.pushRequestId();
        com.teamcenter.soa.internal.common.Monitor.markStart("com.d9.services.loose.work.ManHourManageServiceRestBindingStub.saveOP");
        com.teamcenter.soa.internal.common.Monitor.markStart("com.d9.services.loose.work.ManHourManageServiceRestBindingStub.saveOP.localToWire");
		com.d9.schemas.work._2018_06.manhourmanageservice.SaveOPInput wireIn = null;
        wireIn = ManHourManageService_201806Factory.createSaveOPInput();
        wireIn.setUsername( username ); //BASIC_ELEMENT_TO_WIRE
        wireIn.setYear( year ); //BASIC_ELEMENT_TO_WIRE
        wireIn.setMonth( month ); //BASIC_ELEMENT_TO_WIRE
         List wireInManHours = wireIn.getManHours();// VECTOR_TO_WIRE 
         for (int i = 0; i < manHours.length; i ++ )
         {
                     wireInManHours.add(toWire(manHours[i])); // STRUCT_IN_VECTOR_TO_WIRE 

         }
         List wireInPrograms = wireIn.getPrograms();// VECTOR_TO_WIRE 
         for (int i = 0; i < programs.length; i ++ )
         {
                     wireInPrograms.add(toWire(programs[i])); // STRUCT_IN_VECTOR_TO_WIRE 

         }

        com.teamcenter.soa.internal.common.Monitor.markEnd  ("com.d9.services.loose.work.ManHourManageServiceRestBindingStub.saveOP.localToWire");

        Object outObj = restSender.invoke2( MANHOURMANAGESERVICE_201806_PORT_NAME, "saveOP", wireIn, 
                        MANHOURMANAGESERVICE_201806_CONTEXT_PATH, MANHOURMANAGESERVICE_201806_CONTEXT_PATH);
		modelManager.lockModel();
		

        com.teamcenter.soa.internal.common.Monitor.markStart("com.d9.services.loose.work.ManHourManageServiceRestBindingStub.saveOP.wireToLocal");

        com.d9.schemas.work._2018_06.manhourmanageservice.ManHourEntrySet wireOut = 
       (com.d9.schemas.work._2018_06.manhourmanageservice.ManHourEntrySet)outObj;
        com.d9.services.loose.work._2018_06.ManHourManageService.ManHourEntrySet localOut;
        
        localOut =  toLocal( wireOut,modelManager ); // STRUCT_RETURN

        if(!localConnection.getOption(Connection.OPT_CACHE_MODEL_OBJECTS).equals( "true" ))
      	{
        	localConnection.getClientDataModel().removeAllObjects(); 
        }
        com.teamcenter.soa.internal.common.Monitor.markEnd  ("com.d9.services.loose.work.ManHourManageServiceRestBindingStub.saveOP.wireToLocal");
        com.teamcenter.soa.internal.common.Monitor.markEnd  ("com.d9.services.loose.work.ManHourManageServiceRestBindingStub.saveOP");
         return localOut;
       }
       finally
       {
        restSender.popRequestId();
        modelManager.unlockModel();
       }
    }


}
