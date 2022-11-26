package com.apo.contact.report;
/********************************************************************
* @(#)ToggleDetails.java 1.00 06/17/10
* Copyright (c) 2010-2011 by Richard T. Salamone, Jr. All rights reserved.
*
* ToggleDetails: A toggle button that is synced with the visibility of
* anther component: When the button is down the component is visible and
* vice versa.
*
* @version 1.00 06/17/10
* @author Rick Salamone
* 20110117, rts moved into raw package, icons to apo.jar
*******************************************************/
import com.apo.contact.Raw;
import com.shanebow.util.SBMisc;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.*;

public final class ToggleDetails extends JToggleButton
	implements ActionListener, PropertyChangeListener
	{
	private static final String ICON_ROOT = "com/apo/det";
	private static final int NO_SELECTION=0;
	private static final int SELECTION_HIDE=1;
	private static final int SELECTION_SHOW=2;
	private Component m_component;

	public ToggleDetails(Component component)
		{
		this();
		setComponent(component);
		}

	public ToggleDetails()
		{
		super();
		setToolTipText("View/Hide selected contact details");
		addActionListener(this);
		setIcon(createIcon(SELECTION_HIDE));
		setDisabledIcon(createIcon(NO_SELECTION));
		setSelectedIcon(createIcon(SELECTION_SHOW));
		setEnabled(false);
		setMargin(new Insets(0,0,0,0));
		}

	public void setComponent(Component component)
		{
		m_component = component;
		m_component.addComponentListener( new ComponentAdapter()
			{
			public void componentHidden(ComponentEvent e) { setSelected(false); }
			public void componentShown (ComponentEvent e) { setSelected(true); }
			});
		}

	protected ImageIcon createIcon(int x)
		{
		String path = ICON_ROOT + x + ".gif";
		return new ImageIcon (SBMisc.findResource(path));
		}

	public void actionPerformed(ActionEvent e)
		{
		showComponent( isSelected());
		}

	public void propertyChange(PropertyChangeEvent evt)
		{
		String property = evt.getPropertyName();
		if ( property.equals(Raw.SELECTED_PROPERTY))
			{
			if ( evt.getNewValue() == null ) // no selection
				{
				setEnabled(false);
				showComponent( false );
				}
			else
				{
				setEnabled(true);
				showComponent( isSelected());
				}
			}
		}

	private void showComponent( boolean visible )
		{
		m_component.setVisible( visible ); 	// hide disloag
		}
	}
