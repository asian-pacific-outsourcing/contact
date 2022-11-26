package com.apo.contact;
/********************************************************************
* @(#)SBRadioGroup.java 1.00 09/12/05
* Copyright (c) 2010-2011 by Richard T. Salamone, Jr. All rights reserved.
*
*
*	A kinder gentler way of creating a set of radio buttons, that lets you
*	select an object from an array of objects.
*
*	The contructor creates a radio button for each element in the array
*	argument and adds it to the specified container. Call addActionListener
*	to register the specified listener with each button. Note that toString()
*	is used to set each button's actionCommand. Alternatively, you may call
*	getSelected() to get the selected object.
*
*	Example usage:
*
*		// initialization
*		BarSize[] sizes = {BarSize.FIVE_MIN, BarSize.FIFTEEN_MIN, BarSize.ONE_DAY };
*		SBRadioGroup<BarSize> group = new SBRadioGroup<BarSize>( 3, sizes );
*    somePanel.add( group.getContainer());
*		group.addActionListener(this);
*		group.select(1); // same as: group.select(BarSize.FIFTEEN_MIN);
*
*		// processing - use an action listener or
*		BarSize bs = group.getSelected();
*
* @version 1.00 12/05/09
* @author Rick Salamone
* 20091205 RTS created
* 20100623 RTS added setToolTipText
* 20110714 rts constructor makes a COPY of button choices to fix bug in select
* 20110715 rts added constructor to make container and method to getContainer
* 20110717 rts added added method getItemAt(aIndex)
*********************************************/
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JRadioButton;
import java.util.Enumeration;

public class SBRadioGroup<E> extends ButtonGroup
	{
	private final Object[] m_list; // E[] m_list;
	private final JComponent fContainer;

	public SBRadioGroup( int aColumns, E... list )
		{
		this(new javax.swing.JPanel(new java.awt.GridLayout(0,aColumns)), list );
		}

	public SBRadioGroup( JComponent p, E... list )
//	public SBRadioGroup( JComponent p, E[] list ) // Object... list )
		{
		super();
		fContainer = p;
//		m_list = list;
//		m_list = (E[])(new Object[list.length]);
		m_list = new Object[list.length];
		System.arraycopy(list, 0, m_list, 0, list.length);

		for ( E obj : list )
			{
			String text = obj.toString();
			JRadioButton it = new JRadioButton(text);
			it.setActionCommand(text);
			it.setMnemonic(text.charAt(0));
//	it.setOpaque(true); //MUST do this for background to show up
	it.setContentAreaFilled(false);
			super.add(it);
			p.add(it);
			}
		}

	public final JComponent getContainer() { return fContainer; }

	public void addActionListener( ActionListener al )
		{
		for (Enumeration<AbstractButton> e = getElements(); e.hasMoreElements();)
			e.nextElement().addActionListener(al);
		}

	public void setItems( E... list )
		{
		int i = 0;
		for ( Enumeration<AbstractButton> e = getElements(); e.hasMoreElements(); )
			{
			AbstractButton it = e.nextElement();
			String text = list[i].toString();
			it.setText(text);
			it.setActionCommand(text);
			m_list[i] = list[i]; // bug here somewhere!!
			++i;
			}
		}

	public E getItemAt(int aIndex) { return (E)m_list[aIndex]; }

	public void setEnabled( boolean b )
		{
		for ( Enumeration<AbstractButton> e = getElements(); e.hasMoreElements(); )
			e.nextElement().setEnabled(b);
		}

	@Override public void add(AbstractButton ab)
		{
		throw new UnsupportedOperationException("add");
		}

	@Override public void remove(AbstractButton ab)
		{
		throw new UnsupportedOperationException("remove");
		}

	public E getSelected()
		{
// System.out.println( "SBRG getSelection: " + getSelection());
// for ( E obj : m_list ) System.out.println("  " + obj.toString());
		if ( getSelection() == null ) return null;
		String actText = getSelection().getActionCommand();
		for ( Object obj : m_list )
//		for ( E obj : m_list )
			if ( obj.toString().equals( actText ))
				return (E)obj;
		return null;
		}

	public int getSelectedIndex()
		{
		if ( getSelection() != null )
			{
			int i = 0;
			String actText = getSelection().getActionCommand();
			for (Enumeration<AbstractButton> e = getElements(); e.hasMoreElements(); i++)
				if ( e.nextElement().getActionCommand().equals(actText))
					return i;
			}
		return -1;
		}

	public void select( int i ) { select( (E)m_list[i] ); }
	public void select( E obj )
		{
		AbstractButton ab = getButton(obj);
		if ( ab != null )
			ab.doClick(); // setSelected(true);
		}

	public void setEnabled( E obj, boolean b )
		{
		AbstractButton ab = getButton(obj);
		if ( ab != null )
			ab.setEnabled(b);
		}

	public void setToolTipText( E obj, String tip )
		{
		AbstractButton ab = getButton(obj);
		if ( ab != null )
			ab.setToolTipText(tip);
		}

	public AbstractButton getButton( E obj )
		{
		AbstractButton it;
		for (Enumeration<AbstractButton> e = getElements(); e.hasMoreElements();)
			if ((it = e.nextElement()).getActionCommand().equals(obj.toString()))
				return it;
		return null;
		}
	}
