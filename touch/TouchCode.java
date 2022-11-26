package com.apo.contact.touch;
/********************************************************************
* @(#)TouchCode.java	1.00 20101005
* Copyright (c) 2010-2011 by Richard T. Salamone, Jr. All rights reserved.
*
* TouchCode: Types of contact interactions, used to track contact history.
*
* @author Rick Salamone
* @version 1.00 20101005 RTS initial creation
* @version 1.01 20101011 RTS CHECKIN was same id as CHECKOUT, fixed
* @version 1.02 20101111 RTS added ASSIGNED
* @version 1.03 20110309 RTS added MAILSENT
* @version 1.04 20110309 RTS added privledge levels
*******************************************************/
import com.shanebow.dao.DataField;
import com.shanebow.dao.DataFieldException;
import com.shanebow.util.SBArray;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class TouchCode
	implements DataField, Comparable<TouchCode>
	{
	// @TODO: Move these privledge levels into a security package
	private static final long DM   = 0x0002; // run the Data Miner app
	private static final long RD   = 0x0004; // run the Raw Dispo app
	private static final long TQ   = 0x0008; // run the Telephone Qualifier app
	private static final long VO   = 0x0010; // run the Vero app
	private static final long AO   = 0x0020; // run the Account Opener app
	private static final long LO   = 0x0040; // run the Loader app
	private static final long MS   = 0x0080; // Mail Staff
	private static final long MGR  = 0x1000; // run as Manager
	private static final long ADM  = 0x2000; // run as Admin
	private static final long ALL  = DM|RD|TQ|VO|AO|LO|MS|MGR|ADM; // run as Admin

	private static final SBArray<TouchCode> _all = new SBArray<TouchCode>(24);
	public static final TouchCode XX        = new TouchCode((byte)  0, ALL, "--", null );
	public static final TouchCode MINED     = new TouchCode((byte)  1, DM, "Mined", " from " );
	public static final TouchCode IMPORTED  = new TouchCode((byte)  2, DM, "Imported", null );
	public static final TouchCode MODIFIED  = new TouchCode((byte)  3, ADM, "Data Modified", null );
	public static final TouchCode COMMENTED = new TouchCode((byte)  4, ALL, "Commented", null );
	public static final TouchCode CHECKOUT  = new TouchCode((byte)  5, TQ, "Checked Out", " to page " );
	public static final TouchCode CHECKIN   = new TouchCode((byte)  6, TQ, "Checked In", null );
	public static final TouchCode TQCALLED  = new TouchCode((byte)  7, TQ, "TQ Called", " dispoed as " );
	public static final TouchCode EMAILED   = new TouchCode((byte)  8, ALL, "eMailed", null );
	public static final TouchCode QUALIFIED = new TouchCode((byte)  9, TQ|VO|MS, "Qualified", null );
	public static final TouchCode VOCALLED  = new TouchCode((byte) 10, VO, "VO Called", null );
	public static final TouchCode MAILREQ   = new TouchCode((byte) 11, TQ|VO|AO|MS, "Brochure requested", null );
	public static final TouchCode VEROED    = new TouchCode((byte) 12, VO|AO, "Verified", null );
	public static final TouchCode MAILSENT  = new TouchCode((byte) 13, ALL, "Sent mail", null );
	public static final TouchCode ASSIGNED  = new TouchCode((byte) 14, AO|LO, "Assigned", " to " );
	public static final TouchCode IDBROAD   = new TouchCode((byte) 16, AO, "Broadway Added", null );
	public static final TouchCode MAILBAD   = new TouchCode((byte) 17, ADM|VO|MS, "Mail Returned", null );
	public static final TouchCode AOCALLED  = new TouchCode((byte) 20, AO, "AO Called", null );
	public static final TouchCode NEWORDER  = new TouchCode((byte) 22, AO|LO, "Order written", null );
	public static final TouchCode MODORDER  = new TouchCode((byte) 23, AO|LO, "Order modified", null );
	public static final TouchCode ACTORDER  = new TouchCode((byte) 24, AO|LO, "Order Activity", null );
	public static final TouchCode LOCALLED  = new TouchCode((byte) 30, LO, "LO Called", null );
	public static final TouchCode LOADED    = new TouchCode((byte) 32, LO, "Loaded", null );
	public static final TouchCode EMAILCHG  = new TouchCode((byte) 33, VO|AO|LO|MS, "Changed eMail Address", " was " );

	private static final String ERR_NOT_FOUND="Unrecognied TouchCode: "; 

	public  static final int countAll() { return _all.capacity(); }
	public  static final Iterable<TouchCode> getAll()  { return _all; }

	public static TouchCode parse( String text )
		throws DataFieldException
		{
		if (text == null)
			return TouchCode.XX;
		String trimmed = text.trim();
		if ( trimmed.isEmpty() || trimmed.equals("0"))
			return TouchCode.XX;
		try
			{
			byte id = Byte.parseByte(trimmed);
			return find(id);
			}
		catch(Exception e) {}
		for ( TouchCode code : _all )
			if ( code.m_name.equalsIgnoreCase(trimmed))
				return code;
		throw new DataFieldException(ERR_NOT_FOUND + text);
		}

	public static TouchCode read(ResultSet rs, int rsCol)
		throws DataFieldException
		{
		try { return find(rs.getByte(rsCol)); }
		catch (SQLException e) { throw new DataFieldException(e); }
		}

	public static TouchCode findSlow(byte id)
		{
		for ( TouchCode code : _all )
			if ( code.m_id == id )
				return code;
		return TouchCode.XX;
		}

	public static TouchCode find(byte id)
		{
		int index = _all.binarySearch(id);
		return (index < 0)? TouchCode.XX : _all.get(index);
		}

	private final byte m_id;
	private final long m_access;
	private final String m_name; // the full name
	private final String m_prefixDetails;
	private TouchCode( byte id, long access, String name, String prefixDetails )
		{
		m_id = id;
		m_access = access;
		m_name = name;
		m_prefixDetails = prefixDetails;
		_all.add( this );
		}

	public final boolean accessible(long aPrivledges)
		{
		return (aPrivledges & m_access) != 0;
		}

	@Override public int compareTo(TouchCode other)
		{
		return m_id - other.m_id;
		}

	@Override public boolean equals(Object other)
		{
		if ( other == null ) return false;
		if ( other instanceof TouchCode )
			return ((TouchCode)other).m_id == this.m_id;
		else if ( other instanceof Number )
			return ((Number)other).byteValue() == this.m_id;
		else return false;
		}

	@Override public int hashCode() { return m_id; }

	@Override public String toString() { return m_name; }
	public boolean isEmpty() { return (m_id == 0); }
	public byte id() { return m_id; }
	public String name() { return m_name; }
	public String prefixDetails() { return (m_prefixDetails == null)? " ** ": m_prefixDetails; }
	public String csvRepresentation() { return (isEmpty()? "" : "" + m_id); }
	public String dbRepresentation()  { return "" + m_id; }
	}
