package com.netthreads.network.osc.router.service;

import java.util.HashMap;
import java.util.Map;

import javax.sound.midi.ShortMessage;

import com.google.inject.Singleton;

@Singleton
public class MessageLookupImpl implements MessageLookup
{
	public static String[] NAMES =
	{
	        "None", "Note On", "Note Off", "Polyphonic Key Pressure", "Channel Pressure", "Pitch Bend Change", "Program Change", "Control Change"
	};
	
	public static int[] VALUES =
	{
	        ShortMessage.CONTINUE, ShortMessage.NOTE_ON, ShortMessage.NOTE_OFF, ShortMessage.POLY_PRESSURE, ShortMessage.CHANNEL_PRESSURE, ShortMessage.PITCH_BEND, ShortMessage.PROGRAM_CHANGE, ShortMessage.CONTROL_CHANGE
	};
	
	@SuppressWarnings("serial")
    private static final Map<String, Integer> TABLE = new HashMap<String, Integer>()
	{
		{
			for (int i = 0; i < NAMES.length; i++)
			{
				put(NAMES[i], VALUES[i]);
			}
		}
	};
	
	@Override
	public int getMessage(String name)
	{
		return TABLE.get(name);
	}
	
}
