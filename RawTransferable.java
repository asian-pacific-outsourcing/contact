package com.apo.contact;
/********************************************************************
* @(#)RawTransferable.java 1.00 20110203
* Copyright (c) 2011 by Richard T. Salamone, Jr. All rights reserved.
*
* RawTransferable: The database fields for a contact
*
* @author Rick Salamone
* @version 1.00, 20110203 rts created
*******************************************************/
import java.awt.datatransfer.*;

public final class RawTransferable
	implements Transferable,
	ClipboardOwner
	{
	public static DataFlavor rawFlavor=null;
	static
		{
     try
			{
			rawFlavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType
				                      + "; class=com.apo.contact.Raw", "Local Raw");
			}
		catch(Exception e) { System.err.println(e); }
		}

	private static final DataFlavor[] SUPORTED_FLAVORS = { rawFlavor };

	private final Raw fRaw;
	public RawTransferable(Raw aRaw)
		{
		fRaw = aRaw;
		}

   @Override public Object getTransferData(DataFlavor aFlavor)
		throws UnsupportedFlavorException, java.io.IOException
		{
		// check to see if the requested flavor matches
		if ( !aFlavor.equals(rawFlavor))
			throw( new UnsupportedFlavorException(aFlavor));
		return fRaw;  // easy!
		}

	@Override public DataFlavor[] getTransferDataFlavors()
		{
		return SUPORTED_FLAVORS;
		}

	@Override public boolean isDataFlavorSupported(DataFlavor aFlavor)
		{
		return aFlavor.equals(rawFlavor);
		}

	@Override public void lostOwnership(Clipboard clipboard, Transferable contents) {}
	}
