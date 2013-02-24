package com.netthreads.network.osc.router.table.cell;

import java.io.InputStream;

import javafx.scene.control.TableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import com.netthreads.network.osc.router.definition.ApplicationStyles;
import com.netthreads.network.osc.router.model.OSCItem;

/**
 * Working status custom cell.
 * 
 */
public class DeviceStatusTableCell extends TableCell<OSCItem, Integer>
{
	private String[] ICONS =
	{
	        "/bullet_red.png", "/bullet_green.png"
	};
	
	private Image[] IMAGES =
	{
	        null, null
	};
	
	private ImageView imageView;
	private HBox hBox;
	
	/**
	 * Construct cell image holder.
	 * 
	 */
	public DeviceStatusTableCell()
	{
		imageView = new ImageView();
		
		hBox = new HBox();
		hBox.getChildren().add(imageView);
		hBox.getStyleClass().add(ApplicationStyles.STYLE_WORKING_STATUS_CELL);
	}
	
	/**
	 * This will take the value and lookup the appropriate icon for display in
	 * the cell.
	 */
	@Override
	protected void updateItem(Integer item, boolean empty)
	{
		if (!empty)
		{
			if (item < ICONS.length)
			{
				String iconName = ICONS[item];
				
				Image goImage = IMAGES[item];

				if (goImage == null)
				{
					
					InputStream stream = getClass().getResourceAsStream(iconName);
					goImage = new Image(stream);
				}
				
				imageView.setImage(goImage);
				
				setGraphic(hBox);
			}
		}
	}
	
}
