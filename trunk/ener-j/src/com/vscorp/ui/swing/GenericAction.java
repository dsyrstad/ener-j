//Ener-J
//Copyright 2000-2006 Visual Systems Corporation
//$Header: /cvsroot/ener-j/ener-j/src/com/vscorp/ui/swing/GenericAction.java,v 1.1 2006/01/23 22:15:32 dsyrstad Exp $

package com.vscorp.ui.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Icon;

/**
 * A generic Action that delegates to an ActionListener. Useful with Redirectors
 * for action composition.<p>
 * 
 * @version $Id: GenericAction.java,v 1.1 2006/01/23 22:15:32 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class GenericAction extends AbstractAction
{
    private ActionListener mListener;
    
    //--------------------------------------------------------------------------------
    /**
     * Construct a GenericAction. 
     *
     * @param aListener the delegate listener. May be null if no action is desired.
     */
    public GenericAction(ActionListener aListener)
    {
        mListener = aListener;
    }
    
    //--------------------------------------------------------------------------------
    /**
     * Construct a GenericAction. 
     *
     * @param aName the action's name. For convenience, this also becomes the
     *  SHORT_DESCRIPTION property.
     * @param anIcon the action's icon. May be null.
     * @param aListener the delegate listener. May be null if no action is desired.
     */
    public GenericAction(String aName, Icon anIcon, ActionListener aListener)
    {
        super(aName, anIcon);
        putValue(SHORT_DESCRIPTION, aName);
        mListener = aListener;
    }
    
    //--------------------------------------------------------------------------------
    public void actionPerformed(ActionEvent anEvent)
    {
        if (mListener != null) {
            mListener.actionPerformed(anEvent);
        }
    }
}
