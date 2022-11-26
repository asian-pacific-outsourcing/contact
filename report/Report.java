package com.apo.contact.report;
/********************************************************************
* @(#)Report.java 1.00 10/05/24
* Copyright © 2010 by Richard T. Salamone, Jr. All rights reserved.
*
* Report: A panel that accepts SQL input and displays the results
* of the SQL in a table.
*
* @author Rick Salamone
* @version 1.00, 20100524 rts for the SQL Editor program
* @version 1.50, 20101030 rts now makes requests to application server
* @version 1.51, 20110203 rts decoupled table from report
* @version 1.51, 20110525 rts added count action
*******************************************************/
import com.apo.contact.Raw;
import com.apo.contact.DlgDetails;
import com.apo.net.Access;
import com.apo.net.SysDAO;
import com.shanebow.ui.SBAction;
import com.shanebow.util.SBLog;
import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import javax.swing.border.*;

public final class Report
	extends JPanel
	{
	private final ReportControls  fControls;
	private final RawTableModel   fModel = new RawTableModel();
	private final RawTable        fTable = new RawTable(fModel);
	private final JLabel          fStatusBar = new JLabel("");

	private final SBAction fFetchAction
		= new SBAction("Fetch", 'F', "Display contacts statisfying criteria", null)
		{
		@Override public void action()
			{
			String sql = fControls.getSQL();
			int show = fControls.getMaxShowCount();
			SBLog.write( "SQL: '" + sql + "'" );
			if ( fModel.fetch( show, sql ))
				{
				fControls.updateHistory();
				fStatusBar.setText( "Retrieved " + fModel.getRowCount() + " items" );
				}
			else fStatusBar.setText( "ERROR: " + fModel.getLastError());
			}
		};

	private final SBAction fCountAction
		= new SBAction("Count", 'C', "Count contacts statisfying criteria", null)
		{
		@Override public void action()
			{
			String where = " WHERE " + fControls.getWhereClause();
			String msg;
			try
				{
				msg = "" + SysDAO.DAO().sqlCount(Raw.DB_TABLE, where)
				    + " contacts meet the specified criteria";
				}
			catch (Exception e) { msg = "COUNT ERROR: " + e; }
			fStatusBar.setText( msg );
			}
		};

	private final SBAction fSaveAction
		= new SBAction("Save", 'S', "Save the displayed contacts to a csv file", null)
		{
		@Override public void action() { fModel.saveAs(fTable); }
		};

	public Report(DlgDetails aDlgContact)
		{
		super ( new BorderLayout());
		RawTable.dlgDetails = aDlgContact;

		fControls = new ReportControls( aDlgContact, fTable);
		fTable.makeConfigurable();

		JSplitPane report = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT );
		report.setLeftComponent( fControls );
		report.setRightComponent( resultsPanel());
		report.setDividerLocation( 260 );
		report.setOneTouchExpandable(true);
		report.setContinuousLayout(true);

		add(report, BorderLayout.CENTER);
		add(statusPanel(), BorderLayout.SOUTH);
		}

	private JComponent statusPanel()
		{
		Dimension edgeSpacer = new Dimension(5, 0);
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.LINE_AXIS));
		p.add(Box.createRigidArea(edgeSpacer));
		p.add( new JButton(fFetchAction));
		p.add(Box.createRigidArea(edgeSpacer));
		p.add( new JButton(fCountAction));
		p.add(Box.createRigidArea(edgeSpacer));
		p.add( fStatusBar );
		short uid = Access.getUID();
		if ( uid == 7 || uid == 2 )
			{
			p.add(Box.createHorizontalGlue());
			p.add( new JButton(fSaveAction)); //, BorderLayout.EAST );
			}
		p.add(Box.createRigidArea(edgeSpacer));
		return p;
		}

	private JComponent resultsPanel()
		{
		JScrollPane scroller = new JScrollPane(fTable);
		scroller.setBorder( BorderFactory.createLoweredBevelBorder());
		return scroller;
		}

/*******************
	private JComponent toggleDetails(Component dlgContact, JTable table)
		{
		if ( !(dlgContact instanceof PropertyChangeListener))
			throw new IllegalArgumentException("dlgContact must be a prop change listener");
		table.addPropertyChangeListener((PropertyChangeListener)dlgContact);
		dlgContact.addPropertyChangeListener(new PropertyChangeListener()
			{
			public void propertyChange(PropertyChangeEvent evt)
				{
				String property = evt.getPropertyName();
				// SBLog.write( "Controls", "received propertyChange " + property );
				if ( property.equals("DELETED CONTACT")
				||   property.equals("UPDATED CONTACT"))
					btnFetch.doClick();
				}
			});
		ToggleDetails tbDetails = new ToggleDetails(dlgContact);
		table.addPropertyChangeListener(Raw.SELECTED_PROPERTY, tbDetails );
		return tbDetails;
		}
*******************/
	}
