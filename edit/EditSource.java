package com.apo.contact.edit;
/********************************************************************
* @(#)EditSource.java	1.00 10/06/27
* Copyright (c) 2010 by Richard T. Salamone, Jr. All rights reserved.
*
* EditSource: A component for editing the field that stores the source
* of a contact. The source is stored as a foreign key into the source
* table. Due to the relatively small number of anticipated sources, we
* simply extend JComboBox to implement FieldEditor, and keep all the
* values in memory.
*
* @version 1.00 06/27/10
* @author Rick Salamone
* 20110420 rts added constuctor for checking access levels
*******************************************************/
import com.apo.contact.Source;
import com.shanebow.dao.DataField;
import com.shanebow.dao.DataFieldException;
import com.shanebow.dao.edit.FieldEditor;
import java.sql.ResultSet;
import javax.swing.JComboBox;

public class EditSource extends JComboBox
	implements FieldEditor
	{
	public EditSource ()
		{
		super();
		for ( Source t : Source.getAll())
			addItem(t);
		}

	public EditSource (long aRights)
		{
		super();
		addItem(Source.XX);
		for ( Source t : Source.getAll())
			if ( t.accessible(aRights))
				addItem(t);
		}

	public void clear() {}

	public void set(String text)
		{
		try { setSelectedItem(Source.parse(text)); }
		catch (Exception e) { setSelectedIndex(0); }
		}

	public void set(DataField field)
		{
		try { setSelectedItem(field); }
		catch (Exception e) { setSelectedIndex(0); }
		}

	public Source get()
		throws DataFieldException
		{
		return (Source)getSelectedItem();
		}

	public boolean isEmpty() { return false; }
	}
