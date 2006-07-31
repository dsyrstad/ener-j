// Ener-J
// Copyright 2000 Visual Systems Corporation
// $Header: /cvsroot/ener-j/ener-j/src/com/vscorp/ui/swing/table/EditableSortableTableColumn.java,v 1.1 2005/11/20 03:12:22 dsyrstad Exp $

package com.vscorp.ui.swing.table;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.lang.reflect.*;


/**
 * Supports a consistent look and feel for table columns and cells.
 * Provides common TableColumn functionality for classes implementing cell rendering, cell
 * editing, and column sorting. Column sorting also supports firing of an sort order ChangeEvent when the header is clicked
 * or when the sort order is programmatically changed.
 * Supports a consistent look and feel alignment of header labels.
 * Implements the TableCellRenderer and Editor for the column.<p>
 *
 * If you want the column to respond to sort clicks in the header, you must use the
 * following code:<p><code>
 * JTable yourTable = new JTable(....);<p>
 * new SortableTableListener(yourTable);<p></code>
 * <p>
 * All sub-classes must implement calculatePreferredWidth and getTableCellRendererComponent.
 * <p>
 * In addition, if the column allows editing, the sub-classes must at minimum override
 * getTableCellEditorComponent and stopCellEditing. Note that if the cell is editable,
 * getTableCellRendererComponent should return a component that renders the
 * visual appearance of the editor.<p>
 * <p>
 *
 * @author Daniel A. Syrstad
 */
