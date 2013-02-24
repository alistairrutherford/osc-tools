package com.netthreads.osc.test.task;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netthreads.network.osc.client.OSCClient;
import com.netthreads.osc.note.service.NoteModel;
import com.netthreads.osc.note.service.NoteModelImpl;

public class OSCClientTask implements Callable<Void>
{
	private static final Logger logger = LoggerFactory.getLogger(OSCClientTask.class);

	private static final long SLEEP_MSEC = 0;
	private NoteModel noteModel;

	private OSCClient oscClient;

	/**
	 * Construct task.
	 * 
	 * @param oscClient
	 *            OSC Client instance.
	 */
	public OSCClientTask(OSCClient oscClient, NoteModel noteModel)
	{
		this.oscClient = oscClient;
		this.noteModel = noteModel;
	}

	/**
	 * Send bundles of notes on and then off.
	 * 
	 */
	@Override
	public Void call() throws Exception
	{
		try
		{
			logger.info("Connecting..");

			if (oscClient.isConnected())
			{
				for (int i = 0; i < NoteModelImpl.COUNT_BARS; i++)
				{
					noteModel.play();

					Thread.sleep(SLEEP_MSEC);
				}

			}
		}
		catch (InterruptedException e)
		{
			logger.info("Interrupted", e);
		}
		finally
		{
			logger.info("Disconnecting");

			oscClient.disconnect();
		}

		logger.info("Finished");
		
		return null;
	}

}
