package com.netthreads.network.osc.router.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * OSC Values
 */
public class OSCValue
{
	public static final String ATTR_TYPE = "type";
	public static final String ATTR_VALUE = "value";
	public static final String ATTR_LABEL = "label";
	
	public static final String TITLE_TYPE = "Type";
	public static final String TITLE_VALUE = "Value";
	public static final String TITLE_LABEL = "Label";
	
	private StringProperty typeProperty;
	private StringProperty valueProperty;
	private StringProperty labelProperty;
	
	/**
	 * Construct results.
	 * 
	 */
	public OSCValue()
	{
		typeProperty = new SimpleStringProperty(this, ATTR_TYPE);
		valueProperty = new SimpleStringProperty(this, ATTR_VALUE);
		labelProperty = new SimpleStringProperty(this, ATTR_LABEL);
		
		typeProperty.set("");
		valueProperty.set("");
		labelProperty.set("");
	}
	
	public final String getLabel()
	{
		return labelProperty.get();
	}
	
	public final void setLabel(String item)
	{
		this.labelProperty.set(item);
	}
	
	public final String getType()
	{
		return typeProperty.get();
	}
	
	public final void setType(String item)
	{
		this.typeProperty.set(item);
	}
	
	public final String getValue()
	{
		return valueProperty.get();
	}
	
	public final void setValue(String item)
	{
		this.valueProperty.set(item);
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
	public StringProperty type()
	{
		return typeProperty;
	}
	
	/**
	 * Return property.
	 * 
	 * @return The property.
	 */
	public StringProperty value()
	{
		return valueProperty;
	}
	
	/**
	 * Return property.
	 * 
	 * @return The property.
	 */
	public final StringProperty label()
	{
		return labelProperty;
	}
	
}
