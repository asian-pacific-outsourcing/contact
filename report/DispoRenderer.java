package com.apo.contact.report;
/********************************************************************
* @(#)DispoRenderer.java 1.00 20110507
* Copyright (c) 2011 by Richard T. Salamone, Jr. All rights reserved.
*
* DispoRenderer: Renders a Dispo using it's color as the background.
*
* @author Rick Salamone
* @version 1.00 20110507
* 20110507 rts created
*******************************************************/
import com.apo.contact.Dispo;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class DispoRenderer
	extends JLabel
	implements TableCellRenderer
	{
	// Common constructor code
		{
		setOpaque(true);
		setHorizontalAlignment(CENTER);
		setFont(new JTextField().getFont());
		setForeground( Color.WHITE );
		}

	public Component getTableCellRendererComponent(
                            JTable table, Object aObject,
                            boolean isSelected, boolean hasFocus,
                            int row, int column)
		{
		Dispo dispo = (Dispo)aObject;
		Color bg = dispo.getColor();
		if ( isSelected ) bg = bg.darker();
//		isSelected?	table.getSelectionBackground() : dispo.getColor();
		setText( dispo.equals(Dispo.XX)? "" : dispo.toString());
		setBackground( bg );
		return this;
		}
	}
