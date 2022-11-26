package com.apo.contact.checkout;
/********************************************************************
* @(#)ActTQUndoCheckOut.java 1.0 20110606
* Copyright 2011 by Richard T. Salamone, Jr. All rights reserved.
*
* ActTQUndoCheckOut: Logic to control the check out process as well as to
* display the formatted results and progress.
*
* @version 1.00 20110606
* @author Rick Salamone
* 20110606 rts created
*******************************************************/
import com.shanebow.ui.SBAction;
import java.awt.event.*;

public class ActTQUndoCheckOut
	extends SBAction
	{
	public static final String UNDO_TQ_CO="Undo TQ CheckOut";

	public ActTQUndoCheckOut()
		{
		super ( UNDO_TQ_CO, 'U',
		"Check in all pages of a checkout WITHOUT updating dispos",	"" );
		}

	@Override public boolean menuOnly() { return true; }
	@Override public void actionPerformed(ActionEvent evt)
		{
		new CheckOutUndoWiz();
		}
	}
