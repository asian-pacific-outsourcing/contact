package com.apo.contact.edit;
/********************************************************************
* @(#)EditDispo.java	1.00 10/05/29
* Copyright (c) 2010 by Richard T. Salamone, Jr. All rights reserved.
*
* EditDispo: A component that presents a disposition as a radio
* button group.
*
* @author Rick Salamone
* @version 1.00 20100529 rts created
* @version 1.01 20100607 rts now a radio group of Dispo class rather than String
* @version 1.02 20100625 rts get returns Dispo.XX if no selection, use getSelected
*              if you want the null instead
* @version 1.03 20100705 rts Added disposition UD
* @version 1.05 20100729 rts Added tool tips to the dispositions
* @version 1.05 20101020 rts bug fix select(Dispo.XX) now clears all buttons
* @version 1.06 20101103 rts grid rows calculated on number of dispositions
*******************************************************/
import com.apo.contact.Dispo;
import com.shanebow.dao.DataField;
import com.shanebow.dao.edit.FieldEditor;
import com.apo.contact.SBRadioGroup;
import com.shanebow.util.SBLog;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class EditDispo extends JPanel
	implements FieldEditor
	{
	private int m_field; // the index (col) of this field in the Contact record
	private SBRadioGroup<Dispo>  radio;

/********
	public EditDispo() { this( null); }
********/

	public EditDispo( Dispo[] availableDispos )
		{
		super();
		if ( availableDispos == null )
			throw new IllegalArgumentException("Empty array not allowed");
		int rows = (availableDispos.length < 7) ? 1
		         : (availableDispos.length < 12) ? 2
		         : 3;
		setLayout(new GridLayout(rows,0));
		radio = new SBRadioGroup<Dispo>( this, availableDispos );
		for (Dispo dispo : availableDispos )
			radio.setToolTipText( dispo, dispo.name());
		setOpaque(true); //MUST do this for background to show up
		}

	public void addActionListener( ActionListener al )
		{
		radio.addActionListener(al);
		}

	public Dispo getSelected() { return radio.getSelected(); }
	public Dispo get()
		{
		return getSelected();
		}

	public void set(String text)
		{
		try { select(Dispo.parse(text)); }
		catch (Exception e) { clear(); }
		}

	public void set(DataField d) { select((Dispo)d); }

	public void select(Dispo dispo)
		{
		if ( dispo == null )
			radio.clearSelection();
		else radio.select(dispo);
		}
	public void   clear() { radio.clearSelection(); }
	public boolean isEmpty() { return get() == null; }
	public String toString() { return getClass().getSimpleName(); }
	}
