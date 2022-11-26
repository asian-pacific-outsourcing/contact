package com.apo.contact.edit;
/********************************************************************
* @(#)LabeledCounter.java 1.00 20110428
* Copyright (c) 2011 by Richard T. Salamone, Jr. All rights reserved.
*
* LabeledCounter: A label that maintains a counter for a given date.
* Checks the previous login date to determine wether the counter should
* be reset or continued.
*
* @author Rick Salamone
* @version 1.00 20110428
* 20110428 rts created
*******************************************************/
import com.apo.contact.Raw;
import com.shanebow.util.SBDate;
import com.shanebow.util.SBProperties;
import javax.swing.JLabel;

public final class LabeledCounter
	extends JLabel
	{
	private final String fLabel;
	private int fCount;

	public LabeledCounter(String aLabel)
		{
		super();
		fLabel = aLabel;
		SBProperties props = SBProperties.getInstance();
		String yyyymmdd = SBDate.yyyymmdd(Raw.DAO.getServerTime());
		int count = 0;
		String lastLogin = props.getProperty("usr.lastLogin", "");
		if ( yyyymmdd.equals(lastLogin))
			count = props.getInt("usr."+fLabel, 0);
		props.set("usr.lastLogin", yyyymmdd);
		reset(count);
		}

	public void decrement() { --fCount; _update(); }
	public void increment() { ++fCount; _update(); }
	public void reset(int aCount) { fCount = aCount; _update(); }
	private void _update()
		{
		SBProperties.set("usr."+fLabel, "" + fCount);
		setText(fLabel + ": " + fCount);
		}
	}
