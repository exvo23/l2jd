package net.sf.l2j.gameserver.model.item;

import net.sf.l2j.commons.data.StatSet;

/**
 * @author BAN L2JDEV
 * 
 */
public class DressmeJewels
{
	private final String _name;
	
	private final int _itemId;
	
	private final int _chestId;
	private final int _prince;
	private final int _princeCont;
	private final int _enchant;
	
	public DressmeJewels(StatSet set)
	{
		_name = set.getString("name");
		
		_itemId = set.getInteger("itemId", 0);
		_chestId = set.getInteger("jewelsId", 0);
		_prince = set.getInteger("princeId", 0);
		_princeCont = set.getInteger("princeCont", 0);
		_enchant = set.getInteger("enchantLevel", 0);
	}
	
	public final int getLinkId()
	{
		return _itemId;
	}
	public int getJewelsId()
	{
		return _chestId;
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
	
	public int getEnchantLevel()
	{
		return _enchant;
	}
}
