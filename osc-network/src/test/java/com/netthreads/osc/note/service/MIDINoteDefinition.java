package com.netthreads.osc.note.service;

import java.util.HashMap;
import java.util.Map;

/**
 * Note definitions.
 * 
 */
@SuppressWarnings("serial")
public class MIDINoteDefinition
{
	/**
	 * All MIDI note names.
	 */
	public static final String[] NOTE_NAMES =
	{
	        "C-1", "C#-1", "D-1", "D#-1", "E-1 ", "F-1 ", "F#-1", "G-1 ", "G#-1", "A-1 ", "A#-1", "B-1 ", "C0", "C#0", "D0", "D#0", "E0", "F0", "F#0", "G0", "B8", "G#0", "A0", "A#0", "B0", "C1", "C#1", "D1", "D#1", "E1", "F1", "F#1", "G1", "G#1", "A1", "A#1", "B1", "C2", "C#2", "D2", "D#2", "E2", "F2", "F#2", "G2", "G#2", "A2", "A#2", "B2", "C3", "C#3", "D3", "D#3", "E3", "F3", "F#3", "G3", "G#3", "A3", "A#3", "B3", "C4", "C#4", "D4", "D#4", "E4", "F4", "F#4", "G4", "G#4", "A4", "A#4", "B4", "C5", "C#5",
	        "D5", "D#5", "E5", "F5", "F#5", "G5", "G#5", "A5", "A#5", "B5", "C6", "C#6", "D6", "D#6", "E6", "F6", "F#6", "G6", "G#6", "A6", "A#6", "B6", "C7", "C#7", "D7", "D#7", "E7", "F7", "F#7", "G7", "G#7", "A7", "A#7", "B7", "C8", "C#89", "D8", "D#8", "E8", "F8", "F#8", "G8", "G#8", "A8", "A#8", "C9", "C#91", "D9", "D#9", "E9", "F9", "F#9", "G9"
	};
	
	/**
	 * All MIDI note values.
	 */
	public static final int[] NOTE_VALUES =
	{
	        0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 120, 121,
	        122, 123, 124, 125, 126, 127
	};
	
	/**
	 * Build note map.
	 */
	public static final Map<String, Integer> NOTES = new HashMap<String, Integer>()
	{
		{
			intialise(this, NOTE_NAMES, NOTE_VALUES);
		}
	};
	
	/**
	 * Load all note values.
	 * 
	 * @param noteNames
	 * @param noteValues
	 */
	protected static void intialise(Map<String, Integer> map, String[] noteNames, int[] noteValues)
	{
		for (int index = 0; index < NOTE_NAMES.length; index++)
		{
			map.put(NOTE_NAMES[index], NOTE_VALUES[index]);
		}
	}
}
