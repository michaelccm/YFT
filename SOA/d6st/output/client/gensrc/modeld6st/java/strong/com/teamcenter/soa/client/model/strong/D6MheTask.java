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

package com.teamcenter.soa.client.model.strong;

import java.util.List;
import com.teamcenter.soa.exceptions.NotLoadedException;

import com.teamcenter.soa.client.model.strong.WorkspaceObject;

import java.util.Calendar;
import com.teamcenter.soa.client.model.strong.Schedule;

/**
 * Generated class to represent the TcType/TcClass D6MheTask
 */

public class D6MheTask extends WorkspaceObject
{

    public D6MheTask( com.teamcenter.soa.client.model.Type type, String uid )
    {
        super( type, uid );
    }

    public String get_d6TaskType()
    throws NotLoadedException
    {
        return getPropertyObject("d6TaskType").getStringValue();
    }

    public String get_d6PrjId()
    throws NotLoadedException
    {
        return getPropertyObject("d6PrjId").getStringValue();
    }

    public String get_d6PrjName()
    throws NotLoadedException
    {
        return getPropertyObject("d6PrjName").getStringValue();
    }

    public String get_d6UserName()
    throws NotLoadedException
    {
        return getPropertyObject("d6UserName").getStringValue();
    }

    public Calendar get_d6PrjStartDate()
    throws NotLoadedException
    {
        return getPropertyObject("d6PrjStartDate").getCalendarValue();
    }

    public Calendar get_d6PrjEndDate()
    throws NotLoadedException
    {
        return getPropertyObject("d6PrjEndDate").getCalendarValue();
    }

    public String get_d6Status()
    throws NotLoadedException
    {
        return getPropertyObject("d6Status").getStringValue();
    }

    public Schedule get_d6Schedule_tag()
    throws NotLoadedException
    {
        return (Schedule)getPropertyObject("d6Schedule_tag").getModelObjectValue();
    }


}

