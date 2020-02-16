/* 
 @<COPYRIGHT>@
 ==================================================
 Copyright 2012
 Siemens Product Lifecycle Management Software Inc.
 All Rights Reserved.
 ==================================================
 @<COPYRIGHT>@

 ==================================================

  Auto-generated source from Teamcenter Data Model.
                 DO NOT EDIT

 ==================================================
*/

package com.teamcenter.soa.client.model;

import java.lang.reflect.Constructor;
import java.util.HashMap;

import com.teamcenter.soa.client.model.ModelObjectFactory;
import com.teamcenter.soa.client.model.Type;


/**
 * Generated class to construct the various client model classes
 * that represent the TcTypes and TcClasses in the server.
 * This subclass specifically adds objects for extension model D6st.
 */

public class StrongObjectFactoryD6st extends StrongObjectFactory implements ModelObjectFactory
{
    static boolean inited = false;

    public static synchronized void init()
    {
        if ( !inited )
        {
            StrongObjectFactory.init();

            Class[] parameterTypes = {Type.class, String.class};
            try
        
            {
                objConstructorMap.put("D6ManHourEntry", com.teamcenter.soa.client.model.strong.D6ManHourEntry.class.getConstructor(parameterTypes));
                objConstructorMap.put("D6MheTask", com.teamcenter.soa.client.model.strong.D6MheTask.class.getConstructor(parameterTypes));
        
        
            }
            catch(Exception e)
            {
                throw new IllegalArgumentException( e.getMessage());
            }
            inited = true;
        }
        
    }
    
}
