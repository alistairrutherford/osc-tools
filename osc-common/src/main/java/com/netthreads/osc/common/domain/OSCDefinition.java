package com.netthreads.osc.common.domain;

/**
 * 'i' int32
 * 
 * 'f' float32
 * 
 * 's' OSC-string
 * 
 * 'b' OSC-blob
 * 
 * 'h' 64 bit big-endian two's complement integer
 * 
 * 't' OSC-timetag
 * 
 * 'd' 64 bit ("double") IEEE 754 floating point number
 * 
 * 'S' Alternate type represented as an OSC-string (for example, for systems
 * that differentiate "symbols" from "strings")
 * 
 * 'c' an ascii character, sent as 32 bits
 * 
 * 'r' 32 bit RGBA color
 * 
 * 'm' 4 byte MIDI message. Bytes from MSB to LSB are: port id, status byte,
 * data1, data2
 * 
 * 'T' True. No bytes are allocated in the argument data.
 * 
 * 'F' False. No bytes are allocated in the argument data.
 * 
 * 'N' Nil. No bytes are allocated in the argument data.
 * 
 * 'I' Infinitum. No bytes are allocated in the argument data.
 * 
 * '[' Indicates the beginning of an array. The tags following are for data in
 * the Array until a close brace tag is reached.
 * 
 * ']' Indicates the end of an array.
 * 
 */
public class OSCDefinition
{
	public static final String MESSAGE_BUNDLE_START = "#bundle";
	public static final char MESSAGE_TYPE_START = ',';
	
	public static final char TYPE_INT = 'i';
	public static final char TYPE_FLOAT = 'f';
	public static final char TYPE_STRING = 's';
	public static final char TYPE_BLOB = 'b';
	public static final char TYPE_LONG = 'h';
	
	public static final char TYPE_TIMETAG = 't';
	public static final char TYPE_DOUBLE = 'd';
	public static final char TYPE_ALTERNATE_STRING = 'S';
	
	public static final char TYPE_INFINITUM = 'I';
	public static final char TYPE_TRUE = 'T';
	public static final char TYPE_FALSE = 'F';
	public static final char TYPE_ARRAY_START = '[';
	public static final char TYPE_ARRAY_END = ']';
	
}
