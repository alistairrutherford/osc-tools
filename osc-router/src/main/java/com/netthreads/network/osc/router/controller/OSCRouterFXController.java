package com.netthreads.network.osc.router.controller;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Callback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netthreads.network.osc.router.AppInjector;
import com.netthreads.network.osc.router.definition.ApplicationMessages;
import com.netthreads.network.osc.router.definition.AssertHelper;
import com.netthreads.network.osc.router.model.OSCItem;
import com.netthreads.network.osc.router.model.OSCValue;
import com.netthreads.network.osc.router.properties.ApplicationProperties;
import com.netthreads.network.osc.router.service.MIDIDeviceCache;
import com.netthreads.network.osc.router.service.MIDIMessageLookupImpl;
import com.netthreads.network.osc.router.service.OSCService;
import com.netthreads.network.osc.router.table.cell.DeviceStatusTableCell;
import com.netthreads.network.osc.router.table.cell.DeviceTableCell;
import com.netthreads.network.osc.router.table.cell.LabelTableCell;
import com.netthreads.network.osc.router.table.cell.RouteTableCell;
import com.netthreads.network.osc.router.table.cell.WorkingStatusTableCell;

/**
 * OSCRouter UI controller.
 * 
 */
public class OSCRouterFXController implements Initializable, ImplementsRefresh
{
	private Logger logger = LoggerFactory.getLogger(OSCRouterFXController.class);
	
	private static final String IMAGE_RUN = "/control_play_blue.png";
	private static final String IMAGE_PAUSE = "/control_pause.png";
	private static final String IMAGE_OPEN = "/document_open.png";
	private static final String IMAGE_SAVE = "/document_save.png";
	
	@FXML
	private TableView<OSCItem> dataTable;
	
	@FXML
	private TableView<OSCValue> valueTable;
	
	@FXML
	private TextField portTextField;
	
	@FXML
	private Button openButton;
	
	@FXML
	private Button saveButton;
	
	@FXML
	private Button activateButton;
	
	@FXML
	private SplitPane mainSplitPane;
	
	private FileChooser fileChooser;
	
	// Model list
	private LinkedList<OSCItem> list;
	private ObservableList<OSCItem> observableList;
	
	private OSCService oscService;
	
	private MIDIDeviceCache midiDeviceCache;
	
	private Stage stage;
	
	private ApplicationProperties applicationProperties;
	
	private ImageView[] activateButtonStates;
	private ImageView[] openButtonStates;
	private ImageView[] saveButtonStates;
	
	private boolean refreshing;
	
	/**
	 * Construct controller.
	 * 
	 */
	public OSCRouterFXController()
	{
		fileChooser = new FileChooser();

		// Create observable list.
		list = new LinkedList<OSCItem>();
		
		observableList = FXCollections.synchronizedObservableList(FXCollections.observableList(list));
		
		applicationProperties = AppInjector.getInjector().getInstance(ApplicationProperties.class);
		
		midiDeviceCache = AppInjector.getInjector().getInstance(MIDIDeviceCache.class);
		
		openButtonStates = new ImageView[2];
		saveButtonStates = new ImageView[2];
		activateButtonStates = new ImageView[2];
	}
	
