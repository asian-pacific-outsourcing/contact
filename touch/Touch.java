package com.apo.contact.touch;
/********************************************************************
* @(#)Touch.java 1.00 20101004
* Copyright (c) 2010 by Richard T. Salamone, Jr. All rights reserved.
*
* Touch: Record of a single interaction with the contact. So, a Touch is
* one entry in the contact's history.
*
* @author Rick Salamone
* @version 1.00 20101004 rts created
* @version 1.01 20101020 rts add methods take uid as short instead of string
* @version 1.02 20101022 rts add fetchMostRecent and fetch contact list methods
* @version 1.03 20101023 rts details field now a Comment rather than a String
* @version 1.04 20101024 rts separated Touch into Touch & TouchDB subclass
* @version 1.05 20101024 rts removed sql and made csv & datafiekd constructors
*******************************************************/
import com.apo.contact.Source;
import com.shanebow.dao.*;
import com.shanebow.util.CSV;
import com.shanebow.util.SBDate;
import com.shanebow.util.SBLog;

public final class Touch
	{
	public static final String DB_TABLE = "touch";
	public static TouchDAO DAO;

	private ContactID  contactID;
	private When       when;
	private Source   source;
	private TouchCode  code;
	private Comment    details;

	public Touch( ContactID cid, When when, Source uid,
	               TouchCode code, Comment details )
		{
		this.contactID = cid;
		this.when      = when;
		this.source    = uid;
		this.code      = code;
		this.details   = details;
		}

	public Touch( String csv ) // inverse of toCSV()
		throws DataFieldException
		{
		String[] pieces = CSV.split(csv, 5);
		this.contactID = ContactID.parse(pieces[0]);
		this.when      =  When.parse(pieces[1]);
		this.source    =  Source.parse(pieces[2]);
		this.code      = TouchCode.parse(pieces[3]);
		this.details   =   Comment.parse(pieces[4]);
		}

	public String toCSV()
		{
		return  contactID.csvRepresentation()
		+ "," +      when.csvRepresentation()
		+ "," +    source.csvRepresentation()
		+ "," +      code.csvRepresentation()
		+ "," +   details.csvRepresentation();
		}

	void log( String fmt, Object... args )
		{
		SBLog.write( "Touch", String.format(fmt, args));
		}

	public ContactID  getContactID() { return contactID; }
	public When       getWhen()      { return when; }
	public Source   getSource()    { return source; }
	public TouchCode  getTouchCode() { return code; }
	public Comment    getDetails()   { return details; }

	public String toString()
		{
		return  code.csvRepresentation()
		+ "," +      when.getLong()
		+ "," + contactID.csvRepresentation()
		+ "," +    source.csvRepresentation()
		+ "," +   details.csvRepresentation();
		}

	public String formatted()
		{
		long time = when.getLong();
		String it = SBDate.DDD(time) + " " + SBDate.yyyymmdd__hhmmss(time)
		          + " Contact " + contactID + " " + code + " by " + source;
		if ( !details.isEmpty())
			it += code.prefixDetails() + details;
		return it;
		}

	public String html()
		{
		long time = when.getLong();
		return "<TR><TD>" + SBDate.DDD(time)
		     + "</TD><TD>" + SBDate.yyyymmdd__hhmmss(time)
		     + "</TD><TD>" + contactID
		     + "</TD><TD>" + code
		     + "</TD><TD>" + source
		     + "</TD><TD>" + (details.isEmpty()? "&nbsp"
		                   : code.prefixDetails() + details);
		}

	/****************************************************
	* THESE METHODS SHOULD ONLY BE CALLED FROM TouchDB  *
	****************************************************/
	public void setWhen(When when) { this.when = when; }
	public void setSource(Source sid) { source = sid; }
	}
