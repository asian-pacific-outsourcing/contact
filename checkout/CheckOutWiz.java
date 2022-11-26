package com.apo.contact.checkout;
/********************************************************************
* @(#)CheckOutWiz.java 1.0 20100603
* Copyright © 2010-2013 by Richard T. Salamone, Jr. All rights reserved.
*
* CheckOutWiz: Logic to control the check out process as well as to
* display the formatted results and progress.
*
* @author Rick Salamone
* @version 1.00
* 20100603 rts created
* 20100620 rts Now a stand alone wizzard to control CheckOut
* 20100702 rts added cmdAvailable
* 20100822 rts uses DlgProgress to monitor progress
* 20130216 rts modified imports
*******************************************************/
import com.shanebow.tools.fileworker.DlgProgress;
import com.shanebow.ui.SBDialog;
import com.shanebow.ui.LAF;
import com.shanebow.util.SBLog;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class CheckOutWiz
	extends JDialog
	implements ActionListener
	{
	private static final String DLG_TITLE="TQ Check Out Wizzard";
	private static final String BUSY="Please wait until the current CheckOut\n"
	                                 + "\ncompletes, then try again.";

	private static CheckOutWiz _theWiz = null;
	static void launch()
		{
		if ( CheckOutWorker.isBusy())
			{
			SBDialog.inform( "Check Out Busy", BUSY );
			return;
			}
		if ( _theWiz == null ) _theWiz = new CheckOutWiz();
		_theWiz.setVisible(true);
		}

	private static final String CMD_AVAIL="Available";
	private static final String CMD_START="Check Out";
	private static final String CMD_CLOSE="Close";

	CheckOutInputs inputs = new CheckOutInputs();
	private JButton btnStart;

	private CheckOutWiz()
		{
		super((Frame)null, DLG_TITLE, false);
		JPanel top = new JPanel();
		top.setBorder(LAF.getStandardBorder());
		top.setLayout(new BorderLayout());
		top.add(inputs, BorderLayout.CENTER);
		top.add(btnPanel(), BorderLayout.SOUTH);
		setContentPane(top);
		pack();
		setResizable(false);
		setLocationByPlatform(true);
		LAF.addUISwitchListener(this);
		}

	private void addLabeledPair( JPanel p, String label, JComponent comp )
		{
		p.add(new JLabel(label, JLabel.RIGHT), "label");
		p.add(comp, "field");
		}

	private JPanel btnPanel()
		{
		JPanel p = new JPanel();
		p.add(new JButton(new ActPurgeTQWorkQ()));
		p.add(makeButton(CMD_AVAIL));
		p.add(btnStart = makeButton(CMD_START));

		p.add( makeButton(CMD_CLOSE));
		return p;
		}

	private JButton makeButton(String caption)
		{
		JButton b = new JButton(caption);
		b.addActionListener(this);
		return b;
		}

	public void actionPerformed(ActionEvent e)
		{
		String cmd = e.getActionCommand();
		if ( cmd.equals(CMD_AVAIL))
			cmdAvail();
		else if ( cmd.equals(CMD_START))
			execute();
		else if ( cmd.equals(CMD_CLOSE))
			setVisible(false);
		}

	private void cmdAvail()
		{
		long num = inputs.available();
		if ( num >= 0 )
			SBDialog.inform( this, "Count Contacts",
			   "" + num + " contacts are available for check out" );
		}

	public void execute()
		{
		CheckOutWorker coWorker = inputs.onStart();
		if ( coWorker == null )
			return;
		setVisible(false);
		DlgProgress dlgProgress = new DlgProgress("CheckOut Progress");
		coWorker.m_contactsList = dlgProgress.getLog();
		coWorker.addPropertyChangeListener(dlgProgress);
		coWorker.execute();
		}
	}