	/**
	 * Initialise controller.
	 * 
	 */
	@Override
	public void initialize(URL url, ResourceBundle rsrcs)
	{
		logger.debug("initialize");
		
		assert dataTable != null : AssertHelper.fxmlInsertionError("dataTable");
		assert portTextField != null : AssertHelper.fxmlInsertionError("portTextField");
		assert openButton != null : AssertHelper.fxmlInsertionError("openButton");
		assert saveButton != null : AssertHelper.fxmlInsertionError("saveButton");
		assert activateButton != null : AssertHelper.fxmlInsertionError("activateButton");
		assert mainSplitPane != null : AssertHelper.fxmlInsertionError("mainSplitPane");
		
		mainSplitPane.setDividerPosition(0, 0.6);
		
		// Assemble Tables
		buildDataTable(dataTable);
		
		buildValueTable(valueTable);
		
		// ---------------------------------------------------------------
		// Activate button.
		// ---------------------------------------------------------------
		
		// Go button graphic(s)
		InputStream stream = getClass().getResourceAsStream(IMAGE_RUN);
		Image image = new Image(stream);
		ImageView imageView = new ImageView(image);
		activateButtonStates[0] = imageView;
		
		stream = getClass().getResourceAsStream(IMAGE_PAUSE);
		image = new Image(stream);
		imageView = new ImageView(image);
		activateButtonStates[1] = imageView;
		
		activateButton.setGraphic(activateButtonStates[0]);
		
		// ---------------------------------------------------------------
		// Open button.
		// ---------------------------------------------------------------
		stream = getClass().getResourceAsStream(IMAGE_OPEN);
		image = new Image(stream);
		imageView = new ImageView(image);
		openButtonStates[0] = imageView;
		
		openButton.setGraphic(openButtonStates[0]);
		
		// ---------------------------------------------------------------
		// Save button.
		// ---------------------------------------------------------------
		stream = getClass().getResourceAsStream(IMAGE_SAVE);
		image = new Image(stream);
		imageView = new ImageView(image);
		saveButtonStates[0] = imageView;
		
		saveButton.setGraphic(saveButtonStates[0]);
		
		// Set UI
		String portValue = String.valueOf(applicationProperties.getPort());
		portTextField.setText(portValue);
		
		// TODO Remove
		// testValues();
	}
	
	/**
	 * Test method to create dummy entry we can develop our interface against.
	 */
	@SuppressWarnings("unused")
	private void testValues()
	{
		// ----------------------------------------------------------------
		// ----------------------------------------------------------------
		// TEST
		// ----------------------------------------------------------------
		// ----------------------------------------------------------------
		
		OSCItem testItem = new OSCItem();
		testItem.setAddress("test");
		testItem.setWorking(0);
		testItem.setRoute(MIDIMessageLookupImpl.NAMES[0]);
		
		ObservableList<OSCValue> testValues = testItem.getValues();
		for (int i = 0; i < 3; i++)
		{
			OSCValue value = new OSCValue();
			value.setValue(String.valueOf(i));
			testValues.add(value);
		}
		
		list.add(testItem);
		
		valueTable.setItems(testValues);
	}
	
	/**
	 * Activate Button Action Handler.
	 * 
	 * @param event
	 */
	public void activateButtonAction(ActionEvent event)
	{
		logger.debug("activateButtonAction");
		
		String portValue = portTextField.getText();
		
		if (portValue == null || portValue.isEmpty())
		{
			// Alert
			Alert alert = new Alert(stage, ApplicationMessages.MSG_ERROR_INVALID_PORT);
			
			alert.showAndWait();
		}
		else
		{
			try
			{
				int port = Integer.valueOf(portValue);
				
				// Perform operation and alter icon depending on active state.
				
				if (oscService.getActive())
				{
					// Stop service.
					oscService.cancel();
					
					activateButton.setGraphic(activateButtonStates[0]);
				}
				else
				{
					activateButton.setGraphic(activateButtonStates[1]);
					
					// Start service.
					oscService.process(port);
				}
			}
			catch (NumberFormatException e)
			{
				Alert alert = new Alert(stage, ApplicationMessages.MSG_ERROR_INVALID_PORT);
				
				alert.showAndWait();
			}
		}
	}
	
	/**
	 * Open Button Action Handler.
	 * 
	 * @param event
	 */
	public void openButtonAction(ActionEvent event)
	{
		logger.debug("openButtonAction");
		
		Window window = getWindow(openButton);
		
		if (window != null)
		{
			File directory = fileChooser.showOpenDialog(window);
			
			if (directory != null)
			{
				try
				{
					oscService.load(directory.getAbsolutePath());
					
					refresh();
				}
				catch (Exception exception)
				{
					// Alert
					Alert alert = new Alert(stage, ApplicationMessages.MSG_ERROR_INVALID_FILE_LOAD);
					
					alert.showAndWait();
				}
				
			}
		}
	}
	
