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


import com.teamcenter.soa.client.Connection;
import com.teamcenter.soa.client.model.ModelManager;
import com.teamcenter.utest.SoaSession;

import junit.framework.TestCase;

public class ManHourManagementTest extends TestCase
{
    private Connection        connection;
    private ModelManager      manager;
    private ManHourManagementService   service;
    

    public ManHourManagementTest( String name )
    {
        super( name );
    }

    protected void setUp( ) throws Exception
    {
        super.setUp( );

        connection  = SoaSession.getConnection();
        manager     = connection.getModelManager();       
        service     = ManHourManagementService.getService(connection);

    }
        
    
    public void testGetManHourInfo()
    {
        // TODO write test code, then remove fail()
        // service.getManHourInfo(  )
        fail("This test has not been implemented yet");
    }

    public void testClearManHours()
    {
        // TODO write test code, then remove fail()
        // service.clearManHours(  )
        fail("This test has not been implemented yet");
    }

    public void testLoad()
    {
        // TODO write test code, then remove fail()
        // service.load(  )
        fail("This test has not been implemented yet");
    }

    public void testSave()
    {
        // TODO write test code, then remove fail()
        // service.save(  )
        fail("This test has not been implemented yet");
    }

    public void testRevise()
    {
        // TODO write test code, then remove fail()
        // service.revise(  )
        fail("This test has not been implemented yet");
    }

    public void testMheTest()
    {
        // TODO write test code, then remove fail()
        // service.mheTest(  )
        fail("This test has not been implemented yet");
    }


}

