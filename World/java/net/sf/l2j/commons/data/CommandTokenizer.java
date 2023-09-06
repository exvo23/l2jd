package net.sf.l2j.commons.data;

import java.util.StringTokenizer;

import net.sf.l2j.commons.lang.StringUtil;

/**
 * @author Administrator7
 *
 */
public class CommandTokenizer extends StringTokenizer
{
	private static final String REGEX = "-?\\d+(\\.\\d+)?";
	
	private final String _command;
	
	public CommandTokenizer(String command)
	{
		this(command, " ", true);
	}
	
	public CommandTokenizer(String command, String delim)
	{
		this(command, delim, true);
	}
	
	public CommandTokenizer(String command, String delim, boolean skipCommand)
	{
		super(command, delim);
		
		_command = skipCommand ? nextString() : "";
	}
	
	public String getCommand()
	{
		return _command;
	}
	
	public boolean isCommand(String command)
	{
		return _command.equalsIgnoreCase(command);
	}
	
	public int nextInt()
	{
		return nextInt(0);
	}
	
	public int nextInt(int defaultValue)
	{
		final String param = nextString();
		return param.matches(REGEX) ? Integer.parseInt(param) : defaultValue;
	}
	
	public long nextLong()
	{
		return nextLong(0L);
	}
	
	public long nextLong(long defaultValue)
	{
		final String param = nextString();
		return param.matches(REGEX) ? Long.parseLong(param) : defaultValue;
	}
	
	public double nextDouble()
	{
		return nextDouble(0.0D);
	}
	
	public double nextDouble(double defaultValue)
	{
		final String param = nextString();
		return param.matches(REGEX) ? Double.parseDouble(param) : defaultValue;
	}
	
	public String nextString()
	{
		return nextString("");
	}
	
	public String nextString(String defaultValue)
	{
		return hasMoreTokens() ? nextToken() : defaultValue;
	}
	
	public <E extends Enum<E>> E nextEnum(final Class<E> enumClass)
	{
		return nextEnum(enumClass, null);
	}
	
	public <E extends Enum<E>> E nextEnum(final Class<E> enumClass, final E defaultValue)
	{
		try
		{
			return Enum.valueOf(enumClass, nextString());
		}
		catch (Exception e) // silent exception
		{
		}
		return defaultValue;
	}
	
	public String getAllNextTokens()
	{
		final StringBuilder sb = new StringBuilder();
		while (hasMoreTokens())
			StringUtil.append(sb, " ", nextToken());
		
		return sb.toString().trim();
	}
}
