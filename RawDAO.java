package com.apo.contact;
/********************************************************************
* @(#)RawDAO.java 1.00 20110208
* Copyright (c) 2011 by Richard T. Salamone, Jr. All rights reserved.
*
* RawDAO: An interface that defines IO methods for Raw contact information.
* Concrete implementations should be written to access data directly from
* the database, over the network, or via a file.
*
* @author Rick Salamone
* 20110208 rts initial version
* 20110428 rts added keepAlive
* 20110606 rts added nextCheckOutPage
*******************************************************/
import com.apo.contact.Raw;
import com.apo.contact.Source;
import com.apo.contact.touch.TouchCode;
import com.shanebow.dao.*;
import com.shanebow.util.SBLog;
import java.util.List;

public abstract class RawDAO
	{
	public abstract long getServerTime();

	public abstract void keepAlive()
		throws DataFieldException;

	/**
	* Intended to be called at app exit - Make a best effort
	* to close db/net/file connections.
	*/
	public abstract void shutdown();
	public abstract void addRaw( Raw aRaw, EmpID aEmpID,
		TouchCode aTouchCode, String aSource )
		throws DataFieldException;
	public abstract ContactID addLead(Raw raw, EmpID aEmpID,
		TouchCode aTouchCode, String aSource )
		throws DataFieldException, DuplicateException;
	public abstract void assign( Source aTo, Source aBy, String aCsvRawIDs )
		throws DataFieldException;
	public abstract int countWork(String aCriteria)
		throws DataFieldException;
	public abstract boolean delete(ContactID id)
		throws DataFieldException;
	public abstract Raw fetch(ContactID id)
		throws DataFieldException;

	public abstract void checkOut(List<Raw> aList, int aMaxRecords, int aPerPage,
		String aWhereClause, short uid)
		throws DataFieldException;
	/**
	* @TODO: The query must be a complete sql statement, but if it doesn't
	* begin with 'SELECT * FROM Raw.DB_TABLE' it will crash!!
	*/
	public abstract void fetch(List<Raw> aList, int maxRecords, String query)
		throws DataFieldException;
	public abstract Raw getWork(String aCriteria)
		throws DataFieldException;
	public abstract void reqEmail(ContactID aRawID, EMailAddress aEmail)
		throws DataFieldException;
	public abstract void mailReq(Comment aDesc, When aTime, EmpID aEmpID, ContactID aRawID)
		throws DataFieldException;
	public abstract boolean supportsDelete();
	public abstract void release( Raw aRaw )
		throws DataFieldException;
	public abstract void sentMail( Comment aDesc, When aWhenSent, Source aSentBy,
		boolean aScheduleCall, String aCsvRawIDs )
		throws DataFieldException;
	public abstract void update( Raw aRaw, boolean aReleaseLock,
		TouchCode aTouchCode, String touchDetails, long when, short uID )
		throws DataFieldException;
	public abstract long nextCheckOutPage()
		throws DataFieldException;

	public static final String makeList( DataField... fields )
		{
		String it = "";
		for ( DataField f : fields )
			if ( !f.isEmpty())
				{
				if ( !it.isEmpty()) it += ",";
				it += f.dbRepresentation();
				}
		return it;
		}

	public final void assign( Source aAssignedTo, Source aAssignedBy, Raw aRaw )
		throws DataFieldException
		{
		assign( aAssignedTo, aAssignedBy, aRaw.id().toString());
		}

	// Logging support
	private final String MODULE = getClass().getSimpleName();
	private static final String SEPARATOR="==================================================";
	protected String lastError = "";

	public final String getLastError() { return lastError; }

	protected final void log( String fmt, Object... args )
		{
		SBLog.write( MODULE, String.format(fmt, args));
		}

	protected final void logSeparate( String msg )
		{
		SBLog.write( SEPARATOR );
		SBLog.write( MODULE, msg );
		}

	protected final boolean logError( String msg )
		{
		java.awt.Toolkit.getDefaultToolkit().beep();
		lastError = msg;
		SBLog.error(MODULE + " ERROR", msg );
		return false;
		}

	protected final boolean logSuccess()
		{
		lastError = "";
		SBLog.write(MODULE, "Success" );
		return true;
		}
	}
