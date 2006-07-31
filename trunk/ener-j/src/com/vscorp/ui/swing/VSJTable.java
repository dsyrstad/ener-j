/*+
 * Copyright 1998 Visual Systems Corporation
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Neither the name "Visual Systems" nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY VISUAL SYSTEMS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL VISUAL SYSTEMS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *-
 */

package com.vscorp.ui.swing;

import java.awt.Component;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import com.vscorp.ui.swing.table.TableSorter;

/** 
 * Extensions to JTable to provide additional features
 *
 * @author Daniel A. Syrstad
 */
public class VSJTable extends JTable {
	//----------------------------------------------------------------------
	// Constructors which duplicate JTable(...)
	//----------------------------------------------------------------------

	//----------------------------------------------------------------------
	/**
	 * Constructs a default VSJTable which is initialized with a default
	 * data model, a default column model, and a default selection
	 * model.
	 *
	 * @see #createDefaultDataModel()
	 * @see #createDefaultColumnModel()
	 * @see #createDefaultSelectionModel()
	 */
	public VSJTable() {
		super();
	}

	//----------------------------------------------------------------------
	/**
	 * Constructs a VSJTable which is initialized with <i>dm</i> as the
	 * data model, a default column model, and a default selection
	 * model.
	 *
	 * @param dm        The data model for the table
	 * @see #createDefaultColumnModel()
	 * @see #createDefaultSelectionModel()
	 */
	public VSJTable(TableModel dm) {
		super(dm);
	}

	//----------------------------------------------------------------------
	/**
	 * Constructs a VSJTable which is initialized with <i>dm</i> as the
	 * data model, <i>cm</i> as the column model, and a default selection
	 * model.
	 *
	 * @param dm        The data model for the table
	 * @param cm        The column model for the table
	 * @see #createDefaultSelectionModel()
	 */
	public VSJTable(TableModel dm, TableColumnModel cm) {
		super(dm, cm);
	}

	//----------------------------------------------------------------------
	/**
	 * Constructs a VSJTable which is initialized with <i>dm</i> as the
	 * data model, <i>cm</i> as the column model, and <i>sm</i> as the
	 * selection model.  If any of the parameters are <b>null</b> this
	 * method will initialize the table with the corresponding
	 * default model.
	 *
	 * @param dm        The data model for the table
	 * @param cm        The column model for the table
	 * @param sm        The row selection model for the table
	 * @see #createDefaultDataModel()
	 * @see #createDefaultColumnModel()
	 * @see #createDefaultSelectionModel()
	 */
	public VSJTable(TableModel dm, TableColumnModel cm, ListSelectionModel sm) {
		super(dm, cm, sm);
	}

	//----------------------------------------------------------------------
	/**
	 * Constructs a VSJTable with <i>numColumns</i> and <i>numRows</i> of
	 * empty cells using the DefaultTableModel.  The columns will have
	 * names of the form "A", "B", "C", etc.
	 *
	 * @param numColumns        The number of columns the table holds
	 * @param numRows           The number of rows the table holds
	 * @see com.sun.java.swing.table.DefaultTableModel
	 */
	public VSJTable(int numColumns, int numRows) {
		super(numColumns, numRows);
	}

	//----------------------------------------------------------------------
	/**
	 * Constructs a VSJTable using a DefaultTableModel and initialize the table
	 * with the values in the <i>data</i> Vector.  The vectors contained
	 * in the outer vector should each contain a single row of values.
	 * In other words, the value of the cell at column 5, row 1
	 * can be obtain with the follow code: <p>
	 *
	 * <code>((Vector)data.elementAt(1)).elementAt(5);</code>
	 *
	 * @param data              The data for the new table
	 * @param columnNames       Names of each column
	 * @exception IllegalArgumentException if data is null or if the number
	 *                          of columns in data does not equal the
	 *                          number of names in columnNames.
	 */
	public VSJTable(Vector data, Vector columnNames) {
		super(data, columnNames);
	}

