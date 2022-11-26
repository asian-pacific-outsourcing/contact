package com.apo.contact.touch;
/********************************************************************
* @(#)TouchDAO.java 1.00 20110208
* Copyright (c) 2011 by Richard T. Salamone, Jr. All rights reserved.
*
* TouchDAO: An interface that defines IO methods for contact Touch information.
* Concrete implementations should be written to access data directly from
* the database, over the network, or via a file.
*
* @author Rick Salamone
* @version 1.00, 20110208 rts initial version
*******************************************************/
import com.apo.contact.touch.Touch;
import com.shanebow.dao.Comment;
import com.shanebow.dao.ContactID;
import com.shanebow.dao.DataFieldException;
import com.shanebow.dao.When;
import com.shanebow.util.SBLog;
import java.util.List;

public abstract class TouchDAO
	{
	abstract public long getServerTime();

	abstract public void add(Touch aTouch)
		throws DataFieldException;

	abstract public List<Touch> fetch(ContactID aID)
		throws DataFieldException;

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
