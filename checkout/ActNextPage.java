package com.apo.contact.checkout;
/********************************************************************
* @(#)ActNextPage.java 1.0 20110606
* Copyright 2011 by Richard T. Salamone, Jr. All rights reserved.
*
* ActNextPage: Action to launch the TQ check out process.
*
* @version 1.00 20110606
* @author Rick Salamone
* 20110606 rts created
*******************************************************/
import com.apo.contact.Raw;
import com.shanebow.ui.SBAction;
import com.shanebow.ui.SBDialog;
import java.awt.event.*;

public final class ActNextPage
	extends SBAction
	{
	static final String NEXT_CO_PAGE="Next Checkout Page";

	public ActNextPage()
		{
		super ( NEXT_CO_PAGE, 'P',
		"Page # to be assigned to the next check out",	"" );
		}

	@Override public boolean menuOnly() { return true; }
	@Override public void actionPerformed(ActionEvent evt)
		{
		try
			{
			long page = Raw.DAO.nextCheckOutPage();
			SBDialog.inform( NEXT_CO_PAGE,
				"The next check out will begin with page # " + page );
			}
		catch (Exception e) { SBDialog.error(NEXT_CO_PAGE + " Error", e.getMessage()); }
		}
	}