abstract public class EditableSortableTableColumn extends TableColumn
        implements TableCellRenderer, TableCellEditor, CellEditor
{
    // Sort Directions for the header arrow
    public static final SortDirection SORT_ASCENDING = new SortDirection("SORT_ASCENDING");
    public static final SortDirection SORT_DESCENDING = new SortDirection("SORT_DESCENDING");
    public static final SortDirection SORT_NONE = new SortDirection("SORT_NONE");

    public static final HeaderTextAlignment HEADER_LEADING = new HeaderTextAlignment("HEADER_LEADING");
    public static final HeaderTextAlignment HEADER_TRAILING = new HeaderTextAlignment("HEADER_TRAILING");
    public static final HeaderTextAlignment HEADER_CENTER = new HeaderTextAlignment("HEADER_CENTER");

    /** Border when no focus is present. Protected so sub-classes can get it */
    protected static final Border NO_FOCUS_BORDER = BorderFactory.createEmptyBorder(1, 2, 1, 2);

    private static final ImageIcon ASCENDING_ICON = new ImageIcon();
    private static final ImageIcon DESCENDING_ICON = new ImageIcon();
    private static final ImageIcon SORTABLE_ICON = new ImageIcon();

    /** Constructor signature for classes compatible with the createColumn method */
    private static final Class[] sColumnConstructorSignature = {
        Class.class,
        String.class,
        String[].class
    };

    /** Is the column editable? */
    private boolean mEditable = false;

    /** Is the column sortable? */
    private boolean mSortable = false;

    /** Object currently being edited */
    private Object mEditorValue = null;

    /** Listeners for CellEditor */
    private EventListenerList mEditorListenerList = new EventListenerList();

    /** Listeners for Sort Order changes */
    private EventListenerList mSortListenerList = new EventListenerList();

    /** Cached ChangeEvent for CellEditor */
    private ChangeEvent mChangeEvent = null;

    /** Cached Sort ChangeEvent */
    private ChangeEvent mSortChangeEvent = null;

    /** Cached Header Renderer label */
    private JLabel mHeaderLabel;

    /** Default JTableHeader to use for default display attributes */
    private JTableHeader mDefaultHeader;

    /** Click count to start editing */
    private int mNumClicksToEdit = 1;

    /** Sorting direction: One of SORT_* above */
    private SortDirection mSortDirection;

    /** Header Text Alignment: One of HEADER_* above */
    private HeaderTextAlignment mHeaderTextAlignment;

    /** Optional field/attribute name being represented by the column. May be null
     *  if not used.
     */
    private String mFieldName = null;

    //----------------------------------------------------------------------
    /**
     * Construct a new column. Column is editable. A single click is
     * required to start editing. Column is sortable by default, but is not sorted
     * after construction. Default heading alignment is HEADER_CENTER. Heading text,
     * set by setHeaderValue, is an empty string.
     *
     */
    public EditableSortableTableColumn()
    {
        this("", true);
    }

    //----------------------------------------------------------------------
    /**
     * Construct a new column. Column is optionally editable. A single click is
     * required to start editing. Column is sortable by default, but is not sorted
     * after construction. Default heading alignment is HEADER_CENTER.
     *
     * @param aColumnTitle the columns heading, if any. This may be null.
     * @param anEditingFlag if true, the column can be edited, otherwise it cannot.
     */
    public EditableSortableTableColumn(String aColumnTitle, boolean anEditingFlag)
    {
        setCellRenderer(this);
        setCellEditor(this);

        setHeaderValue(aColumnTitle);
        setEditable(anEditingFlag);

        // We control the label in the header.
        setHeaderRenderer( new ColumnHeaderRenderer() );

        mHeaderLabel = new JLabel( getHeaderValue().toString() );
        mHeaderLabel.setHorizontalTextPosition(JLabel.LEADING);
        // Add a border so that LEADING and TRAILING headers aren't crammed against the
        // header border. We wrap this in a composite border that joins it with
        // the header border.
        Border outsideBorder = UIManager.getBorder("TableHeader.cellBorder");
        Border insideBorder = BorderFactory.createEmptyBorder(0, 3, 0, 3);
        mHeaderLabel.setBorder( BorderFactory.createCompoundBorder(outsideBorder, insideBorder) );

        setSortable(true);
        setSortDirection(SORT_NONE);
        setHeaderTextAlignment(HEADER_CENTER);
    }

    //----------------------------------------------------------------------
    /**
     * Construct a new column. Column is optionally editable and sortable. A 
     * single click is required to start editing. Column is not sorted after 
     * construction. Default heading alignment is HEADER_CENTER.
     *
     * @param aColumnTitle the columns heading, if any. This may be null.
     * @param anEditingFlag if true, the column can be edited, otherwise it cannot.
     * @param aSortableFlag if true, the column is sortable.
     */
    public EditableSortableTableColumn(String aColumnTitle, boolean anEditingFlag, boolean aSortableFlag)
    {
        this(aColumnTitle, anEditingFlag);
        setSortable(aSortableFlag);
    }

    //----------------------------------------------------------------------
    /**
     * Factory method to create a column instance based on a universally-specified
     * set of arguments. The column values are derived via reflection. This
     * eliminates the need for creating specific subclasses of columns to retrieve
     * the column value.
     *
     * @param aColumnClass the column class which derives the column
     *  value via reflection of a specific attribute. This class must have a
     *  constructor of the form:<p>
     *   public ColumnClassName(Class anObjectClass, String anAttributeName, String[] someArgs)<p>
     *  whose arguments correspond to the following arguments.
     *
     * @param anObjectClass the class of the object returned from TableModel.getValueAt.
     *
     * @param anAttributeName a String representing the name of the object's attribute.
     *  The attribute may be declared private.
     *
     * @param someArgs an array of Strings used to configure this specific column.
     *  It may be null or empty to use the default settings. See the documentation
     *  for the specified aColumnClass for the definition of the accepted arguments.
     *
     * @throws IllegalArgumentException (unchecked exception) if anAttributeName doesn't exist on
     *  anObjectClass or if aColumnClass does not contain the proper constructor.
     */
    public static EditableSortableTableColumn createColumn(Class aColumnClass, Class anObjectClass,
                    String anAttributeName, String[] someArgs)
    {
        Constructor constructor;

        try {
            constructor = aColumnClass.getConstructor(sColumnConstructorSignature);
        }
        catch (Exception e) {
            throw new IllegalArgumentException("No proper constructor on " + aColumnClass.getName() + ": " + e);
        }

        try {
            Object[] constructorArgs = new Object[] { anObjectClass, anAttributeName, someArgs };
            return (EditableSortableTableColumn)constructor.newInstance(constructorArgs);
        }
        catch (InvocationTargetException e) {
            throw new IllegalArgumentException("Exception instantiating column " + aColumnClass.getName() + ": " + e.getTargetException() );
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Exception instantiating column " + aColumnClass.getName() + ": " + e);
        }
    }

    //----------------------------------------------------------------------
    /**
     * Factory method to create a column instances based on a universally-specified
     * set of arguments. The column values are derived via reflection. This
     * eliminates the need for creating specific subclasses of columns to retrieve
     * the column value. <p>
     *
     * The intent of this method is to take an array argument
     * strings derived from a ResourceBundle and create columns from them.
     *
     * @param someArgs an array of Strings used to instantiate and configure this
     *  specific column.  The arguments are:<p>
     *
     * someArgs[0] - the column class name (suitable for use with Class.forName)
     *  which derives the column value via reflection of a specific attribute. This class must have a
     *  constructor of the form:<p>
     *   public ColumnClassName(Class anObjectClass, String anAttributeName, String[] someArgs)<p>
     *  whose arguments correspond to the following arguments.<p>
     *
     * someArgs[1] - the class name of the object returned from TableModel.getValueAt.<p>
     *
     * someArgs[2] - the name of the someArgs[1] attribute. The attribute may be declared private.<p>
     *
     * someArgs[3] - the String "true" if the column is editable, else "false".<p>
     *
     * someArgs[4] - One of two possible arguments: the heading for the column; or 
     *  a boolean ("true" or "false") to indicate whether the column is sortable. If
     *  a heading is specified, then let OFS = 0. If a sortable flag is specified,
     *  then let OFS = 1 and let the next argument (someArgs[5]) be the heading for the
     *  column.<p>
     *
     * someArgs[OFS + 5] - the number of column-specific arguments that follow.<p>
     *
     * someArgs[OFS + 6 .. OFS + 6 + (int)someArgs[OFS + 5] ] - column-specific arguments. See the documentation
     *  for the specified column class for the definition of the accepted arguments.<p>
     *
     * The above sequence repeats for as many columns as necessary.
     *
     * @throws IllegalArgumentException (unchecked exception) if anAttributeName doesn't exist on
     *  anObjectClass, if aColumnClass does not contain the proper constructor, or
     *  if there are an improper number of arguments.
     */
    public static EditableSortableTableColumn[] createColumns(String[] someArgs)
    {
        final int MIN_ARGS = 6;
        final int COLUMN_CLASS_INDEX = 0;
        final int OBJECT_CLASS_INDEX = 1;
        final int ATTR_NAME_INDEX = 2;
        final int EDIT_FLAG_INDEX = 3;
        final int HEADING_INDEX = 4;
        final int SORTABLE_INDEX = 4;
        final int COLUMN_ARG_COUNT_INDEX = 5;
        final int COLUMN_ARGS_INDEX = 6;
        
        ArrayList columns = new ArrayList();
        for (int i = 0; i < someArgs.length; ) {
            // We must at least MIN_ARGS arguments from this point on
            if ( (i + MIN_ARGS) > someArgs.length) {
                throw new IllegalArgumentException("Not enough arguments at index " + i);
            }

            int sortableOffset = 0;
            boolean sortable = false;
            String headingOrSortable = someArgs[i + HEADING_INDEX];
            if (headingOrSortable.equalsIgnoreCase("true") || headingOrSortable.equalsIgnoreCase("false")) {
                sortable = headingOrSortable.equalsIgnoreCase("true");
                sortableOffset = 1;
            }

            int numColumnArgs = 0;
            try {
                numColumnArgs = Integer.parseInt(someArgs[i + sortableOffset + COLUMN_ARG_COUNT_INDEX]);
            }
            catch (NumberFormatException e) {
                throw new IllegalArgumentException("Bad number of column args at index " + (i + sortableOffset + COLUMN_ARG_COUNT_INDEX) + ": " + someArgs[i + COLUMN_ARG_COUNT_INDEX]);
            }

            // Ok, now we must have at least MIN_ARGS + numColumnArgs
            if ((i + sortableOffset + MIN_ARGS + numColumnArgs) > someArgs.length) {
                throw new IllegalArgumentException("Not enough arguments at index " + i);
            }

            // Now we know we have the right number of arguments to process this entry
            Class columnClass;
            Class objectClass;
            try {
                columnClass = Class.forName(someArgs[i + COLUMN_CLASS_INDEX]);
                objectClass = Class.forName(someArgs[i + OBJECT_CLASS_INDEX]);
            }
            catch (Exception e) {
                throw new IllegalArgumentException( e.toString() );
            }

            String[] columnArgs = new String[numColumnArgs];
            System.arraycopy(someArgs, i + sortableOffset + COLUMN_ARGS_INDEX, columnArgs, 0, numColumnArgs);

            EditableSortableTableColumn column =
                EditableSortableTableColumn.createColumn(columnClass, objectClass,
                        someArgs[i + ATTR_NAME_INDEX], columnArgs);

            column.setEditable( Boolean.valueOf(someArgs[i + EDIT_FLAG_INDEX]).booleanValue() );
            if (sortableOffset > 0) {
                column.setSortable(sortable);
            }
            
            column.setHeaderValue(someArgs[i + sortableOffset + HEADING_INDEX]);           

            columns.add(column);

            i += MIN_ARGS + sortableOffset + numColumnArgs;
        }

        EditableSortableTableColumn[] rawColumns = new EditableSortableTableColumn[columns.size()];
        columns.toArray(rawColumns);
        return rawColumns;
    }

    //----------------------------------------------------------------------
    /**
     * Sets the optional field name for the column. The field name is usually
     * the name of the attribute representing the column. Most "reflected" forms
     * of this sub-class implement the field name automatically.
     *
     * @param aFieldName the field name, or null if no field name is desired.
     */
    public void setFieldName(String aFieldName)
    {
        mFieldName = aFieldName;
    }

    //----------------------------------------------------------------------
    /**
     * Gets the optional field name for the column.
     *
     * @return the field name, or null if no field name has been set.
     *
     * @see setFieldName
     */
    public String getFieldName()
    {
        return mFieldName;
    }

    //----------------------------------------------------------------------
    /**
     * Sets whether this column can be edited.
     *
     * @param anEditingFlag if true, the column can be edited, otherwise it cannot.
     */
    public void setEditable(boolean anEditingFlag)
    {
        mEditable = anEditingFlag;
    }

    //----------------------------------------------------------------------
    /**
     * Answers whether this column is editable.
     *
     * @return true if the column is editable, else false.
     */
    public boolean isEditable()
    {
        return mEditable;
    }

    //----------------------------------------------------------------------
    /**
     * Sets whether this column can be sorted.
     *
     * @param aSortingFlag if true, the column can be sorted, otherwise it cannot.
     */
    public void setSortable(boolean aSortingFlag)
    {
        mSortable = aSortingFlag;
        if (!mSortable) {
            setSortDirection(SORT_NONE);
        }
    }

    //----------------------------------------------------------------------
    /**
     * Answers whether this column is sortable.
     *
     * @return true if the column is sortable, else false.
     */
    public boolean isSortable()
    {
        return mSortable;
    }

    //----------------------------------------------------------------------
    /**
     * Gets the current sort direction.
     *
     * @return the SortDirection value: one of:
     *  EditableSortableTableColumn.SORT_ASCENDING, SORT_DESCENDING, or SORT_NONE.
     */
    public SortDirection getSortDirection()
    {
        return mSortDirection;
    }

    //----------------------------------------------------------------------
    /**
     * Sets the sort direction.
     * An event is fired to listeners interested in sort order if the sort was
     * <em>not</em> changed to SORT_NONE.
     *
     * @param aDirection the SortDirection value: one of:
     *  EditableSortableTableColumn.SORT_ASCENDING, SORT_DESCENDING, or SORT_NONE.
     */
    public void setSortDirection(SortDirection aDirection)
    {
        SortDirection prevDirection = mSortDirection;
        mSortDirection = aDirection;
        if (!isSortable() || mSortListenerList.getListenerCount() == 0) {
            mSortDirection = SORT_NONE;
            mHeaderLabel.setIcon(null);
        }
        else if(mSortDirection == SORT_NONE) {
            mHeaderLabel.setIcon(SORTABLE_ICON);
        }
        else if (mSortDirection == SORT_ASCENDING) {
            mHeaderLabel.setIcon(ASCENDING_ICON);
        }
        else { // if (mSortDirection == SORT_DECENDING)
            mHeaderLabel.setIcon(DESCENDING_ICON);
        }

        /* Force repaint header and fire a change event on real change */
        if (prevDirection != mSortDirection) {
            // Forces a property change to be fired, which causes the header to repaint
            setHeaderValue( getHeaderValue() );
            if (mSortDirection != SORT_NONE) {
                fireSortOrderEvent();
            }
        }
    }

    //----------------------------------------------------------------------
    /**
     * Flips the current sort direction. Here's a mapping of what happens:<p>
     * Current Direction    Resulting Direction<p>
     * -----------------    -------------------<p>
     * SORT_NONE            SORT_ASCENDING<p>
     * SORT_ASCENDING       SORT_DESCENDING<p>
     * SORT_DESCENDING      SORT_ASCENDING<p>
     *
     * An event is fired to listeners interested in sort order.
     */
    public void flipSortDirection()
    {
        if (mSortDirection == SORT_NONE) {
            setSortDirection(SORT_ASCENDING);
        }
        else if (mSortDirection == SORT_ASCENDING) {
            setSortDirection(SORT_DESCENDING);
        }
        else { // if (mSortDirection == SORT_DECENDING)
            setSortDirection(SORT_ASCENDING);
        }
    }

    //----------------------------------------------------------------------
    /**
     * Tells listeners registered by addSortChangeListener that the column's
     * sort order has changed.
     */
    protected void fireSortOrderEvent()
    {
        if (!isSortable()) {
            return;
        }

        Object[] listeners = mSortListenerList.getListenerList();

        // Process the listeners last to first
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ChangeListener.class) {
                // Lazily create the event
                if (mSortChangeEvent == null) {
                    mSortChangeEvent = new ChangeEvent(this);
                }

                ((ChangeListener)listeners[i+1]).stateChanged(mSortChangeEvent);
            }
        }
    }

    //----------------------------------------------------------------------
    /**
     * Add a sort order change listener. Listener is notified when the sort order/
     * direction changes.
     *
     * @param aListener the listener to be added.
     */
    public void addSortChangeListener(ChangeListener aListener)
    {
        mSortListenerList.add(ChangeListener.class, aListener);
        // Force header icon to be reconfigured
        setSortDirection( getSortDirection() );
    }

    //----------------------------------------------------------------------
    /**
     * Removes a sort order change listener.
     *
     * @param aListener the listener to be removed.
     */
    public void removeSortChangeListener(ChangeListener aListener)
    {
        mSortListenerList.remove(ChangeListener.class, aListener);
        // Force header icon to be reconfigured
        setSortDirection( getSortDirection() );
    }

    //----------------------------------------------------------------------
    /**
     * Gets the current header text alignment.
     *
     * @return the HeaderTextAlignment value: one of:
     *  EditableSortableTableColumn.HEADER_LEADING (left), HEADER_TRAILING (right), or
     *  HEADER_CENTER.
     */
    public HeaderTextAlignment getHeaderTextAlignment()
    {
        return mHeaderTextAlignment;
    }

    //----------------------------------------------------------------------
    /**
     * Sets the header text alignment.
     *
     * @param aDirection the HeaderTextAlignment value: one of:
     *  EditableSortableTableColumn.HEADER_LEADING (left), HEADER_TRAILING (right), or
     *  HEADER_CENTER.
     */
    public void setHeaderTextAlignment(HeaderTextAlignment aHeaderTextAlignment)
    {
        mHeaderTextAlignment = aHeaderTextAlignment;
        if (mHeaderTextAlignment == HEADER_LEADING) {
            mHeaderLabel.setHorizontalAlignment(JLabel.LEADING);
        }
        else if (mHeaderTextAlignment == HEADER_TRAILING) {
            mHeaderLabel.setHorizontalAlignment(JLabel.TRAILING);
        }
        else { // if (mHeaderTextAlignment == HEADER_CENTER)
            mHeaderLabel.setHorizontalAlignment(JLabel.CENTER);
        }
    }

    //----------------------------------------------------------------------
    /**
     * Gets the number of clicks required to start editing a cell.
     *
     * @return the number of clicks in a cell before editing starts.
     */
    public int getNumClicksToEdit()
    {
        return mNumClicksToEdit;
    }

    //----------------------------------------------------------------------
    /**
     * Sets the number of clicks required to start editing a cell.
     *
     * @param aNumClicksToEdit the number of clicks in a cell before editing starts.
     */
    public void setNumClicksToEdit(int aNumClicksToEdit)
    {
        mNumClicksToEdit = aNumClicksToEdit;
    }

    //----------------------------------------------------------------------
    /**
     * Sets the value being edited.
     *
     * @param aValue the object being edited in the cell.
     */
    protected void setEditorValue(Object aValue)
    {
        mEditorValue = aValue;
    }

    //----------------------------------------------------------------------
    /**
     * Gets the value being edited.
     *
     * @return The object being edited in the cell.
     */
    protected Object getEditorValue()
    {
        return mEditorValue;
    }

    //----------------------------------------------------------------------
    /**
     * Calculates the preferred width of the TableColumn using the specified JComponent.
     * Modifies the TableColumn's preferredWidth. May also modify the JTable's
     * row height if the components preferred height is larger than the JTable's
     * current row height.
     *
     * @param aTable the JTable to be used for font information, etc.
     * @param aComponent a JComponent to derive the column's width from. If this is
     *  null, calculatePreferredWidth(aTable) is called to calculate the width.
     */
    public void calculatePreferredWidth(JTable aTable, JComponent aComponent)
    {
        if (aComponent == null) {
            // This method will probably call us again with a valid component.
            calculatePreferredWidth(aTable);
            return;
        }

        Dimension preferredSize = aComponent.getPreferredSize();
        int width = preferredSize.width;
        // Get size of header
        TableCellRenderer headerRenderer = this.getHeaderRenderer();
        if (headerRenderer == null) {
            headerRenderer = aTable.getTableHeader().getDefaultRenderer();
        }

        Component headerComponent = headerRenderer.getTableCellRendererComponent(null,
				getHeaderValue(), false, false, -1, -1);

        // Allow 3 pixels on either side of header, plus size of sort icon if sortable
        int headerWidth = headerComponent.getPreferredSize().width + 6;
        if (isSortable()) {
            headerWidth += ASCENDING_ICON.getIconWidth();
        }

        // If header width is larger, use that.
        if (headerWidth > width) {
            width = headerWidth;
        }

        // If component height is larger, use that
        int rowHeight = aTable.getRowHeight();
        if (preferredSize.height > rowHeight) {
            aTable.setRowHeight(preferredSize.height);
        }

        this.setPreferredWidth(width);
    }

    //----------------------------------------------------------------------
    /**
     * Calculates the preferred width of the TableColumn using the specified JTable.
     * Modifies the TableColumn's preferredWidth. It's recommended that sub-classes
     * create a Component of the proper width and in turn call
     * calculatePreferredWidth(aTable, component).
     *
     * @param aTable the JTable to be used for font information, etc.
     */
    abstract protected void calculatePreferredWidth(JTable aTable);

    //----------------------------------------------------------------------
    /**
     * Configure a renderer for the standard colors, etc.
     */
    protected void configureRenderer(JComponent aRenderer, JTable aTable,
                boolean aSelectedFlag, boolean aFocusFlag,
                int aRow, int aColumn)
    {
        aRenderer.setFont( aTable.getFont() );
        aRenderer.setOpaque(true);

        if (aSelectedFlag) {
            aRenderer.setForeground( aTable.getSelectionForeground() );
            aRenderer.setBackground( aTable.getSelectionBackground() );
        }
        else {
            aRenderer.setForeground( aTable.getForeground() );
            aRenderer.setBackground( aTable.getBackground() );
        }

        if (aFocusFlag) {
            aRenderer.setBorder( UIManager.getBorder("Table.focusCellHighlightBorder") );
        }
        else {
            aRenderer.setBorder(NO_FOCUS_BORDER);
        }
    }

    //----------------------------------------------------------------------
    // Stuff for CellEditor interface...
    //----------------------------------------------------------------------

    //----------------------------------------------------------------------
    /*
     * Notifies all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     * @see EventListenerList
     */
    protected void fireEditingStopped()
    {
        Object[] listeners = mEditorListenerList.getListenerList();

        // Process the listeners last to first
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == CellEditorListener.class) {
                // Lazily create the event
                if (mChangeEvent == null) {
                    mChangeEvent = new ChangeEvent(this);
                }

                ((CellEditorListener)listeners[i+1]).editingStopped(mChangeEvent);
            }
        }
    }

    //----------------------------------------------------------------------
    /*
     * Notifies all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     * @see EventListenerList
     */
    protected void fireEditingCanceled()
    {
        Object[] listeners = mEditorListenerList.getListenerList();

        // Process the listeners last to first
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == CellEditorListener.class) {
                // Lazily create the event
                if (mChangeEvent == null) {
                    mChangeEvent = new ChangeEvent(this);
                }

                ((CellEditorListener)listeners[i+1]).editingCanceled(mChangeEvent);
            }
        }
    }

    //----------------------------------------------------------------------
    public Object getCellEditorValue()
    {
        return getEditorValue();
    }

    //----------------------------------------------------------------------
    public boolean isCellEditable(EventObject anEvent)
    {
        if (anEvent instanceof MouseEvent &&
            ((MouseEvent)anEvent).getClickCount() < mNumClicksToEdit ) {
            return false;
        }

        return mEditable;
    }

    //----------------------------------------------------------------------
    public boolean shouldSelectCell(EventObject anEvent)
    {
        return true;
    }

    //----------------------------------------------------------------------
    /**
     * Default implementation returns true.
     * Sub-classes may implement validation before calling firingEditingStopped()
     * Sub-classes must set the column value on getEditorValue() before
     * calling firingEditingStopped()
     *
     * @return true
     */
    public boolean stopCellEditing()
    {
        return true;
    }

    //----------------------------------------------------------------------
    public void cancelCellEditing()
    {
        fireEditingCanceled();
    }

    //----------------------------------------------------------------------
    public void addCellEditorListener(CellEditorListener aListener)
    {
        mEditorListenerList.add(CellEditorListener.class, aListener);
    }

    //----------------------------------------------------------------------
    public void removeCellEditorListener(CellEditorListener aListener)
    {
        mEditorListenerList.remove(CellEditorListener.class, aListener);
    }

    //----------------------------------------------------------------------
    // ...End of Stuff for CellEditor interface
    //----------------------------------------------------------------------

    //----------------------------------------------------------------------
    /**
     * From TableCellEditor interface. Default implementation. If sub-class
     * overrides this method, it should call setEditorValue with the value
     * being edited.
     *
     * @return null.
     */
    public Component getTableCellEditorComponent(JTable aTable, Object aValue,
                        boolean isSelected, int aRow, int aColumn)
    {
        return null;
    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    /**
     * Sort Direction enumeration type.
     */
    public static final class SortDirection
    {
        private String mName;

        //----------------------------------------------------------------------
        // We're the only ones who can create these things.
        private SortDirection(String aName)
        {
            mName = aName;
        }

        //----------------------------------------------------------------------
        public String toString()
        {
            return mName;
        }
    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    /**
     * Header Text Alignment enumeration type.
     */
    public static final class HeaderTextAlignment
    {
        private String mName;

        //----------------------------------------------------------------------
        // We're the only ones who can create these things.
        private HeaderTextAlignment(String aName)
        {
            mName = aName;
        }

        //----------------------------------------------------------------------
        public String toString()
        {
            return mName;
        }
    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    /**
     * Our HeaderRenderer.
     */
    private final class ColumnHeaderRenderer implements TableCellRenderer
    {
        //----------------------------------------------------------------------
        private ColumnHeaderRenderer()
        {
        }

        //----------------------------------------------------------------------
        /**
         * From TableCellRenderer interface. This default implementation returns
         * component from getTableCellEditorComponent configured for proper
         * background/foreground colors. Sub-classes should call the configureRenderer
         * helper method prior to returning thier component. This will ensure that
         * renderers display in a consistent manner.
         *
         * @return the component from getTableCellEditorComponent.
         */
        public Component getTableCellRendererComponent(JTable aTable,
                    Object aValue, boolean aSelectedFlag, boolean aFocusFlag,
                    int aRow, int aColumn)
        {
            JTableHeader header;
            if (aTable == null) {
                if (mDefaultHeader == null) {
                    mDefaultHeader = new JTableHeader();
                }

                header = mDefaultHeader;
            }
            else {
                header = aTable.getTableHeader();
            }

            if (header != null) {
                mHeaderLabel.setForeground( header.getForeground() );
                mHeaderLabel.setBackground( header.getBackground() );
                mHeaderLabel.setFont( header.getFont() );
            }

            mHeaderLabel.setText( (aValue == null) ? "" : aValue.toString() );
            return mHeaderLabel;
        }
    }
}
