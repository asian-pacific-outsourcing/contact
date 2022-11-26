package com.apo.contact.edit;
/********************************************************************
* @(#)EditHTRCount.java 1.00 20100818
* Copyright (c) 2010-2011 by Richard T. Salamone, Jr. All rights reserved.
*
* EditHTRCount: Extends EditString to handle a HTRCount.
*
* @version 1.00 08/18/10
* @author Rick Salamone
* 20100818 RTS created
* 20101128 RTS constructor defaults to display default max htr
* 20110121 RTS get() returns HTRCount rather than DataField
* 20110504 RTS get() added contructor to take HTRCount
*******************************************************/
import com.apo.contact.HTRCount;
import com.shanebow.dao.DataFieldException;
import com.shanebow.dao.edit.EditString;
import com.shanebow.dao.edit.FieldEditor;

public final class EditHTRCount
	extends EditString
	implements FieldEditor
	{
	public EditHTRCount()
		{
		this( HTRCount.DEFAULT_MAX_HTR );
		}

	public EditHTRCount(HTRCount aHTRCount)
		{
		super();
		set( aHTRCount );
		}

	public HTRCount get() throws DataFieldException
		{
		return HTRCount.parse( getText());
		}
	}
