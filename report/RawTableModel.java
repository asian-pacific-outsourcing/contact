package com.apo.contact.report;
/********************************************************************
* @(#)RawTableModel.java 1.00 20100524
* Copyright (c) 2010-2011 by Richard T. Salamone, Jr. All rights reserved.
*
* RawTableModel: Extends AbstractTableModel to display Raw contact info
* in a JTable with sort and save support.
*
* @author Rick Salamone
* @version 1.00, 20100604 RTS initial version for admin app
* @version 1.01, 20100627 RTS now central UI component for admin app
* @version 1.02, 20101008 RTS investigating join to touch table
* @version 1.03, 20101010 RTS data type cleanup & no join code required
* @version 1.04, 20101108 RTS added toCSV() method to save table
* @version 1.05, 20101030 rts now makes requests to application server
* @version 1.06, 20110204 rts added insertRow, removeRow, getRowCSV for dnd support
* @version 1.07, 20110205 rts sort table model itself instead of table sorter
* @version 2.00, 20110200 rts using RawDAO to fetch data
* @version 2.01, 20110309 rts addes csvContactIDs()
*******************************************************/
import com.apo.contact.Raw;
import com.shanebow.dao.ContactID;
import com.shanebow.ui.table.AbstractSavableTableModel;
import com.shanebow.util.SBProperties;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;
import javax.swing.event.TableModelEvent;

public class RawTableModel
	extends AbstractSavableTableModel
	implements com.shanebow.dao.table.SortableTableModel
	{
	private static final ArrayList<RawTableModel> _all = new ArrayList<RawTableModel>();
	public static void updateAll(Raw contact)
		{
		for ( RawTableModel model : _all )
			{
			int index = model.indexOf(contact);
			if ( index >= 0 ) model.set(index, contact);
			}
		}

	private int[]     fFields; // the Raw fields displayed in the columns
	private final List<Raw> fRows = new Vector<Raw>(); // the row data

	public RawTableModel()
		{
		super();
		SBProperties props = SBProperties.getInstance();
		fFields = props.getIntArray("usr.raw.report.fields", 0, 16, 1, 2, 3, 4);
		_all.add(this);
		}

	void setFields(int[] aFields)
		{
		fFields = aFields;
		fireTableStructureChanged();
		}

	public String getLastError() { return Raw.DAO.getLastError(); }

	public boolean fetch(int maxRecords, String query)
		{
		clear();
		try
			{
			Raw.DAO.fetch(fRows, maxRecords, query);
			fireTableChanged(null); // Tell the listeners a new table has arrived.
			return true;
			}
		catch (Exception ex) { return false; }
		}

	public final void clear()
		{
		fRows.clear();
		fireTableChanged(null);
		}

	public void add( int row, Raw contact )
		{
		fRows.add(row, contact);
		fireTableRowsInserted(row, row);
		}

	public void add( Raw contact )
		{
		add ( fRows.size(), contact );
		}

	public int indexOf(Raw contact) { return fRows.indexOf(contact); }

	/**
	* Generates a comma separated list of all the contact ids in the model.
	*/
	public final String csvRawIDs()
		{
		StringBuffer it = new StringBuffer();
		for ( Raw raw : fRows )
			it.append(raw.id().toString() + ",");
		it.deleteCharAt(it.length() - 1);
		return it.toString();
		}

	public Raw get(int row) { return fRows.get(row); }

	public Raw set(int row, Raw contact)
		{
		Raw it = fRows.set(row, contact);
		fireTableRowsUpdated(row, row);
		return it;
		}

	public Raw removeRow(int row)
		{
		Raw contact = fRows.remove(row);
		fireTableRowsDeleted(row, row);
		return contact;
		}

	@SuppressWarnings("unchecked") // we explicitly check "isAssignableFrom
	public void sort(final int aSortColumn, final boolean aIsAscending )
		{
		final int ascend = aIsAscending? 1 : -1;
		final int sortField = fFields[aSortColumn];
		Class sortClass = Raw.getFieldClass(sortField);
		if ( Comparable.class.isAssignableFrom(sortClass))
{
System.out.println("DataField sort");
			Collections.sort(fRows,new Comparator<Raw>()
				{
				public int compare(Raw r1, Raw r2)
					{
					try
						{
						Comparable c1 = (Comparable)r1.get(sortField);
						Comparable c2 = (Comparable)r2.get(sortField);
						return ascend * c1.compareTo(c2);
						}
					catch (Exception e) { return 0; }
					}
				});
}
		else
{
System.out.println("String sort");
		Collections.sort(fRows,new Comparator<Raw>()
			{
			public int compare(Raw r1, Raw r2)
				{
				return ascend
				       * r1.get(sortField).toString().compareTo(r2.get(sortField).toString());
				}
			});
}
		fireTableDataChanged();
		}

	@Override public int getColumnCount()
		{
		return fFields.length;
		}

	@Override public String getColumnName(int column)
		{
		return Raw.getLabelText(fFields[column]);
		}

	@Override public Class getColumnClass(int column)
		{
		int field = fFields[column];
		return Raw.getFieldClass(field);
		}

	@Override public boolean isCellEditable(int row, int column)
		{
		return false;
		}

	@Override public int getRowCount()
		{
		return fRows.size();
		}

	@Override public Object getValueAt(int row, int col)
		{
		int field = fFields[col];
		return fRows.get(row).get(field);
		}
	} // 240
