package com.netthreads.network.osc.router.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDevice.Info;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;

@Singleton
public class MIDIManagerImpl implements MIDIManager
{
	private Logger logger = LoggerFactory.getLogger(MIDIManagerImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.netthreads.network.osc.service.MIDIService#getDevices()
	 */
	@Override
	public List<MidiDevice> getDevices()
	{
		List<MidiDevice> devices = new ArrayList<MidiDevice>();

		MidiDevice.Info[] midiDeviceInfo = MidiSystem.getMidiDeviceInfo();

		List<Info> infos = Arrays.asList(midiDeviceInfo);

		for (Info info : infos)
		{
			try
			{

				MidiDevice device = MidiSystem.getMidiDevice(info);

				devices.add(device);
			}
			catch (MidiUnavailableException e)
			{
				logger.error(e.getLocalizedMessage());
			}
		}

		return devices;
	}

	/**
	 * Open device.
	 * 
	 * @param device
	 *            The device.
	 * 
	 * @return True if successful.
	 */
	@Override
	public boolean openDevice(MidiDevice device)
	{
		boolean status = false;

		try
		{
			device.open();

			status = true;
		}
		catch (MidiUnavailableException e)
		{
			logger.error(e.getLocalizedMessage());
		}

		return status;
	}

	/**
	 * Close device.
	 * 
	 * @param device
	 *            The device.
	 */
	@Override
	public void closeDevice(MidiDevice device)
	{
		device.close();
	}

}
