package com.apo.contact.touch;
/********************************************************************
* @(#)ContactHistory.java 1.00 20101005
* Copyright 2010 by Richard T. Salamone, Jr. All rights reserved.
*
* ContactHistory: Formatted display of a contact's history.
*
* @author Rick Salamone
* @version 1.00, 20101005 rts 4 am inspiration
* @version 1.01, 20101023 rts moved from admin to contact, for client use
*******************************************************/
import com.shanebow.dao.ContactID;
import com.shanebow.ui.SBTextPanel;
import com.shanebow.util.TextFile;
import java.util.List;

public class ContactHistory
	extends SBTextPanel
	{
	private static final String TITLE="Contact History";

	public ContactHistory()
		{
		super( TITLE, false, java.awt.Color.CYAN );
		setTimeStamp(false);
		}

	public void reset(List<Touch> aTouchList)
		{
		clear();
		if ( aTouchList == null )
			return;
		for ( Touch touch : aTouchList)
			add( touch.formatted());
		moveCursorToBeginning();
		}

	public void setContact( ContactID id )
		{
		clear();
		if ( id == null )
			return;
		try
			{
			for ( Touch touch : Touch.DAO.fetch(id))
				add( touch.formatted());
			moveCursorToBeginning();
			}
		catch(Exception e) { super.add("ERROR Retrieving history: " + e.getMessage()); }
		}
	}
