package com.netthreads.network.osc.router.model;

import java.util.LinkedList;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * OSC Item represents received OSC message and attributes for routing.
 * 
 * 
 */
public class OSCItem
{
	public static final String ATTR_ADDRESS = "address";
	public static final String ATTR_WORKING = "working";
	public static final String ATTR_ROUTE = "route";
	public static final String ATTR_DEVICE = "device";
	public static final String ATTR_STATUS = "status";
	
	public static final String TITLE_ADDRESS = "Address";
	public static final String TITLE_WORKING = "~";
	public static final String TITLE_ROUTE = "Route";
	public static final String TITLE_DEVICE = "Device";
	public static final String TITLE_STATUS = "Status";
	
	public static final int COMPLETE = 0;
	public static final int INCOMPLETE = 1;
	
	public static final int WORKING_READY = 0;
	public static final int WORKING_BUSY = 1;
	public static final int WORKING_DONE = 2;
	public static final int WORKING_ERROR= 3;

	public static final int STATUS_CLOSED = 0;
	public static final int STATUS_OPEN = 1;
	
	private StringProperty addressProperty;
	private IntegerProperty workingProperty;
	private StringProperty routeProperty;
	private StringProperty deviceProperty;
	private IntegerProperty statusProperty;
	
	private ObservableList<OSCValue> values;
	
	/**
	 * Construct results.
	 * 
	 */
	public OSCItem()
	{
		addressProperty = new SimpleStringProperty(this, ATTR_ADDRESS);
		workingProperty = new SimpleIntegerProperty(this, ATTR_WORKING);
		routeProperty = new SimpleStringProperty(this, ATTR_ROUTE);
		deviceProperty = new SimpleStringProperty(this, ATTR_DEVICE);
		statusProperty = new SimpleIntegerProperty(this, ATTR_STATUS);
		
		addressProperty.set("");
		workingProperty.set(WORKING_READY);
		routeProperty.set("");
		statusProperty.set(STATUS_CLOSED);
		
		values = FXCollections.synchronizedObservableList(FXCollections.observableList(new LinkedList<OSCValue>()));
	}
	
	/**
	 * Message address.
	 * 
	 * @return The address.
	 */
	public final String getAddress()
	{
		return addressProperty.get();
	}
	
	/**
	 * Set message address.
	 * 
	 * @param address
	 */
	public final void setAddress(String address)
	{
		this.addressProperty.set(address);
	}
	
	/**
	 * Working status.
	 * 
	 * @return The working status.
	 */
	public int getWorking()
	{
		return workingProperty.get();
	}
	
	/**
	 * Set working status.
	 * 
	 * @param working
	 */
	public void setWorking(int working)
	{
		this.workingProperty.set(working);
	}
	
	/**
	 * Return route.
	 * 
	 * @return The route.
	 */
	public String getRoute()
	{
		return routeProperty.get();
	}
	
	/**
	 * Set the route value.
	 * 
	 * @param item
	 */
	public void setRoute(String item)
	{
		this.routeProperty.set(item);
	}
	
	/**
	 * Return device.
	 * 
	 * @return The device.
	 */
	public String getDevice()
	{
		return deviceProperty.get();
	}
	
	/**
	 * Set the device value.
	 * 
	 * @param item
	 */
	public void setDevice(String item)
	{
		this.deviceProperty.set(item);
	}

	/**
	 * Device Status.
	 * 
	 * @return The device status.
	 */
	public int getStatus()
	{
		return statusProperty.get();
	}
	
	/**
	 * Set device status.
	 * 
	 * @param status
	 */
	public void setStatus(int status)
	{
		this.statusProperty.set(status);
	}
	
	/**
	 * Properties.
	 * 
	 */
	
	/**
	 * Return property.
	 * 
	 * @return The property.
	 */
	public final StringProperty address()
	{
		return addressProperty;
	}
	
	/**
	 * Return property.
	 * 
	 * @return The property.
	 */
	public IntegerProperty working()
	{
		return workingProperty;
	}
	
	/**
	 * Return property.
	 * 
	 * @return The property.
	 */
	public StringProperty route()
	{
		return routeProperty;
	}
	
	/**
	 * Return property.
	 * 
	 * @return The property.
	 */
	public StringProperty device()
	{
		return deviceProperty;
	}
	
	/**
	 * OSC Values
	 * 
	 * @return The list of OSC Values associated with the message.
	 */
	public ObservableList<OSCValue> getValues()
	{
		return values;
	}

	/**
	 * Return property.
	 * 
	 * @return The property.
	 */
	public IntegerProperty status()
	{
		return statusProperty;
	}
	
}
