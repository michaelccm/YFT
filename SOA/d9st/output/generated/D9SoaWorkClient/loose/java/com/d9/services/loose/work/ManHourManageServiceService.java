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

import com.teamcenter.soa.SoaConstants;
import com.teamcenter.soa.client.Connection;

/**
 * ManHourManageService
 * <br>
 * <br>
 * <br>
 * <b>Library Reference:</b>
 * <ul>
 * <li type="disc">D9SoaWorkLoose.jar
 * </li>
 * <li type="disc">D9SoaWorkTypes.jar (runtime dependency)
 * </li>
 * </ul>
 */

public abstract class ManHourManageServiceService
  implements     com.d9.services.loose.work._2018_06.ManHourManageService
{
    /**
     * 
     * @param connection 
     * @return A instance of the service stub for the given Connection
     */
    public static ManHourManageServiceService getService( Connection connection )
    {
        if(connection.getBinding().equalsIgnoreCase( SoaConstants.REST ))
        {
            return new ManHourManageServiceRestBindingStub( connection );
        }

        throw new IllegalArgumentException("The "+connection.getBinding()+" binding is not supported.");
    }


}
