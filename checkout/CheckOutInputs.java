package com.apo.contact.checkout;
/********************************************************************
* @(#)CheckOutInputs.java	1.00 05/23/10
* Raw Admin Check Out Utility 
* Copyright 2010-2011 by Richard T. Salamone, Jr. All rights reserved.
* 
* @(#)CheckOutInputs.java	1.00 10/05/23 - The user interface for specifying
* check out qualifiers. Call onStart() to validate the inputs
* and use them to construct a CheckOutWorker.
* 
* @author Rick Salamone
* @version 1.00
* 20100610 RTS 1.04 sorts checkouts on dispo DESC then id ASC
* 20100614 RTS 1.05 Moved processing code to CheckOutWorker
* 20100620 RTS 1.06 Modified to work with the CheckOutWiz
* 20100702 RTS 1.08 puts country on page headers (kludgy)
* 20100702 RTS 1.09 added available() and verify prior to checkout
* 20100729 RTS 1.09 split out TQCriteria and this class entends it
* 20100822 RTS 1.10 prompt for name rather than csv & txt files
* 20101025 RTS 1.11 now extends AbstractCallerCriteria
* @version 1.12, 20110206 rts using dispos from Role
*******************************************************/
import com.apo.contact.Dispo;
import com.apo.employee.Role;
import com.shanebow.ui.SBDialog;
import com.shanebow.util.SBDate;
import java.awt.GridLayout;
import java.io.File;
import javax.swing.*;
import javax.swing.border.*;

public final class CheckOutInputs
	extends AbstractCallerCriteria
	{
	public CheckOutInputs()
		{
		super( new CheckOutOptions());
		}

	@Override public Dispo[] getDefaultDispos() { return Role.TQ.fetchDispos(); }

	CheckOutWorker onStart()
		{
		String where = buildWhere();
		if ( where == null )
			return null;

		CheckOutOptions opts = (CheckOutOptions)super.getOptionsPanel();
		if ( !opts.validInputs())
			return null;

		int perPage = opts.perPage();
		int reqTotal = opts.reqTotal();
		if ( reqTotal > available())
			{
			SBDialog.inputError( "Only " + available() + " contacts are available for check\n"
			     + "out based on the current options.\n\nAjust the settings and try again." );
			return null;
			}
		CheckOutWorker.m_headerInfo = "Country: " + getCountry();
		return new CheckOutWorker( reqTotal, perPage, where,
		                           opts.txtFile(), opts.csvFile());
		}
	}

final class CheckOutOptions extends JPanel
	{
	private static final String LBL_NAME = "Checkout (File) Name";
	private static final String LBL_QTY  = "Total Quantity";
	private static final String LBL_PER  = "Quantity Per Page";
	private static final String ASK_PARTIAL_OK
	  = "This will result in a partial page at the end!!"
	  + "\nProceed anyway?"
	  + "\n(Click 'No' to change settings)";

	private JTextField   tfName;      // root file name for formatted output
	private JTextField   tfQuantity;  // total number of contacts to check out
	private JTextField   tfPerPage;   // number of contacts per formatted page

	CheckOutOptions()
		{
		setLayout(new GridLayout(0, 2, 4, 0 ));
		setBorder(BorderFactory.createTitledBorder( "Output Configuration:"));
		addPair( LBL_NAME,  tfName = new JTextField(""));
		addPair( LBL_QTY,   tfQuantity = new JTextField("5005"));
		addPair( LBL_PER,   tfPerPage = new JTextField("11"));
		}

	private void addPair( String label, JComponent c )
		{
		add(new JLabel(label, JLabel.RIGHT));
		add(c);
		}

	public int perPage()
		{
		try { return Integer.parseInt(tfPerPage.getText()); }
		catch ( Exception e ) {	return -1; }
		}

	public int reqTotal()
		{
		try { return Integer.parseInt(tfQuantity.getText()); }
		catch ( Exception e ) { return -1; }
		}

	private String buildFileName( String name, String ext )
		{
		String it = SBDate.yyyymmdd();
		if ( !name.isEmpty()) it += "_" + name;
		return  it + ext;
		}

	public String getCheckOutFileName( String ext )
		{
		return buildFileName( tfName.getText(), ext );
		}

	private boolean nameExists( String name )
		{
		return new File( buildFileName( name, ".txt")).exists();
		}

	public boolean validInputs()
		{
		int perPage = perPage();
		if ( perPage <= 0 )
			return SBDialog.inputError( LBL_PER + "\nMust be a positive integer" );
		int reqTotal = reqTotal();
		if ( reqTotal <= 0 )
			return SBDialog.inputError( LBL_QTY + "\nMust be a positive integer" );
		if ((( reqTotal % perPage ) != 0 )
		&&   !SBDialog.confirm( ASK_PARTIAL_OK ))
			return false;
		String name = tfName.getText().trim();
		if ( nameExists( name ))
			{
			String msg = "There is already a";
			msg += name.isEmpty()? "n unnamed checkout"
			                     : (" checkout named '" + name + "'");
			msg += " for today.\nPlease specify a different name.";
			return SBDialog.inputError( msg );
			}
		return true;
		}

	public String txtFile() { return buildFileName( tfName.getText(), ".txt"); }
	public String csvFile() { return buildFileName( tfName.getText(), ".csv"); }
	}
