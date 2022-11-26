package com.apo.contact;
/********************************************************************
* @(#)DlgFile.java 1.00 20100809
* Copyright © 2010-2013 by Richard T. Salamone, Jr. All rights reserved.
*
* DlgFile: Extends JFileChooser with features specific to APO files.
*
* @author Rick Salamone
* @version 1.00
* 20100809 rts created
* 20130216 rts modified imports
*******************************************************/
import com.shanebow.tools.fileworker.FileLineParser;
import com.shanebow.ui.SBDialog;
import com.shanebow.util.SBLog;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import javax.swing.border.*;

public final class DlgFile extends JFileChooser
	{
	public static final byte FT_SAVE_AS=0;
	public static final byte FT_EXPORT_CONTACTS=1;
	public static final byte FT_IMPORT_CONTACTS=2;
	public static final byte FT_IMPORT_CALL_LOG=3;
	public static final byte FT_UPDATE_CONTACTS=4;

	private static final DlgFile _chooser = new DlgFile();
	public static File get( byte fileType )
		{
		_chooser.setMultiSelectionEnabled(false);
		if ( _chooser.prompt(null, fileType) == APPROVE_OPTION )
			return _chooser.getSelectedFile();
		return null;
		}

	public static File[] getFiles( byte fileType )
		{
		_chooser.setMultiSelectionEnabled(true);
		if ( _chooser.prompt(null, fileType) == APPROVE_OPTION )
			return _chooser.getSelectedFiles();
		_chooser.setMultiSelectionEnabled(false);
		return null;
		}

	public DlgFile()
		{
		super( new File(System.getProperty("user.dir")));
// super( "c:/apps/src/com/apo/_docs/Call Stats/" );

		setDialogTitle("Import Telephone Call Log");
		setFileFilter(
			     new FileNameExtensionFilter( "CSV (Comma delimited)", "csv"));
		setMultiSelectionEnabled(false);
		}

	public int prompt(Component parent, byte fileType )
		{
		switch ( fileType )
			{
			case FT_EXPORT_CONTACTS:
				setDialogTitle("Export Contacts To");
				return super.showSaveDialog(parent);
			case FT_SAVE_AS:
				setDialogTitle("Save As");
				return super.showSaveDialog(parent);
			case FT_IMPORT_CONTACTS:
				setDialogTitle("Import Contacts From");
				return super.showOpenDialog(parent);
			case FT_UPDATE_CONTACTS:
				setDialogTitle("Update Contacts From");
				return super.showOpenDialog(parent);
			case FT_IMPORT_CALL_LOG:
				setDialogTitle("Import Calls From");
				return super.showOpenDialog(parent);
			default: throw new IllegalArgumentException("DlgFile");
			}
		}
	}
