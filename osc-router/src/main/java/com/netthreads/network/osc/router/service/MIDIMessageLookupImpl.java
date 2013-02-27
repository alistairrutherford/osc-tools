package com.netthreads.network.osc.router.service;

import java.util.HashMap;
import java.util.Map;

import javax.sound.midi.ShortMessage;

import com.google.inject.Singleton;

@Singleton
public class MIDIMessageLookupImpl implements MIDIMessageLookup
{
	public static String[] NAMES =
	{
	        "None", "Note On", "Note Off", "Polyphonic Key Pressure", "Channel Pressure", "Pitch Bend Change", "Program Change", "Control Change"
	};
	
	public static int[] VALUES =
	{
	        ShortMessage.CONTINUE, ShortMessage.NOTE_ON, ShortMessage.NOTE_OFF, ShortMessage.POLY_PRESSURE, ShortMessage.CHANNEL_PRESSURE, ShortMessage.PITCH_BEND, ShortMessage.PROGRAM_CHANGE, ShortMessage.CONTROL_CHANGE
	};
	
	public static int[] PARAMETERS =
	{
	        0, 3, 3, 1, 1, 1, 1, 1
	};
	
	@SuppressWarnings("serial")
	private static final Map<String, Integer> MESSAGE_TABLE = new HashMap<String, Integer>()
	{
		{
			for (int i = 0; i < NAMES.length; i++)
			{
				put(NAMES[i], VALUES[i]);
			}
		}
	};
	
	@SuppressWarnings("serial")
	private static final Map<String, Integer> PARAMETERS_TABLE = new HashMap<String, Integer>()
	{
		{
			for (int i = 0; i < NAMES.length; i++)
			{
				put(NAMES[i], PARAMETERS[i]);
			}
		}
	};
	
	@Override
	public int getMessage(String name)
	{
		return MESSAGE_TABLE.get(name);
	}
	
	@Override
	public int getParametersCount(String name)
	{
		int count = 0;
		Integer parameterCount = PARAMETERS_TABLE.get(name);
		if (parameterCount != null)
		{
			count = parameterCount;
		}
		
		return count;
	}
	
}
