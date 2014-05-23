package com.netthreads.network.osc.router.service;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.ShortMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netthreads.network.osc.router.AppInjector;
import com.netthreads.network.osc.router.model.OSCItem;
import com.netthreads.network.osc.router.model.OSCValue;

/**
 * MIDI Router runs in it's own thread and send messages as fast as it can.
 * 
 */
public class MIDIRouter implements Router, Runnable
{
	private Logger logger = LoggerFactory.getLogger(MIDIRouter.class);
	
	private final MIDIDeviceCache midiDeviceCache;
	
	private MIDIMessageLookup messageLookup;
	
	private final BlockingQueue<OSCItem> queue;
	
	private ExecutorService pool;
	
	private boolean active;
	
	/**
	 * MIDI Service.
	 * 
	 */
	public MIDIRouter()
	{
		this.queue = new ArrayBlockingQueue<OSCItem>(1024);
		
		// Device cache.
		midiDeviceCache = AppInjector.getInjector().getInstance(MIDIDeviceCache.class);
		
		// MIDI Message table.
		messageLookup = AppInjector.getInjector().getInstance(MIDIMessageLookup.class);
		
		active = false;
		
		pool = Executors.newFixedThreadPool(1);
	}
	
	/**
	 * Activate service.
	 * 
	 */
	@Override
	public void start()
	{
		pool.execute(this);
	}
	
	/**
	 * Kill the service.
	 * 
	 * We override it as we might want to do some sort of cleanup.
	 * 
	 */
	@Override
	public void stop()
	{
		active = false;

		pool.shutdownNow();
	}
	
	/**
	 * Add message to queue.
	 * 
	 * @param shortMessage
	 */
	@Override
	public synchronized boolean route(OSCItem oscItem)
	{
		queue.add(oscItem);
		
		return true;
	}
	
	/**
	 * Create service task.
	 * 
	 */
	@Override
	public void run()
	{
		active = true;

		try
		{
			while (active)
			{
				// Listen for notes
				OSCItem oscItem = queue.take();
				
				routeItem(oscItem);
			}
		}
		catch (InterruptedException interrupted)
		{
			logger.debug("Stopped");
		}
		catch (Throwable t)
		{
			logger.error(t.getLocalizedMessage());
		}
	}
	
	/**
	 * Route message according to setting.
	 * 
	 * @param oscItem
	 */
	private void routeItem(OSCItem oscItem)
	{
		// Route with message?
		String routeType = oscItem.getRoute();
		
		// Route message to device?
		String device = oscItem.getDevice();
		
		// Fetch device and examine it's status.
		MidiDevice midiDevice = midiDeviceCache.get(device);
		
		if (midiDevice != null && midiDevice.isOpen())
		{
			int message = messageLookup.getMessage(routeType);
			
			// Send message.
			switch (message)
			{
				case ShortMessage.CONTINUE:
					// TODO
					break;
				
				case ShortMessage.NOTE_ON:
					// Parameter check.
					List<OSCValue> noteOnValues = oscItem.getValues();
					if (messageLookup.getParametersCount(routeType) != noteOnValues.size())
					{
						oscItem.setWorking(OSCItem.WORKING_ERROR);
					}
					else
					{
						int noteOn = Integer.valueOf(noteOnValues.get(0).getValue());
						int velocityOn = Integer.valueOf(noteOnValues.get(1).getValue());
						int channelOn = Integer.valueOf(noteOnValues.get(2).getValue());
						
						sendNote(ShortMessage.NOTE_ON, noteOn, velocityOn, channelOn, midiDevice);
						
						// Set status.
						oscItem.setWorking(OSCItem.WORKING_DONE);
					}
					break;
				
				case ShortMessage.NOTE_OFF:
					// Parameter check.
					List<OSCValue> noteOffValues = oscItem.getValues();
					if (messageLookup.getParametersCount(routeType) != noteOffValues.size())
					{
						oscItem.setWorking(OSCItem.WORKING_ERROR);
					}
					else
					{
						int noteOff = Integer.valueOf(noteOffValues.get(0).getValue());
						int velocityOff = Integer.valueOf(noteOffValues.get(1).getValue());
						int channelOff = Integer.valueOf(noteOffValues.get(2).getValue());
						
						sendNote(ShortMessage.NOTE_OFF, noteOff, velocityOff, channelOff, midiDevice);
						
						// Set status.
						oscItem.setWorking(OSCItem.WORKING_DONE);
					}
					break;
				
				case ShortMessage.POLY_PRESSURE:
					// TODO
					break;
				case ShortMessage.CHANNEL_PRESSURE:
					// TODO
					break;
				case ShortMessage.PITCH_BEND:
					// TODO
					break;
				case ShortMessage.PROGRAM_CHANGE:
					// TODO
					break;
				case ShortMessage.CONTROL_CHANGE:
					// TODO
					break;
				default:
					break;
			}
		}
	}
	
	/**
	 * Send MIDI note.
	 * 
	 * @param note
	 * @param velocity
	 * @param channel
	 * @param midiDevice
	 */
	private void sendNote(int command, int note, int velocity, int channel, MidiDevice midiDevice)
	{
		try
		{
			ShortMessage midiMessage = new ShortMessage();
			
			midiMessage.setMessage(command, channel, note, velocity);
			
			midiDevice.getReceiver().send(midiMessage, -1);
		}
		catch (InvalidMidiDataException e)
		{
			logger.error(e.getLocalizedMessage());
		}
		catch (MidiUnavailableException e)
		{
			logger.error(e.getLocalizedMessage());
		}
	}
}
