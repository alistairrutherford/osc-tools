package com.netthreads.network.osc.router.table.cell;

import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.util.converter.DefaultStringConverter;

import com.netthreads.network.osc.router.model.OSCItem;

/**
 * Custom cell.
 * 
 * // http://www.wobblycogs.co.uk/index.php/computing/javafx/145-editing-null-
 * data-values-in-a-cell-with-javafx-2
 * 
 */
public class DeviceTableCell extends ComboBoxTableCell<OSCItem, String>
{
	public DeviceTableCell(String[] devices)
	{
		super(new DefaultStringConverter(), devices);
	}
	
	@Override
	public void updateItem(String item, boolean empty)
	{
		super.updateItem(item, false);
	}
	
}
