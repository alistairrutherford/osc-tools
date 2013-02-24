package com.netthreads.network.osc.router.service;

import java.util.Collection;

import javax.sound.midi.MidiDevice;

import com.google.inject.ImplementedBy;

/**
 * We define interface in case we choose to inject it.
 * 
 */
@ImplementedBy(MIDIDeviceCacheImpl.class)
public interface MIDIDeviceCache
{
	/**
	 * Return named device.
	 * 
	 * @param name
	 *            The device name.
	 * 
	 * @return The device or null if not found.
	 */
	public MidiDevice get(String name);

	/**
	 * Put device in cache.
	 * 
	 * @param name
	 * 
	 * @param device
	 */
	public void put(String name, MidiDevice device);

	/**
	 * Set device as selected. This will mark it for opening when service activated and closed when stopped.
	 * 
	 * @param name
	 * @param state
	 */
	public void select(String name, boolean state);

	/**
	 * Open selected devices.
	 * 
	 */
	public void openAll();

	/**
	 * Close selected devices.
	 * 
	 */
	public void closeAll();

	/**
	 * Reload cache.
	 * 
	 */
	public void reload();

	/**
	 * Return devices.
	 * 
	 * @return The devices.
	 */
	public Collection<MidiDevice> getValues();

	/**
	 * Return device names.
	 * 
	 * @return The list of names.
	 */
	public String[] getNames();
}
