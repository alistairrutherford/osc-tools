package com.netthreads.network.osc.router.definition;

public class AssertHelper
{
	/**
	 * TODO make this better.
	 * 
	 * @param name
	 * 
	 * @return Error message.
	 */
	public static String fxmlInsertionError(String name)
	{
		String message = "fx:id=" + name + "was not injected: check your FXML file.";

		return message;
	}
}
