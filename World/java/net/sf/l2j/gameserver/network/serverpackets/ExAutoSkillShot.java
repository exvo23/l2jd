package net.sf.l2j.gameserver.network.serverpackets;

import net.sf.l2j.gameserver.skills.L2Skill;

/**
 * @author BAN {@Link} L2JDEV 2023
 *
 */
public class ExAutoSkillShot  extends L2GameServerPacket
{
	private final L2Skill _skill;
	private final int _type;
	
	public ExAutoSkillShot(L2Skill skills, int type)
	{
		_skill = skills;
		_type = type;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0xFE);
		writeH(0x12);
		writeD(_skill.getId());
		writeD(_type);
	}
}
