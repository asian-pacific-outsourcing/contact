package com.apo.contact.report;
/********************************************************************
* @(#)RawTable.java 1.00 20100524
* Copyright © 2010-2011 by Richard T. Salamone, Jr. All rights reserved.
*
* RawTable: A extends JTable to display Raw contact info with sorting
* and mouse processing.
*
* @author Rick Salamone
* @version 1.00, 20100524 rts created from the SQL Editor program
* @version 1.50, 20101030 rts now makes requests to application server
* @version 1.51, 20110203 rts decoupled table from report
* @version 1.52, 20110205 rts sort table model itself instead of table sorter
*******************************************************/
import com.apo.contact.Raw;
import com.apo.contact.Dispo;
import com.shanebow.dao.ContactID;
import com.shanebow.dao.table.DFTable;
import com.shanebow.dao.table.ConfigurableTable;
import com.shanebow.ui.SBDialog;
import com.shanebow.util.SBProperties;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;

import javax.swing.Action;
import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.KeyStroke;

public class RawTable
	extends DFTable
	implements ConfigurableTable
	{
	public static com.apo.contact.DlgDetails dlgDetails;
	private Raw fSelected;
	private final String fPropertyPrefix = "usr.raw.report.";

	public RawTable(RawTableModel model)
		{
		super(model);
		configure();
		setDefaultRenderer( Dispo.class, new DispoRenderer());

		setDropMode(DropMode.INSERT_ROWS);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// Install a mouse listener in the Table itself for double clicks
		addMouseListener( new MouseAdapter()
			{
			public void mouseClicked(MouseEvent e)
				{
				if ( e.getClickCount() > 1 ) onDoubleClick();
				} 
			});

		// Use a scrollbar, because there are many columns
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF); 
		setFillsViewportHeight( true );
		Action deleteAction = new AbstractAction()
			{
			public void actionPerformed(ActionEvent ae)
				{
		/***********
				if ( !ContactTableDelMenuItem.checked())
					{
					java.awt.Toolkit.getDefaultToolkit().beep();
					SBDialog.inform( "Table Delete Not Enabled",
					  "You must check 'Allow Table Delete'\non the Option Menu to enable the"
						+ "\ndelete key." );
					return;
					}
		***********/
				int row = getSelectedRow();
				if ( row >= 0 )
					{
					RawTableModel model = (RawTableModel)getModel();
					ContactID id = model.removeRow(row).id();
					log( "Delete row " + row + " " + id + " " + fSelected.id());
					try
						{
						Raw.DAO.delete(id); // DO NOT use m_selected ID it's already updated
						if ( row == model.getRowCount())
							--row;
						if ( row >= 0 )
							setRowSelectionInterval(row,row);
						}
					catch (Exception e) { SBDialog.error( "Delete Error", e.getMessage()); }
					}
				else java.awt.Toolkit.getDefaultToolkit().beep();
				}
			};
		KeyStroke delete = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, Event.CTRL_MASK);
		InputMap im = this.getInputMap(DFTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		im.put(delete, "Delete Contact");
		getActionMap().put("Delete Contact", deleteAction);
		}

	private void onDoubleClick()
		{
		int row = getSelectionModel().getLeadSelectionIndex();
		if ( row >= 0 )	// clicked on a contact, row will be -1 for header/empty table
			dlgDetails.setVisible(true);
		}

	// implement ListSelectionListener to get selected row
	@Override public void valueChanged( ListSelectionEvent e )
		{
		super.valueChanged(e);
		ListSelectionModel lsm = (ListSelectionModel)e.getSource();
		if ( lsm.getValueIsAdjusting())
			return;

		RawTableModel model = (RawTableModel)getModel();
		// Find out which indexes are selected
		int i = lsm.getLeadSelectionIndex();
		if ( lsm.isSelectionEmpty())
			{
			if ( model.getRowCount() > 0 )
				lsm.setSelectionInterval(0,0);
			else
				selected(null);
			return;
			}
		else // if ( lsm.isSelectedIndex(i))
			selected( model.get(i));
		}

	private void selected( Raw aRaw )
		{
		dlgDetails.setContact(fSelected = aRaw );
		}

	// Stuff to make the table configurable via a PreferencesEditor
	public final String getPropertyPrefix() { return fPropertyPrefix; }
	public final com.shanebow.dao.FieldMeta[] getAvailableFields() { return Raw.meta; }
	public final void configure()
		{
		SBProperties props = SBProperties.getInstance();
		int[] fields = props.getIntArray(fPropertyPrefix + "fields", 0, 1, 2, 3, 4, 6);
		((RawTableModel)getModel()).setFields(fields);

		int fontSize = props.getInt(fPropertyPrefix + "font.size", 12);
		setFont(new Font(Font.SANS_SERIF, Font.PLAIN, fontSize));
		}
	} //176
