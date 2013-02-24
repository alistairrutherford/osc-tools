/**
 * (c) - Alistair Rutherford - www.netthreads.co.uk
 */
package com.netthreads.network.osc.router;

import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import com.netthreads.network.osc.router.controller.OSCRouterFXController;
import com.netthreads.network.osc.router.service.OSCService;

/**
 * Mavenize tool GUI.
 * 
 */
public class OSCRouterFX extends Application
{
	public static final String APPLICATION_TITLE = "OSC Router";
	public static final String FXML_FILE = "/routerui.fxml";
	public static final String CSS_FILE = "/routerui";
	
	public static final String ID_ROOT = "root";
	
	private OSCService oscService;
	private OSCRouterFXController oscRouterFXController;
	
	/**
	 * Load layout and display.
	 * 
	 */
	@Override
	public void start(Stage stage) throws Exception
	{
		// ---------------------------------------------------------------
		// Resources
		// ---------------------------------------------------------------
		URL layoutURL = getClass().getResource(FXML_FILE);
		URL cssURL = loadCSS(CSS_FILE);
		
		// ---------------------------------------------------------------
		// Load view.
		// ---------------------------------------------------------------
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setLocation(layoutURL);
		fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
		
		Parent root = (Parent) fxmlLoader.load(layoutURL.openStream());
		root.setId(ID_ROOT);
		
		// ---------------------------------------------------------------
		// Get controller and assign model object
		// ---------------------------------------------------------------
		oscRouterFXController = fxmlLoader.getController();
		
		// ---------------------------------------------------------------
		// Client/Model
		// ---------------------------------------------------------------
		oscService = new OSCService(oscRouterFXController.getObservableList(), oscRouterFXController);
		
		// ---------------------------------------------------------------
		// Controller
		// ---------------------------------------------------------------
		oscRouterFXController.setService(oscService);
		oscRouterFXController.setStage(stage);
		
		// View.
		stage.setTitle(APPLICATION_TITLE);
		
		// Scene
		Scene scene = new Scene(root, 780, 400);
		
		scene.getStylesheets().addAll(cssURL.toExternalForm());
		
		stage.setScene(scene);
		
		stage.show();
	}
	
	@Override
	public void stop() throws Exception
	{
		// Kill service.
		
	    oscService.cancel();
	}
	
	/**
	 * JavaFx binary encodes CSS so check for both.
	 * 
	 * @param resource
	 * 
	 * @return The URL.
	 */
	private URL loadCSS(String resource)
	{
		URL cssURL = getClass().getResource(resource + ".css");
		if (cssURL == null)
		{
			cssURL = getClass().getResource(resource + ".bss");
		}
		
		return cssURL;
	}
	
	/**
	 * Main method.
	 * 
	 * @param args
	 *            Command line arguments
	 */
	public static void main(String[] args)
	{
		Application.launch(args);
	}
	
}