	/**
	 * Open Button Action Handler.
	 * 
	 * @param event
	 */
	public void saveButtonAction(ActionEvent event)
	{
		logger.debug("saveButtonAction");
		
		Window window = getWindow(saveButton);
		
		if (window != null)
		{
			File directory = fileChooser.showSaveDialog(window);
			
			if (directory != null)
			{
				try
				{
					oscService.save(directory.getAbsolutePath());
					
					refresh();
				}
				catch (Exception exception)
				{
					// Alert
					Alert alert = new Alert(stage, ApplicationMessages.MSG_ERROR_INVALID_FILE_LOAD);
					
					alert.showAndWait();
				}
				
			}
		}
	}
	
	/**
	 * Build message table columns.
	 * 
	 * @param dataTable
	 */
	@SuppressWarnings("unchecked")
	private void buildDataTable(TableView<OSCItem> dataTable)
	{
		dataTable.setEditable(true);
		
		// ---------------------------------------------------------------
		// Build columns.
		// ---------------------------------------------------------------
		
		// ---------------------------------------------------------------
		// Address
		// ---------------------------------------------------------------
		TableColumn<OSCItem, String> addressCol = new TableColumn<OSCItem, String>(OSCItem.TITLE_ADDRESS);
		addressCol.setCellValueFactory(new PropertyValueFactory<OSCItem, String>(OSCItem.ATTR_ADDRESS));
		
		// ---------------------------------------------------------------
		// Route indicator
		// ---------------------------------------------------------------
		TableColumn<OSCItem, String> routeCol = new TableColumn<OSCItem, String>(OSCItem.TITLE_ROUTE);
		routeCol.setCellValueFactory(new PropertyValueFactory<OSCItem, String>(OSCItem.ATTR_ROUTE));
		
		// Custom Cell factory converts index to image.
		routeCol.setCellFactory(new Callback<TableColumn<OSCItem, String>, TableCell<OSCItem, String>>()
		{
			@Override
			public TableCell<OSCItem, String> call(TableColumn<OSCItem, String> item)
			{
				RouteTableCell cell = new RouteTableCell();
				
				// Can't edit when the service is running.
				cell.editableProperty().bind(oscService.runningProperty().not());
				
				return cell;
			}
		});
		
		routeCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<OSCItem, String>>()
		{
			@Override
			public void handle(TableColumn.CellEditEvent<OSCItem, String> t)
			{
				t.getRowValue().setRoute(t.getNewValue());
			}
		});
		
		// ---------------------------------------------------------------
		// Device Choice
		// ---------------------------------------------------------------
		final String[] deviceNames = midiDeviceCache.getNames();
		
		TableColumn<OSCItem, String> deviceCol = new TableColumn<OSCItem, String>(OSCItem.TITLE_DEVICE);
		deviceCol.setCellValueFactory(new PropertyValueFactory<OSCItem, String>(OSCItem.ATTR_DEVICE));
		
		// Custom Cell factory converts index to image.
		deviceCol.setCellFactory(new Callback<TableColumn<OSCItem, String>, TableCell<OSCItem, String>>()
		{
			@Override
			public TableCell<OSCItem, String> call(TableColumn<OSCItem, String> item)
			{
				DeviceTableCell cell = new DeviceTableCell(deviceNames);
				
				// Can't edit when the service is running.
				cell.editableProperty().bind(oscService.runningProperty().not());
				
				return cell;
			}
		});
		
		deviceCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<OSCItem, String>>()
		{
			@Override
			public void handle(TableColumn.CellEditEvent<OSCItem, String> t)
			{
				// Deselect old device.
				String oldName = t.getOldValue();
				
				midiDeviceCache.select(oldName, false);
				
				// Select new device.
				String newName = t.getNewValue();
				
				t.getRowValue().setDevice(newName);
				
				midiDeviceCache.select(newName, true);
			}
		});
		
		// ---------------------------------------------------------------
		// Device Status
		// ---------------------------------------------------------------
		TableColumn<OSCItem, Integer> deviceStatusCol = new TableColumn<OSCItem, Integer>(OSCItem.TITLE_STATUS);
		deviceStatusCol.setCellValueFactory(new PropertyValueFactory<OSCItem, Integer>(OSCItem.ATTR_STATUS));
		
		// Custom Cell factory converts index to image.
		deviceStatusCol.setCellFactory(new Callback<TableColumn<OSCItem, Integer>, TableCell<OSCItem, Integer>>()
		{
			@Override
			public TableCell<OSCItem, Integer> call(TableColumn<OSCItem, Integer> item)
			{
				DeviceStatusTableCell cell = new DeviceStatusTableCell();
				
				return cell;
			}
		});
		
		// ---------------------------------------------------------------
		// Working indicator
		// ---------------------------------------------------------------
		TableColumn<OSCItem, Integer> workingCol = new TableColumn<OSCItem, Integer>(OSCItem.TITLE_WORKING);
		workingCol.setCellValueFactory(new PropertyValueFactory<OSCItem, Integer>(OSCItem.ATTR_WORKING));
		
		// Custom Cell factory converts index to image.
		workingCol.setCellFactory(new Callback<TableColumn<OSCItem, Integer>, TableCell<OSCItem, Integer>>()
		{
			@Override
			public TableCell<OSCItem, Integer> call(TableColumn<OSCItem, Integer> item)
			{
				WorkingStatusTableCell cell = new WorkingStatusTableCell();
				
				return cell;
			}
		});
		
		// ---------------------------------------------------------------
		// Set widths and bind to data table width.
		// ---------------------------------------------------------------
		addressCol.prefWidthProperty().bind(dataTable.widthProperty().divide(4));
		routeCol.prefWidthProperty().bind(dataTable.widthProperty().divide(6));
		deviceCol.prefWidthProperty().bind(dataTable.widthProperty().divide(5));
		deviceStatusCol.prefWidthProperty().bind(dataTable.widthProperty().divide(5));
		workingCol.prefWidthProperty().bind(dataTable.widthProperty().divide(5));
		
		// ---------------------------------------------------------------
		// Add columns.
		// ---------------------------------------------------------------
		dataTable.getColumns().setAll(addressCol, routeCol, deviceCol, deviceStatusCol, workingCol);
		
		// ---------------------------------------------------------------
		// Selection handler.
		// ---------------------------------------------------------------
		dataTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<OSCItem>()
		{
			public void changed(javafx.beans.value.ObservableValue<? extends OSCItem> ov, OSCItem oldValue, OSCItem newValue)
			{
				valueTable.setItems(newValue.getValues());
			};
		});
		
		// ---------------------------------------------------------------
		// Assign list
		// ---------------------------------------------------------------
		dataTable.setItems(observableList);
	}
	
	/**
	 * Build values table columns.
	 * 
	 * @param dataTable
	 */
	@SuppressWarnings("unchecked")
	private void buildValueTable(TableView<OSCValue> valueTable)
	{
		valueTable.setEditable(true);
		
		// ---------------------------------------------------------------
		// ---------------------------------------------------------------
		// Build columns.
		// ---------------------------------------------------------------
		// ---------------------------------------------------------------
		
		// ---------------------------------------------------------------
		// Argument
		// ---------------------------------------------------------------
		TableColumn<OSCValue, String> typeCol = new TableColumn<OSCValue, String>(OSCValue.TITLE_TYPE);
		
		typeCol.setCellValueFactory(new PropertyValueFactory<OSCValue, String>(OSCValue.ATTR_TYPE));
		
		// ---------------------------------------------------------------
		// Value
		// ---------------------------------------------------------------
		TableColumn<OSCValue, String> valueCol = new TableColumn<OSCValue, String>(OSCValue.TITLE_VALUE);
		
		valueCol.setCellValueFactory(new PropertyValueFactory<OSCValue, String>(OSCValue.ATTR_VALUE));
		
		// ---------------------------------------------------------------
		// Labels
		// ---------------------------------------------------------------
		TableColumn<OSCValue, String> labelCol = new TableColumn<OSCValue, String>(OSCValue.TITLE_LABEL);
		labelCol.setCellValueFactory(new PropertyValueFactory<OSCValue, String>(OSCValue.ATTR_LABEL));
		
		// Add Label editor
		labelCol.setCellFactory(new Callback<TableColumn<OSCValue, String>, TableCell<OSCValue, String>>()
		{
			@Override
			public TableCell<OSCValue, String> call(TableColumn<OSCValue, String> p)
			{
				LabelTableCell<OSCValue, String> cell = new LabelTableCell<OSCValue, String>();
				
				return cell;
			}
		});
		// Add label commit change handler.
		labelCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<OSCValue, String>>()
		{
			@Override
			public void handle(TableColumn.CellEditEvent<OSCValue, String> t)
			{
				t.getRowValue().setLabel(t.getNewValue());
			}
		});
		
		// ---------------------------------------------------------------
		// ---------------------------------------------------------------
		// Set widths and bind to data table width.
		// ---------------------------------------------------------------
		// ---------------------------------------------------------------
		typeCol.prefWidthProperty().bind(dataTable.widthProperty().divide(5));
		valueCol.prefWidthProperty().bind(dataTable.widthProperty().divide(5));
		labelCol.prefWidthProperty().bind(dataTable.widthProperty().divide(5));
		
		// ---------------------------------------------------------------
		// ---------------------------------------------------------------
		// Add columns.
		// ---------------------------------------------------------------
		// ---------------------------------------------------------------
		valueTable.getColumns().setAll(typeCol, valueCol, labelCol);
	}
	
	/**
	 * Controller client act as an intermediary between the workers and the UI
	 * controller.
	 * 
	 * @param oscService
	 */
	public void setService(OSCService oscService)
	{
		this.oscService = oscService;
	}
	
	/**
	 * Return list object.
	 * 
	 * @return The list object.
	 */
	public ObservableList<OSCItem> getObservableList()
	{
		return observableList;
	}
	
	/**
	 * Assign stage.
	 * 
	 * @param stage
	 */
	public void setStage(Stage stage)
	{
		this.stage = stage;
	}
	
	public synchronized boolean isRefreshing()
	{
		return refreshing;
	}
	
	public synchronized void setRefreshing(boolean refreshing)
	{
		this.refreshing = refreshing;
	}
	
	/**
	 * Get window from node.
	 * 
	 * @param node
	 * 
	 * @return The node Window.
	 */
	private Window getWindow(Node node)
	{
		Window window = null;
		
		Scene scene = node.getScene();
		
		if (scene != null)
		{
			window = scene.getWindow();
		}
		
		return window;
	}
	
	/**
	 * Trick to force data table refresh.
	 * 
	 */
	@Override
	public void refresh()
	{
		Platform.runLater(new Runnable()
		{
			/**
			 * Run later. Note we can't hammer the UI with refresh requests all
			 * the time as it caused the UI interaction to become sluggish.
			 * 
			 */
			public void run()
			{
				if (!isRefreshing())
				{
					setRefreshing(true);
					
					refreshDataTable(dataTable);
					refreshValueTable(valueTable);
					
					setRefreshing(false);
				}
			}
			
			/**
			 * Refresh data table.
			 * 
			 * @param tableView
			 */
			private void refreshDataTable(TableView<OSCItem> tableView)
			{
				ObservableList<TableColumn<OSCItem, ?>> columns = tableView.getColumns();
				TableColumn<OSCItem, ?> column = columns.get(0);
				
				if (column != null)
				{
					column.setVisible(false);
					column.setVisible(true);
				}
				
			}
			
			/**
			 * Refresh values table.
			 * 
			 * @param tableView
			 */
			private void refreshValueTable(TableView<OSCValue> tableView)
			{
				ObservableList<TableColumn<OSCValue, ?>> columns = tableView.getColumns();
				TableColumn<OSCValue, ?> column = columns.get(0);
				
				if (column != null)
				{
					column.setVisible(false);
					column.setVisible(true);
				}
				
			}
			
		});
		
	}
	
}
