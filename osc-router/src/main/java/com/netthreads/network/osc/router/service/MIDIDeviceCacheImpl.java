package com.netthreads.network.osc.router.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Cache of MIDI devices and status..
 * 
 */
@Singleton
public class MIDIDeviceCacheImpl implements MIDIDeviceCache
{
	private Logger logger = LoggerFactory.getLogger(MIDIDeviceCacheImpl.class);
	
	private final Map<String, MidiDevice> cache;
	
	private final Map<String, Boolean> selected;
	
	private final MIDIService midiService;
	
	/**
	 * Create cache and inject service.
	 * 
	 * @param midiService
	 */
	@Inject
	public MIDIDeviceCacheImpl(MIDIService midiService)
	{
		this.midiService = midiService;
		
		cache = new HashMap<String, MidiDevice>();
		
		selected = new HashMap<String, Boolean>();
		
		reload();
	}
	
	/**
	 * Return device from cache.
	 * 
	 */
	@Override
	public MidiDevice get(String name)
	{
		return cache.get(name);
	}
	
	/**
	 * Add device to cache.
	 * 
	 */
	@Override
	public void put(String name, MidiDevice device)
	{
		cache.put(name, device);
	}
	
	/**
	 * Set device selection state.
	 */
	@Override
	public void select(String name, boolean state)
	{
		if (cache.get(name) != null)
		{
			selected.put(name, state);
		}
	}
	
	/**
	 * Open all selected devices.
	 * 
	 */
	@Override
	public void openAll()
	{
		Collection<String> names = cache.keySet();
		
		for (String name : names)
		{
			Boolean state = selected.get(name);
			
			if (state != null && state.booleanValue())
			{
				openDevice(name);
			}
		}
		
	}
	
	/**
	 * Open Device.
	 * 
	 * @param name
	 *            The device name.
	 */
	@Override
	public boolean openDevice(String name)
	{
		boolean status = false;
		
		MidiDevice midiDevice = cache.get(name);
		
		if (midiDevice != null)
		{
			try
			{
				if (!midiDevice.isOpen())
				{
					midiDevice.open();
				}
				
				status = true;
			}
			catch (MidiUnavailableException e)
			{
				// Error.
				logger.error(e.getLocalizedMessage());
			}
		}
		
		return status;
	}
	
	/**
	 * Open Device.
	 * 
	 * @param name
	 *            The device name.
	 */
	@Override
	public void closeDevice(String name)
	{
		MidiDevice midiDevice = cache.get(name);
		
		if (midiDevice != null)
		{
			if (midiDevice.isOpen())
			{
				midiDevice.close();
			}
		}
	}
	
	/**
	 * Close all open devices.
	 * 
	 */
	@Override
	public void closeAll()
	{
		Collection<MidiDevice> devices = cache.values();
		
		for (MidiDevice midiDevice : devices)
		{
			if (midiDevice.isOpen())
			{
				midiDevice.close();
			}
		}
	}
	
	/**
	 * Reload cache.
	 * 
	 */
	@Override
	public void reload()
	{
		// Ensure you close any open device.
		closeAll();
		
		// Now reload.
		List<MidiDevice> devices = midiService.getDevices();
		
		for (MidiDevice device : devices)
		{
			cache.put(device.getDeviceInfo().getName(), device);
		}
	}
	
	/**
	 * Return cache values.
	 * 
	 * The list of values.
	 */
	public Collection<MidiDevice> getValues()
	{
		return cache.values();
	}
	
	/**
	 * Return device names.
	 * 
	 * The list of names.
	 */
	public String[] getNames()
	{
		Object[] items = cache.keySet().toArray();
		
		String[] names = new String[items.length];
		
		return cache.keySet().toArray(names);
	}
	
}
