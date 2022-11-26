package com.apo.contact.checkout;
/********************************************************************
* @(#)AbstractCallerCriteria.java	1.00 05/23/10
* Copyright 2010 by Richard T. Salamone, Jr. All rights reserved.
* 
* AbstractCallerCriteria: Provides a default implementation of
* CallerCriteria that handles most of the dirty work for the
* programmer. To create a CallerCriteria for a new module, only
* the following must be coded:
* 
*	1) public Dispo[] getDefaultDispos() // returns the array of Dispos checked by default
*
* @author Rick Salamone
* @version 1.00, 20100523 rts created to specify TQ leads for checkout
* @version 1.04, 20100610 RTS sorts checkouts on dispo DESC then id ASC
* @version 1.06, 20100614 RTS Moved processing code to CheckOutWorker
* @version 1.07, 20100620 RTS Modified to work with the CheckOutWiz
* @version 1.08, 20100702 RTS puts country on page headers (kludgy)
* @version 1.09, 20100702 RTS added available() and verify prior to checkout
* @version 1.10, 20100802 RTS added contact type, removed region
* @version 1.11, 20100802 RTS added contact type, removed region
* @version 1.12, 20100802 RTS removed sourceID
* @version 2.00, 20101025 RTS generalized as AbstractCallerCriteria
* @version 2.01, 20110603 RTS counts available using SysDAO
*******************************************************/
import com.apo.contact.Raw;
import com.apo.contact.Dispo;
import com.apo.contact.edit.CheckDispo;
import com.apo.net.SysDAO;
import com.shanebow.dao.*;
import com.shanebow.dao.edit.*;
import com.shanebow.ui.SBDialog;
import com.shanebow.util.SBDate;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.*;
import javax.swing.border.*;

public abstract class AbstractCallerCriteria extends JPanel
	implements CallerCriteria
	{
	private CheckDispo   chkDispos = new CheckDispo(getDefaultDispos());
	private EditWhen tfCallback;    // include DC up to & including this date
	private EditCountry  cbCountry; // only search this country
	private EditType     cbType;    // only search this type
	private JTextField   tfHTR;

	// member variables
	protected Country m_countryID = Country.XX;
	private  String    m_whereNoCountry;

	private JPanel m_options;

	public AbstractCallerCriteria()
		{
		super( new BorderLayout());
		setBorder(new EmptyBorder(10, 10, 10, 10));
		add(dispoPanel(),  BorderLayout.NORTH);
		add(filterPanel(), BorderLayout.CENTER);
		}

	public AbstractCallerCriteria(JPanel options)
		{
		super(new BorderLayout());
		setBorder(new EmptyBorder(10, 10, 10, 10));

	if (options != null )
		add(m_options = options, BorderLayout.NORTH);
		add(dispoPanel(),  BorderLayout.CENTER);
		add(filterPanel(), BorderLayout.SOUTH);
		}

	public JPanel dispoPanel()
		{
		chkDispos.setBorder(BorderFactory.createTitledBorder( "Include Dispositions:"));
		return chkDispos;
		}

	public JPanel filterPanel()
		{
		JPanel p = new JPanel(new GridLayout(0, 2, 2, 0));
		p.setBorder(BorderFactory.createTitledBorder( "Filters:"));
		addPair(p, "Callback on or before:", tfCallback = new EditWhen());
//		tfCallback.setFieldIndex(Raw.CALLBACK);
tfCallback.setTime( SBDate.toTime(SBDate.yyyymmdd() + "  23:59:59" ));
		addPair(p, "Max Hard to Reach:", tfHTR = new JTextField(2));
		tfHTR.setText("5");
		addPair(p, "Country:", cbCountry = new EditCountry());
		addPair(p, "Type:", cbType = new EditType());
		return p;
		}

	private void addPair( JPanel p, String label, JComponent c )
		{
		p.add(new JLabel(label, JLabel.RIGHT));
		p.add(c);
		}

	public JPanel getOptionsPanel() { return m_options; }

	// implement CallerCriteria
	public final Country getCountry()
		{ 
		try { return cbCountry.get(); }
		catch (Exception e) { return Country.XX; }
		}

	public final String getWhere()
		{
		return m_whereNoCountry
		     + (( m_countryID == Country.XX )? ""
		       : " AND countryID = " + m_countryID.dbRepresentation());
		}

	public final String buildWhere()
		{
		When    calldate = null;
		ContactType typeID = null;
		try
			{
			m_countryID = cbCountry.get();
			calldate = tfCallback.get();
			typeID = cbType.get();
			}
		catch (Exception e)
			{
			SBDialog.inputError(e.toString());
			return null;
			}
		m_whereNoCountry = "WHERE page=0"
            + " AND type = " + typeID.dbRepresentation()
            + " AND disposition IN (" + chkDispos.getCSV() + ")"
            + " AND noAnswer <= " + tfHTR.getText()
            + " AND callback <= " + calldate.dbRepresentation();
		return m_whereNoCountry
		     + (( m_countryID == Country.XX )? ""
		       : " AND countryID = " + m_countryID.dbRepresentation());
		}

	public final long available()
		{
		try { return SysDAO.DAO().sqlCount(Raw.DB_TABLE, buildWhere()); }
		catch (Exception e) { return -1; }
		}

	public final JPanel getComponent()
		{
		return this;
		}

	public abstract Dispo[]   getDefaultDispos();
	}
