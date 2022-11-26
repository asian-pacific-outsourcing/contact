package com.apo.contact.report;
/********************************************************************
* @(#)ReportControls.java 1.00 20100821
* Copyright © 2010-2011 by Richard T. Salamone, Jr. All rights reserved.
*
* ReportControls: Provides filters for the Contact list. The user
* interacts with the controls herein then presses the 'fetch' button
* which generates as SQL request and sends it to the table model for
* processing.
*
* @author Rick Salamone
* @version 1.00
* 20100821 rts initial iteration
* 20100905 rts Added in office accounts
* 20101005 rts uses EditDateRange
* 20110116 rts major overhaul for use by admin or client
* 20110206 rts using dispos from Role
* 20110420 rts checking access to touch codes
* 20110614 rts added Check Out Page filter
*******************************************************/
import com.apo.contact.Raw;
import com.apo.contact.Dispo;
import com.apo.contact.HTRCount;
import com.apo.contact.Source;
import com.apo.contact.edit.CheckDispo;
import com.apo.contact.edit.EditHTRCount;
import com.apo.contact.edit.EditSource;
import com.apo.contact.touch.TouchCode;
import com.shanebow.dao.*;
import com.shanebow.dao.edit.*;
import com.apo.employee.Role;
import com.apo.net.Access;
import com.shanebow.ui.calendar.MonthCalendar;
import com.shanebow.ui.SBDialog;
import com.shanebow.ui.layout.LabeledPairPanel;
import com.shanebow.ui.LAF;
import com.shanebow.util.SBDate;
import com.shanebow.util.SBProperties;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;

