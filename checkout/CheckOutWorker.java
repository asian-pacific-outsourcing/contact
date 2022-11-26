package com.apo.contact.checkout;
/********************************************************************
* @(#)CheckOutWorker.java	1.00 05/23/10
* Raw Admin Check Out Utility 
* Copyright 2010 by Richard T. Salamone, Jr. All rights reserved.
* 
* @(#)CheckOutWorker.java	1.00 10/05/23 - The user interface for checkiing
* out pages of raw contacts to be called by the qualifiers. The output is
* formatted for printing.
* 
* @author Rick Salamone
* @version 1.00
* 20100610 RTS 1.04 sorts checkouts on dispo DESC then id ASC
* 20100614 RTS 1.05 extends SwingWorker to run in background
* 20100705 RTS 1.10 Added disposition UD
* 20100822 RTS 1.11 execs Microsoft Word when done
* 20100822 RTS 1.11 trace now shows ids on each page
* @version 1.12 RTS 20101004 makes touch entry
*******************************************************/
import com.apo.admin.TouchDB;
import com.apo.net.Access;
import com.apo.contact.Raw;
import com.apo.contact.touch.TouchCode;
import com.shanebow.dao.*;
import com.shanebow.ui.SBDialog;
import com.shanebow.util.MessageLogger;
import com.shanebow.util.SBLog;
import java.io.PrintWriter;
import java.io.IOException; 
import java.util.List;
import java.util.Vector;
import javax.swing.SwingWorker;

public final class CheckOutWorker
	extends SwingWorker<Void, String>
	{
	private static boolean LAUNCH_WORD=false;
	private static final String MS_WORD_EXE
//		= "\"c:\\Program Files\\Microsoft Office\\OFFICE11\\winword.exe\" ";
		= "\"c:\\Program Files\\Microsoft Office\\Office12\\winword.exe\" ";
	private static final String LINE_SEPARATOR = "_____________________________"
		+ "_____________________________________________________________________"
		+ "_______";
	private static volatile boolean _isBusy = false;
	static boolean isBusy() { return _isBusy; }

	private String m_where;     // where clause for SQL query
	private String m_csvFile;   // csv file for comma delimited output
	private String m_txtFile;   // text file for formatted output
	private int    m_total = 0; // total # contacts processed so far
	private int    m_reqTotal;  // total # contacts requested for check out
	private int    m_perPage;   // number per formatted page

	public MessageLogger  m_contactsList;
	public static String  m_headerInfo = "";

	public CheckOutWorker( int reqTotal, int perPage, String whereClause,
	                       String txtFile, String csvFile )
		{
		m_reqTotal = reqTotal;
		m_perPage = perPage;
		m_where   = whereClause;
		m_txtFile = txtFile;
		m_csvFile = csvFile;
		}

	@Override
	protected void process(List<String> chunks)
		{
		for ( String str : chunks )
			m_contactsList.write( str );
		setProgress( m_total * 100 / m_reqTotal );
		}

	@Override
	protected void done()
		{
		if ( m_total < m_reqTotal )
			SBDialog.inputError ( "Only " + m_total + " are available" );
		}

	@Override public Void doInBackground()
		{
		_isBusy = true;
		PrintWriter txt = null;
		PrintWriter csv = null;
		short uid = Access.getUID();
		int onPage = 0;
		long id = 0;
		try
			{
			List<Raw> raws = new Vector<Raw>(m_reqTotal);
publish( "CO Worker total: " + m_reqTotal + " where: " + m_where );
//if ( _isBusy ) return (Void)null;
			Raw.DAO.checkOut(raws, m_reqTotal, m_perPage, m_where, Access.getUID());
			long page = raws.get(0).page().getValue();
			txt = new PrintWriter ( m_txtFile );
			csv = new PrintWriter ( m_csvFile );

			String idsOnThisPage = "";
			String headerFmt = "Page: " + page + " " + m_headerInfo + "\n" + LINE_SEPARATOR;
			txt.println( headerFmt );
			csv.println(Raw.ID_HEADER);
			for ( Raw raw : raws )
				{
				if ( isCancelled()) break;
				id = raw.id().toLong();

				String mobile = raw.mobile().toString();
				String lineA = String.format(" %-23.23s %-26.26s %-23.23s BD UD NI XC CB L\n",
				                raw.name(), raw.company(), raw.position());
				String lineB = String.format(" Ph: %-22.22s Mobile %-23.23s CB Date _______________  ID: %d",
				                raw.phone(), (mobile.isEmpty()? "_______________" : mobile), id);

				txt.println( lineA );
				txt.println( lineB );
				txt.println( LINE_SEPARATOR );
// pause();
				csv.println(raw.toCSV());
				++m_total;
				idsOnThisPage += " " + id;
				if ( m_total >= m_reqTotal )
					{
					publish( "page " + page + ":" + idsOnThisPage );
					break;
					}
				if ( ++onPage >= m_perPage )
					{
					publish( "page " + page + ":" + idsOnThisPage );
					idsOnThisPage = "";
					++page;
					onPage = 0;
					String newPage = "\f" + "Page: " + page + " " + m_headerInfo + "\n"
					               + LINE_SEPARATOR;
					txt.println( newPage );
					}
				}
			}
		catch (Throwable t) { failed( "Unexpected", id, t.toString()); }
		finally
			{
			try
				{
				if ( csv != null ) csv.close();
				if ( txt != null ) txt.close();
				if ( LAUNCH_WORD )
					{
					Runtime.getRuntime().exec( MS_WORD_EXE
					 + System.getProperty("user.dir") + "\\" + m_txtFile );
					}
				}
			catch ( Exception e ) {log("exec error: " + e.getMessage()); }
			}
		_isBusy = false;
//		log ( "Checked Out %d Contacts", m_total );
		publish ( "Checked Out " + m_total + " Contacts" );
		firePropertyChange("state", "", "FINISHED" );
		return (Void)null;
		}

	private void failed( String titleType, long id, String msg )
		{
		SBDialog.error( "Check Out " + titleType + " Error",
		                "Failed at Contact id: " + id + "\nDue to " + msg);
		}

	private void pause()
		{
		try { Thread.sleep( 1000 ); }
		catch ( InterruptedException e ) {}
		}
	private String quoted(String s) { return "\"" + s + "\""; }
	private String noPercent(String s)
		{
		if ( s.isEmpty()) return "";
		char[] work = s.toCharArray();
		int j = 0;
		for ( int i = 0; i < work.length; i++ )
			{
			if ( work[i] != '%' )
				work[j++] = work[i];
			}
		return new String(work, 0, j);
		}

	private void log ( String msg, Object... args )
		{
		SBLog.write( "COW", String.format(msg, args) );
		}
	}
