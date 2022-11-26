package com.apo.contact;
/********************************************************************
* @(#)DlgDetails.java	1.13 10/04/07
* Copyright (c) 2010 by Richard T. Salamone, Jr. All rights reserved.
*
* DlgDetails: allows the user to add, edit or delete a contact.
*
* @author Rick Salamone
* @version 1.01 20100627 RTS position at lower right corner of screen
* @version 1.02 20100627 RTS responds to change in theme menu
* @version 1.03 20101005 RTS added history tab
* @version 1.04 20101006 RTS added touches
* @version 1.05 20101010 RTS added comments, cleaned up button bar
* @version 1.06 20101012 RTS added contact search for id feature
* @version 1.07 20101021 RTS using common DlgComment
*******************************************************/
import com.shanebow.dao.ContactID;
import com.apo.contact.Raw;
import com.shanebow.ui.LAF;
import com.shanebow.util.SBLog;
import com.shanebow.util.SBProperties;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public abstract class DlgDetails
	extends JDialog
	{
	public DlgDetails(JFrame f)
		{
		super(f, LAF.getDialogTitle("Contact Details"), false);
		LAF.addUISwitchListener(this);
		SBProperties props = SBProperties.getInstance();
		setBounds( props.getRectangle( "usr.app.details.bounds", 490,130,610,550));
		addComponentListener( new ComponentAdapter()
			{
			public void componentMoved(ComponentEvent e) { saveBounds(); }
			public void componentResized(ComponentEvent e) { saveBounds(); }
			});
		}

	private void saveBounds()
		{
		SBProperties.getInstance().setProperty( "usr.app.details.bounds", getBounds());
		}
	abstract public void setContact(Raw aRaw);

	protected final void log ( String msg ) { SBLog.write( msg ); }

	protected final void centerDialog()
		{
/***********
		Dimension screenSize = this.getToolkit().getScreenSize();
		Dimension size = this.getSize();
		screenSize.height = screenSize.height/2;
		screenSize.width = screenSize.width/2;
		size.height = size.height/2;
		size.width = size.width/2;
		int y = screenSize.height - size.height;
		int x = screenSize.width - size.width;
		this.setLocation(x,y);
***********/
		}
	}
