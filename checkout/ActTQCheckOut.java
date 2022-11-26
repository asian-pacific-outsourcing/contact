package com.apo.contact.checkout;
/********************************************************************
* @(#)ActTQCheckOut.java 1.0 20110606
* Copyright 2011 by Richard T. Salamone, Jr. All rights reserved.
*
* ActTQCheckOut: Action to launch the TQ check out process.
*
* @version 1.00 20110606
* @author Rick Salamone
* 20110606 rts created
*******************************************************/
import com.shanebow.ui.SBAction;
import java.awt.event.*;

public class ActTQCheckOut
	extends SBAction
	{
	public ActTQCheckOut()
		{
		super ( "TQ Check Out", 'T', "Create TQ Call Lists", null );
		}

	@Override public boolean menuOnly() { return true; }
	@Override public void actionPerformed(ActionEvent evt)
		{
		CheckOutWiz.launch();
		}
	}