	//----------------------------------------------------------------------
	/**
	 * Constructs a VSJTable using a DefaultTableModel and initialize the
	 * table with the values in the <i>data</i> array.  The first index
	 * in the Object[][] is the row index and the second is
	 * the column index.
	 *
	 * @param data              The data for the new table
	 * @param columnNames       Names of each column
	 * @exception IllegalArgumentException if data is null or if the number
	 *                          of columns in data does not equal the
	 *                          number of names in columnNames.
	 */
	public VSJTable(Object[][] data, Object[] columnNames) {
		super(data, columnNames);
	}

	//----------------------------------------------------------------------
	/** Adds a column sorter model (TableSorter) to the table. It interposes
	 * the VSTableSorter model inbetween the current model and the table.
	 */
	public void addColumnSorter() {
		TableSorter tbl_sorter = new TableSorter(getModel(), true);
		setModel(tbl_sorter);
		tbl_sorter.addMouseListenerToHeaderInTable(this);
	}

	//----------------------------------------------------------------------
	/** Resize all column headers to the size of the header label.
	 * If tooltips is true, a tooltip is added to each header with the name
	 * of the column.
	 */
	public void resizeHeaders(boolean tooltips) {
		int ncolumns = this.getColumnCount();
		for (int i = 0; i < ncolumns; i++) {
			TableColumn column = this.getColumnModel().getColumn(i);
			Object header_value = column.getHeaderValue();

			JComponent comp = (JComponent)column.getHeaderRenderer().
								getTableCellRendererComponent(null,
									header_value,
									false, false, 0, 0);

			if (tooltips)
				comp.setToolTipText(header_value.toString());

			column.setPreferredWidth(comp.getPreferredSize().width + 8);
		}
	}

	//----------------------------------------------------------------------
	/** Resize all columns to the maximum width of data in each column.
	 * If checkHeaders is true and the header is larger than maximum data width,
	 * the column sized to the size of the header.
	 */
	public void resizeColumnWidths(boolean checkHeaders) {
		int ncolumns = this.getColumnCount();
		for (int i = 0; i < ncolumns; i++)
			resizeColumnWidth(i, checkHeaders);
	}

	//----------------------------------------------------------------------
	/** Resize the specified column to the maximum width of data in the column.
	 * If checkHeaders is true and the header is larger than maximum data width,
	 * the column sized to the size of the header.
	 */
	public void resizeColumnWidth(int col, boolean checkHeader) {
		int nrows = this.getRowCount();
		int maxCellWidth = 0;
		int cellWidth;
		TableColumn column = this.getColumnModel().getColumn(col);

		for (int i = 0; i < nrows; i++) {
			Object cellValue = this.getValueAt(i, col);

			TableCellRenderer cellrend = (TableCellRenderer)this.getCellRenderer(i, col);

			Component comp = (Component)cellrend.getTableCellRendererComponent(this,
									  cellValue, false, false, i, col);

			// Get width of cell in pixels. Keep track of max nbr of pixels
			cellWidth = comp.getPreferredSize().width;
			if (cellWidth > maxCellWidth)
				maxCellWidth = cellWidth;
		}

		// Check to see if column header pixel width is larger than
		// maxCellWidth.
		if (checkHeader) {
			TableColumn header = this.getColumnModel().getColumn(col);
			Object headerValue = column.getHeaderValue();

			JComponent comp = (JComponent)column.getHeaderRenderer().
									 getTableCellRendererComponent(null,
																   headerValue,
																   false,
																   false, 0, 0);
			int headerWidth = comp.getPreferredSize().width + 8;

			if (headerWidth > maxCellWidth)
				maxCellWidth = headerWidth;
		}

		column.setPreferredWidth(maxCellWidth);

		// Swing Bug 1.1beta3: Headers don't always repaint - force it
		Component hdr = column.getHeaderRenderer().
							getTableCellRendererComponent(this, null, false, false, -1, col);
		hdr.repaint();
	}
}
