package com.apo.contact.edit;
/********************************************************************
* @(#)CheckDispo.java	1.0 10/06/03
* Copyright 2010-2011 by Richard T. Salamone, Jr. All rights reserved.
*
* CheckDispo: Check box selector for all dispositions.
*
* @author Rick Salamone
* @version 1.00, 20100603 rts
* @version 1.01, 20101010 rts contructor & select() take array of Dispo's
* @version 1.02, 20101121 rts moved into datafield.edit package
* @version 1.03, 20110121 rts added getSelected() to return Dispo[]
* @version 1.04, 20110316 rts fixed select() to select iff in arg list
* @version 1.05, 20110420 rts added constructor to specify dispos
*******************************************************/
import com.apo.contact.Dispo;
import com.shanebow.ui.SBDialog;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public final class CheckDispo extends JPanel
	{
	private final JCheckBox[] chkDispos;

	public CheckDispo() { this ( 3, 0 ); }

	public CheckDispo(int rows, int cols) { this( rows, cols, Dispo.getAll()); }

	public CheckDispo( int rows, int cols, Iterable<Dispo> aDispos )
		{
		super ( new GridLayout( rows, cols, 0, 0 ));
		int num = 0;
		for ( Dispo d : aDispos ) ++num;
		chkDispos = new JCheckBox[num];
		int i = 0;
		for ( Dispo d : aDispos)
			{
			chkDispos[i] = new JCheckBox( d.toString());
			chkDispos[i].setToolTipText( d.name() + " (" + d.id() + ")");
			add( chkDispos[i++] );
			}
		}

	public CheckDispo(Dispo... dispos)
		{
		this();
		select( dispos );
		}

	public void select ( Dispo... dispos )
		{
		for ( JCheckBox chk : chkDispos )
			chk.setSelected(false);
		if ( dispos == null  || dispos.length == 0 )
			return;
		for ( Dispo d : dispos )
			for ( JCheckBox chk : chkDispos )
				if ( chk.getText().equals(d.toString()))
					chk.setSelected(true);
		}

	public boolean isSelected ( Dispo d )
		{
		for ( JCheckBox chk : chkDispos )
			if ( chk.getText().equals(d.toString()))
				return chk.isSelected();
		return false;
		}

	public String getCSV()
		{
		String it = "";
		for ( JCheckBox chk : chkDispos )
			if ( chk.isSelected())
				try {it += (it.isEmpty()? "" : ",")
							+ Dispo.parse( chk.getText()).dbRepresentation(); }
				catch(Exception e) {}
		return it;
		}

	public Dispo[] getSelected()
		{
		int numSelected = 0;
		for ( JCheckBox chk : chkDispos )
			if ( chk.isSelected())
				++numSelected;
		Dispo[] selected = new Dispo[numSelected];
		int i = 0;
		for ( JCheckBox chk : chkDispos )
			if ( chk.isSelected())
				try { selected[i++] = Dispo.parse( chk.getText()); }
				catch(Exception e) {} // should not be possible
		return selected;
		}
	}
