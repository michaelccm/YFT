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

package com.d6.services.rac.work;

import com.teamcenter.soa.SoaConstants;
import com.teamcenter.soa.client.Connection;
import com.teamcenter.rac.kernel.TCSession;

/**
 * ManHourManagement
 */

public abstract class ManHourManagementService
  implements     com.d6.services.rac.work._2012_02.ManHourManagement
{

    /**
     * 
     * @param session 
     * @return A instance of the service stub for the given TCSession
     */
    public static ManHourManagementService getService( TCSession session )
    {

        Connection connection = session.getSoaConnection();
        if(connection.getBinding().equalsIgnoreCase( SoaConstants.REST ))
        {
            return new ManHourManagementRestBindingStub( connection );
        }

        throw new IllegalArgumentException("The "+connection.getBinding()+" binding is not supported.");
    }

}