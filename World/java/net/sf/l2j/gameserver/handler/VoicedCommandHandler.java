package net.sf.l2j.gameserver.handler;

import java.util.HashMap;
import java.util.Map;

import net.sf.l2j.gameserver.handler.voicedcommandhandlers.VoicedMenu;

/**
 * @author BAN L2JDEV
 */
public class VoicedCommandHandler
{
	
	private static VoicedCommandHandler _instance;
	
	private final Map<String, IVoicedCommandHandler> _datatable;
	
	public static VoicedCommandHandler getInstance()
	{
		if (_instance == null)
		{
			_instance = new VoicedCommandHandler();
		}
		
		return _instance;
	}
	
	public VoicedCommandHandler()
	{
		_datatable = new HashMap<>();
		
		registerHandler(new VoicedMenu());
	
		
	}
	
	public void registerHandler(final IVoicedCommandHandler handler)
	{
		String[] ids = handler.getVoicedCommandList();
		
		for (final String id : ids)
		{
			_datatable.put(id, handler);
		}
		
		ids = null;
	}
	
	public IVoicedCommandHandler getVoicedCommandHandler(final String voicedCommand)
	{
		String command = voicedCommand;
		
		if (voicedCommand.indexOf(" ") != -1)
		{
			command = voicedCommand.substring(0, voicedCommand.indexOf(" "));
		}
		return _datatable.get(command);
	}
	
	/**
	 * @return
	 */
	public int size()
	{
		return _datatable.size();
	}
}
