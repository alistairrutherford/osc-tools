package com.netthreads.network.osc.router.service;

import javafx.util.StringConverter;

import javax.sound.midi.MidiDevice;

/**
 * MIDI Device convertor lets you convert between object and description and vice versa.
 * 
 */
public class MIDIDeviceStringConvertor extends StringConverter<MidiDevice>
{
	private final MIDIDeviceCache midiDeviceCache;

	public MIDIDeviceStringConvertor(MIDIDeviceCache midiDeviceCache)
	{
		this.midiDeviceCache = midiDeviceCache;
	}

	/** {@inheritDoc} */
	@Override
	public String toString(MidiDevice value)
	{
		return (value != null) ? value.getDeviceInfo().getName() : "";
	}

	/** {@inheritDoc} */
	@Override
	public MidiDevice fromString(String value)
	{
		return midiDeviceCache.get(value);
	}

}
