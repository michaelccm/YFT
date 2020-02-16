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

package com.d9.services.strong.work;


import com.teamcenter.soa.client.Connection;
import com.teamcenter.soa.client.model.ModelManager;
import com.teamcenter.utest.SoaSession;

import junit.framework.TestCase;

public class ManHourManageServiceTest extends TestCase
{
    private Connection        connection;
    private ModelManager      manager;
    private ManHourManageServiceService   service;
    

    public ManHourManageServiceTest( String name )
    {
        super( name );
    }

    protected void setUp( ) throws Exception
    {
        super.setUp( );

        connection  = SoaSession.getConnection();
        manager     = connection.getModelManager();       
        service     = ManHourManageServiceService.getService(connection);

    }
        
    
    public void testClearManHoursOP()
    {
        // TODO write test code, then remove fail()
        // service.clearManHoursOP(  )
        fail("This test has not been implemented yet");
    }

    public void testGetManHourInfoOP()
    {
        // TODO write test code, then remove fail()
        // service.getManHourInfoOP(  )
        fail("This test has not been implemented yet");
    }

    public void testLoadOP()
    {
        // TODO write test code, then remove fail()
        // service.loadOP(  )
        fail("This test has not been implemented yet");
    }

    public void testMheTest()
    {
        // TODO write test code, then remove fail()
        // service.mheTest(  )
        fail("This test has not been implemented yet");
    }

    public void testReviseOP()
    {
        // TODO write test code, then remove fail()
        // service.reviseOP(  )
        fail("This test has not been implemented yet");
    }

    public void testSaveOP()
    {
        // TODO write test code, then remove fail()
        // service.saveOP(  )
        fail("This test has not been implemented yet");
    }


}

