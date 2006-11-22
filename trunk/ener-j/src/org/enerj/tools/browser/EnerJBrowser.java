/*******************************************************************************
 * Copyright 2000, 2006 Visual Systems Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License version 2
 * which accompanies this distribution in a file named "COPYING".
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *      
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *      
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *******************************************************************************/
//Ener-J
//Copyright 2001-2005 Visual Systems Corporation
//$Header: /cvsroot/ener-j/ener-j/src/org/enerj/tools/enerjbrowser/EnerJBrowser.java,v 1.17 2006/02/21 02:37:47 dsyrstad Exp $

package org.enerj.tools.browser;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListSelectionEvent;

import org.odmg.QueryException;
import org.enerj.core.EnerJTransaction;
import org.enerj.tools.browser.model.QueryDef;
import org.enerj.tools.browser.model.EnerJBrowserModel;
import org.enerj.tools.browser.model.EnerJBrowserModel.TableInfo;

import com.vscorp.ui.swing.GenericAction;
import com.vscorp.ui.swing.JToolBarButton;
import com.vscorp.ui.swing.VSBackgroundTask;
import com.vscorp.ui.swing.VSJFrame;
import com.vscorp.ui.swing.VSSwingUtil;
import com.vscorp.ui.swing.redirector.ActionRedirector;
import com.vscorp.ui.swing.redirector.ListDataRedirector;
import com.vscorp.ui.swing.redirector.ListSelectionRedirector;
import com.vscorp.ui.swing.redirector.WindowRedirector;
import com.vscorp.ui.swing.table.SortableTableListener;
import com.vscorp.ui.swing.table.objectSource.ObjectSourceJTable;

