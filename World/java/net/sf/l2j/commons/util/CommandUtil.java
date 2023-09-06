package net.sf.l2j.commons.util;

import java.util.StringTokenizer;

/**
 * @author BAN - L2JDEV
 */
public class CommandUtil extends StringTokenizer
{
	private final String _command;
	
	public CommandUtil(String command)
	{
		this(command, " ", true);
	}
	
	public CommandUtil(String command, String split)
	{
		this(command, split, true);
	}
	
	public CommandUtil(String command, String split, boolean skipCommand)
	{
		super(command, split);
		_command = skipCommand ? nextString() : "";
	}
	
	public String getCommand()
	{
		return _command;
	}
	
	public int nextInt()
	{
		return nextInt(0);
	}
	
	public int nextInt(int defaultValue)
	{
		final String param = hasMoreTokens() ? nextToken() : "";
		return param.matches("-?\\d+(\\.\\d+)?") ? Integer.parseInt(param) : defaultValue;
	}
	
	public String nextString()
	{
		return nextString("");
	}
	
	public String nextString(String defaultValue)
	{
		return hasMoreTokens() ? nextToken() : defaultValue;
	}
	
}