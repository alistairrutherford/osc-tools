package com.netthreads.network.osc.router.table.cell;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * http://www.wobblycogs.co.uk/index.php/computing/javafx/145-editing-null-
 * data-values-in-a-cell-with-javafx-2
 * 
 * Provides the basis for an editable table cell using a text field. Sub-classes
 * can provide formatters for display and a commitHelper to control when editing
 * is committed.
 * 
 * @author Graham Smith
 */
public abstract class EditableTextTableCell<S, T> extends TableCell<S, T>
{
	protected TextField textField;
	
	@Override
	public void startEdit()
	{
		super.startEdit();
		if (textField == null)
		{
			createTextField();
		}
		setGraphic(textField);
		setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		Platform.runLater(new Runnable()
		{
			@Override
			public void run()
			{
				textField.selectAll();
				textField.requestFocus();
			}
		});
	}
	
	@Override
	public void cancelEdit()
	{
		super.cancelEdit();
		setText(getString());
		setContentDisplay(ContentDisplay.TEXT_ONLY);
		// Once the edit has been cancelled we no longer need the text field
		// so we mark it for cleanup here. Note though that you have to handle
		// this situation in the focus listener which gets fired at the end
		// of the editing.
		textField = null;
	}
	
	@Override
	public void updateItem(T item, boolean empty)
	{
		super.updateItem(item, empty);
		if (empty)
		{
			setText(null);
			setGraphic(null);
		}
		else
		{
			if (isEditing())
			{
				if (textField != null)
				{
					textField.setText(getString());
				}
				setGraphic(textField);
				setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
			}
			else
			{
				setText(getString());
				setContentDisplay(ContentDisplay.TEXT_ONLY);
			}
		}
	}
	
	/**
	 * Create editable text field and hook into handlers.
	 * 
	 */
	private void createTextField()
	{
		textField = new TextField(getString());
		textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
		textField.setOnKeyPressed(new EventHandler<KeyEvent>()
		{
			@SuppressWarnings(
			{
			        "rawtypes", "unchecked"
			})
			@Override
			public void handle(KeyEvent t)
			{
				if (t.getCode() == KeyCode.ENTER)
				{
					commitHelper(false);
				}
				else if (t.getCode() == KeyCode.ESCAPE)
				{
					cancelEdit();
				}
				else if (t.getCode() == KeyCode.TAB)
				{
					commitHelper(false);
					
					TableColumn nextColumn = getNextColumn(!t.isShiftDown());
					if (nextColumn != null)
					{
						getTableView().edit(getTableRow().getIndex(), nextColumn);
					}
				}
			}
		});
		textField.focusedProperty().addListener(new ChangeListener<Boolean>()
		{
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)
			{
				// This focus listener fires at the end of cell editing when
				// focus is lost
				// and when enter is pressed (because that causes the text field
				// to lose focus).
				// The problem is that if enter is pressed then cancelEdit is
				// called before this
				// listener runs and therefore the text field has been cleaned
				// up. If the
				// text field is null we don't commit the edit. This has the
				// useful side effect
				// of stopping the double commit.
				if (!newValue && textField != null)
				{
					commitHelper(true);
				}
			}
		});
	}
	
	/**
	 * Return target column.
	 * 
	 * @param forward
	 *            true gets the column to the right, false the column to the
	 *            left of the current column
	 * 
	 * @return Return column.
	 */
	private TableColumn<S, ?> getNextColumn(boolean forward)
	{
		List<TableColumn<S, ?>> columns = new ArrayList<>();
		for (TableColumn<S, ?> column : getTableView().getColumns())
		{
			columns.addAll(getLeaves(column));
		}
		// There is no other column that supports editing.
		if (columns.size() < 2)
		{
			return null;
		}
		int currentIndex = columns.indexOf(getTableColumn());
		int nextIndex = currentIndex;
		if (forward)
		{
			nextIndex++;
			if (nextIndex > columns.size() - 1)
			{
				nextIndex = 0;
			}
		}
		else
		{
			nextIndex--;
			if (nextIndex < 0)
			{
				nextIndex = columns.size() - 1;
			}
		}
		return columns.get(nextIndex);
	}
	
	/**
	 * Return column leaves.
	 * 
	 * @param root
	 * 
	 * @return the column leaves.
	 */
	private List<TableColumn<S, ?>> getLeaves(TableColumn<S, ?> root)
	{
		List<TableColumn<S, ?>> columns = new ArrayList<>();
		if (root.getColumns().isEmpty())
		{
			// We only want the leaves that are editable.
			if (root.isEditable())
			{
				columns.add(root);
			}
			return columns;
		}
		else
		{
			for (TableColumn<S, ?> column : root.getColumns())
			{
				columns.addAll(getLeaves(column));
			}
			return columns;
		}
	}

	/**
	 * Return text field for input.
	 * 
	 * @return
	 */
	public TextField getTextField()
    {
    	return textField;
    }
	
	/**
	 * Any action attempting to commit an edit should call this method rather
	 * than commit the edit directly itself. This method will perform any
	 * validation and conversion required on the value. For text values that
	 * normally means this method just commits the edit but for numeric values,
	 * for example, it may first parse the given input.
	 * <p>
	 * The only situation that needs to be treated specially is when the field
	 * is losing focus. If you user hits enter to commit the cell with bad data
	 * we can happily cancel the commit and force them to enter a real value. If
	 * they click away from the cell though we want to give them their old value
	 * back.
	 * 
	 * @param losingFocus
	 *            true if the reason for the call was because the field is
	 *            losing focus.
	 */
	protected abstract void commitHelper(boolean losingFocus);
	
	/**
	 * Provides the string representation of the value of this cell when the
	 * cell is not being edited.
	 */
	protected abstract String getString();
	
}