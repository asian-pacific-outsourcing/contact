package com.apo.contact.edit;
/********************************************************************
* @(#)RawPanel.java 1.00 20100407
* Copyright (c) 2010 by Richard T. Salamone, Jr. All rights reserved.
*
* RawPanel: A panel for editing and validating the fields of a
* Contact record.
*
* Programming Notes: field represents the ContactField # which is the
* the field's index 
*
* @version 1.00 04/07/10
* @author Rick Salamone
* 20100525 RTS Split out from DlgContact, general cleanup & debug
*              for use with Data Miner
* 20100526 RTS Added double click = paste functionality
* 20100616 RTS Uses two columns if lots of fields
* 20100623 RTS adapted to use FieldEditor
* 20101101 RTS validInputs takes a time argument (used only by subclasses)
* 20110331 RTS num cols as argument
*******************************************************/
import com.apo.contact.Raw;
import com.apo.contact.Dispo;
import com.shanebow.dao.DataField;
import com.shanebow.dao.Address;
import com.shanebow.dao.ContactID;
import com.shanebow.dao.EMailAddress;
import com.shanebow.dao.When;
import com.shanebow.dao.DataFieldException;
import com.shanebow.dao.edit.FieldEditor;
import com.shanebow.ui.LAF;
import com.shanebow.ui.SBDialog;
import com.shanebow.ui.layout.LabeledPairLayout;
import com.shanebow.util.SBLog;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class RawPanel
	extends JPanel
	{
	private static final int SECS_PER_DAY = 24 * 60 * 60;
	private static final int WARN_FAR_OUT_DAYS = 30;
	private static final int WARN_FAR_OUT = WARN_FAR_OUT_DAYS * SECS_PER_DAY;

	private final int[]        fRFNs;    // the contact data Raw Field Numbers in order
	private final JComponent[] fEditors; // a field editor for each RFN
	private Raw          fRaw;     // the model object for a contact's raw data
	// The record we're building
	private final DataField[] fEdits = new DataField[Raw.NUM_FIELDS];
	private boolean      fDirty;
	private boolean      fSettingContact;

	public RawPanel(byte nColumns, int[] fields, Dispo[] allowedDispos)
		{
		super();
		int mid = -1;
		for ( int i = 0; i < fields.length; i++ )
			if ( fields[i] == -1 ) mid = i;

		JPanel center = this; // if no dispo & fewer than 10 fields use one panel
		fRFNs = fields;
		int numFields = fRFNs.length;
		fEditors = new JComponent[numFields];
		boolean skipDispo = false;

		for ( int ffi = 0; ffi < numFields; ffi++ )
			{
			JComponent ed;
			int cfn = fRFNs[ffi];
			if ( cfn == -1 ) continue;
			if ( cfn == Raw.DISPO        // Special handling for editable
			&&   allowedDispos != null ) // dispo - put in BorderLayout.SOUTH
				{
				this.setLayout( new BorderLayout());
				EditDispo dispoEdit = new EditDispo(allowedDispos);
				ed = (JComponent)dispoEdit;
				skipDispo = true;
				ed.setBorder( BorderFactory.createTitledBorder(
										Raw.getLabelText(Raw.DISPO)));
				this.add(((JComponent)dispoEdit), BorderLayout.SOUTH );
				center = new JPanel(); // panel for the other fields
				}
			else ed = Raw.getEditor(cfn);
			fEditors[ffi] = ed;
			ed.addKeyListener(keyListener);
			((FieldEditor)ed).addActionListener(actionListener);
			ed.setToolTipText(Raw.getToolTip(cfn));
			}
		if ( mid != -1 ) // 2 column layout
			{
			center.setLayout( new GridLayout( 0, 2, 10, 20 ));
			JPanel p = new JPanel();
			addFields( p, 0, mid, skipDispo );
			center.add( p );
			p = new JPanel();
			addFields( p, mid, numFields, skipDispo );
			center.add( p );
			}
		else
			addFields( center, 0, numFields, skipDispo );
		if ( center != this )
			add( center, BorderLayout.CENTER );
		}

	private void addFields( JPanel p, int first, int last, boolean skipDispo )
		{
		p.setLayout( new LabeledPairLayout());
		for ( int i = first; i < last; i++ )
			{
			int cfn = fRFNs[i];

			if ( cfn == -1 ) continue;
			if ((cfn == Raw.DISPO) && skipDispo)
				continue; // this field was handled elsewhere
			p.add(new JLabel(Raw.getLabelText(cfn), JLabel.RIGHT), "label");
			p.add(fEditors[i], "field");
			}
		}

	private final ActionListener actionListener = new ActionListener()
		{
		@Override public final void actionPerformed(ActionEvent e)
			{
			if (!fSettingContact)
				setDirty(true);
			}
		};

	private final KeyAdapter keyListener = new KeyAdapter()
		{
		@Override public final void keyTyped(KeyEvent e) { setDirty(true); }
		};

	protected void setDirty(boolean on) { fDirty = on; }

	public boolean isDirty() { return fDirty; }

	private int indexOf( int aRFN )
		{
		for ( int i = 0; i < fRFNs.length; i++ )
			if ( fRFNs[i] == aRFN ) return i;
		return -1;
		}

	final public boolean includes( int aRFN )
		{
		return indexOf(aRFN) >= 0;
		}

	protected final JComponent getEditorComponent(int aRFN)
		{
		int ffi = indexOf(aRFN);
		return (ffi >= 0)? fEditors[ffi] : null;
		}

	private final FieldEditor getFieldEditor(int aRFN)
		{
		JComponent entryField = getEditorComponent(aRFN);
		return (entryField instanceof FieldEditor) ?
			(FieldEditor)entryField : null;
		}

	public void clearField( int aRFN )
		{
		FieldEditor entryField = getFieldEditor(aRFN);
		if ( entryField instanceof FieldEditor)
			entryField.clear();
		}

	public DataField get( int aRFN )
		throws DataFieldException
		{
		try
			{
			JComponent entryField = getEditorComponent(aRFN);
			if ( entryField != null ) // && entryField instanceof FieldEditor)
				fEdits[aRFN] = ((FieldEditor)entryField).get();
			return fEdits[aRFN];
			}
		catch (DataFieldException e ) { falseBecause ( e.toString()); throw e; }
		}

	public void setContact( Raw aRaw )
		{
		fSettingContact = true;
		fRaw = (aRaw == null)? Raw.BLANK : aRaw;
		for ( int i = 0; i < Raw.NUM_FIELDS; i++ )
			_set(i, fRaw.getDefensiveCopy(i));
		fSettingContact = false;
		fEditors[0].requestFocusInWindow();
		setDirty(false);
// new Raw(fEdits).dump("post setContact " + aRaw );
		}

	public void set( int aRFN, DataField aValue )
		{
		if ( fEdits[aRFN] == null || !fEdits[aRFN].equals(aValue))
			{
			_set( aRFN, aValue );
			setDirty(true);
			}
		else _set( aRFN, aValue );
		}

	private void _set( int aRFN, DataField aValue )
		{
		fEdits[aRFN] = aValue;
		FieldEditor ed = getFieldEditor(aRFN);
		if ( ed != null )
			ed.set(aValue);
		}

	public void setEnabled( int aRFN, boolean on )
		{
		JComponent entryField = getEditorComponent(aRFN);
		if ( entryField == null ) return;
		entryField.setEnabled(on);
		}

	public boolean isBlank( int aRFN )
		throws DataFieldException
		{
		if ( !includes(aRFN)) // user cannot be held responsible
			return false;      // for field that's not on the form
		DataField df = get(aRFN);
		return (df == null) || df.isEmpty();
		}

	private boolean badBlank(int aRFN)
		{
		return falseBecause( Raw.getLabelText(aRFN) + " cannot be blank" );
		}

	public boolean validInputs()
		{
		try
			{
			for ( int rfn : fRFNs )
				if ( rfn != -1 )
				get(rfn); // if invalid will throw an exception

			if ( isBlank(Raw.NAME))
				return badBlank(Raw.NAME);

			if ( isBlank(Raw.PHONE) && isBlank(Raw.MOBILE) && isBlank(Raw.ALTPHONE))
				return falseBecause( "At least one phone number required" );

			if ( isBlank(Raw.COUNTRYID))
				return badBlank(Raw.COUNTRYID);

			Dispo dispo = (Dispo)get(Raw.DISPO);
			if ( dispo != null && dispo.id() >= dispo.XX.id())
				{
				if ( includes(Raw.CALLBACK))
					{
					long serverTime = Raw.DAO.getServerTime();
					When callback = (When)get(Raw.CALLBACK);
					if ( dispo.id() >= Dispo.CB.id() )
						{
						if ( callback.isEmpty())
							return falseBecause( "Callback date required" );
						long callbackTime = callback.getLong();
						if ( callbackTime < serverTime )
							return falseBecause( "Callback must be in the future" );
						if ( callbackTime > (serverTime + WARN_FAR_OUT))
								return SBDialog.confirm( "Are you sure you want to schedule"
								  + " the callback for \n"
                     + ((callbackTime - serverTime)/SECS_PER_DAY) + " days from now?" );
						}
					}
				if ( dispo.id() >= Dispo.L.id())
					{
					if ( isBlank(Raw.POSITION))
						return falseBecause( "POSITION required!" );
					if ( isBlank(Raw.EMAIL))
						return falseBecause( "EMAIL address required!" );
					if ( isBlank(Raw.HOMELAND))
						return falseBecause( "HOMELAND required!" );
					}
				}
			return true;
			}
		catch (DataFieldException e) { return false; } // get reports error
		catch (Exception e) { return falseBecause( e.toString()); }
		}

	public final boolean haveDispo()
		{
		if ( !includes(Raw.DISPO)) return true; // cause caller can't fill it in
		try { return get(Raw.DISPO) != null; }
		catch (Exception e) { return false; }
		}

	public boolean hasBlankFields()
		{
		try
			{
			for ( int cfn : fRFNs )
			if ( cfn == -1 ) continue;
			else if ( isBlank(cfn))
				return !falseBecause( Raw.getLabelText(cfn) + " cannot be blank" );
			}
		catch(Exception e) { return true; }
		return false;
		}

	protected final void log( String fmt, Object... args )
		{
		SBLog.write( getClass().getSimpleName(), String.format( fmt, args ));
		}

	protected final boolean falseBecause ( String msg )
		{
		return inputError( msg );
		}

	protected final boolean inputError ( String msg )
		{
		return SBDialog.inputError( msg );
		}

	public final Raw getUnedited()
		{
		return (fRaw == Raw.BLANK)? null : fRaw;
		}

	public Raw getEdited()
		{
		return validInputs()? new Raw(fEdits) : null;
		}

	public String getTitle()
		{
		Raw raw = getUnedited();
		return (raw==null)? "xx" : raw.title(); // "" causes exception on no work
		}

	public ContactID getID()
		{
		Raw contact = getUnedited();
		return (contact == null)? null : contact.id();
		}

	public Address getAddress()
		{
		try { return (Address)get(Raw.ADDRESS); }
		catch (Exception e) { return null; }
		}

	public EMailAddress getEMailAddress()
		{
		try { return (EMailAddress)get(Raw.EMAIL); }
		catch (Exception e) { return null; }
		}
	} // 178
