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

using System;
using System.Collections;
using System.Reflection;

using Teamcenter.Soa.Client.Model.Strong;
using Teamcenter.Soa.Internal.Client.Model;


namespace Teamcenter.Soa.Client.Model
{

    /**
     * Generated class to construct the various client model classes
     * that represent the TcTypes and TcClasses in the server.
     * This subclass specifically adds objects for extension model D6st.
     */

    public class StrongObjectFactoryD6st : StrongObjectFactory
    {
        static bool inited = false;
        private static readonly object singletonLock = new object();

        public static void Init()
        {
            lock (singletonLock)
            {
                if ( !inited )
                {
                    StrongObjectFactory.Init();
    
                    Type[] types = { typeof(Teamcenter.Soa.Client.Model.SoaType), typeof(string) };
    
                     objConstructorMap["D6ManHourEntry"] = typeof(  Teamcenter.Soa.Client.Model.Strong.D6ManHourEntry ).GetConstructor( types);
                objConstructorMap["D6MheTask"] = typeof(  Teamcenter.Soa.Client.Model.Strong.D6MheTask ).GetConstructor( types);

     
                    inited = true;
                }
            }
        }
    }
}
