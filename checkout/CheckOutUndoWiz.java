package com.apo.contact.checkout;
/********************************************************************
* @(#)CheckOutUndoWiz.java 1.00 20100821
* Copyright © 2010-2013 by Richard T. Salamone, Jr. All rights reserved.
*
* CheckOutUndoWiz: Wizzard to prompt user for an checkout to
* undo, then launches a file worker to update the database
* in the background.
*
* @author Rick Salamone
* @version 1.00
* 20100821 created from bulk update wiz
* 20110606 rts modified to use DAO, now networkable
* 20130216 rts modified imports
*******************************************************/
import com.shanebow.tools.fileworker.DlgProgress;
import com.shanebow.tools.fileworker.FileWorker;
import com.shanebow.ui.SBDialog;
import com.shanebow.util.SBMisc;
import java.io.File;
import javax.swing.JOptionPane;

final class CheckOutUndoWiz
	extends DlgProgress
	{
	private static final String TITLE="Undo Check Out";
	private static final String USER_DIR = System.getProperty("user.dir");

	private Object m_selectedUndo; // file name, sans dir & extension
	private File getSelected(String ext)
		{
		return new File( USER_DIR + "/" + m_selectedUndo + ext ); 
		}
		
	CheckOutUndoWiz()
		{
		super( TITLE );
		setCancelable(false);
		onSelectUndo();
		}

	public void onSelectUndo()
		{
		String[] fileList = SBMisc.fileList( USER_DIR, ".txt" );
		if ( fileList == null )
			{
			SBDialog.error( "Undo CheckOut", "Error: No checkouts found!" );
			return;
			}

		m_selectedUndo = (String)(JOptionPane.showInputDialog(null, 
		                       "Choose CheckOut to Undo", "CheckOuts Found",
		                       JOptionPane.INFORMATION_MESSAGE, null,
		                       fileList, fileList[fileList.length-1]));
		 
		if ( m_selectedUndo != null )
			onStart();
		}

	private void onStart()
		{
		try
			{
			File file = getSelected(".csv");
			log("Undoing " + file.getName());
			FileWorker worker = new FileWorker(new CheckOutUndoLineParser(getLog()), file);
			worker.addPropertyChangeListener( this );
			worker.execute();
			}
		catch ( Exception e)
			{
			log("Failed to launch: " + e.getMessage());
			promptClose();
			return;
			}
		}

	@Override
	protected void success()
		{
		log("Removing: " + m_selectedUndo + " files ('.txt' & '.csv')");
		getSelected(".csv").delete();
		getSelected(".txt").delete();
		}
	}
