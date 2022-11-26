package com.apo.contact.checkout;
/********************************************************************
* @(#)CheckOutUndoLineParser.java 1.00 20100822
* Copyright © 2010-2013 by Richard T. Salamone, Jr. All rights reserved.
*
* CheckOutUndoLineParser: Parses lines of the csv file containing
* checked out records, reads the ID field and resets the page to 0.
*
* @author Rick Salamone
* @version 2.00
* 20100822 rts created
* 20130216 rts modified imports
*******************************************************/
import com.shanebow.dao.CheckOutID;
import com.shanebow.dao.DataFieldException;
import com.shanebow.tools.fileworker.FileLineParser;
import com.shanebow.util.CSV;
import com.shanebow.ui.SBDialog;
import com.shanebow.util.MessageLogger;
import com.shanebow.util.SBLog;
import com.apo.contact.Raw;
import com.apo.net.SysDAO;
import java.util.*;

final class CheckOutUndoLineParser
	implements FileLineParser
	{
	private static final String MODULE="Undo Check Out";

	private Set<CheckOutID> fPages = new TreeSet<CheckOutID>();

	private int            m_errCount = 0; // running count of errors/exceptions
	private MessageLogger  logTrace = null;

	CheckOutUndoLineParser(MessageLogger logTrace)
		{
		this.logTrace = logTrace;
		}
 
	public int getErrorCount()     { return m_errCount; }
	public int getDuplicateCount() { return FileLineParser.DUPS_NA; }

	private void appLog ( String format, Object... args )
		{
		SBLog.write( MODULE, String.format( format, args ));
		}

	private void trace ( String format, Object... args )
		{
		logTrace.write( String.format( format, args ));
		}

	public boolean checkHeaders( String text )
		{
		if ( !text.equals(Raw.ID_HEADER))
			return SBDialog.error( MODULE, "Header does not match Check Out file format" );

		return true;
		}

	public void beginProcessing() throws Exception
		{
		}

	public void endProcessing(int line, int totalLines )
		throws Exception
		{
		StringBuffer pageList = new StringBuffer();
		for ( CheckOutID page : fPages )
			pageList.append(page).append(",");
		pageList.deleteCharAt(pageList.length()-1);
		String pageCSV = pageList.toString();
		trace("Checking in pages: " + pageCSV);
		String pageFieldName = Raw.dbField(Raw.PAGE);
		String sql = "UPDATE " + Raw.DB_TABLE
		           + " SET " + pageFieldName + " = 0"
		           + " WHERE " + pageFieldName + " IN (" + pageCSV + ")";
		SysDAO.DAO().sqlUpdate(sql);

		String msg = MODULE
		           + "\nFound Pages: " + pageCSV
		           + "\n*** Processed " + line + " of " + totalLines + " lines"
		           + "\n*** Errors: " + m_errCount;
		appLog(msg);
		}

	public void processLine(int line, String text) throws Exception
		{
		try
			{
			if ( text.isEmpty())
				return;
			Raw raw = new Raw(text);
			fPages.add(raw.page());
			}
		catch (DataFieldException e)
			{
			trace( "Line #%d, Error #%d: %s", line, ++m_errCount, e.toString());
			}
		catch (Exception t)
			{
			++m_errCount;
			trace( "Line #%d, Error #%d: %s", line, m_errCount, t.toString());
			trace( "csv: " + text );
			throw t;
			}
		}
	}
