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


/**
 * Generated class to represent the TcType/TcClass D6ManHourEntry
 */

public class D6ManHourEntry extends WorkspaceObject
{

    public D6ManHourEntry( com.teamcenter.soa.client.model.Type type, String uid )
    {
        super( type, uid );
    }

    public String get_d6BillType()
    throws NotLoadedException
    {
        return getPropertyObject("d6BillType").getStringValue();
    }

    public String get_d6Holiday()
    throws NotLoadedException
    {
        return getPropertyObject("d6Holiday").getStringValue();
    }

    public String get_d6UserName()
    throws NotLoadedException
    {
        return getPropertyObject("d6UserName").getStringValue();
    }

    public String get_d6WorkingHours()
    throws NotLoadedException
    {
        return getPropertyObject("d6WorkingHours").getStringValue();
    }

    public String get_d6WorkRequired()
    throws NotLoadedException
    {
        return getPropertyObject("d6WorkRequired").getStringValue();
    }

    public String get_d6DayOfWeek()
    throws NotLoadedException
    {
        return getPropertyObject("d6DayOfWeek").getStringValue();
    }

    public String get_d6Month()
    throws NotLoadedException
    {
        return getPropertyObject("d6Month").getStringValue();
    }

    public String get_d6Year()
    throws NotLoadedException
    {
        return getPropertyObject("d6Year").getStringValue();
    }

    public String get_d6Day()
    throws NotLoadedException
    {
        return getPropertyObject("d6Day").getStringValue();
    }

    public String get_d6RefTE()
    throws NotLoadedException
    {
        return getPropertyObject("d6RefTE").getStringValue();
    }

    public String get_d6PrjName()
    throws NotLoadedException
    {
        return getPropertyObject("d6PrjName").getStringValue();
    }

    public String get_d6PrjId()
    throws NotLoadedException
    {
        return getPropertyObject("d6PrjId").getStringValue();
    }

    public String get_d6TaskType()
    throws NotLoadedException
    {
        return getPropertyObject("d6TaskType").getStringValue();
    }

    public String get_d6RefBR()
    throws NotLoadedException
    {
        return getPropertyObject("d6RefBR").getStringValue();
    }


}