public final class ReportControls
	extends JPanel
	{
	private static final String[] SHOW = { "500", "1000", "5000", "10000" };

	protected final MonthCalendar calendar = new MonthCalendar();
	private final JComboBox     cbChoices = new JComboBox(); // select touch code
	private final JComboBox     cbShow = new JComboBox(SHOW);
	private final EditDateRange editDateRange = new EditDateRange(EditDateRange.VERTICAL);
	private final EditCountry   cbCountry = new EditCountry();
	private final EditHTRCount  edHTR = new EditHTRCount();
	private final SelectRelationalOperator htrOp = new SelectRelationalOperator();
	private final EditCheckOutID edPage = new EditCheckOutID();
	private final SelectRelationalOperator pageOp = new SelectRelationalOperator();
	private final EditSource    cbSource = new EditSource(Access.rights());
	private final JTextField    tfMisc = new JTextField();
	private final JTextField    tfName = new JTextField();
	private final CheckDispo    chkDispo;
	private final String        fDefaultDispoList;

	public ReportControls(Component dlgContact, JTable table)
		{
		super( new BorderLayout());

		calendar.addPropertyChangeListener(
			MonthCalendar.TIMECHANGED_PROPERTY_NAME, editDateRange);
		calendar.setOpaque(false);

		Set<Dispo> dispos = Access.accessibleDispos();
		chkDispo = new CheckDispo(0,3, dispos);
		fDefaultDispoList = Dispo.dbCriteriaList(dispos.toArray(new Dispo[0]));

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setTopComponent(calendar);
		splitPane.setBottomComponent(new JScrollPane(filtersPanel()));
		splitPane.setDividerLocation(190); //XXX ignored in some releases bug 4101306
		add(splitPane, BorderLayout.CENTER);
		}
	
	public void updateHistory()
		{
//		tfMisc.addToHistory();
//		tfOrder.addToHistory();
		}

	private JComponent filtersPanel()
		{
		SBProperties props = SBProperties.getInstance();
		Role role = Access.getRole();
		long permissions = Access.rights();
		cbChoices.addItem("--");
		for ( TouchCode code : TouchCode.getAll())
			if (code!=TouchCode.XX && code.accessible(permissions))
				cbChoices.addItem(code);
		if ( Access.allowedTo(Access.TQ))
			cbChoices.addItem( new ScheduledAction( "TQ Call", Role.TQ.fetchDispos()));
//		cbChoices.addItem( new ScheduledAction( "Tingle",  Role.QV.fetchDispos()));
		if ( Access.allowedTo(Access.VO))
			cbChoices.addItem( new ScheduledAction( "VO Call", Role.VO.fetchDispos()));
		if ( Access.allowedTo(Access.AO))
			cbChoices.addItem( new ScheduledAction( "AO Call", Role.AO.fetchDispos()));
		if ( Access.allowedTo(Access.LO))
			cbChoices.addItem( new ScheduledAction( "LO Call", Role.LO.fetchDispos()));
		cbChoices.addItemListener(new ItemListener()
			{
			@Override public void itemStateChanged(ItemEvent e)
				{
				boolean haveChoice = cbChoices.getSelectedIndex() != 0;
				editDateRange.setEnabled(haveChoice);
				cbSource.setEnabled(haveChoice);
				}
			});
		String maxShow = props.getProperty("app.fetch.max.items", "");
		if ( !maxShow.isEmpty())
			cbShow.addItem(maxShow);
		cbShow.setSelectedIndex(0);

		LabeledPairPanel p = new LabeledPairPanel();  // "Filters" );
		p.setBorder(LAF.getStandardBorder());

		p.addRow(new Section("Action"), cbChoices);
		p.addRow(     "", editDateRange );
		if ( !role.isCaller())
			p.addRow( "by", cbSource );

		p.addRow(new Section(), new Section());
		p.addRow(new Section("Filters"), new JLabel());
		p.addRow( "Country", cbCountry );
		if ( Access.allowedTo(Access.TQ|Access.VO))
			p.addRow(  "HTR", htrOp, edHTR );
		htrOp.setSelectedItem("<=");
		try { edHTR.set(HTRCount.parse("99")); } catch (Exception e) {}
		if ( Access.allowedTo(Access.TQ|Access.VO))
			p.addRow(  "Check Out Page", pageOp, edPage );
		pageOp.setSelectedItem(">=");
		edPage.set(CheckOutID.CHECKED_IN);
		p.addRow( "Dispos", chkDispo );
		p.addRow( "Name", tfName );
		if ( Access.allowedTo(Access.DBA))
			p.addRow( "misc", tfMisc );

		p.addRow(new Section(), new Section());
		p.addRow(new Section("Limit"), cbShow ); // cbShow, new JLabel("contacts"));

		return p;
		}

	public int getMaxShowCount()
		{
		try { return Integer.parseInt((String)cbShow.getSelectedItem()); }
		catch (Exception e) { return -1; }
		}

	public String getSQL()
		{
		return "SELECT * FROM raw"
		          + "\n WHERE " + getWhereClause()
		          + "\n ORDER BY " + getOrderClause();
		}

	public String getOrderClause()
		{
		return "id ASC";
		}

	public String getWhereClause()
		{
		Role role = Access.getRole();
		long dates[] = getDateRange();
		Object choice = cbChoices.getSelectedItem();
		Country countryID = null;
		Source touchedByID = Source.XX;
		HTRCount htrCount;
		CheckOutID page;
		try
			{
			countryID = cbCountry.get();
			if ( !role.isCaller())
				touchedByID = cbSource.get();
			htrCount = (HTRCount)(edHTR.get());
			page = (CheckOutID)(edPage.get());
			}
		catch (Exception e)
			{
			SBDialog.inputError(e.toString());
			return null;
			}
		Dispo[] dispos = chkDispo.getSelected();
		String it = Raw.dbField(Raw.DISPO) + " IN "
		   + ((dispos.length > 0)? Dispo.dbCriteriaList(dispos)
		                          : fDefaultDispoList);
		it += (role.isCaller()? ("AND sourceID=" + Access.getUID())
		                      : ("AND noAnswer " + htrOp.getSelectedItem()
		          									+ " " + htrCount.dbRepresentation()));

		if ( Access.allowedTo(Access.TQ|Access.VO))
			it += " AND page" + pageOp.getSelectedItem() + page.dbRepresentation();

		if ( countryID != Country.XX )
			it += " AND countryID = " + countryID.dbRepresentation();

		if ( choice instanceof TouchCode )
			{
			TouchCode code = (TouchCode)choice;
			it += " AND EXISTS (SELECT NULL FROM touch WHERE touch.contactID = raw.id";
			it += " AND touch.touchCode=" + code.dbRepresentation();
			if ( touchedByID != Source.XX )
				it += " AND touch.employeeID = " + touchedByID.dbRepresentation();
			it += " AND touch.when BETWEEN " + dates[0] + " AND " + dates[1] + ")";
			}
		else if ( choice instanceof ScheduledAction )
			{
			ScheduledAction scheduled = (ScheduledAction)choice;
			it += " AND " + scheduled.sql();
			if ( touchedByID != Source.XX )
				it += " AND sourceID = " + touchedByID.dbRepresentation();
			it += " AND " + Raw.dbField(Raw.CALLBACK)
			    + " BETWEEN " + dates[0] + " AND " + dates[1];
			}
		String name = tfName.getText();
		if ( !name.isEmpty())
			it += " AND name Like '%" + name + "%'";
		String misc = tfMisc.getText();
		if ( !misc.isEmpty())
			it += " AND " + misc;
		return it;
		}

	public long[] getDateRange() { return editDateRange.getDateRange(); }
	}

final class ScheduledAction
	{
	private final String m_desc;
	private final String m_sql;
	public ScheduledAction( String desc, Dispo[] dispos )
		{
		m_desc = desc;
		String sql = Raw.dbField(Raw.DISPO) + " IN ";
		for ( int i = 0; i < dispos.length; i++ )
			sql += ((i == 0)? "(" : ",") + dispos[i].dbRepresentation();
		sql += ")";
		m_sql = sql;
		}

	public String toString() { return "Scheduled for " + m_desc; }
	public String sql() { return m_sql; }
	}

class Section extends JLabel
	{
	public Section()
		{
		super( "<html><HR width=5000>" );
		}
	public Section(String title)
		{
		super( "<html><B>" + title );
		}
	}
