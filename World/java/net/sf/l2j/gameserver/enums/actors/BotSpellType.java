package net.sf.l2j.gameserver.enums.actors;

import java.util.Arrays;
import java.util.List;

/**
 * @author BAN - L2JDEV
 */
public enum BotSpellType
{
	COMMON(Arrays.asList(0, 1, 2, 3)),
	CHANCE(Arrays.asList(4, 5, 6, 7)),
	LOW_LIFE(Arrays.asList(8, 9, 10, 11));
	
	private final List<Integer> _slots;
	
	private BotSpellType(List<Integer> slots)
	{
		_slots = slots;
	}
	
	public final List<Integer> getSlots()
	{
		return _slots;
	}
}