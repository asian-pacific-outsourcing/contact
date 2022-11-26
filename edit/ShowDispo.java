package com.apo.contact.edit;
/********************************************************************
* @(#)ShowDispo.java 1.00 20110410
* Copyright (c) 2011 by Richard T. Salamone, Jr. All rights reserved.
*
* ShowDispo: A component for color coded display only dispo: Extends
* JLabel to implement FieldEditor.
*
* @version 1.00 20110509
* @author Rick Salamone
*******************************************************/
import com.shanebow.dao.DataField;
import com.shanebow.dao.DataFieldException;
import com.apo.contact.Dispo;
import com.shanebow.dao.edit.FieldEditor;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;

public final class ShowDispo
	extends JLabel
	implements FieldEditor // , TableCellRenderer
	{
	private Dispo fDispo;

	public ShowDispo ()
		{
		super();
		setOpaque(true);
		setFont( new javax.swing.JList().getFont());
		}

	@Override public void addActionListener(java.awt.event.ActionListener al) {}

	public void clear()
		{
		fDispo = null;
		setText("");
		setBackground(Color.WHITE);
		}

	public void set(DataField f)
		{
		if ( f == null || !(f instanceof Dispo))
			{
			clear();
			return;
			}
		fDispo = (Dispo)f;
		setText(fDispo.name());
		setBackground(fDispo.getColor());
		}

	public Dispo get() { return fDispo; }

	public boolean isEmpty() { return (fDispo == null) || fDispo.isEmpty(); }
	}
