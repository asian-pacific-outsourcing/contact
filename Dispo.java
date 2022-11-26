package com.apo.contact;
/********************************************************************
* @(#)Dispo.java	1.00 10/05/30
* Copyright (c) 2010-2011 by Richard T. Salamone, Jr. All rights reserved.
*
* Dispo: A contact disposition. Stroed in the contact data table
* as an integer code, but displayed to users as a short (1 - 3 characters)
* string.
*
* @author Rick Salamone
* @version 1.00 20100530 rts created
* @version 1.01 20100604 RTS Added disposition XC
* @version 1.02 20100607 RTS Added disposition for hard to reach
* @version 1.03 20100622 RTS added static parse & now extends DataField
* @version 1.04 20100705 RTS Added disposition UD
* @version 1.05 20100727 RTS moved array of TQ_DISPOS here for access by server
* @version 1.06 20101021 RTS added vero dispositions
* @version 1.07 20101117 RTS added get/setCallbackDelay
* @version 1.08 20101125 RTS added resetsHTR
* @version 1.09 20110121 RTS added dbCriteriaList
* @version 1.10 20110203 RTS added AO & LO dispositions (best guess)
* @version 1.11 20110316 RTS removed arrays of dispos (use lists in Role objects)
* @version 1.12 20110325 RTS refined AO dispos based on user feedback (UNC, KOL, TOL)
* @version 1.13 20110404 RTS added RMP, ADP to handle order flow thru admin & RM
* @version 1.14 20110405 RTS added CO for canceled order
* @version 1.15 20110420 RTS made all AO/LO dispos NOT increment HTR
* @version 1.16 20110506 RTS consolidated dispos and added color
* @version 1.17 20110604 rts implements hashCode for binarySearch in SBArray
*******************************************************/
import com.shanebow.dao.DataField;
import com.shanebow.dao.DataFieldException;
import com.shanebow.util.SBArray;
import java.awt.Color;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class Dispo
	implements DataField, Comparable<Dispo>
	{
	public static final Dispo[] NO_DISPOS = new Dispo[] {};
	private static final SBArray<Dispo> _all = new SBArray<Dispo>(23);

	public static final Dispo TOL = new Dispo(-10, "TOL", Color.PINK,   false, true,  "Take Off List");
	public static final Dispo UNC = new Dispo( -8, "UNC", Color.RED,    false, false, "Uncontactable");
	public static final Dispo UD  = new Dispo( -7,  "UD", Color.RED,    false, false, "Undesirable Data");
	public static final Dispo BD  = new Dispo( -3,  "BD", Color.RED,    false, false, "Dead, NS, or Left Co");
	public static final Dispo NI  = new Dispo( -2,  "NI", Color.RED,    false, false, "Not Interested");
//	public static final Dispo PD  = new Dispo(  1,  "PD", false, false, "Possible Duplicate");
	public static final Dispo XX  = new Dispo(  0,  "__", Color.WHITE,  false, false, "New Data");
	public static final Dispo CB  = new Dispo(  3,  "CB", Color.LIGHT_GRAY, true,  false, "TQ Call Back");
	public static final Dispo XC  = new Dispo(  4,  "XC", Color.LIGHT_GRAY, true,  false, "No Connection");
	public static final Dispo L   = new Dispo(100,  "QL", Color.YELLOW, false, true,  "Qualified Lead");
	public static final Dispo VCB1= new Dispo(102,"VCB1", Color.ORANGE, true,  false, "Ting Call Back");
	public static final Dispo VCB = new Dispo(103, "VCB", Color.ORANGE, true,  false, "VO Call Back");
	public static final Dispo BR  = new Dispo(170,  "BR", Color.ORANGE, false, true, "Brochure Requested");
	public static final Dispo VOL = new Dispo(200,  "VL", Color.CYAN,   false, true,  "Verified Lead");
	public static final Dispo KOL = new Dispo(202, "KOL", Color.GRAY,   false, true,  "Keep On List");
	public static final Dispo ACB = new Dispo(203, "ACB", Color.CYAN,   false, false, "AO Call Back");
	public static final Dispo CO  = new Dispo(240,  "CO", Color.DARK_GRAY, false, false, "Canceled Order");
	public static final Dispo AOP = new Dispo(250, "AOP", Color.BLUE,   false, false, "AO Pending Order");
	public static final Dispo RMP = new Dispo(260, "RMP", new Color(0,0, 80), false, false, "RM Pending Order");
	public static final Dispo ADP = new Dispo(270, "ADP", new Color(0,0,160), false, false, "ADM Pending Order");
	public static final Dispo AOF = new Dispo(280, "AOF", new Color(0,128,0), false, true,  "AO Filled Order");
	public static final Dispo LI  = new Dispo(300, "LI",  Color.GREEN,  false, true,  "LO Intro");
	public static final Dispo LCB = new Dispo(303, "LCB", Color.MAGENTA, false, false, "LO Call Back");

	public  static final int countAll() { return _all.capacity(); }
	public  static final Iterable<Dispo> getAll()  { return _all; }

	public  static final Dispo[] ALL_DISPOS =
		{
//BD, UD, NI, PD, XX, VM, CB, XC, DC, L, VCB1, VCB, BR, VOL,
BD, UD, NI, XX, XC, CB, L, VCB1, VCB, BR, VOL,
UNC, CO, ACB, TOL, KOL, AOP, RMP, ADP, AOF, LI, LCB
		};

	public static Dispo parse( String text )
		throws DataFieldException
		{
		if (text == null)
			return Dispo.XX;
		String trimmed = text.trim().toUpperCase();
		if ( trimmed.isEmpty() || trimmed.equals("0"))
			return XX;
		try { return find(Integer.parseInt(trimmed)); }
		catch(Exception e) {}
		for ( Dispo dispo : _all )
			if ( dispo.fCode.equals(trimmed))
				return dispo;
		throw new DataFieldException("Dispo - " + BAD_LOOKUP + text);
		}

	public static Dispo read(ResultSet rs, int rsCol)
		throws DataFieldException
		{
		try { return find(rs.getInt(rsCol)); }
		catch (SQLException e) { throw new DataFieldException(e); }
		}

	public static Dispo findSlow(int id)
		{
		for ( Dispo dispo : _all )
			if ( dispo.fID == id )
				return dispo;
		return Dispo.XX;
		}

	public static Dispo find(int id)
		{
		int index = _all.binarySearch(id);
		return (index < 0)? Dispo.XX : _all.get(index);
		}

	/** setCallbackDelays
	* Sets the default callback delays for several dispos from a
	* comma separated list of dispo names and call back delays
	* @param csv has the format is dispo1name,dispo1delay,dispo2name,
	*            dispo2delay,... so for example, L,30,VOL,600
	*            specifies to call back L's after 30 minutes and
	*            VOL's after 600 minutes
	* @return true if successful, false if any problem
	*/
	public static boolean setCallbackDelays(String csv)
		{
		if ( csv == null ) return false;
		String[] pieces = csv.split(",");
		try
			{
			for ( int i = 0; i < pieces.length; i += 2 )
				{
				Dispo dispo = Dispo.parse(pieces[i]);
				if ( !dispo.equals(Dispo.XX))
					dispo.setCallbackDelay(Long.parseLong(pieces[i+1]) * 60);
				}
			return true;
			}
		catch (Exception e) { return false; }
		}

	public static Dispo[] csvToArray(String csv)
		{
		try
			{
			if ( csv == null || csv.isEmpty())
				return NO_DISPOS;
			String[] pieces = csv.split(",");
			Dispo[] it = new Dispo[pieces.length];
			for ( int i = 0; i < pieces.length; i++ )
				it[i] = Dispo.parse(pieces[i]);
			return it;
			}
		catch (Exception e) { return NO_DISPOS; }
		}

	public static String arrayToCSV( Dispo[] dispos )
		{
		String it = "";
		if ( dispos != null )
			for ( int i = 0; i < dispos.length; i++ )
				it += ((i == 0)? "" : ",") + dispos[i].dbRepresentation();
		return it;
		}

	public static String dbCriteriaList( Dispo[] dispos )
		{
		String it = arrayToCSV(dispos);
		return it.isEmpty()? it : ("(" + it + ")");
		}

	private final int fID;
	private final String  fCode; // the acronym
	private final String  fName; // the full name
	private final Color   fColor;
	private final boolean fIsHTR; // does this dispo increment hard to reach?
	private final boolean fResetsHTTR; // does this dispo reset hard to reach?
	private long fCallbackDelay = 0; // in seconds

	private Dispo( int aID, String aCode, Color aColor,
		boolean aIsHTR, boolean aResetsHTR, String aName )
		{
		fID = aID;
		fCode = aCode;
		fColor = aColor;
		fName = aName;
		fIsHTR = aIsHTR;
		fResetsHTTR = aResetsHTR;
		_all.add( this );
		}

	@Override public int compareTo(Dispo other)
		{
		return fID - other.fID;
		}

	@Override public boolean equals(Object other)
		{
		if ( other == null ) return false;
		if ( !(other instanceof Dispo)) return false;
		return ((Dispo)other).fID == this.fID;
		}

	@Override public int hashCode() { return fID; }
	public Color getColor() { return fColor; }
//	public long getCallbackDelay() { return fCallbackDelay; }
	public void setCallbackDelay(long seconds) { fCallbackDelay = seconds; }

	public boolean isEmpty()   { return false; } // (fID == 0); }
	public boolean isHTR()     { return fIsHTR; }
	public boolean resetsHTR() { return fResetsHTTR; }
	public String toString()   { return fCode; }
	public int id() { return fID; }
	public String name() { return fName; }
	public String nameAndCode() { return fCode + " (" + fName + ")"; }
	public String csvRepresentation() { return "" + fID; }
	public String dbRepresentation()  { return "" + fID; }
	}
