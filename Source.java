package com.apo.contact;
/********************************************************************
* @(#)Source.java	1.00 10/05/30
* Copyright (c) 2010 by Richard T. Salamone, Jr. All rights reserved.
*
* Source: Identifies the source of some data, usually an employee,
* but could be a contractor, or a program (e.g. crawler).
*
* @version 1.00 05/30/10
* @author Rick Salamone
* 20100623 RTS Created based on Dispo
* 20100923 RTS statically loads user list from User
*******************************************************/
import com.shanebow.dao.DataField;
import com.shanebow.dao.DataFieldException;
import com.apo.employee.User;
import com.shanebow.util.SBArray;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class Source
	implements DataField
	{
	private static final SBArray<Source> _all = new SBArray<Source>(1);
	public static final Source XX = new Source(  0, "--", 0 );
	static
		{
		for ( User usr : User.getAll())
			if ( usr != User.XX )
				new Source( usr.id(), usr.login(), usr.getPermissions());
		}

	public  static final int countAll() { return _all.capacity(); }
	public  static final Iterable<Source> getAll()  { return _all; }

	public static Source parse( String text )
		throws DataFieldException
		{
		if (text == null)
			return Source.XX;
		String trimmed = text.trim();
		if ( trimmed.isEmpty() || trimmed.equals("0"))
			return Source.XX;
		for ( Source usr : _all )
			if ( usr.m_name.equalsIgnoreCase(trimmed))
				return usr;
		try
			{
			int id = Integer.parseInt(trimmed);
			return find(id);
			}
		catch(Exception e) {}
		throw new DataFieldException("Source field - " + BAD_LOOKUP + text);
		}

	public static Source read(ResultSet rs, int rsCol)
		throws DataFieldException
		{
		try { return find(rs.getInt(rsCol)); }
		catch (SQLException e) { throw new DataFieldException(e); }
		}

	public static Source find(int id)
		{
		for ( Source src : _all )
			if ( src.m_id == id )
				return src;
		return Source.XX;
		}

	int    m_id;
	String m_name; // the user id
	long   m_access;
	private Source( int id, String name, long aAccess )
		{
		m_id = id;
		m_name = name;
		m_access = aAccess;
		_all.add( this );
		}

	public boolean isEmpty() { return (m_id == 0); }
	public String  toString() { return m_name; }
	public int     id() { return m_id; }
	public String  name() { return m_name; }
	public long    access() { return m_access; }
	public boolean accessible(long aRights) { return (aRights & m_access) != 0; }
	public String  csvRepresentation() { return "" + m_id; }
	public String  dbRepresentation()  { return "" + m_id; }
	}
