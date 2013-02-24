package com.netthreads.network.osc.router.table.cell;


/**
 * This is based on the edit cell from:
 * 
 * http://www.wobblycogs.co.uk/index.php/computing/javafx/145-editing-null-
 * data-values-in-a-cell-with-javafx-2
 * 
 */
public class LabelTableCell<S, T> extends EditableTextTableCell<S, T>
{
	@Override
	protected String getString()
	{
		return getItem() == null ? "" : getItem().toString();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	protected void commitHelper(boolean losingFocus)
	{
		T value = (T) getTextField().getText();
		
		commitEdit(value);
	}
	
	/**
	 * We override to allow null values.
	 * 
	 */
	@Override
	public void updateItem(T item, boolean empty)
	{
		super.updateItem(item, false);
	};
}