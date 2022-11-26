package com.apo.contact;
/********************************************************************
* @(#)HTRCount.java 1.00 10/06/23
* Copyright (c) 2010 by Richard T. Salamone, Jr. All rights reserved.
*
* HTRCount: The 'Hard to Reach' counter for a contact - If the HTR
* exceeds the threshhold the contact is dispositioned as HTR.
*
* @version 1.00 06/23/10
* @author Rick Salamone
* 20101128 RTS added constant for default max htr
*******************************************************/
import com.shanebow.dao.DataField;
import com.shanebow.dao.DataFieldException;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class HTRCount
	implements DataField, Comparable<HTRCount>
	{
	public static final HTRCount DEFAULT_MAX_HTR = new HTRCount(5);
	public static final HTRCount ZERO_HTR = new HTRCount(0);

	public static HTRCount read(ResultSet rs, int rsCol)
		throws DataFieldException
		{
		try { return new HTRCount(rs.getInt(rsCol)); }
		catch (SQLException e) { throw new DataFieldException(e); }
		}

	public static HTRCount parse(String input)
		throws DataFieldException
		{
		int count = 0;
		try
			{
			String trimmed = input.trim();
			if ( !trimmed.isEmpty())
				count = Integer.parseInt(trimmed);
			}
	  catch (Exception e)
	    	{
       	throw new DataFieldException("HTR count - " + NUMBER_REQD + input);
	    	}
		return new HTRCount(count);
		}

	protected int m_value;
	private HTRCount ( int value )
		{
		m_value = value;
		}

	@Override public int compareTo(HTRCount other)
		{
		return m_value - other.m_value;
		}

	@Override public boolean equals(Object other)
		{
		if ( other == null ) return false;
		if ( other instanceof HTRCount )
			return ((HTRCount)other).m_value == this.m_value;
		else if ( other instanceof Number )
			return ((Number)other).intValue() == this.m_value;
		else return false;
		}

	public void    reset() { m_value = 0; }
	public int     increment() { return ++m_value; }
	public int     get() { return m_value; }
	public void    set(int x) { m_value = x; }
	public String  toString() { return (m_value <= 0) ? "" : "" + m_value; }
	public boolean isEmpty() { return m_value <= 0; }
	public String  csvRepresentation() { return toString(); }
	public String  dbRepresentation()  { return "" + m_value; }
	public int     intValue() { return m_value; }
	}
