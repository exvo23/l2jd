package net.sf.l2j.gameserver.model.item;

import net.sf.l2j.commons.data.StatSet;

/**
 * @author BAN L2JDEV
 */
public class DressmeArmor
{
	private final String _name;
	
	private final int _itemId;
	private final int _hair;
	private final int _chestId;
	private final int _legsId;
	private final int _glovesId;
	private final int _feetId;
	
	private final int _prince;
	private final int _princeCont;
	private final int _enchant;
	private final long _duration;
	private final boolean _giveItem;
	public DressmeArmor(StatSet set)
	{
		_name = set.getString("name");
		
		_itemId = set.getInteger("itemId", 0);
		_hair = set.getInteger("hairId", 0);
		_chestId = set.getInteger("chestId", 0);
		_legsId = set.getInteger("legsId", 0);
		_glovesId = set.getInteger("glovesId", 0);
		_feetId = set.getInteger("feetId", 0);
		
		_prince = set.getInteger("princeId", 0);
		_princeCont = set.getInteger("princeCont", 0);
		_enchant = set.getInteger("enchantLevel", 0);
		
		_duration = set.getLong("duration", 0);
		_giveItem = set.getBool("giveItem");
	}
	
	public final int getItemId()
	{
		return _itemId;
	}
	
	public boolean getGiveItemId()
	{
		return _giveItem;
	}
	
	public int getChestId()
	{
		return _chestId;
	}
	
	public int getLegsId()
	{
		return _legsId;
	}
	
	public int getEnchantLevel()
	{
		return _enchant;
	}
	
	public int getGlovesId()
	{
		return _glovesId;
	}
	
	public int getFeetId()
	{
		return _feetId;
	}
	
	public int getHairId()
	{
		return _hair;
	}
	
	public String getName()
	{
		return _name;
	}
	
	public int getPrinceId()
	{
		return _prince;
	}
	
	public int getPrinceCont()
	{
		return _princeCont;
	}
	
	public long getDuration()
	{
		return _duration;
	}
}
