package com.apo.contact.checkout;
/********************************************************************
* @(#)ActPurgeTQWorkQ.java 1.0 20110616
* Copyright 2011 by Richard T. Salamone, Jr. All rights reserved.
*
* ActPurgeTQWorkQ: Action to to tell the server to clear the TQ
* work queue in order to make the records in the queue available
* for check out.
*
* @version 1.00 20110616
* @author Rick Salamone
* 20110616 rts created
*******************************************************/
import com.apo.net.Access;
import com.apo.net.SysDAO;
import com.shanebow.ui.SBAction;
import com.shanebow.ui.SBDialog;
import java.awt.event.*;

public final class ActPurgeTQWorkQ
	extends SBAction
	{
	static final String PURGE_TQ_WORK="Clear TQ Work Queue";

	public ActPurgeTQWorkQ()
		{
		super ( PURGE_TQ_WORK, 'U',
		"Empties the server's work queue to free contents for check out",	"" );
		}

	@Override public boolean menuOnly() { return true; }
	@Override public void actionPerformed(ActionEvent evt)
		{
		try
			{
			SysDAO.DAO().purgeWorkQueue(Access.TQ);
			SBDialog.inform( PURGE_TQ_WORK,
				"<HTML>The work queue has been cleared,<BR>"
				+ "but it will refill when a TQ app asks for work" );
			}
		catch (Exception e) { SBDialog.error(PURGE_TQ_WORK + " Error", e.getMessage()); }
		}
	}
