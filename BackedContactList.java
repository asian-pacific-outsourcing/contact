package com.apo.contact;
/********************************************************************
* @(#)BackedContactList.java	1.00 10/04/17
* Copyright 2010 by Richard T. Salamone, Jr. All rights reserved.
*
* BackedContactList: Extends the ContactList text panel to use a
* backing store to disk.
*
* @version 1.0 04/17/10
* @author Rick Salamone
*
* 20100510 RTS Changed to have time stamps off by default 
* 20100628 RTS moved to contact package & cleanup
* 20101116 RTS now keeps file, header and autosaves on add 
* 20101116 RTS accepts a JLabel to act as production display 
*******************************************************/
import com.apo.contact.*;
import com.shanebow.ui.SBTextPanel;
import com.shanebow.util.TextFile;
import java.util.List;
import java.util.Vector;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

public final class BackedContactList
	extends ContactsList // which extends SBTextPanel
	{
	private List<String> m_list;
	private String       m_header;
	private String       m_filespec = null;
	private String       m_backup = null;
	private JLabel       lblCounter = null;

	public BackedContactList(String header) { this( null, header ); }
	public BackedContactList(JLabel counter, String header )
		{
		super( null );
		setTimeStamp(false);
		m_list = new Vector<String>();
		m_header = header;
		lblCounter = counter;
		}

	public void setFilespec( String pre, String yyyymmdd, String name )
		{
		m_filespec = pre + yyyymmdd + name + ".csv";
		m_backup = pre + name + ".bak";
		com.shanebow.util.SBLog.write("Backup: " + m_filespec);
		restore( m_filespec );
		updateCounter();
		}

	public void setCounterLabel(JLabel counter)
		{
		lblCounter = counter;
		}

	public JLabel getCounterLabel()
		{
		return lblCounter;
		}

	private void updateCounter()
		{
		if ( lblCounter == null ) return;
		if (SwingUtilities.isEventDispatchThread())
			lblCounter.setText("" + m_list.size());
		else SwingUtilities.invokeLater(new Runnable()
			{
			public void run() { lblCounter.setText("" + m_list.size()); }
			});
		}

	public void add(String csv)
		{
		super.add(csv);
		m_list.add(csv);
		TextFile.freeze( m_list, m_filespec, m_header );
		TextFile.freeze( m_list, m_backup, m_header );
		updateCounter();
		}

//	public int getRowCount() { return m_list.size(); }
	private void save( String filespec, String header )
		{
		TextFile.freeze( m_list, filespec, header );
		}

	private void restore( String filespec )
		{
		m_list.clear();
		TextFile.thaw( String.class, filespec, m_list, true );
		for ( String csv : m_list )
			super.add(csv);
		}
	}
