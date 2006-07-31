// Ener-J
// Copyright 2000 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/com/vscorp/ui/swing/table/examples/ObjectSourceTableExample.java,v 1.2 2005/11/21 02:06:47 dsyrstad Exp $

package com.vscorp.ui.swing.table.examples;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;

import com.vscorp.ui.swing.redirector.*;
import com.vscorp.ui.swing.table.EditableSortableTableColumn;
import com.vscorp.ui.swing.table.SortableTableListener;
import com.vscorp.ui.swing.table.objectSource.ObjectSourceJTable;

/**
 * Example of an ObjectSource-based JTable.
 * 
 * @version $Id: ObjectSourceTableExample.java,v 1.2 2005/11/21 02:06:47 dsyrstad Exp $
 * @author <a href="mailto:dsyrstad@ener-j.org">Dan Syrstad </a>
 */
public class ObjectSourceTableExample extends WindowAdapter
{
    private static final String[] resourceArgs = {
        "com.vscorp.ui.swing.table.examples.TestObjectSource$NameIconColumn",
            "com.vscorp.ui.swing.table.examples.TestObjectSource$TestObject",
            "notused", "true", "Name/Icon", "0",

        "com.vscorp.ui.swing.table.objectSource.ReflectedProgressColumn",
            "com.vscorp.ui.swing.table.examples.TestObjectSource$TestObject",
            "mProgress", "false", "Reflected Progress", "0",

        "com.vscorp.ui.swing.table.objectSource.ReflectedTextColumn",
            "com.vscorp.ui.swing.table.examples.TestObjectSource$TestObject",
            "mName", "false", "Reflected Name", "0",

        "com.vscorp.ui.swing.table.objectSource.ReflectedTextColumn",
            "com.vscorp.ui.swing.table.examples.TestObjectSource$TestObject",
            "mName", "true", "Ref. Editable Name", "0",

        "com.vscorp.ui.swing.table.objectSource.ReflectedCheckBoxColumn",
            "com.vscorp.ui.swing.table.examples.TestObjectSource$TestObject",
            "mFlag", "true", "Ref. Editable Flag", "0",

        "com.vscorp.ui.swing.table.objectSource.ReflectedCheckBoxColumn",
            "com.vscorp.ui.swing.table.examples.TestObjectSource$TestObject",
            "mFlag", "false", "Ref. Flag", "0",

        "com.vscorp.ui.swing.table.objectSource.ReflectedComboBoxColumn",
            "com.vscorp.ui.swing.table.examples.TestObjectSource$TestObject",
            "mIntValue", "true", "ComboBox Edit", "10", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",

        "com.vscorp.ui.swing.table.objectSource.ReflectedComboBoxColumn",
            "com.vscorp.ui.swing.table.examples.TestObjectSource$TestObject",
            "mIntValue", "false", "ComboBox View", "10", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",

        "com.vscorp.ui.swing.table.objectSource.ReflectedDateTimeColumn",
            "com.vscorp.ui.swing.table.examples.TestObjectSource$TestObject",
            "mDate", "true", "Date", "0", // Use default date format

        "com.vscorp.ui.swing.table.objectSource.ReflectedDateTimeColumn",
            "com.vscorp.ui.swing.table.examples.TestObjectSource$TestObject",
            "mDate", "false", "Date Only", "1", "MM/dd/yyyy",

        "com.vscorp.ui.swing.table.objectSource.ReflectedTimeIntervalColumn",
            "com.vscorp.ui.swing.table.examples.TestObjectSource$TestObject",
            "mTimeInterval", "true", "Time Interval", "0",

        "com.vscorp.ui.swing.table.objectSource.ReflectedNumberColumn",
            "com.vscorp.ui.swing.table.examples.TestObjectSource$TestObject",
            "mIntValue", "true", "Int Number", "0", // Use default number format

        "com.vscorp.ui.swing.table.objectSource.ReflectedNumberColumn",
            "com.vscorp.ui.swing.table.examples.TestObjectSource$TestObject",
            "mDouble", "true", "Double Obj Number", "1", "#,##0.00;(#)",

    };

    //----------------------------------------------------------------------
    private ObjectSourceTableExample()
    {
    }

    //----------------------------------------------------------------------
    public static void main(String[] args) throws Exception
    {
        //String lookAndFeelClassname = UIManager.getSystemLookAndFeelClassName();
        //UIManager.setLookAndFeel(lookAndFeelClassname);

        ObjectSourceTableExample thisObj = new ObjectSourceTableExample();

        JFrame frame = new JFrame("ObjectSource JTable Test");
        frame.getContentPane().setLayout( new BorderLayout() );

        TestObjectSource objectSource = new TestObjectSource();

        EditableSortableTableColumn[] columns =
            EditableSortableTableColumn.createColumns(resourceArgs);

        for (int i = 0; i < columns.length; i++) {
            columns[i].addSortChangeListener(new ChangeRedirector(thisObj, "sortColumn") );
        }

        ObjectSourceJTable table = new ObjectSourceJTable(objectSource, columns );
        new SortableTableListener(table);
        Font font = table.getFont();
        table.setFont( new Font(font.getName(), font.getStyle(), 14) );
        JScrollPane scrollPane = new JScrollPane(table);

        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        JButton button = new JButton("Test Focus Change");
        frame.getContentPane().add(button, BorderLayout.NORTH);
        frame.addWindowListener(thisObj);
        frame.pack();
        frame.setSize(1024, 768);
        frame.setVisible(true);
    }

    //----------------------------------------------------------------------
    private void sortColumn(ChangeEvent anEvent)
    {
        EditableSortableTableColumn column = (EditableSortableTableColumn)anEvent.getSource();
        System.out.println("Column=" + column.getHeaderValue() + " Sort direction=" + column.getSortDirection() );
    }

    //----------------------------------------------------------------------
    public void windowClosing(WindowEvent anEvent)
    {
        JFrame frame = (JFrame)anEvent.getSource();
        frame.setVisible(false);
        frame.dispose();
        System.exit(1);
    }
}
