package com.netthreads.network.osc.router.service;

import com.google.inject.ImplementedBy;

/**
 * We define interface in case we choose to inject it.
 * 
 */
@ImplementedBy(MIDIMessageLookupImpl.class)
public interface MIDIMessageLookup
{
	/**
	 * Return MIDI message against name.
	 * 
	 * @param name
	 *            The message name.
	 * 
	 * @return The message.
	 */
	int getMessage(String name);
	
	/**
	 * Return the number of expected parameters.
	 * 
	 * @param name
	 *            The message name.
	 * 
	 * @return The parameters count.
	 */
	public int getParametersCount(String name);
}
