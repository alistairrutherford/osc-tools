package com.netthreads.network.osc.router.service;

import java.util.List;

import javax.sound.midi.MidiDevice;

import com.google.inject.ImplementedBy;

/**
 * We define interface in case we choose to inject it.
 *
 */
@ImplementedBy(MIDIServiceImpl.class)
public interface MIDIService
{
	/**
	 * Return list of MIDI devices.
	 * 
	 * @return The list.
	 */
	public List<MidiDevice> getDevices();

	/**
	 * Open device.
	 * 
	 * @param device
	 *            The device.
	 * 
	 * @return True if successful.
	 */
	public boolean openDevice(MidiDevice device);

	/**
	 * Close device.
	 * 
	 * @param device
	 *            The device.
	 */
	public void closeDevice(MidiDevice device);

}
