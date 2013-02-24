package com.netthreads.network.osc;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDevice.Info;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.ShortMessage;

import org.junit.Test;

public class TestMidiTXRX
{
	private static final String SYNTH_NAME = "Gervill";

	@Test
	public void testSendNote()
	{
		MidiDevice.Info[] midiDeviceInfo = MidiSystem.getMidiDeviceInfo();

		for (Info info : midiDeviceInfo)
		{
			try
			{
				MidiDevice device = MidiSystem.getMidiDevice(info);

				if (device.getDeviceInfo().getName().equals(SYNTH_NAME))
				{
					device.open();

					ShortMessage midiMessage = new ShortMessage();
					try
					{
						midiMessage.setMessage(ShortMessage.NOTE_ON, 0, 60, 93);
					}
					catch (InvalidMidiDataException e)
					{
						e.printStackTrace();
					}

					if (device.isOpen())
					{
						System.out.println("Send note");

						device.getReceiver().send(midiMessage, -1);

					}

					Thread.sleep(2000);

					device.close();
				}

			}
			catch (MidiUnavailableException e)
			{
				e.printStackTrace();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

}