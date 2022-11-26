package com.apo.contact.checkout;
/********************************************************************
* @(#)AbstractCallerCriteria.java	1.00 05/23/10
* Copyright 2010 by Richard T. Salamone, Jr. All rights reserved.
*
* CallerCriteria: The interface for specifying the the workload for a
* type of caller. Essentially it is used to create the SQL where clause
* for a paper checkout or the GETWORK OpCode. The app server requires
* a concrete instance of this class for each caller monitor (TQ, VO, ...)
*
* @author Rick Salamone
* @version 1.00, 20101025 rts created from TQCriteria, then retrofitted it
*******************************************************/
import com.apo.contact.Dispo;
import com.shanebow.dao.Country;
import javax.swing.JComponent;

public interface CallerCriteria
	{
	// abstract methods
	public abstract JComponent getComponent();
	public abstract String    buildWhere();
	public abstract String    getWhere();
//	public abstract Country getCountry();
	public abstract Dispo[]   getDefaultDispos();
	public abstract long      available();
	}
