package com.netthreads.network.osc.router.table.cell;

import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.util.converter.DefaultStringConverter;

import com.netthreads.network.osc.router.model.OSCItem;
import com.netthreads.network.osc.router.service.MIDIMessageLookupImpl;

/**
 * Route type custom cell.
 * 
 * // http://www.wobblycogs.co.uk/index.php/computing/javafx/145-editing-null-
 * data-values-in-a-cell-with-javafx-2
 * 
 */
public class RouteTableCell extends ComboBoxTableCell<OSCItem, String>
{
	public RouteTableCell()
	{
		super(new DefaultStringConverter(), MIDIMessageLookupImpl.NAMES);
	}
	
	@Override
	public void updateItem(String item, boolean empty)
	{
		super.updateItem(item, false);
	}
	
}