/**
 * Object Browser main. <p>
 * 
 * @version $Id: EnerJBrowser.java,v 1.17 2006/02/21 02:37:47 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class EnerJBrowser
{
    //private static final String IDLE_ICON_NAME = "images/gearIdle.gif";
    //private static final String WORKING_ICON_NAME = "images/gearWorking.gif";
    private static final String IDLE_ICON_NAME = "images/Throbber.png";
    private static final String WORKING_ICON_NAME = "images/Throbber.gif";
    private static final String VIEW_EXTENTS_ICON_NAME = "images/Toolbar-Home.png";
    private static final String VIEW_SCHEMA_ICON_NAME = "images/Toolbar-Download.png";
    private static final String VIEW_NAMED_OBJECTS_ICON_NAME = "images/Toolbar-Bookmark.png";
    private static final String RUN_QUERY_ICON_NAME ="images/Go.png";
    private static final String BACK_ICON_NAME = "images/Toolbar-Back.png";
    private static final String FORWARD_ICON_NAME = "images/Toolbar-Forward.png";
    
    
    private EnerJBrowserModel mModel;
    
    private VSJFrame mFrame;
    private JLabel mWorkingLabel;
    private JLabel mResultsLabel;
    private JList mHistoryList;
    private JTextArea mQueryText;
    private Object mCurrentResultsObject = null;
    private ObjectSourceJTable mResultsTable;
    private JLabel mStatusBar;

    private ImageIcon mIdleImage;
    private ImageIcon mWorkingImage;
    private ImageIcon mViewExtentsImage;
    private ImageIcon mViewSchemaImage;
    private ImageIcon mViewNamedObjectsImage;
    private ImageIcon mRunQueryImage;
    private ImageIcon mViewGoBackImage;
    private ImageIcon mViewGoForwardImage;
    
    private Action mViewExtentsAction;
    private Action mViewSchemaAction;
    private Action mViewNamedObjectsAction;
    private Action mRunQueryAction;
    private Action mViewGoBackAction;
    private Action mViewGoForwardAction;

    

    /**
     * Construct a EnerJBrowser. 
     *
     * @param aDBURI a database URI. May be null.
     */
    public EnerJBrowser(String aDBURI)
    {
        if (aDBURI == null) {
            // TODO
            aDBURI = "oo7";
        }

        mModel = new EnerJBrowserModel(aDBURI, new BusyActionRedirector(this, "handleObjectLinkAction") );
        
        initComponents();
    }
    

    /**
     * Object Browser main. 
     *
     * @param args optional: a database URI.
     */
    public static void main(String[] args)
    {
         String dbURI = null;
         if (args.length > 0) {
             dbURI = args[0];
         }
         
         new EnerJBrowser(dbURI);
    }

    

    /**
     * Initialize the main frame and its components.
     */
    private void initComponents()
    {
        mIdleImage = VSSwingUtil.getImageIconResource(this.getClass(), IDLE_ICON_NAME);
        mWorkingImage = VSSwingUtil.getImageIconResource(this.getClass(), WORKING_ICON_NAME);
        mRunQueryImage = VSSwingUtil.getImageIconResource(this.getClass(), RUN_QUERY_ICON_NAME);
        mViewExtentsImage = VSSwingUtil.getImageIconResource(this.getClass(), VIEW_EXTENTS_ICON_NAME);
        mViewSchemaImage = VSSwingUtil.getImageIconResource(this.getClass(), VIEW_SCHEMA_ICON_NAME);
        mViewNamedObjectsImage = VSSwingUtil.getImageIconResource(this.getClass(), VIEW_NAMED_OBJECTS_ICON_NAME);
        mViewGoBackImage = VSSwingUtil.getImageIconResource(this.getClass(), BACK_ICON_NAME);
        mViewGoForwardImage = VSSwingUtil.getImageIconResource(this.getClass(), FORWARD_ICON_NAME);

        mFrame = new VSJFrame("Ener-J Object Browser");
        mFrame.setDefaultCloseOperation(VSJFrame.DISPOSE_ON_CLOSE);
        mFrame.addWindowListener( new WindowRedirector(this, null, null, "frameClosing", null, null, null, null) );
        mFrame.getContentPane().setLayout( new BorderLayout() );

        initActions();
        initMenus();
        initToolbar();
        initStatusBar();

        // History Pane
        JPanel listPanel = new JPanel( new BorderLayout() );
        
        JLabel histLabel = new JLabel("History", JLabel.CENTER);
        histLabel.setBorder( BorderFactory.createLineBorder(Color.BLACK) );
        listPanel.add(histLabel, BorderLayout.NORTH);

        ListModel listModel = mModel.getHistoryListModel();
        mHistoryList = new JList(listModel);
        mHistoryList.addListSelectionListener( new ListSelectionRedirector(this, "selectedListItem") );
        listModel.addListDataListener( new ListDataRedirector(this, null, "updateForAddedHistory", null) );
        
        JScrollPane listScroller = new JScrollPane(mHistoryList);
        
        listPanel.add(listScroller, BorderLayout.CENTER);

        // TODO - Implement tabs on Query pane for multiple open queries.
        // Query Pane
        JPanel queryPanel = new JPanel( new BorderLayout() );
        
        JLabel queryLabel = new JLabel("Query", JLabel.CENTER);
        queryLabel.setBorder( BorderFactory.createLineBorder(Color.BLACK) );
        queryPanel.add(queryLabel, BorderLayout.NORTH);

        mQueryText = new JTextArea();
        JScrollPane queryScroller = new JScrollPane(mQueryText);
        
        queryPanel.add(queryScroller, BorderLayout.CENTER);
        
        // Results Pane
        JPanel resultsPanel = new JPanel( new BorderLayout() );
        
        mResultsLabel = new JLabel("Results", JLabel.CENTER);
        mResultsLabel.setBorder( BorderFactory.createLineBorder(Color.BLACK) );
        resultsPanel.add(mResultsLabel, BorderLayout.NORTH);

        TableInfo tableInfo = mModel.getEmptyTableInfo();
        mResultsTable = new ObjectSourceJTable(tableInfo.getSource(), tableInfo.getTableColumns());
        new SortableTableListener(mResultsTable);
        JScrollPane tableScroller = new JScrollPane(mResultsTable);
        
        resultsPanel.add(tableScroller, BorderLayout.CENTER);
        
        JSplitPane vertSplitter = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, queryPanel, resultsPanel);
        vertSplitter.setAutoscrolls(true);

        JSplitPane horzSplitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, listPanel, vertSplitter);
        horzSplitter.setAutoscrolls(true);
        horzSplitter.setOneTouchExpandable(true);
        
        mFrame.getContentPane().add(horzSplitter, BorderLayout.CENTER);
        mFrame.pack();
        mFrame.setExtendedState(VSJFrame.MAXIMIZED_BOTH);
        mFrame.setVisible(true);
        horzSplitter.setDividerLocation(.10);
        vertSplitter.setDividerLocation(.25);
    }


    private void initMenus() {
        // TODO  use  Action w/ toolbar&menu
        JMenu fileMenu = new JMenu();
        fileMenu.setText("File");
        JMenuItem exitItem = new JMenuItem();
        exitItem.setText("Exit");
        exitItem.addActionListener( new ActionRedirector(this, "exitAppAction") );
        fileMenu.add(exitItem);

        JMenu viewMenu = new JMenu();
        viewMenu.setText("View");
        viewMenu.add(mViewExtentsAction);
        viewMenu.add(mViewSchemaAction);
        viewMenu.add(mViewNamedObjectsAction);

        JMenu actionMenu = new JMenu();
        actionMenu.setText("Actions");
        actionMenu.add(mViewGoBackAction);
        actionMenu.add(mViewGoForwardAction);
        actionMenu.add(mRunQueryAction);

        JMenu helpMenu = new JMenu();
        helpMenu.setText("Help");
        JMenuItem aboutItem = new JMenuItem();
        aboutItem.setText("About");
        aboutItem.addActionListener( new ActionRedirector(this, null) );
        helpMenu.add(aboutItem);

        JMenuBar mainMenu = new JMenuBar();
        mainMenu.add(fileMenu);
        mainMenu.add(viewMenu);
        mainMenu.add(actionMenu);
        mainMenu.add(helpMenu);
        mFrame.setJMenuBar(mainMenu);
    }


    private void initStatusBar() {
        mStatusBar = new JLabel("Ready");
        mStatusBar.setBorder(BorderFactory.createEtchedBorder());
        mFrame.getContentPane().add(mStatusBar, BorderLayout.SOUTH);
    }


    private void initToolbar() {
        JPanel topPanel = new JPanel(new BorderLayout());
        mFrame.getContentPane().add(topPanel, BorderLayout.NORTH);

        JToolBar toolbar = new JToolBar();
        topPanel.add(toolbar, BorderLayout.WEST);

        toolbar.setFloatable(false);
        
        toolbar.add( new JToolBarButton(mViewGoBackAction) );
        toolbar.add( new JToolBarButton(mViewGoForwardAction) );
        toolbar.add( new JToolBarButton(mViewSchemaAction) );
        toolbar.add( new JToolBarButton(mViewExtentsAction) );
        toolbar.add( new JToolBarButton(mViewNamedObjectsAction) );
        toolbar.add( new JToolBarButton(mRunQueryAction) );

        VSSwingUtil.resizeToolBarButtons(toolbar);

        // Busy Icon
        JPanel busyPanel = new JPanel( new BorderLayout() );
        topPanel.add(busyPanel, BorderLayout.EAST);
        
        mWorkingLabel = new JLabel(mIdleImage);
        mWorkingLabel.setBorder(BorderFactory.createEmptyBorder());
        mWorkingLabel.setHorizontalAlignment(JLabel.CENTER);
        mWorkingLabel.setVerticalAlignment(JLabel.CENTER);
        busyPanel.add(mWorkingLabel, BorderLayout.CENTER);
    }


    private void initActions()
    {
        mViewExtentsAction = new GenericAction("View Extents", mViewExtentsImage,
                        new BusyActionRedirector(this, "viewExtents"));

        mViewSchemaAction = new GenericAction("View Schema", mViewSchemaImage,
                        new BusyActionRedirector(this, "viewSchema"));

        mViewNamedObjectsAction = new GenericAction("View Named Objects", mViewNamedObjectsImage,
                        new BusyActionRedirector(this, "viewNamedObjects"));

        mViewGoBackAction = new GenericAction("Back", mViewGoBackImage,
                        new ActionRedirector(this, "goBack"));

        mViewGoForwardAction = new GenericAction("Forward", mViewGoForwardImage,
                        new ActionRedirector(this, "goForward"));

        mRunQueryAction = new GenericAction("Execute Query", mRunQueryImage,
                        new BusyActionRedirector(this, "executeQuery"));
        mRunQueryAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("F5") );
    }
    

    private void frameClosing(WindowEvent anEvent)
    {
        JFrame frame = (JFrame)anEvent.getSource();
        // Check if we can close...
        exitApp();
    }


    private void exitAppAction(ActionEvent anAction)
    {
        exitApp();
    }
    

    private void exitApp() 
    {
        EnerJTransaction txn = EnerJTransaction.getCurrentTransaction();
        if (txn != null && txn.isOpen()) {
            txn.abort();
        }
        
        System.exit(0);
    }


    /**
     * Sets the TableInfo on the table. Must be called from Event Dispatch thread.
     */
    private void setTableInfo(TableInfo aTableInfo)
    {
        Object obj = aTableInfo.getObject();
        if (mCurrentResultsObject != obj) {
            mCurrentResultsObject = obj;
            mResultsLabel.setText( "Results: " + mModel.getObjectName(obj) );
            mResultsTable.setObjectSource( aTableInfo.getSource() );
            mResultsTable.setColumns( aTableInfo.getTableColumns() );
            
            int numObjs = mResultsTable.getRowCount();
            mStatusBar.setText(numObjs + " object" + (numObjs == 1 ? ' ' : 's'));
        }
    }
    

    /**
     * Sets the TableInfo on the table. Must be called from Event Dispatch thread.
     * Alternative form callable by VSSwingUtil.invokeLater().
     * 
     * @param anObj a TableInfo object.
     */
    private void setTableInfo(Object anObj)
    {
        setTableInfo((TableInfo)anObj); 
    }
    

    /**
     * Shows an error message dialog. Must be called from Event Dispatch thread.
     * Callable by VSSwingUtil.invokeLater().
     * 
     * @param anObj a String representing a message.
     */
    private void showErrorMessage(Object anObj)
    {
        JLabel msg = new JLabel((String)anObj);
        JOptionPane.showMessageDialog(mFrame, msg, "Error", JOptionPane.ERROR_MESSAGE); 
    }
    

    private void viewExtents(ActionEvent anAction)
    {
        VSSwingUtil.invokeLater(this, mModel.getTableInfoForExtents(), "setTableInfo");
    }


    private void viewSchema(ActionEvent anAction)
    {
        VSSwingUtil.invokeLater(this, mModel.getTableInfoForSchema(), "setTableInfo");
    }


    private void viewNamedObjects(ActionEvent anAction)
    {
        VSSwingUtil.invokeLater(this, mModel.getTableInfoForBindery(), "setTableInfo");
    }


    private void executeQuery(ActionEvent anAction)
    {
        String query = mQueryText.getSelectedText();
        if (query == null) {
            query = mQueryText.getText();
        }
        
        TableInfo tableInfo;
        try {
            tableInfo = mModel.getTableInfoForQuery(query);
            VSSwingUtil.invokeLater(this, tableInfo, "setTableInfo");
        }
        catch (QueryException e) {
            VSSwingUtil.invokeLater(this, "Query Error: " + e.getMessage(), "showErrorMessage");
        }
    }


    /** 
     * Called from the event thread to handle the back button. Should be quick.
     */
    private void goBack(ActionEvent anAction)
    {
        int currIdx = mHistoryList.getSelectedIndex();
        // If nothing currently selected, assume end.
        if (currIdx < 0) {
            currIdx = mModel.getHistoryListModel().getSize() - 1;
        }
        
        --currIdx;
        if (currIdx >= 0) {
            // This will cause a selection event which will cause the results to be updated.
            mHistoryList.setSelectedIndex(currIdx);
        }
    }


    /** 
     * Called from the event thread to handle the forward button. Should be quick.
     */
    private void goForward(ActionEvent anAction)
    {
        int currIdx = mHistoryList.getSelectedIndex();
        int size = mModel.getHistoryListModel().getSize(); 
        // If nothing currently selected, assume end.
        if (currIdx < 0) {
            currIdx = size;
        }
        
        ++currIdx;
        if (currIdx < size) {
            // This will cause a selection event which will cause the results to be updated.
            mHistoryList.setSelectedIndex(currIdx);
        }
    }


    /**
     * Called from the event thread to handle History list selections.
     */
    private void selectedListItem(ListSelectionEvent anEvent)
    {
        if (!anEvent.getValueIsAdjusting()) {
            int idx = mHistoryList.getSelectedIndex();
            
            Object obj = mModel.getObjectForHistoryEntry(idx);
            if (obj instanceof QueryDef) {
                QueryDef def = (QueryDef)obj;
                obj = def.getResult();
                mQueryText.setText( def.getQuery() );
            }
            
            // Don't set results if this is the same object as the current results object.
            // Avoids same results being set twice when updateForAddedHistory is fired.
            if (obj != mCurrentResultsObject) {
                setTableInfo( mModel.getTableInfoWithoutHistory(obj) );
            }
        }
    }


    /**
     * Called from the event thread when history is added.
     */
    private void updateForAddedHistory(ListDataEvent anEvent)
    {
        // Make sure last entry is selected
        // NOTE: Don't select the last entry right now - race condition exists and can cause the table to 
        // screw up on subsequent queries. 
        /*if (anEvent.getType() == ListDataEvent.INTERVAL_ADDED) {
            int idx = anEvent.getIndex0();
            mHistoryList.setSelectedIndex(idx);
        }*/
        
    }
    

    private void handleObjectLinkAction(ActionEvent anAction)
    {
        TableInfo tableInfo = mModel.getTableInfo( anAction.getSource() );
        VSSwingUtil.invokeLater(this, tableInfo, "setTableInfo");
    }
    


    /**
     * Extends ActionRedirector to mark the frame busy during long actions. 
     * The specified event method is invoked from a background thread.
     */
    private final class BusyActionRedirector extends ActionRedirector
    {

        /**
         * Construct a BusyActionRedirector. 
         *
         * @param anEventReceiver
         * @param anActionPerformedMethodName
         */
        private BusyActionRedirector(Object anEventReceiver, String anActionPerformedMethodName)
        {
            super(anEventReceiver, anActionPerformedMethodName);
        }


        private void handleActionInBackground(Object anEvent) 
        {
            super.actionPerformed((ActionEvent)anEvent);
        }


        private void finishAction(Object anEvent) 
        {
            mFrame.setBusy(false);
            mWorkingLabel.setIcon(mIdleImage);
        }


        public void actionPerformed(ActionEvent anEvent)
        {
            mFrame.setBusy(true);
            mWorkingLabel.setIcon(mWorkingImage);

            VSBackgroundTask.runTask(this, anEvent, "handleActionInBackground", "finishAction");
        }
        
    }
}
