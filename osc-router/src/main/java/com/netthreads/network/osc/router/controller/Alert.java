package com.netthreads.network.osc.router.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBoxBuilder;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Simple Alert.
 *
 */
public class Alert extends Stage
{
	private static final String TITLE_TEXT = "Alert";
	private static final String BUTTON_TEXT = "Ok";
	
	public Alert(Stage owner, String msg)
	{
		setTitle(TITLE_TEXT);
		initOwner(owner);
		initStyle(StageStyle.UTILITY);
		initModality(Modality.APPLICATION_MODAL);
		Button btnOk = new Button(BUTTON_TEXT);
		btnOk.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent ae)
			{
				close();
			}
		});
		final Scene scene = new Scene(VBoxBuilder.create()
				.children(new Label(msg), btnOk)
				.alignment(Pos.CENTER)
				.padding(new Insets(10))
				.spacing(20.0)
				.build());
		
		setScene(scene);
		sizeToScene();
		setResizable(false);
		show();
		hide(); // needed to get proper value from scene.getWidth() and
		        // scene.getHeight()
		
		setX(owner.getX() + Math.abs(owner.getWidth() - scene.getWidth()) / 2.0);
		setY(owner.getY() + Math.abs(owner.getHeight() - scene.getHeight()) / 2.0);
	}
}