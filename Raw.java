package com.apo.contact;
/********************************************************************
* @(#)Raw.java	1.00 10/04/07
* Copyright (c) 2010 by Richard T. Salamone, Jr. All rights reserved.
*
* Raw: The database fields for a contact
*
* @author Rick Salamone
* @version 1.00, 20100407 rts created
* 20100522 RTS finalied field names & csv order for version 1
* 20100525 RTS added CSV_HEADER and csvSplit to handle csv files
* 20100622 RTS added read method to handle field ResultSet data
* 20100626 RTS finalied parse() method to return DataField
* 20100627 RTS added combobox type editors for source, country, region
* 20100818 RTS moved private class FieldMeta out to share with CallFields
* 20101003 RTS added altPhone field
* 20110315 RTS added constant for BLANK Raw object
*******************************************************/
import com.shanebow.util.CSV;
import com.apo.contact.edit.EditHTRCount;
import com.apo.contact.edit.EditSource;
import com.apo.contact.edit.ShowDispo;
import com.shanebow.dao.*;
import com.shanebow.dao.edit.*;
import java.sql.ResultSet;
import javax.swing.JComponent;

public final class Raw
	{
	public static RawDAO DAO;
	public static final String DB_TABLE = "raw";

	public static final String DOUBLECLICK_PROPERTY="rawContactDoubleClik";
	public static final String SELECTED_PROPERTY="rawContactSelected";
	public static String NO_ID_HEADER= "name,position,company,phone,mobile,altPhone,"
	                               + "email,address,countryID,type,disposition,"
	                               + "sourceID,regionID,noAnswer,callBack,page,website";

	public static String ID_HEADER= "id," + NO_ID_HEADER;
	public static String DMCSV_HEADER="control,miner,host," + ID_HEADER;
	public static String TQCSV_HEADER="control,tqer," + ID_HEADER;

	public static final int        ID = 0;
	public static final int      NAME = 1;
	public static final int  POSITION = 2;
	public static final int   COMPANY = 3;
	public static final int     PHONE = 4;
	public static final int    MOBILE = 5;
	public static final int  ALTPHONE = 6;
	public static final int     EMAIL = 7;
	public static final int   ADDRESS = 8;
	public static final int COUNTRYID = 9;  // Simple Lookup
	public static final int      TYPE = 10;
	public static final int     DISPO = 11; // Simple Lookup
	public static final int  CALLER = 12; // Into source Lookup
	public static final int HOMELAND = 13;
	public static final int  HTRCOUNT = 14;
	public static final int  CALLBACK = 15; // date
	public static final int      PAGE = 16; // long
	public static final int   WEBSITE = 17;

	public static final FieldMeta[] meta =
		{ // number, label, dbFieldName, class, editor class, tooltip
		new FieldMeta( ID, "#", "id", ContactID.class,
		                            EditContactID.class, "Unique Contact Number" ),
		new FieldMeta( NAME, "Name", "name", ContactName.class,
		                            EditName.class, "Contact Name" ),
		new FieldMeta( POSITION, "Position", "position", Position.class,
		                            EditPosition.class, "Contact's Job or Position" ),
		new FieldMeta( COMPANY, "Company", "company", Company.class,
		                            EditCompany.class, "Company" ),
		new FieldMeta( PHONE, "Phone", "phone", PhoneNumber.class,
		                            EditPhone.class, "Primary Phone number" ),
		new FieldMeta( MOBILE, "Mobile", "mobile", PhoneNumber.class,
		                            EditPhone.class, "Mobile Phone number" ),
		new FieldMeta( ALTPHONE, "AltPhone", "altphone", PhoneNumber.class,
		                            EditPhone.class, "Alternate Phone number" ),
		new FieldMeta( EMAIL, "eMail", "email", EMailAddress.class,
                                 EditEMailAddress.class, "e-Mail Address" ),
		new FieldMeta( ADDRESS, "Address", "address", Address.class,
		                            EditAddress.class, "Mailing Address" ),
		new FieldMeta( COUNTRYID, "Country", "countryID", Country.class,
		                            EditCountry.class, "Country of residence" ),
		new FieldMeta( TYPE, "Type", "type", ContactType.class,
		                            EditType.class, "Contact Type" ),
		new FieldMeta( DISPO, "Status", "disposition", Dispo.class,
		                            ShowDispo.class, "Status of this contact" ),
		new FieldMeta( CALLER, "Caller", "sourceID", Source.class,
		                            EditSource.class, "Employee assigned to this contact" ),
		new FieldMeta( HOMELAND, "Nationality", "regionID", Country.class,
		                            EditCountry.class, "Contact's Nationality" ),
		new FieldMeta( HTRCOUNT, "HTR", "noAnswer", HTRCount.class,
                                 EditHTRCount.class, "Hard to Reach counter" ),
		new FieldMeta( CALLBACK, "CallBack", "callBack", When.class,
		                            EditWhen.class, "Call back on or before date" ),
		new FieldMeta( PAGE, "Page", "page", CheckOutID.class,
		                            EditCheckOutID.class, "Check Out Page Number" ),
		new FieldMeta( WEBSITE, "WebSite", "WebSite", WebAddress.class,
                                 EditWebAddress.class, "Company's Web Site" ),
		};
	public static final int  NUM_FIELDS = meta.length;
	public static final int  NUM_DB_FIELDS = NUM_FIELDS;

	public static Object getDefaultValue(int field) { return (field==ID)? "" : "0"; }
	public static int    getNumFields()        { return meta.length; }
	public static String getToolTip(int i)     { return meta[i].toolTip(); }
	public static String getLabelText(int i)   { return meta[i].label(); }
	public static String dbField(int i) { return meta[i].dbFieldName(); }
	public static String dbFieldName(int i)    { return meta[i].dbFieldName(); }
	public static JComponent getEditor(int i)  { return meta[i].editor(); }
	public static Class<? extends DataField> getFieldClass(int i)
		{
		return meta[i].getFieldClass();
		}
	public static int dbFieldIndex( String dbFieldName )
		{
		for ( int i = 0; i < meta.length; i++ )
			if ( dbFieldName.equalsIgnoreCase(meta[i].dbFieldName()))
				return i;
		return -1;
		}

	/**
	* This is a special Raw to be used whenever an emply record is needed
	* Note: causes null pointer if moved above the meta data in the file
	*/
	public static final Raw BLANK = new Raw(	ContactID.NEW_CONTACT, (ContactName)null,
	            (Position) null, (Company) null,
//	            PhoneNumber aPhone, PhoneNumber aMobile, PhoneNumber aAltPhone,
						null, null, null,
	            EMailAddress.BLANK, Address.BLANK,	(Country)null,
	            ContactType.BIZ, Dispo.XX, Source.XX,
	            Country.XX, HTRCount.ZERO_HTR, new When(0),
						CheckOutID.CHECKED_IN, WebAddress.BLANK );

	public static DataField parse( int fieldIndex, String value )
		throws DataFieldException
		{
		switch ( fieldIndex )
			{
			case ID:          return ContactID.parse(value);
			case NAME:        return ContactName.parse(value);
			case POSITION:    return Position.parse(value);
			case COMPANY:     return Company.parse(value);
			case PHONE:
			case MOBILE:
			case ALTPHONE:    return PhoneNumber.parse(value);
			case EMAIL:       return EMailAddress.parse(value);
			case ADDRESS:     return Address.parse(value);
			case COUNTRYID:   return Country.parse(value);
			case TYPE:        return ContactType.parse(value);
			case DISPO:       return Dispo.parse(value);
			case CALLER:      return Source.parse(value);
			case HOMELAND:    return Country.parse(value);
			case HTRCOUNT:    return HTRCount.parse(value);
			case CALLBACK:    return When.parse(value);
			case PAGE:        return CheckOutID.parse(value);
			case WEBSITE:     return WebAddress.parse(value);
			}
		throw new DataFieldException( "" + fieldIndex + ": No parser available" );
		}

	public static DataField read( int fieldIndex, ResultSet rs, int rsCol )
		throws DataFieldException
		{
		switch ( fieldIndex )
			{
			case ID:          return ContactID.read( rs, rsCol);
			case NAME:        return ContactName.read( rs, rsCol);
			case POSITION:    return Position.read( rs, rsCol);
			case COMPANY:     return Company.read( rs, rsCol);
			case PHONE:
			case MOBILE:
			case ALTPHONE:    return PhoneNumber.read( rs, rsCol);
			case EMAIL:       return EMailAddress.read( rs, rsCol);
			case ADDRESS:     return Address.read( rs, rsCol);
			case COUNTRYID:   return Country.read( rs, rsCol);
			case TYPE:        return ContactType.read( rs, rsCol);
			case DISPO:       return Dispo.read( rs, rsCol);
			case CALLER:      return Source.read( rs, rsCol);
			case HOMELAND:    return Country.read( rs, rsCol);
			case HTRCOUNT:    return HTRCount.read( rs, rsCol);
			case CALLBACK:    return When.read( rs, rsCol);
			case PAGE:        return CheckOutID.read( rs, rsCol);
			case WEBSITE:     return WebAddress.read( rs, rsCol);
			}
		throw new DataFieldException("Invalid field index: " + fieldIndex );
		}

	private final DataField fFields[] = new DataField[meta.length];
	public DataField    get(int field){ return fFields[field]; }
	public DataField[]  getFields(){ return fFields; }

	/**
	* Returns a defensive copy of all fields
	*/
	public DataField[]  getFieldsDefensive()
		{
		DataField[] copy = new DataField[fFields.length];
		for ( int i = 0; i < fFields.length; i++ )
			copy[i] = getDefensiveCopy(i);
		return copy;
		}

	/**
	* Returns a defensive copy of a field or null if a problem
	*/
	public DataField getDefensiveCopy(int field)
		{
		try { return parse(field, fFields[field].toString()); }
		catch ( Exception e ) { return null; }
		}

	/**
	* For development only: Pops up a dialog showing all the field values.
	*/
	public void dump( String title )
		{
		String msg = "<HTML>";
		for ( int i = 0; i < NUM_FIELDS; i++ )
			msg += "<B>" + getLabelText(i) + ":</B> " + fFields[i] + "<BR>";
		com.shanebow.ui.SBDialog.error(title, msg);
		}

	public ContactID    id()          { return (ContactID)fFields[ID]; }
	public ContactName  name()        { return (ContactName)fFields[NAME]; }
	public Position     position()    { return (Position)fFields[POSITION]; }
	public Company      company()     { return (Company)fFields[COMPANY]; }
	public PhoneNumber  phone()       { return (PhoneNumber)fFields[PHONE]; }
	public PhoneNumber  mobile()      { return (PhoneNumber)fFields[MOBILE]; }
	public PhoneNumber  altPhone()    { return (PhoneNumber)fFields[ALTPHONE]; }
	public EMailAddress eMail()       { return (EMailAddress)fFields[EMAIL]; }
	public Address      address()     { return (Address)fFields[ADDRESS]; }
	public Country      country()     { return (Country)fFields[COUNTRYID]; }
	public ContactType  contactType() { return (ContactType)fFields[TYPE]; }
	public Dispo        dispo()       { return (Dispo)fFields[DISPO]; }
	public Source       source()      { return (Source)fFields[CALLER]; }
	public Source       assignedTo()  { return (Source)fFields[CALLER]; }
	public Country      nationality() { return (Country)fFields[HOMELAND]; }
	public HTRCount     htr()         { return (HTRCount)fFields[HTRCOUNT]; }
	public When         callback()    { return (When)fFields[CALLBACK]; }
	public CheckOutID   page()        { return (CheckOutID)fFields[PAGE]; }
	public WebAddress   webSite()     { return (WebAddress)fFields[WEBSITE]; }

	public Raw(	DataField[] aFields)
		{
		for ( int i = 0; i < fFields.length; i++ )
			fFields[i] = aFields[i];
		}

	public Raw(	ContactID aID, ContactName aName,
	            Position aPosition, Company aCompany,
	            PhoneNumber aPhone, PhoneNumber aMobile, PhoneNumber aAltPhone,
	            EMailAddress aEMail, Address aAddress,	Country aCountry,
	            ContactType aType, Dispo aDispo, Source aSource,
	            Country aNationality, HTRCount aHTR, When aCallback,
						CheckOutID aPage, WebAddress aWebSite )
		{
		fFields[ID] = aID;
		fFields[NAME] = aName;
		fFields[POSITION] = aPosition;
		fFields[COMPANY] = aCompany;
		fFields[PHONE] = aPhone;
		fFields[MOBILE] = aMobile;
		fFields[ALTPHONE] = aAltPhone;
		fFields[EMAIL] = aEMail;
		fFields[ADDRESS] = aAddress;
		fFields[COUNTRYID] = aCountry;
		fFields[TYPE] = aType;
		fFields[DISPO] = aDispo;
		fFields[CALLER] = aSource;
		fFields[HOMELAND] = aNationality;
		fFields[HTRCOUNT] = aHTR;
		fFields[CALLBACK] = aCallback;
		fFields[PAGE] = aPage;
		fFields[WEBSITE] = aWebSite;
		}

	public Raw(	String csv )
		throws DataFieldException
		{
		this(CSV.split(csv, meta.length));
		}

	public Raw(	String[] value )
		throws DataFieldException
		{
		for ( int i = 0; i < fFields.length; i++ )
			fFields[i] = parse( i, value[i] );
		}

	public Raw(	ResultSet rs )
		throws DataFieldException
		{
		for ( int i = 0; i < fFields.length; i++ )
			fFields[i] = read( i, rs, i + 1 );
		}

	/**
	* This constructor is used for postponed calls and
	* assigning the contact to a specific employee.
	*/
	public Raw(	Raw aOther, Source aSource, Dispo aDispo, When aCallback )
		{
		for ( int i = 0; i < fFields.length; i++ )
			fFields[i] = aOther.fFields[i];
		if ( aSource != null )
			fFields[CALLER] = aSource;
		if ( aDispo != null )
			fFields[DISPO] = aDispo;
		if ( aCallback != null )
			fFields[CALLBACK] = aCallback;
		}

	public String toCSV()
		{
		String csv = "";
		for ( int i = 0; i < fFields.length; i++ )
			{
			if ( i > 0 ) csv += ",";
			if ( fFields[i] != null )
				csv += fFields[i].csvRepresentation();
			}
		return csv;
		}

	@Override public boolean equals(Object that)
		{
		return that != null && that instanceof Raw
		    && ((Raw)that).id().equals(this.id());
		}

	public String title()
		{
		return "#" + get(ID) + " " + get(NAME) + " in " + get(COUNTRYID);
		}

	@Override public String toString()
		{
		return title();
		}
	}
