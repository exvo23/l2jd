package net.sf.l2j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import net.sf.l2j.commons.config.ExProperties;
import net.sf.l2j.commons.logging.CLogger;
import net.sf.l2j.commons.math.MathUtil;

import net.sf.l2j.gameserver.enums.GeoType;
import net.sf.l2j.gameserver.model.holder.IntIntHolder;
import net.sf.l2j.gameserver.model.holder.RewardHolder;
import net.sf.l2j.gameserver.scripting.script.ai.boss.config.AntharasConfig;
import net.sf.l2j.gameserver.scripting.script.ai.boss.config.BaiumConfig;
import net.sf.l2j.gameserver.scripting.script.ai.boss.config.BenomConfig;
import net.sf.l2j.gameserver.scripting.script.ai.boss.config.CoreConfig;
import net.sf.l2j.gameserver.scripting.script.ai.boss.config.DrChaosConfig;
import net.sf.l2j.gameserver.scripting.script.ai.boss.config.FafurionConfig;
import net.sf.l2j.gameserver.scripting.script.ai.boss.config.FrintezzaConfig;
import net.sf.l2j.gameserver.scripting.script.ai.boss.config.LindviorConfig;
import net.sf.l2j.gameserver.scripting.script.ai.boss.config.OrfenConfig;
import net.sf.l2j.gameserver.scripting.script.ai.boss.config.QAConfig;
import net.sf.l2j.gameserver.scripting.script.ai.boss.config.SailrenConfig;
import net.sf.l2j.gameserver.scripting.script.ai.boss.config.ValakasConfig;
import net.sf.l2j.gameserver.scripting.script.ai.boss.config.VanHalterConfig;
import net.sf.l2j.gameserver.scripting.script.ai.boss.config.ZakenConfig;
import net.sf.l2j.gameserver.scripting.script.event.EventDropConfig;


public final class Config
{
	public static String SERVER_NAME;
	
	private static final CLogger LOGGER = new CLogger(Config.class.getName());
	
	public static final String CLANS_FILE = "./config/clans.properties";
	public static final String EVENTS_FILE = "./config/events/events.properties";
	public static final String GEOENGINE_FILE = "./config/geoengine.properties";
	public static final String HEXID_FILE = "./config/hexid.txt";
	public static final String LOGINSERVER_FILE = "./config/loginserver.properties";
	public static final String NPCS_FILE = "./config/npcs.properties";
	public static final String PLAYERS_FILE = "./config/players.properties";
	public static final String SERVER_FILE = "./config/server.properties";
	public static final String SIEGE_FILE = "./config/siege.properties";
	public static final String PARTYFARM_FILE = "./config/events/PartyFarm.properties";
	public static final String SPECIAL_TUTORIAL_SETTINGS = "./config/Tutorial.properties";
	public static final String COMMUNITY_BORD = "./config/CommunityBord.properties";
	public static final String OFFLINE_FILE = "./config/offline.properties";
	
	// --------------------------------------------------
	// Offline
	// --------------------------------------------------
	public static boolean ENABLE_IP_BOX;
	public static int IP_MAX_DUALBOX;
	public static int IP_TIME_LOGOUT;
	public static int MIN_PROTOCOL_REVISION;
	public static int MAX_PROTOCOL_REVISION;
	
	public static boolean OFFLINE_TRADE_ENABLE;
	public static boolean OFFLINE_CRAFT_ENABLE;
	public static boolean OFFLINE_IN_PEACE_ZONE;
	public static boolean OFFLINE_NO_DAMAGE;
	public static boolean OFFLINE_DISCONNECT;
	public static boolean OFFLINE_RESTORE;
	public static int OFFLINE_MAX_DAYS;
	public static int OFFLINE_NAME_COLOR;
	public static String OFFLINE_EFFECT;

	
	public static int BANKING_SYSTEM_GOLDBARS;
	public static int BANKING_SYSTEM_ADENA;

	public static String RAID_BOSS_IDS;
	public static List<Integer> LIST_RAID_BOSS_IDS;
	
	// --------------------------------------------------
	// Special Tutorial settings
	// --------------------------------------------------
	
	/** Custom Camera On CharacterCreate */
	
	public static int PLAYER_START_CREATION_LEVEL;
	
	public static boolean USE_CUSTOM_CAMERA;
	public static int USE_CUSTOM_DURATION;
	public static List<int[]> CUSTOM_CAMERA = new ArrayList<>();
	
	public static boolean ENABLE_SPAWN_START_PLAYER;
	public static int[] SPAWN_START_TUTORIAL_LOCATION = new int[3];
	
	
	public static int ITEMID_ETC_TUTORIAL_00;
	public static int CONT_ETC_TUTORIAL_00;
	
	public static int ITEMID_ETC_TUTORIAL_01;
	public static int CONT_ETC_TUTORIAL_01;
	
	public static int ITEMID_ETC_TUTORIAL_02;
	public static int CONT_ETC_TUTORIAL_02;
	
	public static int ITEMID_ETC_TUTORIAL_03;
	public static int CONT_ETC_TUTORIAL_03;
	
	public static int ITEMID_ETC_TUTORIAL_04;
	public static int CONT_ETC_TUTORIAL_04;
	
	public static int ITEMID_ETC_TUTORIAL_05;
	public static int CONT_ETC_TUTORIAL_05;
	
	public static int MIN_ENCHANT_TUTORIAL;
	public static int MAX_ENCHANT_TUTORIAL;
	
	public static int ITEMID_WEAPON_TUTORIAL_00;
	public static String NAME_WEAPON_TUTORIAL_00;
	public static String DESC_WEAPON_TUTORIAL_00;
	public static String ICON_WEAPON_00;
	
	public static int ITEMID_WEAPON_TUTORIAL_01;
	public static String NAME_WEAPON_TUTORIAL_01;
	public static String DESC_WEAPON_TUTORIAL_01;
	public static String ICON_WEAPON_01;
	
	public static int ITEMID_WEAPON_TUTORIAL_02;
	public static String NAME_WEAPON_TUTORIAL_02;
	public static String DESC_WEAPON_TUTORIAL_02;
	public static String ICON_WEAPON_02;
	
	public static int ITEMID_WEAPON_TUTORIAL_03;
	public static String NAME_WEAPON_TUTORIAL_03;
	public static String DESC_WEAPON_TUTORIAL_03;
	public static String ICON_WEAPON_03;
	
	
	public static String TUTORIAL_BUFFER_FIGHTER_SET;
	public static String TUTORIAL_BUFFER_MAGE_SET;
	
	public static String NAME_BUFFER_TUTORIAL_00;
	public static String DESC_BUFFER_TUTORIAL_00;
	public static String ICON_BUFFER_FIGHT_00;
	
	public static String NAME_BUFFER_TUTORIAL_01;
	public static String DESC_BUFFER_TUTORIAL_01;
	public static String ICON_BUFFER_MAGIC_01;
	
	
	public static String NAME_ARMOR_TUTORIAL_00;
	public static String DESC_ARMOR_TUTORIAL_00;
	public static String ICON_ARMOR_LIGHT_00;

	public static String NAME_ARMOR_TUTORIAL_01;
	public static String DESC_ARMOR_TUTORIAL_01;
	public static String ICON_ARMOR_HEAVY_01;
	
	public static String NAME_ARMOR_TUTORIAL_02;
	public static String DESC_ARMOR_TUTORIAL_02;
	public static String ICON_ARMOR_ROBE_02;
	
	
	public static String NAME_TELEPORT_TUTORIAL_00;
	public static String DESC_TELEPORT_TUTORIAL_00;
	public static String ICON_TELEPORT_GOTO_00;
	public static int[] SOE_TELEPORT_GOTO_00_LOCATION = new int[3];
	
	public static String NAME_TELEPORT_TUTORIAL_01;
	public static String DESC_TELEPORT_TUTORIAL_01;
	public static String ICON_TELEPORT_GOTO_01;
	public static int[] SOE_TELEPORT_GOTO_01_LOCATION = new int[3];
	
	public static String NAME_TELEPORT_TUTORIAL_02;
	public static String DESC_TELEPORT_TUTORIAL_02;
	public static String ICON_TELEPORT_GOTO_02;
	public static int[] SOE_TELEPORT_GOTO_02_LOCATION = new int[3];
	
	public static String NAME_TELEPORT_TUTORIAL_03;
	public static String DESC_TELEPORT_TUTORIAL_03;
	public static String ICON_TELEPORT_GOTO_03;
	public static int[] SOE_TELEPORT_GOTO_03_LOCATION = new int[3];

	public static List<int[]> SET_HEAVY_ITEMS = new ArrayList<>();
	public static int[] SET_HEAVY_ITEMS_LIST;
	public static List<int[]> SET_LIGHT_ITEMS = new ArrayList<>();
	public static int[] SET_LIGHT_ITEMS_LIST;
	public static List<int[]> SET_ROBE_ITEMS = new ArrayList<>();
	public static int[] SET_ROBE_ITEMS_LIST;
	// --------------------------------------------------
	// Clans settings
	// --------------------------------------------------
	
	public static boolean ALL_ITEM_0_ADENA;
	public static int ALL_ITEMS_SELL_0_ADENA;
	
	/** Items / Handler */
	public static int ITEM_CLASS_ID;
	
	public static int CLAN_ITEMID;
	public static int CLAN_REWARD_REPUTATION;
	public static int CLAN_LEVEL;
	public static boolean CLAN_FULL_SKILL;
	
	public static int NOBLES_ITEMID;
	public static int NOBLES_REWARD_NOBLESS_TIARA;
	
	public static int VIP_COIN_ID1;
	public static int VIP_DAYS_ID1;
	public static int VIP_COIN_ID2;
	public static int VIP_DAYS_ID2;
	public static int VIP_COIN_ID3;
	public static int VIP_DAYS_ID3;
	
	public static int HERO_ITEM_ID_7DAYS;
	public static int HERO_ITEM_ID_30DAYS;
	public static int HERO_ITEM_ID_90DAYS;
	
	public static int HERO_7DAYS;
	public static int HERO_30DAYS;
	public static int HERO_90DAYS;
	
	 // Settings Boss Zone
	public static boolean BOSS_CHAOTIC_ENABLE;
	public static int BOSS_CHAOTIC_RANGED;
	
	/** CHAT LIMITS */
	public static boolean ALLOW_PVP_CHAT;
	public static int PVPS_TO_TALK_ON_SHOUT;
	public static int PVPS_TO_TALK_ON_TRADE;
	
	/** Enter World Message */
	public static boolean PM_MESSAGE;
	public static String PM_SERVER_NAME;
	public static String PM_TEXT1;
	public static String PM_TEXT2;
	
	/** Chat Emotions */
	public static boolean EMOTION_CHAT_SYSTEM;
	public static Map<String, Integer> EMOTION_CHAT_LIST;
	
	/** Clans */
	public static int CLAN_JOIN_DAYS;
	public static int CLAN_CREATE_DAYS;
	public static int CLAN_DISSOLVE_DAYS;
	public static int ALLY_JOIN_DAYS_WHEN_LEAVED;
	public static int ALLY_JOIN_DAYS_WHEN_DISMISSED;
	public static int ACCEPT_CLAN_DAYS_WHEN_DISMISSED;
	public static int CREATE_ALLY_DAYS_WHEN_DISSOLVED;
	public static int MAX_NUM_OF_CLANS_IN_ALLY;
	public static int CLAN_MEMBERS_FOR_WAR;
	public static int CLAN_WAR_PENALTY_WHEN_ENDED;
	public static boolean MEMBERS_CAN_WITHDRAW_FROM_CLANWH;
	
	/** Manor */
	public static int MANOR_REFRESH_TIME;
	public static int MANOR_REFRESH_MIN;
	public static int MANOR_APPROVE_TIME;
	public static int MANOR_APPROVE_MIN;
	public static int MANOR_MAINTENANCE_MIN;
	public static int MANOR_SAVE_PERIOD_RATE;
	
	/** Clan Hall function */
	public static long CH_TELE_FEE_RATIO;
	public static int CH_TELE1_FEE;
	public static int CH_TELE2_FEE;
	public static long CH_SUPPORT_FEE_RATIO;
	public static int CH_SUPPORT1_FEE;
	public static int CH_SUPPORT2_FEE;
	public static int CH_SUPPORT3_FEE;
	public static int CH_SUPPORT4_FEE;
	public static int CH_SUPPORT5_FEE;
	public static int CH_SUPPORT6_FEE;
	public static int CH_SUPPORT7_FEE;
	public static int CH_SUPPORT8_FEE;
	public static long CH_MPREG_FEE_RATIO;
	public static int CH_MPREG1_FEE;
	public static int CH_MPREG2_FEE;
	public static int CH_MPREG3_FEE;
	public static int CH_MPREG4_FEE;
	public static int CH_MPREG5_FEE;
	public static long CH_HPREG_FEE_RATIO;
	public static int CH_HPREG1_FEE;
	public static int CH_HPREG2_FEE;
	public static int CH_HPREG3_FEE;
	public static int CH_HPREG4_FEE;
	public static int CH_HPREG5_FEE;
	public static int CH_HPREG6_FEE;
	public static int CH_HPREG7_FEE;
	public static int CH_HPREG8_FEE;
	public static int CH_HPREG9_FEE;
	public static int CH_HPREG10_FEE;
	public static int CH_HPREG11_FEE;
	public static int CH_HPREG12_FEE;
	public static int CH_HPREG13_FEE;
	public static long CH_EXPREG_FEE_RATIO;
	public static int CH_EXPREG1_FEE;
	public static int CH_EXPREG2_FEE;
	public static int CH_EXPREG3_FEE;
	public static int CH_EXPREG4_FEE;
	public static int CH_EXPREG5_FEE;
	public static int CH_EXPREG6_FEE;
	public static int CH_EXPREG7_FEE;
	public static long CH_ITEM_FEE_RATIO;
	public static int CH_ITEM1_FEE;
	public static int CH_ITEM2_FEE;
	public static int CH_ITEM3_FEE;
	public static long CH_CURTAIN_FEE_RATIO;
	public static int CH_CURTAIN1_FEE;
	public static int CH_CURTAIN2_FEE;
	public static long CH_FRONT_FEE_RATIO;
	public static int CH_FRONT1_FEE;
	public static int CH_FRONT2_FEE;
	
	// --------------------------------------------------
	// Events settings
	// --------------------------------------------------
	public static boolean PCB_ENABLE;
	public static int PCB_COIN_ID;
	public static int PCB_MIN_LEVEL;
	public static int PCB_POINT_MIN;
	public static int PCB_POINT_MAX;
	public static int PCB_CHANCE_DUAL_POINT;
	public static int PCB_INTERVAL;
	
	/** Olympiad */
	public static int OLY_START_TIME;
	public static int OLY_MIN;
	public static long OLY_CPERIOD;
	public static long OLY_BATTLE;
	public static long OLY_WPERIOD;
	public static long OLY_VPERIOD;
	public static int OLY_WAIT_TIME;
	public static int OLY_WAIT_BATTLE;
	public static int OLY_WAIT_END;
	public static int OLY_START_POINTS;
	public static int OLY_WEEKLY_POINTS;
	public static int OLY_MIN_MATCHES;
	public static int OLY_CLASSED;
	public static int OLY_NONCLASSED;
	public static IntIntHolder[] OLY_CLASSED_REWARD;
	public static IntIntHolder[] OLY_NONCLASSED_REWARD;
	public static int OLY_GP_PER_POINT;
	public static int OLY_HERO_POINTS;
	public static int OLY_RANK1_POINTS;
	public static int OLY_RANK2_POINTS;
	public static int OLY_RANK3_POINTS;
	public static int OLY_RANK4_POINTS;
	public static int OLY_RANK5_POINTS;
	public static int OLY_MAX_POINTS;
	public static int OLY_DIVIDER_CLASSED;
	public static int OLY_DIVIDER_NON_CLASSED;
	public static boolean OLY_ANNOUNCE_GAMES;
	
	/** SevenSigns Festival */
	public static boolean SEVEN_SIGNS_BYPASS_PREREQUISITES;
	public static int FESTIVAL_MIN_PLAYER;
	public static int MAXIMUM_PLAYER_CONTRIB;
	public static long FESTIVAL_MANAGER_START;
	public static long FESTIVAL_LENGTH;
	public static long FESTIVAL_CYCLE_LENGTH;
	public static long FESTIVAL_FIRST_SPAWN;
	public static long FESTIVAL_FIRST_SWARM;
	public static long FESTIVAL_SECOND_SPAWN;
	public static long FESTIVAL_SECOND_SWARM;
	public static long FESTIVAL_CHEST_SPAWN;
	
	/** Four Sepulchers */
	public static int FS_TIME_ENTRY;
	public static int FS_TIME_END;
	public static int FS_PARTY_MEMBER_COUNT;
	
	/** dimensional rift */
	public static int RIFT_MIN_PARTY_SIZE;
	public static int RIFT_SPAWN_DELAY;
	public static int RIFT_MAX_JUMPS;
	public static int RIFT_AUTO_JUMPS_TIME_MIN;
	public static int RIFT_AUTO_JUMPS_TIME_MAX;
	public static int RIFT_ENTER_COST_RECRUIT;
	public static int RIFT_ENTER_COST_SOLDIER;
	public static int RIFT_ENTER_COST_OFFICER;
	public static int RIFT_ENTER_COST_CAPTAIN;
	public static int RIFT_ENTER_COST_COMMANDER;
	public static int RIFT_ENTER_COST_HERO;
	public static double RIFT_BOSS_ROOM_TIME_MULTIPLY;
	
	/** Wedding system */
	public static boolean ALLOW_WEDDING;
	public static int WEDDING_PRICE;
	public static boolean WEDDING_SAMESEX;
	public static boolean WEDDING_FORMALWEAR;
	
	/** Lottery */
	public static int LOTTERY_PRIZE;
	public static int LOTTERY_TICKET_PRICE;
	public static double LOTTERY_5_NUMBER_RATE;
	public static double LOTTERY_4_NUMBER_RATE;
	public static double LOTTERY_3_NUMBER_RATE;
	public static int LOTTERY_2_AND_1_NUMBER_PRIZE;
	
	/** Fishing tournament */
	public static boolean ALLOW_FISH_CHAMPIONSHIP;
	public static int FISH_CHAMPIONSHIP_REWARD_ITEM;
	public static int FISH_CHAMPIONSHIP_REWARD_1;
	public static int FISH_CHAMPIONSHIP_REWARD_2;
	public static int FISH_CHAMPIONSHIP_REWARD_3;
	public static int FISH_CHAMPIONSHIP_REWARD_4;
	public static int FISH_CHAMPIONSHIP_REWARD_5;
	
	// --------------------------------------------------
	// GeoEngine
	// --------------------------------------------------
	
	/** Geodata */
	public static String GEODATA_PATH;
	public static GeoType GEODATA_TYPE;
	
	/** Path checking */
	public static int PART_OF_CHARACTER_HEIGHT;
	public static int MAX_OBSTACLE_HEIGHT;
	
	/** Path finding */
	public static String PATHFIND_BUFFERS;
	public static int MOVE_WEIGHT;
	public static int MOVE_WEIGHT_DIAG;
	public static int OBSTACLE_WEIGHT;
	public static int OBSTACLE_WEIGHT_DIAG;
	public static int HEURISTIC_WEIGHT;
	public static int HEURISTIC_WEIGHT_DIAG;
	public static int MAX_ITERATIONS;
	
	// --------------------------------------------------
	// HexID
	// --------------------------------------------------
	
	public static int SERVER_ID;
	public static byte[] HEX_ID;
	
	// --------------------------------------------------
	// Loginserver
	// --------------------------------------------------
	
	public static String LOGINSERVER_HOSTNAME;
	public static int LOGINSERVER_PORT;
	
	public static int LOGIN_TRY_BEFORE_BAN;
	public static int LOGIN_BLOCK_AFTER_BAN;
	public static boolean ACCEPT_NEW_GAMESERVER;
	
	public static boolean SHOW_LICENCE;
	
	public static boolean AUTO_CREATE_ACCOUNTS;
	
	public static boolean FLOOD_PROTECTION;
	public static int FAST_CONNECTION_LIMIT;
	public static int NORMAL_CONNECTION_TIME;
	public static int FAST_CONNECTION_TIME;
	public static int MAX_CONNECTION_PER_IP;
	
	// --------------------------------------------------
	// NPCs / Monsters
	// --------------------------------------------------
	
	/** SubClass Everywhere */
	public static boolean ALT_GAME_SUBCLASS_EVERYWHERE;
	public static int MAX_SUBCLASS_FORBASE;
	public static int SUB_CLASS_LEVEL_SET;
	/** Spawn */
	public static double SPAWN_MULTIPLIER;
	public static String[] SPAWN_EVENTS;
	
	/** Buffer */
	public static int BUFFER_MAX_SCHEMES;
	public static int BUFFER_STATIC_BUFF_COST;
	
	/** Class Master */
	public static boolean ALLOW_CLASS_MASTERS;
	public static boolean ALLOW_ENTIRE_TREE;
	public static ClassMasterSettings CLASS_MASTER_SETTINGS;
	
	/** Misc */
	public static boolean FREE_TELEPORT;
	public static boolean ANNOUNCE_MAMMON_SPAWN;
	public static boolean MOB_AGGRO_IN_PEACEZONE;
	public static boolean SHOW_NPC_LVL;
	public static boolean SHOW_NPC_CREST;
	public static boolean SHOW_SUMMON_CREST;
	
	/** Wyvern Manager */
	public static boolean WYVERN_ALLOW_UPGRADER;
	public static int WYVERN_REQUIRED_LEVEL;
	public static int WYVERN_REQUIRED_CRYSTALS;
	
	/** Raid Boss */
	public static double RAID_HP_REGEN_MULTIPLIER;
	public static double RAID_MP_REGEN_MULTIPLIER;
	public static double RAID_DEFENCE_MULTIPLIER;
	public static int RAID_MINION_RESPAWN_TIMER;

	/** AI */
	public static boolean GUARD_ATTACK_AGGRO_MOB;
	public static int RANDOM_WALK_RATE;
	public static int MAX_DRIFT_RANGE;
	public static int MIN_NPC_ANIMATION;
	public static int MAX_NPC_ANIMATION;
	public static int MIN_MONSTER_ANIMATION;
	public static int MAX_MONSTER_ANIMATION;
	
	// --------------------------------------------------
	// Players
	// --------------------------------------------------
	
	/** Misc */
	public static boolean EFFECT_CANCELING;
	public static double HP_REGEN_MULTIPLIER;
	public static double MP_REGEN_MULTIPLIER;
	public static double CP_REGEN_MULTIPLIER;
	public static int PLAYER_SPAWN_PROTECTION;
	public static int PLAYER_FAKEDEATH_UP_PROTECTION;
	public static double RESPAWN_RESTORE_HP;
	public static int MAX_PVTSTORE_SLOTS_DWARF;
	public static int MAX_PVTSTORE_SLOTS_OTHER;
	public static boolean DEEPBLUE_DROP_RULES;
	public static boolean ALLOW_DELEVEL;
	public static int DEATH_PENALTY_CHANCE;
	
	/** Inventory & WH */
	public static int INVENTORY_MAXIMUM_NO_DWARF;
	public static int INVENTORY_MAXIMUM_DWARF;
	public static int INVENTORY_MAXIMUM_PET;
	public static int MAX_ITEM_IN_PACKET;
	public static double WEIGHT_LIMIT;
	public static int WAREHOUSE_SLOTS_NO_DWARF;
	public static int WAREHOUSE_SLOTS_DWARF;
	public static int WAREHOUSE_SLOTS_CLAN;
	public static int FREIGHT_SLOTS;
	public static boolean REGION_BASED_FREIGHT;
	public static int FREIGHT_PRICE;
	

	
	/** Augmentations */
	public static int AUGMENTATION_NG_SKILL_CHANCE;
	public static int AUGMENTATION_NG_GLOW_CHANCE;
	public static int AUGMENTATION_MID_SKILL_CHANCE;
	public static int AUGMENTATION_MID_GLOW_CHANCE;
	public static int AUGMENTATION_HIGH_SKILL_CHANCE;
	public static int AUGMENTATION_HIGH_GLOW_CHANCE;
	public static int AUGMENTATION_TOP_SKILL_CHANCE;
	public static int AUGMENTATION_TOP_GLOW_CHANCE;
	public static int AUGMENTATION_BASESTAT_CHANCE;
	
	/** Karma & PvP */
	public static boolean KARMA_PLAYER_CAN_SHOP;
	public static boolean KARMA_PLAYER_CAN_USE_GK;
	public static boolean KARMA_PLAYER_CAN_TELEPORT;
	public static boolean KARMA_PLAYER_CAN_TRADE;
	public static boolean KARMA_PLAYER_CAN_USE_WH;
	
	public static boolean KARMA_DROP_GM;
	public static boolean KARMA_AWARD_PK_KILL;
	public static int KARMA_PK_LIMIT;
	
	public static int[] KARMA_NONDROPPABLE_PET_ITEMS;
	public static int[] KARMA_NONDROPPABLE_ITEMS;
	
	public static int PVP_NORMAL_TIME;
	public static int PVP_PVP_TIME;
	
	/** Party */
	public static String PARTY_XP_CUTOFF_METHOD;
	public static int PARTY_XP_CUTOFF_LEVEL;
	public static double PARTY_XP_CUTOFF_PERCENT;
	public static int PARTY_RANGE;
	
	/** GMs & Admin Stuff */
	public static int DEFAULT_ACCESS_LEVEL;
	public static boolean GM_HERO_AURA;
	public static boolean GM_STARTUP_INVULNERABLE;
	public static boolean GM_STARTUP_INVISIBLE;
	public static boolean GM_STARTUP_BLOCK_ALL;
	public static boolean GM_STARTUP_AUTO_LIST;
	
	/** petitions */
	public static boolean PETITIONING_ALLOWED;
	public static int MAX_PETITIONS_PER_PLAYER;
	public static int MAX_PETITIONS_PENDING;
	
	/** Crafting **/
	public static boolean IS_CRAFTING_ENABLED;
	public static int DWARF_RECIPE_LIMIT;
	public static int COMMON_RECIPE_LIMIT;
	public static boolean BLACKSMITH_USE_RECIPES;
	
	/** Skills & Classes **/
	public static boolean AUTO_LEARN_SKILLS;
	public static boolean MAGIC_FAILURES;
	public static int PERFECT_SHIELD_BLOCK_RATE;
	public static boolean LIFE_CRYSTAL_NEEDED;
	public static boolean SP_BOOK_NEEDED;
	public static boolean ES_SP_BOOK_NEEDED;
	public static boolean DIVINE_SP_BOOK_NEEDED;
	public static boolean SUBCLASS_WITHOUT_QUESTS;
	
	/** Buffs */
	public static boolean STORE_SKILL_COOLTIME;
	public static int MAX_BUFFS_AMOUNT;
	
	// --------------------------------------------------
	// Sieges
	// --------------------------------------------------
	
	public static int SIEGE_LENGTH;
	public static int MINIMUM_CLAN_LEVEL;
	public static int MAX_ATTACKERS_NUMBER;
	public static int MAX_DEFENDERS_NUMBER;
	public static int ATTACKERS_RESPAWN_DELAY;
	
	public static int CH_MINIMUM_CLAN_LEVEL;
	public static int CH_MAX_ATTACKERS_NUMBER;
	
	// --------------------------------------------------
	// Server
	// --------------------------------------------------
	
	public static String HOSTNAME;
	public static String GAMESERVER_HOSTNAME;
	public static int GAMESERVER_PORT;
	public static String GAMESERVER_LOGIN_HOSTNAME;
	public static int GAMESERVER_LOGIN_PORT;
	public static int REQUEST_ID;
	public static boolean ACCEPT_ALTERNATE_ID;
	public static boolean USE_BLOWFISH_CIPHER;
	
	/** Access to database */
	public static String DATABASE_URL;
	public static String DATABASE_LOGIN;
	public static String DATABASE_PASSWORD;
	public static int DATABASE_MAX_CONNECTIONS;
	
	/** serverList & Test */
	public static boolean SERVER_LIST_BRACKET;
	public static boolean SERVER_LIST_CLOCK;
	public static int SERVER_LIST_AGE;
	public static boolean SERVER_LIST_TESTSERVER;
	public static boolean SERVER_LIST_PVPSERVER;
	public static boolean SERVER_GMONLY;
	
	/** clients related */
	public static int DELETE_DAYS;
	public static int MAXIMUM_ONLINE_USERS;
	
	/** Auto-loot */
	public static boolean AUTO_LOOT;
	public static boolean AUTO_LOOT_HERBS;
	public static boolean AUTO_LOOT_RAID;
	
	/** Items Management */
	public static boolean ALLOW_DISCARDITEM;
	public static boolean MULTIPLE_ITEM_DROP;
	public static int HERB_AUTO_DESTROY_TIME;
	public static int ITEM_AUTO_DESTROY_TIME;
	public static int EQUIPABLE_ITEM_AUTO_DESTROY_TIME;
	public static Map<Integer, Integer> SPECIAL_ITEM_DESTROY_TIME;
	public static int PLAYER_DROPPED_ITEM_MULTIPLIER;
	
	/** Rate control */
	public static double RATE_XP;
	public static double RATE_SP;
	public static double RATE_PARTY_XP;
	public static double RATE_PARTY_SP;
	public static double RATE_DROP_CURRENCY;
	public static double RATE_DROP_ITEMS;
	public static double RATE_DROP_ITEMS_BY_RAID;
	public static double RATE_DROP_SPOIL;
	public static double RATE_DROP_HERBS;
	public static int RATE_DROP_MANOR;
	
	public static double RATE_QUEST_DROP;
	public static double RATE_QUEST_REWARD;
	public static double RATE_QUEST_REWARD_XP;
	public static double RATE_QUEST_REWARD_SP;
	public static double RATE_QUEST_REWARD_ADENA;
	
	public static double RATE_KARMA_EXP_LOST;
	public static double RATE_SIEGE_GUARDS_PRICE;
	
	public static int PLAYER_DROP_LIMIT;
	public static int PLAYER_RATE_DROP;
	public static int PLAYER_RATE_DROP_ITEM;
	public static int PLAYER_RATE_DROP_EQUIP;
	public static int PLAYER_RATE_DROP_EQUIP_WEAPON;
	
	public static int KARMA_DROP_LIMIT;
	public static int KARMA_RATE_DROP;
	public static int KARMA_RATE_DROP_ITEM;
	public static int KARMA_RATE_DROP_EQUIP;
	public static int KARMA_RATE_DROP_EQUIP_WEAPON;
	
	public static double PET_XP_RATE;
	public static int PET_FOOD_RATE;
	public static double SINEATER_XP_RATE;
	
	/** Allow types */
	public static boolean ALLOW_FREIGHT;
	public static boolean ALLOW_WAREHOUSE;
	public static boolean ALLOW_WEAR;
	public static int WEAR_DELAY;
	public static int WEAR_PRICE;
	public static boolean ALLOW_LOTTERY;
	public static boolean ALLOW_WATER;
	public static boolean ALLOW_BOAT;
	public static boolean ALLOW_CURSED_WEAPONS;
	public static boolean ALLOW_MANOR;
	public static boolean ENABLE_FALLING_DAMAGE;
	
	/** Debug & Dev */
	public static boolean NO_SPAWNS;
	public static boolean DEVELOPER;
	public static boolean PACKET_HANDLER_DEBUG;
	
	/** Deadlock Detector */
	public static boolean DEADLOCK_DETECTOR;
	public static int DEADLOCK_CHECK_INTERVAL;
	public static boolean RESTART_ON_DEADLOCK;
	
	/** Logs */
	public static boolean LOG_CHAT;
	public static boolean LOG_ITEMS;
	public static boolean GMAUDIT;

	/** Flood Protectors */
	public static int ROLL_DICE_TIME;
	public static int HERO_VOICE_TIME;
	public static int SUBCLASS_TIME;
	public static int DROP_ITEM_TIME;
	public static int SERVER_BYPASS_TIME;
	public static int MULTISELL_TIME;
	public static int MANUFACTURE_TIME;
	public static int MANOR_TIME;
	public static int SENDMAIL_TIME;
	public static int CHARACTER_SELECT_TIME;
	public static int GLOBAL_CHAT_TIME;
	public static int TRADE_CHAT_TIME;
	public static int SOCIAL_TIME;
	public static int SKIN_TIME;
	
	/** ThreadPool */
	public static int SCHEDULED_THREAD_POOL_COUNT;
	public static int THREADS_PER_SCHEDULED_THREAD_POOL;
	public static int INSTANT_THREAD_POOL_COUNT;
	public static int THREADS_PER_INSTANT_THREAD_POOL;
	
	/** Misc */
	public static boolean L2WALKER_PROTECTION;
	public static boolean SERVER_NEWS;
	public static int ZONE_TOWN;
	
	// --------------------------------------------------
	// Those "hidden" settings haven't configs to avoid admins to fuck their server
	// You still can experiment changing values here. But don't say I didn't warn you.
	// --------------------------------------------------
	
	/** Reserve Host on LoginServerThread */
	public static boolean RESERVE_HOST_ON_LOGIN = false; // default false
	
	/** MMO settings */
	public static int MMO_SELECTOR_SLEEP_TIME = 20; // default 20
	public static int MMO_MAX_SEND_PER_PASS = 80; // default 80
	public static int MMO_MAX_READ_PER_PASS = 80; // default 80
	public static int MMO_HELPER_BUFFER_COUNT = 20; // default 20
	
	/** Client Packets Queue settings */
	public static int CLIENT_PACKET_QUEUE_SIZE = MMO_MAX_READ_PER_PASS + 2; // default MMO_MAX_READ_PER_PASS + 2
	public static int CLIENT_PACKET_QUEUE_MAX_BURST_SIZE = MMO_MAX_READ_PER_PASS + 1; // default MMO_MAX_READ_PER_PASS + 1
	public static int CLIENT_PACKET_QUEUE_MAX_PACKETS_PER_SECOND = 160; // default 160
	public static int CLIENT_PACKET_QUEUE_MEASURE_INTERVAL = 5; // default 5
	public static int CLIENT_PACKET_QUEUE_MAX_AVERAGE_PACKETS_PER_SECOND = 80; // default 80
	public static int CLIENT_PACKET_QUEUE_MAX_FLOODS_PER_MIN = 2; // default 2
	public static int CLIENT_PACKET_QUEUE_MAX_OVERFLOWS_PER_MIN = 1; // default 1
	public static int CLIENT_PACKET_QUEUE_MAX_UNDERFLOWS_PER_MIN = 1; // default 1
	public static int CLIENT_PACKET_QUEUE_MAX_UNKNOWN_PER_MIN = 5; // default 5
	
	/**
	 * Loads offline settings.
	 */
	private static final void loadOffline()
	{
		final ExProperties offline = initProperties(Config.OFFLINE_FILE);
		
		OFFLINE_TRADE_ENABLE = offline.getProperty("OfflineTradeEnable", false);
		OFFLINE_CRAFT_ENABLE = offline.getProperty("OfflineCraftEnable", false);
		OFFLINE_IN_PEACE_ZONE = offline.getProperty("OfflineInPeaceZone", false);
		OFFLINE_NO_DAMAGE = offline.getProperty("OfflineNoDamage", false);
		OFFLINE_EFFECT = offline.getProperty("OfflineEffect", "none");
		OFFLINE_RESTORE = offline.getProperty("OfflineRestore", false);
		OFFLINE_MAX_DAYS = offline.getProperty("OfflineMaxDays", 10);
		OFFLINE_DISCONNECT = offline.getProperty("OfflineDisconnect", true);
		OFFLINE_NAME_COLOR = Integer.decode("0x" + offline.getProperty("OfflineNameColor", 808080));

	}

	
	// --------------------------------------------------
	
	/**
	 * Initialize {@link ExProperties} from specified configuration file.
	 * @param filename : File name to be loaded.
	 * @return ExProperties : Initialized {@link ExProperties}.
	 */
	public static final ExProperties initProperties(String filename)
	{
		final ExProperties result = new ExProperties();
		
		try
		{
			result.load(new File(filename));
		}
		catch (Exception e)
		{
			LOGGER.error("An error occured loading '{}' config.", e, filename);
		}
		
		return result;
	}
	
	/**
	 * Loads clan and clan hall settings.
	 */
	private static final void loadClans()
	{
		final ExProperties clans = initProperties(CLANS_FILE);
		
		CLAN_JOIN_DAYS = clans.getProperty("DaysBeforeJoinAClan", 5);
		CLAN_CREATE_DAYS = clans.getProperty("DaysBeforeCreateAClan", 10);
		MAX_NUM_OF_CLANS_IN_ALLY = clans.getProperty("MaxNumOfClansInAlly", 3);
		CLAN_MEMBERS_FOR_WAR = clans.getProperty("ClanMembersForWar", 15);
		CLAN_WAR_PENALTY_WHEN_ENDED = clans.getProperty("ClanWarPenaltyWhenEnded", 5);
		CLAN_DISSOLVE_DAYS = clans.getProperty("DaysToPassToDissolveAClan", 7);
		ALLY_JOIN_DAYS_WHEN_LEAVED = clans.getProperty("DaysBeforeJoinAllyWhenLeaved", 1);
		ALLY_JOIN_DAYS_WHEN_DISMISSED = clans.getProperty("DaysBeforeJoinAllyWhenDismissed", 1);
		ACCEPT_CLAN_DAYS_WHEN_DISMISSED = clans.getProperty("DaysBeforeAcceptNewClanWhenDismissed", 1);
		CREATE_ALLY_DAYS_WHEN_DISSOLVED = clans.getProperty("DaysBeforeCreateNewAllyWhenDissolved", 10);
		MEMBERS_CAN_WITHDRAW_FROM_CLANWH = clans.getProperty("MembersCanWithdrawFromClanWH", false);
		
		MANOR_REFRESH_TIME = clans.getProperty("ManorRefreshTime", 20);
		MANOR_REFRESH_MIN = clans.getProperty("ManorRefreshMin", 0);
		MANOR_APPROVE_TIME = clans.getProperty("ManorApproveTime", 6);
		MANOR_APPROVE_MIN = clans.getProperty("ManorApproveMin", 0);
		MANOR_MAINTENANCE_MIN = clans.getProperty("ManorMaintenanceMin", 6);
		MANOR_SAVE_PERIOD_RATE = clans.getProperty("ManorSavePeriodRate", 2) * 3600000;
		
		CH_TELE_FEE_RATIO = clans.getProperty("ClanHallTeleportFunctionFeeRatio", 86400000L);
		CH_TELE1_FEE = clans.getProperty("ClanHallTeleportFunctionFeeLvl1", 7000);
		CH_TELE2_FEE = clans.getProperty("ClanHallTeleportFunctionFeeLvl2", 14000);
		CH_SUPPORT_FEE_RATIO = clans.getProperty("ClanHallSupportFunctionFeeRatio", 86400000L);
		CH_SUPPORT1_FEE = clans.getProperty("ClanHallSupportFeeLvl1", 17500);
		CH_SUPPORT2_FEE = clans.getProperty("ClanHallSupportFeeLvl2", 35000);
		CH_SUPPORT3_FEE = clans.getProperty("ClanHallSupportFeeLvl3", 49000);
		CH_SUPPORT4_FEE = clans.getProperty("ClanHallSupportFeeLvl4", 77000);
		CH_SUPPORT5_FEE = clans.getProperty("ClanHallSupportFeeLvl5", 147000);
		CH_SUPPORT6_FEE = clans.getProperty("ClanHallSupportFeeLvl6", 252000);
		CH_SUPPORT7_FEE = clans.getProperty("ClanHallSupportFeeLvl7", 259000);
		CH_SUPPORT8_FEE = clans.getProperty("ClanHallSupportFeeLvl8", 364000);
		CH_MPREG_FEE_RATIO = clans.getProperty("ClanHallMpRegenerationFunctionFeeRatio", 86400000L);
		CH_MPREG1_FEE = clans.getProperty("ClanHallMpRegenerationFeeLvl1", 14000);
		CH_MPREG2_FEE = clans.getProperty("ClanHallMpRegenerationFeeLvl2", 26250);
		CH_MPREG3_FEE = clans.getProperty("ClanHallMpRegenerationFeeLvl3", 45500);
		CH_MPREG4_FEE = clans.getProperty("ClanHallMpRegenerationFeeLvl4", 96250);
		CH_MPREG5_FEE = clans.getProperty("ClanHallMpRegenerationFeeLvl5", 140000);
		CH_HPREG_FEE_RATIO = clans.getProperty("ClanHallHpRegenerationFunctionFeeRatio", 86400000L);
		CH_HPREG1_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl1", 4900);
		CH_HPREG2_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl2", 5600);
		CH_HPREG3_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl3", 7000);
		CH_HPREG4_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl4", 8166);
		CH_HPREG5_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl5", 10500);
		CH_HPREG6_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl6", 12250);
		CH_HPREG7_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl7", 14000);
		CH_HPREG8_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl8", 15750);
		CH_HPREG9_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl9", 17500);
		CH_HPREG10_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl10", 22750);
		CH_HPREG11_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl11", 26250);
		CH_HPREG12_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl12", 29750);
		CH_HPREG13_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl13", 36166);
		CH_EXPREG_FEE_RATIO = clans.getProperty("ClanHallExpRegenerationFunctionFeeRatio", 86400000L);
		CH_EXPREG1_FEE = clans.getProperty("ClanHallExpRegenerationFeeLvl1", 21000);
		CH_EXPREG2_FEE = clans.getProperty("ClanHallExpRegenerationFeeLvl2", 42000);
		CH_EXPREG3_FEE = clans.getProperty("ClanHallExpRegenerationFeeLvl3", 63000);
		CH_EXPREG4_FEE = clans.getProperty("ClanHallExpRegenerationFeeLvl4", 105000);
		CH_EXPREG5_FEE = clans.getProperty("ClanHallExpRegenerationFeeLvl5", 147000);
		CH_EXPREG6_FEE = clans.getProperty("ClanHallExpRegenerationFeeLvl6", 163331);
		CH_EXPREG7_FEE = clans.getProperty("ClanHallExpRegenerationFeeLvl7", 210000);
		CH_ITEM_FEE_RATIO = clans.getProperty("ClanHallItemCreationFunctionFeeRatio", 86400000L);
		CH_ITEM1_FEE = clans.getProperty("ClanHallItemCreationFunctionFeeLvl1", 210000);
		CH_ITEM2_FEE = clans.getProperty("ClanHallItemCreationFunctionFeeLvl2", 490000);
		CH_ITEM3_FEE = clans.getProperty("ClanHallItemCreationFunctionFeeLvl3", 980000);
		CH_CURTAIN_FEE_RATIO = clans.getProperty("ClanHallCurtainFunctionFeeRatio", 86400000L);
		CH_CURTAIN1_FEE = clans.getProperty("ClanHallCurtainFunctionFeeLvl1", 2002);
		CH_CURTAIN2_FEE = clans.getProperty("ClanHallCurtainFunctionFeeLvl2", 2625);
		CH_FRONT_FEE_RATIO = clans.getProperty("ClanHallFrontPlatformFunctionFeeRatio", 86400000L);
		CH_FRONT1_FEE = clans.getProperty("ClanHallFrontPlatformFunctionFeeLvl1", 3031);
		CH_FRONT2_FEE = clans.getProperty("ClanHallFrontPlatformFunctionFeeLvl2", 9331);
	}

	/** CB / Community Bord */
	public static boolean ENABLE_CB_CUSTOM;
	public static String CB_DEFAULT;
	
	public static int PremiumConsumeItemId;
	public static String ConsumeName;
	public static int PrinceBuyPremium30;
	public static int PrinceBuyPremium90;
	public static int PrinceBuyHero30;
	public static int PrinceBuyHero90;
	
	public static int ItemIdChangeName;
	public static int PrinceChangeName;
	public static String ConsumeNameChange;
	public static int ItemIdChangeRace;
	public static int PrinceChangeRace;
	public static String ConsumeNameRace;
	
	/**
	 * Loads All CB Custom settings.
	 */
	public static final void loadCommunity()
	{
		final ExProperties Community = initProperties(COMMUNITY_BORD);
		ENABLE_CB_CUSTOM = Community.getProperty("CustomCommunityBoard", false);
		CB_DEFAULT = Community.getProperty("CBDefault", "_cbhome");
		
		PremiumConsumeItemId = Community.getProperty("ConsumeItemId", 0);
		ConsumeName = Community.getProperty("ConsumeName", "Gold");
		PrinceBuyPremium30 = Community.getProperty("ValueVip30Days", 0);
		PrinceBuyPremium90 = Community.getProperty("ValueVip90Days", 0);
		
		PrinceBuyHero30 = Community.getProperty("ValueHero30Days", 0);
		PrinceBuyHero90 = Community.getProperty("ValueHero90Days", 0);
		
		ItemIdChangeName = Integer.parseInt(Community.getProperty("ItemIdChangeName", "1"));
		PrinceChangeName = Integer.parseInt(Community.getProperty("PrinceChangeName", "1"));
		ConsumeNameChange = Community.getProperty("ConsumeItemNameChange", "L2JDev");
		
		ItemIdChangeRace = Integer.parseInt(Community.getProperty("ItemIdChangeRace", "1"));
		PrinceChangeRace = Integer.parseInt(Community.getProperty("PrinceChangeRace", "1"));
		ConsumeNameRace = Community.getProperty("ConsumeItemNameRace", "L2JDev");
		
	}
	
	/** Olly / New Time */
	public static boolean ALT_OLY_USE_CUSTOM_PERIOD_SETTINGS;
	public static int ALT_OLY_PERIOD_MULTIPLIER;
	public static String ALT_OLY_PERIOD;
	
	public static String ALT_OLY_RESTRICTED_ITEMS;
	public static List<Integer> LIST_OLY_RESTRICTED_ITEMS = new ArrayList<>();
	public static boolean ALT_OLY_GRADE_A;
	public static int ALT_OLY_ENCHANT_LIMIT;
	
	/**
	 * Loads event settings.<br>
	 * Such as olympiad, seven signs festival, four sepulchures, dimensional rift, weddings, lottery, fishing championship.
	 */
	private static final void loadEvents()
	{
		final ExProperties events = initProperties(EVENTS_FILE);
		
		PCB_ENABLE = Boolean.parseBoolean(events.getProperty("PcBangPointEnable", "true"));
		PCB_MIN_LEVEL = Integer.parseInt(events.getProperty("PcBangPointMinLevel", "20"));
		PCB_POINT_MIN = Integer.parseInt(events.getProperty("PcBangPointMinCount", "20"));
		PCB_POINT_MAX = Integer.parseInt(events.getProperty("PcBangPointMaxCount", "1000000"));
		PCB_COIN_ID = Integer.parseInt(events.getProperty("PCBCoinId", "0"));
		if (PCB_POINT_MAX < 1)
			PCB_POINT_MAX = Integer.MAX_VALUE;
		PCB_CHANCE_DUAL_POINT = Integer.parseInt(events.getProperty("PcBangPointDualChance", "20"));
		PCB_INTERVAL = Integer.parseInt(events.getProperty("PcBangPointTimeStamp", "900"));
		
		OLY_ANNOUNCE_GAMES = events.getProperty("OlyAnnounceGames", true);
		ALT_OLY_USE_CUSTOM_PERIOD_SETTINGS = events.getProperty("AltOlyUseCustomPeriodSettings", false);
		ALT_OLY_PERIOD_MULTIPLIER = events.getProperty("AltOlyPeriodMultiplier", 1);
		ALT_OLY_PERIOD = events.getProperty("AltOlyPeriod", "MONTH");
		ALT_OLY_RESTRICTED_ITEMS = events.getProperty("AltOlyRestrictedItems", "0");
		LIST_OLY_RESTRICTED_ITEMS = new ArrayList<>();
		for (String id : ALT_OLY_RESTRICTED_ITEMS.split(","))
		{
			LIST_OLY_RESTRICTED_ITEMS.add(Integer.parseInt(id));
		}
		ALT_OLY_GRADE_A = events.getProperty("OlyEnableCrystalTypeA", false);
		ALT_OLY_ENCHANT_LIMIT = events.getProperty("AltOlyMaxEnchant", -1);
		
		OLY_START_TIME = events.getProperty("OlyStartTime", 18);
		OLY_MIN = events.getProperty("OlyMin", 0);
		OLY_CPERIOD = events.getProperty("OlyCPeriod", 21600000L);
		OLY_BATTLE = events.getProperty("OlyBattle", 180000L);
		OLY_WPERIOD = events.getProperty("OlyWPeriod", 604800000L);
		OLY_VPERIOD = events.getProperty("OlyVPeriod", 86400000L);
		OLY_WAIT_TIME = events.getProperty("OlyWaitTime", 30);
		OLY_WAIT_BATTLE = events.getProperty("OlyWaitBattle", 60);
		OLY_WAIT_END = events.getProperty("OlyWaitEnd", 40);
		OLY_START_POINTS = events.getProperty("OlyStartPoints", 18);
		OLY_WEEKLY_POINTS = events.getProperty("OlyWeeklyPoints", 3);
		OLY_MIN_MATCHES = events.getProperty("OlyMinMatchesToBeClassed", 5);
		OLY_CLASSED = events.getProperty("OlyClassedParticipants", 5);
		OLY_NONCLASSED = events.getProperty("OlyNonClassedParticipants", 9);
		OLY_CLASSED_REWARD = events.parseIntIntList("OlyClassedReward", "6651-50");
		OLY_NONCLASSED_REWARD = events.parseIntIntList("OlyNonClassedReward", "6651-30");
		OLY_GP_PER_POINT = events.getProperty("OlyGPPerPoint", 1000);
		OLY_HERO_POINTS = events.getProperty("OlyHeroPoints", 300);
		OLY_RANK1_POINTS = events.getProperty("OlyRank1Points", 100);
		OLY_RANK2_POINTS = events.getProperty("OlyRank2Points", 75);
		OLY_RANK3_POINTS = events.getProperty("OlyRank3Points", 55);
		OLY_RANK4_POINTS = events.getProperty("OlyRank4Points", 40);
		OLY_RANK5_POINTS = events.getProperty("OlyRank5Points", 30);
		OLY_MAX_POINTS = events.getProperty("OlyMaxPoints", 10);
		OLY_DIVIDER_CLASSED = events.getProperty("OlyDividerClassed", 3);
		OLY_DIVIDER_NON_CLASSED = events.getProperty("OlyDividerNonClassed", 5);
		OLY_ANNOUNCE_GAMES = events.getProperty("OlyAnnounceGames", true);
		
		SEVEN_SIGNS_BYPASS_PREREQUISITES = events.getProperty("SevenSignsBypassPrerequisites", false);
		FESTIVAL_MIN_PLAYER = MathUtil.limit(events.getProperty("FestivalMinPlayer", 5), 2, 9);
		MAXIMUM_PLAYER_CONTRIB = events.getProperty("MaxPlayerContrib", 1000000);
		FESTIVAL_MANAGER_START = events.getProperty("FestivalManagerStart", 120000L);
		FESTIVAL_LENGTH = events.getProperty("FestivalLength", 1080000L);
		FESTIVAL_CYCLE_LENGTH = events.getProperty("FestivalCycleLength", 2280000L);
		FESTIVAL_FIRST_SPAWN = events.getProperty("FestivalFirstSpawn", 120000L);
		FESTIVAL_FIRST_SWARM = events.getProperty("FestivalFirstSwarm", 300000L);
		FESTIVAL_SECOND_SPAWN = events.getProperty("FestivalSecondSpawn", 540000L);
		FESTIVAL_SECOND_SWARM = events.getProperty("FestivalSecondSwarm", 720000L);
		FESTIVAL_CHEST_SPAWN = events.getProperty("FestivalChestSpawn", 900000L);
		
		FS_TIME_ENTRY = events.getProperty("EntryTime", 55);
		FS_TIME_END = events.getProperty("EndTime", 50);
		FS_PARTY_MEMBER_COUNT = MathUtil.limit(events.getProperty("NeededPartyMembers", 4), 2, 9);
		
		RIFT_MIN_PARTY_SIZE = events.getProperty("RiftMinPartySize", 2);
		RIFT_MAX_JUMPS = events.getProperty("MaxRiftJumps", 4);
		RIFT_SPAWN_DELAY = events.getProperty("RiftSpawnDelay", 10000);
		RIFT_AUTO_JUMPS_TIME_MIN = events.getProperty("AutoJumpsDelayMin", 480);
		RIFT_AUTO_JUMPS_TIME_MAX = events.getProperty("AutoJumpsDelayMax", 600);
		RIFT_ENTER_COST_RECRUIT = events.getProperty("RecruitCost", 18);
		RIFT_ENTER_COST_SOLDIER = events.getProperty("SoldierCost", 21);
		RIFT_ENTER_COST_OFFICER = events.getProperty("OfficerCost", 24);
		RIFT_ENTER_COST_CAPTAIN = events.getProperty("CaptainCost", 27);
		RIFT_ENTER_COST_COMMANDER = events.getProperty("CommanderCost", 30);
		RIFT_ENTER_COST_HERO = events.getProperty("HeroCost", 33);
		RIFT_BOSS_ROOM_TIME_MULTIPLY = events.getProperty("BossRoomTimeMultiply", 1.);
		
		ALLOW_WEDDING = events.getProperty("AllowWedding", false);
		WEDDING_PRICE = events.getProperty("WeddingPrice", 1000000);
		WEDDING_SAMESEX = events.getProperty("WeddingAllowSameSex", false);
		WEDDING_FORMALWEAR = events.getProperty("WeddingFormalWear", true);
		
		LOTTERY_PRIZE = events.getProperty("LotteryPrize", 50000);
		LOTTERY_TICKET_PRICE = events.getProperty("LotteryTicketPrice", 2000);
		LOTTERY_5_NUMBER_RATE = events.getProperty("Lottery5NumberRate", 0.6);
		LOTTERY_4_NUMBER_RATE = events.getProperty("Lottery4NumberRate", 0.2);
		LOTTERY_3_NUMBER_RATE = events.getProperty("Lottery3NumberRate", 0.2);
		LOTTERY_2_AND_1_NUMBER_PRIZE = events.getProperty("Lottery2and1NumberPrize", 200);
		
		ALLOW_FISH_CHAMPIONSHIP = events.getProperty("AllowFishChampionship", true);
		FISH_CHAMPIONSHIP_REWARD_ITEM = events.getProperty("FishChampionshipRewardItemId", 57);
		FISH_CHAMPIONSHIP_REWARD_1 = events.getProperty("FishChampionshipReward1", 800000);
		FISH_CHAMPIONSHIP_REWARD_2 = events.getProperty("FishChampionshipReward2", 500000);
		FISH_CHAMPIONSHIP_REWARD_3 = events.getProperty("FishChampionshipReward3", 300000);
		FISH_CHAMPIONSHIP_REWARD_4 = events.getProperty("FishChampionshipReward4", 200000);
		FISH_CHAMPIONSHIP_REWARD_5 = events.getProperty("FishChampionshipReward5", 100000);
	}
	
	/**
	 * Loads geoengine settings.
	 */
	private static final void loadGeoengine()
	{
		final ExProperties geoengine = initProperties(GEOENGINE_FILE);
		
		GEODATA_PATH = geoengine.getProperty("GeoDataPath", "./data/geodata/");
		GEODATA_TYPE = Enum.valueOf(GeoType.class, geoengine.getProperty("GeoDataType", "L2OFF"));
		
		PART_OF_CHARACTER_HEIGHT = geoengine.getProperty("PartOfCharacterHeight", 75);
		MAX_OBSTACLE_HEIGHT = geoengine.getProperty("MaxObstacleHeight", 32);
		
		PATHFIND_BUFFERS = geoengine.getProperty("PathFindBuffers", "500x10;1000x10;3000x5;5000x3;10000x3");
		MOVE_WEIGHT = geoengine.getProperty("MoveWeight", 10);
		MOVE_WEIGHT_DIAG = geoengine.getProperty("MoveWeightDiag", 14);
		OBSTACLE_WEIGHT = geoengine.getProperty("ObstacleWeight", 30);
		OBSTACLE_WEIGHT_DIAG = (int) (OBSTACLE_WEIGHT * Math.sqrt(2));
		HEURISTIC_WEIGHT = geoengine.getProperty("HeuristicWeight", 12);
		HEURISTIC_WEIGHT_DIAG = geoengine.getProperty("HeuristicWeightDiag", 18);
		MAX_ITERATIONS = geoengine.getProperty("MaxIterations", 3500);
	}
	public static String NEW_PLAYER_TITLE;
	
	public static final void loadSpecialutorial()
	{
		final ExProperties tutorial = initProperties(SPECIAL_TUTORIAL_SETTINGS);
		
		PLAYER_START_CREATION_LEVEL = tutorial.getProperty("PlayerStartLevel", 1);
		PM_MESSAGE = tutorial.getProperty("PmMessage", true);
		PM_SERVER_NAME = tutorial.getProperty("PMServerName", "L2World");
		PM_TEXT1 = tutorial.getProperty("PMText1", "Have Fun and Nice Stay on");
		PM_TEXT2 = tutorial.getProperty("PMText2", "Vote for us every 12h");
		
		ENABLE_SPAWN_START_PLAYER = tutorial.getProperty("EnableSpawnLocation", false);
		String[] propertyPtLoc33 = tutorial.getProperty("SpawnLocation", "0,0,0").split(",");
		if (propertyPtLoc33.length < 3)
		{
			System.out.println("Error : config/JDev/tutorial.properties \"SpawnLocation\" coord locations");
		}
		else
		{
			SPAWN_START_TUTORIAL_LOCATION[0] = Integer.parseInt(propertyPtLoc33[0]);
			SPAWN_START_TUTORIAL_LOCATION[1] = Integer.parseInt(propertyPtLoc33[1]);
			SPAWN_START_TUTORIAL_LOCATION[2] = Integer.parseInt(propertyPtLoc33[2]);
		}

		
		ITEMID_ETC_TUTORIAL_00 = tutorial.getProperty("EtcItemIdMana", 0);
		CONT_ETC_TUTORIAL_00 = tutorial.getProperty("EtcCountMana", 0);
		
		ITEMID_ETC_TUTORIAL_01 = tutorial.getProperty("EtcItemIdHp", 0);
		CONT_ETC_TUTORIAL_01 = tutorial.getProperty("EtcCountHp", 0);
		
		ITEMID_ETC_TUTORIAL_02 = tutorial.getProperty("EtcItemIdScroll", 0);
		CONT_ETC_TUTORIAL_02 = tutorial.getProperty("EtcCountScroll", 0);
		
		ITEMID_ETC_TUTORIAL_03 = tutorial.getProperty("EtcItemIdSoushot", 0);
		CONT_ETC_TUTORIAL_03 = tutorial.getProperty("EtcCountSoushot", 0);
		
		ITEMID_ETC_TUTORIAL_04 = tutorial.getProperty("EtcItemIdBlessedShot", 0);
		CONT_ETC_TUTORIAL_04 = tutorial.getProperty("EtcCountBlessedShot", 0);
		
		ITEMID_ETC_TUTORIAL_05 = tutorial.getProperty("EtcItemIdArrow", 0);
		CONT_ETC_TUTORIAL_05 = tutorial.getProperty("EtcCountArrow", 0);
		NEW_PLAYER_TITLE = tutorial.getProperty("NewPlayerTitle", "");
		
		MIN_ENCHANT_TUTORIAL = tutorial.getProperty("MinEnchantItems", 0);
		MAX_ENCHANT_TUTORIAL = tutorial.getProperty("MaxEnchantItems", 0);
		
		ITEMID_WEAPON_TUTORIAL_00 = tutorial.getProperty("WeaponBowId", 0);
		
		NAME_WEAPON_TUTORIAL_00 = tutorial.getProperty("WeaponBowName", "");
		DESC_WEAPON_TUTORIAL_00 = tutorial.getProperty("WeaponBowDesc", "");
		ICON_WEAPON_00 = tutorial.getProperty("BowIcon32x32", "");

		ITEMID_WEAPON_TUTORIAL_01 = tutorial.getProperty("WeaponSwordId", 0);
		
		NAME_WEAPON_TUTORIAL_01 = tutorial.getProperty("WeaponSwordName", "");
		DESC_WEAPON_TUTORIAL_01 = tutorial.getProperty("WeaponSwordDesc", "");
		ICON_WEAPON_01 = tutorial.getProperty("SwordIcon32x32", "");

		ITEMID_WEAPON_TUTORIAL_02 = tutorial.getProperty("WeaponDaggerId", 0);
		
		NAME_WEAPON_TUTORIAL_02 = tutorial.getProperty("WeaponDaggerName", "");
		DESC_WEAPON_TUTORIAL_02 = tutorial.getProperty("WeaponDaggerDesc", "");
		ICON_WEAPON_02 = tutorial.getProperty("DaggerIcon32x32", "");

		ITEMID_WEAPON_TUTORIAL_03 = tutorial.getProperty("WeaponStaffId", 0);
		NAME_WEAPON_TUTORIAL_03 = tutorial.getProperty("WeaponStaffName", "");
		DESC_WEAPON_TUTORIAL_03 = tutorial.getProperty("WeaponStaffDesc", "");
		ICON_WEAPON_03 = tutorial.getProperty("StaffIcon32x32", "");

		
		NAME_BUFFER_TUTORIAL_00 = tutorial.getProperty("BufferFightName", "");
		DESC_BUFFER_TUTORIAL_00 = tutorial.getProperty("BufferFightDesc", "");
		ICON_BUFFER_FIGHT_00 = tutorial.getProperty("Icon32x32Fight", "");

		NAME_BUFFER_TUTORIAL_01 = tutorial.getProperty("BufferMagicName", "");
		DESC_BUFFER_TUTORIAL_01 = tutorial.getProperty("BufferMagicDesc", "");
		ICON_BUFFER_MAGIC_01 = tutorial.getProperty("Icon32x32Magic", "");

		
		TUTORIAL_BUFFER_FIGHTER_SET = tutorial.getProperty("BufferFighterSet", "");
		TUTORIAL_BUFFER_MAGE_SET = tutorial.getProperty("BufferMageSet", "");
		
		NAME_ARMOR_TUTORIAL_00= tutorial.getProperty("NameArmorLight", "");
		DESC_ARMOR_TUTORIAL_00= tutorial.getProperty("DescArmorLight", "");
		ICON_ARMOR_LIGHT_00= tutorial.getProperty("Icon32x32Light", "");
		
		NAME_ARMOR_TUTORIAL_01= tutorial.getProperty("NameArmorHeavy", "");
		DESC_ARMOR_TUTORIAL_01= tutorial.getProperty("DescArmorHeavy", "");
		ICON_ARMOR_HEAVY_01= tutorial.getProperty("Icon32x32Heavy", "");
		
		NAME_ARMOR_TUTORIAL_02= tutorial.getProperty("NameArmorRobe", "");
		DESC_ARMOR_TUTORIAL_02= tutorial.getProperty("DescArmorRobe", "");
		ICON_ARMOR_ROBE_02= tutorial.getProperty("Icon32x32Robe", "");
		
		
		NAME_TELEPORT_TUTORIAL_00 = tutorial.getProperty("TeleportNameZoneA", "");
		DESC_TELEPORT_TUTORIAL_00 = tutorial.getProperty("DescZoneA", "");
		ICON_TELEPORT_GOTO_00 = tutorial.getProperty("Icon32x32ZoneA", "");
		
		String[] propertyPtLoc323 = tutorial.getProperty("TeleportLocationZoneA", "0,0,0").split(",");
		if (propertyPtLoc323.length < 3)
		{
			System.out.println("Error : config/JDev/tutorial.properties \"TeleportLocationZoneA\" coord locations");
		}
		else
		{
			SOE_TELEPORT_GOTO_00_LOCATION[0] = Integer.parseInt(propertyPtLoc323[0]);
			SOE_TELEPORT_GOTO_00_LOCATION[1] = Integer.parseInt(propertyPtLoc323[1]);
			SOE_TELEPORT_GOTO_00_LOCATION[2] = Integer.parseInt(propertyPtLoc323[2]);
		}

		

		NAME_TELEPORT_TUTORIAL_01 = tutorial.getProperty("TeleportNameZoneB", "");
		DESC_TELEPORT_TUTORIAL_01 = tutorial.getProperty("DescZoneB", "");
		ICON_TELEPORT_GOTO_01 = tutorial.getProperty("Icon32x32ZoneB", "");
		String[] propertyPtLoc1 = tutorial.getProperty("TeleportLocationZoneB", "0,0,0").split(",");
		if (propertyPtLoc1.length < 3)
		{
			System.out.println("Error : config/JDev/tutorial.properties \"TeleportLocationZoneB\" coord locations");
		}
		else
		{
			SOE_TELEPORT_GOTO_01_LOCATION[0] = Integer.parseInt(propertyPtLoc1[0]);
			SOE_TELEPORT_GOTO_01_LOCATION[1] = Integer.parseInt(propertyPtLoc1[1]);
			SOE_TELEPORT_GOTO_01_LOCATION[2] = Integer.parseInt(propertyPtLoc1[2]);
		}
		
		NAME_TELEPORT_TUTORIAL_02 = tutorial.getProperty("TeleportNameZoneC", "");
		DESC_TELEPORT_TUTORIAL_02 = tutorial.getProperty("DescZoneC", "");
		ICON_TELEPORT_GOTO_02 = tutorial.getProperty("Icon32x32ZoneC", "");
		String[] propertyPtLoc11 = tutorial.getProperty("TeleportLocationZoneC", "0,0,0").split(",");
		if (propertyPtLoc11.length < 3)
		{
			System.out.println("Error : config/JDev/tutorial.properties \"TeleportLocationZoneC\" coord locations");
		}
		else
		{
			SOE_TELEPORT_GOTO_02_LOCATION[0] = Integer.parseInt(propertyPtLoc11[0]);
			SOE_TELEPORT_GOTO_02_LOCATION[1] = Integer.parseInt(propertyPtLoc11[1]);
			SOE_TELEPORT_GOTO_02_LOCATION[2] = Integer.parseInt(propertyPtLoc11[2]);
		}
		
		NAME_TELEPORT_TUTORIAL_03 = tutorial.getProperty("TeleportNameZoneD", "");
		DESC_TELEPORT_TUTORIAL_03 = tutorial.getProperty("DescZoneD", "");
		ICON_TELEPORT_GOTO_03 = tutorial.getProperty("Icon32x32ZoneD", "");
		String[] propertyPtLoc111 = tutorial.getProperty("TeleportLocationZoneD", "0,0,0").split(",");
		if (propertyPtLoc111.length < 3)
		{
			System.out.println("Error : config/JDev/tutorial.properties \"TeleportLocationZoneD\" coord locations");
		}
		else
		{
			SOE_TELEPORT_GOTO_03_LOCATION[0] = Integer.parseInt(propertyPtLoc111[0]);
			SOE_TELEPORT_GOTO_03_LOCATION[1] = Integer.parseInt(propertyPtLoc111[1]);
			SOE_TELEPORT_GOTO_03_LOCATION[2] = Integer.parseInt(propertyPtLoc111[2]);
		}
		
		
		USE_CUSTOM_CAMERA = tutorial.getProperty("UseCustomCamera", true);
		USE_CUSTOM_DURATION = tutorial.getProperty("Custom_Camera_Duration", 6000);
		CUSTOM_CAMERA.clear();
		for (String reward : tutorial.getProperty("Custom_Camera", "33333,225,6000").split(";"))
		{
			String[] rewardSplit = reward.split(",");
			CUSTOM_CAMERA.add(new int[]
			{
				Integer.parseInt(rewardSplit[0]),
				Integer.parseInt(rewardSplit[1]),
				Integer.parseInt(rewardSplit[2])
			});
		}
		
		String[] propertySplit = tutorial.getProperty("SetRobe", "4223,1").split(";");
		SET_ROBE_ITEMS.clear();
		for (String reward : propertySplit)
		{
			String[] rewardSplit = reward.split(",");
			if (rewardSplit.length != 2)
			{
				System.out.println("StartingCustomItemsFighter[Config.load()]: invalid config property -> StartingCustomItemsFighter \"" + reward + "\"");
			}
			else
			{
				try
				{
					SET_ROBE_ITEMS.add(new int[]
					{
						Integer.parseInt(rewardSplit[0]),
						Integer.parseInt(rewardSplit[1])
					});
				}
				catch (NumberFormatException nfe)
				{
					if (!reward.isEmpty())
					{
						System.out.println("StartingCustomItemsFighter[Config.load()]: invalid config property -> StartingCustomItemsFighter \"" + reward + "\"");
					}
				}
			}
		}
		propertySplit = tutorial.getProperty("SetHeavy", "4223,1").split(";");
		SET_HEAVY_ITEMS.clear();
		for (String reward : propertySplit)
		{
			String[] rewardSplit = reward.split(",");
			if (rewardSplit.length != 2)
			{
				System.out.println("SetHeavy[Other.load()]: invalid config property -> SetHeavy \"" + reward + "\"");
			}
			else
			{
				try
				{
					SET_HEAVY_ITEMS.add(new int[]
					{
						Integer.parseInt(rewardSplit[0]),
						Integer.parseInt(rewardSplit[1])
					});
				}
				catch (NumberFormatException nfe)
				{
					if (!reward.isEmpty())
					{
						System.out.println("SetHeavy[Other.load()]: invalid config property -> SetHeavy \"" + reward + "\"");
					}
				}
			}
		}
		
		propertySplit = tutorial.getProperty("SetLight", "4223,1").split(";");
		SET_LIGHT_ITEMS.clear();
		for (String reward : propertySplit)
		{
			String[] rewardSplit = reward.split(",");
			if (rewardSplit.length != 2)
			{
				System.out.println("SetLight[Other.load()]: invalid config property -> SetLight \"" + reward + "\"");
			}
			else
			{
				try
				{
					SET_LIGHT_ITEMS.add(new int[]
					{
						Integer.parseInt(rewardSplit[0]),
						Integer.parseInt(rewardSplit[1])
					});
				}
				catch (NumberFormatException nfe)
				{
					if (!reward.isEmpty())
					{
						System.out.println("SetLight[Other.load()]: invalid config property -> SetLight \"" + reward + "\"");
					}
				}
			}
		}
	}
	
	public static int PARTY_FARM_MONSTER_DALAY;
	public static String EVENT_NAME;
	public static int monsterId;
	public static int MONSTER_LOCS_COUNT;
	public static int[][] MONSTER_LOCS;
	
	public static List<Integer> PART_ZONE_MONSTERS_ID;
	public static String PART_ZONE_MONSTERS;
	public static List<RewardHolder> PARTY_ZONE_REWARDS = new ArrayList<>();
	public static List<RewardHolder> PARTY_ZONE_VIP_REWARDS = new ArrayList<>();
    public static int EVENT_BEST_FARM_TIME;
    public static String[] EVENT_BEST_FARM_INTERVAL_BY_TIME_OF_DAY;
	/**
	 * Loads PartyFarm settings.
	 */
	public static final void loadPartyFarm()
	{
		final ExProperties partyfarm = initProperties(Config.PARTYFARM_FILE);
		
		EVENT_BEST_FARM_TIME = Integer.parseInt(partyfarm.getProperty("EventBestFarmTime", "1"));
		EVENT_BEST_FARM_INTERVAL_BY_TIME_OF_DAY = partyfarm.getProperty("BestFarmStartTime", "20:00").split(",");
		
		PARTY_FARM_MONSTER_DALAY = Integer.parseInt(partyfarm.getProperty("MonsterDelay", "10"));
		String[] monsterLocs2 = partyfarm.getProperty("MonsterLoc", "").split(";");
		String[] locSplit3 = null;
		
		monsterId = Integer.parseInt(partyfarm.getProperty("MonsterId", "1"));
		
		PART_ZONE_MONSTERS = partyfarm.getProperty("ListMonsterId");
		PART_ZONE_MONSTERS_ID = new ArrayList<>();
		for (String id : PART_ZONE_MONSTERS.split(","))
			PART_ZONE_MONSTERS_ID.add(Integer.parseInt(id));
		
		PARTY_ZONE_REWARDS = parseReward(partyfarm, "PartyZoneReward");
		
		PARTY_ZONE_VIP_REWARDS = parseReward(partyfarm, "PartyZonePremiumReward");
		EVENT_NAME = partyfarm.getProperty("EventName", "");
		MONSTER_LOCS_COUNT = monsterLocs2.length;
		MONSTER_LOCS = new int[MONSTER_LOCS_COUNT][3];
		int g;
		for (int e = 0; e < MONSTER_LOCS_COUNT; e++)
		{
			locSplit3 = monsterLocs2[e].split(",");
			for (g = 0; g < 3; g++)
			{
				MONSTER_LOCS[e][g] = Integer.parseInt(locSplit3[g].trim());
			}
		}
	}
	
	/**
	 * Loads hex ID settings.
	 */
	private static final void loadHexID()
	{
		final ExProperties hexid = initProperties(HEXID_FILE);
		
		SERVER_ID = Integer.parseInt(hexid.getProperty("ServerID"));
		HEX_ID = new BigInteger(hexid.getProperty("HexID"), 16).toByteArray();
	}
	
	/**
	 * Saves hex ID file.
	 * @param serverId : The ID of server.
	 * @param hexId : The hex ID of server.
	 */
	public static final void saveHexid(int serverId, String hexId)
	{
		saveHexid(serverId, hexId, HEXID_FILE);
	}
	
	/**
	 * Saves hexID file.
	 * @param serverId : The ID of server.
	 * @param hexId : The hexID of server.
	 * @param filename : The file name.
	 */
	public static final void saveHexid(int serverId, String hexId, String filename)
	{
		try
		{
			final File file = new File(filename);
			file.createNewFile();
			
			final Properties hexSetting = new Properties();
			hexSetting.setProperty("ServerID", String.valueOf(serverId));
			hexSetting.setProperty("HexID", hexId);
			
			try (OutputStream out = new FileOutputStream(file))
			{
				hexSetting.store(out, "the hexID to auth into login");
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Failed to save hex ID to '{}' file.", e, filename);
		}
	}
	public static boolean ALTERNATE_CLASS_MASTER;
	/**
	 * Loads NPC settings.<br>
	 * Such as champion monsters, NPC buffer, class master, wyvern, raid bosses and grand bosses, AI.
	 */
	private static final void loadNpcs()
	{
		final ExProperties npcs = initProperties(NPCS_FILE);

		SPAWN_MULTIPLIER = npcs.getProperty("SpawnMultiplier", 1.);
		SPAWN_EVENTS = npcs.getProperty("SpawnEvents", new String[]
		{
			"extra_mob",
			"18age",
			"start_weapon",
		});
		
		BUFFER_MAX_SCHEMES = npcs.getProperty("BufferMaxSchemesPerChar", 4);
		BUFFER_STATIC_BUFF_COST = npcs.getProperty("BufferStaticCostPerBuff", -1);
		ALL_ITEM_0_ADENA = npcs.getProperty("EnableSellItem", false);
		ALL_ITEMS_SELL_0_ADENA = npcs.getProperty("SellItem_0_Adena", 2);
		ALLOW_CLASS_MASTERS = npcs.getProperty("AllowClassMasters", false);
		ALLOW_ENTIRE_TREE = npcs.getProperty("AllowEntireTree", false);
		if (ALLOW_CLASS_MASTERS)
			CLASS_MASTER_SETTINGS = new ClassMasterSettings(npcs.getProperty("ConfigClassMaster"));
		
		ALTERNATE_CLASS_MASTER = npcs.getProperty("AlternateClassMaster", false);
		ALT_GAME_SUBCLASS_EVERYWHERE = npcs.getProperty("SubclassEverywhere", false);
		MAX_SUBCLASS_FORBASE = Integer.parseInt(npcs.getProperty("MaxSubClass", "3"));
		SUB_CLASS_LEVEL_SET = Integer.parseInt(npcs.getProperty("ReturnLevelForSubClass", "40"));
		
		FREE_TELEPORT = npcs.getProperty("FreeTeleport", false);
		ANNOUNCE_MAMMON_SPAWN = npcs.getProperty("AnnounceMammonSpawn", true);
		MOB_AGGRO_IN_PEACEZONE = npcs.getProperty("MobAggroInPeaceZone", true);
		SHOW_NPC_LVL = npcs.getProperty("ShowNpcLevel", false);
		SHOW_NPC_CREST = npcs.getProperty("ShowNpcCrest", false);
		SHOW_SUMMON_CREST = npcs.getProperty("ShowSummonCrest", false);
		
		WYVERN_ALLOW_UPGRADER = npcs.getProperty("AllowWyvernUpgrader", true);
		WYVERN_REQUIRED_LEVEL = npcs.getProperty("RequiredStriderLevel", 55);
		WYVERN_REQUIRED_CRYSTALS = npcs.getProperty("RequiredCrystalsNumber", 10);
		
		RAID_HP_REGEN_MULTIPLIER = npcs.getProperty("RaidHpRegenMultiplier", 1.);
		RAID_MP_REGEN_MULTIPLIER = npcs.getProperty("RaidMpRegenMultiplier", 1.);
		RAID_DEFENCE_MULTIPLIER = npcs.getProperty("RaidDefenceMultiplier", 1.);
		RAID_MINION_RESPAWN_TIMER = npcs.getProperty("RaidMinionRespawnTime", 300000);

		GUARD_ATTACK_AGGRO_MOB = npcs.getProperty("GuardAttackAggroMob", false);
		RANDOM_WALK_RATE = npcs.getProperty("RandomWalkRate", 30);
		MAX_DRIFT_RANGE = npcs.getProperty("MaxDriftRange", 200);
		MIN_NPC_ANIMATION = npcs.getProperty("MinNPCAnimation", 20);
		MAX_NPC_ANIMATION = npcs.getProperty("MaxNPCAnimation", 40);
		MIN_MONSTER_ANIMATION = npcs.getProperty("MinMonsterAnimation", 10);
		MAX_MONSTER_ANIMATION = npcs.getProperty("MaxMonsterAnimation", 40);
		
		BOSS_CHAOTIC_ENABLE = npcs.getProperty("ChaoticBoss", false);
		BOSS_CHAOTIC_RANGED = npcs.getProperty("ChaoticRange", 1500);
	}
	
	public static boolean LEAVE_BUFFS_ON_DIE;
	public static boolean CHAOTIC_LEAVE_BUFFS_ON_DIE;
	public static String BUFFER_FIGHTER_SET;
	public static String BUFFER_MAGE_SET;
	/**
	 * Loads player settings.<br>
	 * Such as stats, inventory/warehouse, enchant, augmentation, karma, party, admin, petition, skill learn.
	 */
	private static final void loadPlayers()
	{
		final ExProperties players = initProperties(PLAYERS_FILE);
		
		
		BANKING_SYSTEM_GOLDBARS = players.getProperty("BankingGoldbarCount", 1);
		BANKING_SYSTEM_ADENA = players.getProperty("BankingAdenaCount", 500000000);
		
		EFFECT_CANCELING = players.getProperty("CancelLesserEffect", true);
		HP_REGEN_MULTIPLIER = players.getProperty("HpRegenMultiplier", 1.);
		MP_REGEN_MULTIPLIER = players.getProperty("MpRegenMultiplier", 1.);
		CP_REGEN_MULTIPLIER = players.getProperty("CpRegenMultiplier", 1.);
		PLAYER_SPAWN_PROTECTION = players.getProperty("PlayerSpawnProtection", 0);
		PLAYER_FAKEDEATH_UP_PROTECTION = players.getProperty("PlayerFakeDeathUpProtection", 5);
		RESPAWN_RESTORE_HP = players.getProperty("RespawnRestoreHP", 0.7);
		MAX_PVTSTORE_SLOTS_DWARF = players.getProperty("MaxPvtStoreSlotsDwarf", 5);
		MAX_PVTSTORE_SLOTS_OTHER = players.getProperty("MaxPvtStoreSlotsOther", 4);
		DEEPBLUE_DROP_RULES = players.getProperty("UseDeepBlueDropRules", true);
		ALLOW_DELEVEL = players.getProperty("AllowDelevel", true);
		DEATH_PENALTY_CHANCE = players.getProperty("DeathPenaltyChance", 20);
		
		INVENTORY_MAXIMUM_NO_DWARF = players.getProperty("MaximumSlotsForNoDwarf", 80);
		INVENTORY_MAXIMUM_DWARF = players.getProperty("MaximumSlotsForDwarf", 100);
		INVENTORY_MAXIMUM_PET = players.getProperty("MaximumSlotsForPet", 12);
		MAX_ITEM_IN_PACKET = Math.max(INVENTORY_MAXIMUM_NO_DWARF, INVENTORY_MAXIMUM_DWARF);
		WEIGHT_LIMIT = players.getProperty("WeightLimit", 1.);
		WAREHOUSE_SLOTS_NO_DWARF = players.getProperty("MaximumWarehouseSlotsForNoDwarf", 100);
		WAREHOUSE_SLOTS_DWARF = players.getProperty("MaximumWarehouseSlotsForDwarf", 120);
		WAREHOUSE_SLOTS_CLAN = players.getProperty("MaximumWarehouseSlotsForClan", 150);
		FREIGHT_SLOTS = players.getProperty("MaximumFreightSlots", 20);
		REGION_BASED_FREIGHT = players.getProperty("RegionBasedFreight", true);
		FREIGHT_PRICE = players.getProperty("FreightPrice", 1000);

		AUGMENTATION_NG_SKILL_CHANCE = players.getProperty("AugmentationNGSkillChance", 15);
		AUGMENTATION_NG_GLOW_CHANCE = players.getProperty("AugmentationNGGlowChance", 0);
		AUGMENTATION_MID_SKILL_CHANCE = players.getProperty("AugmentationMidSkillChance", 30);
		AUGMENTATION_MID_GLOW_CHANCE = players.getProperty("AugmentationMidGlowChance", 40);
		AUGMENTATION_HIGH_SKILL_CHANCE = players.getProperty("AugmentationHighSkillChance", 45);
		AUGMENTATION_HIGH_GLOW_CHANCE = players.getProperty("AugmentationHighGlowChance", 70);
		AUGMENTATION_TOP_SKILL_CHANCE = players.getProperty("AugmentationTopSkillChance", 60);
		AUGMENTATION_TOP_GLOW_CHANCE = players.getProperty("AugmentationTopGlowChance", 100);
		AUGMENTATION_BASESTAT_CHANCE = players.getProperty("AugmentationBaseStatChance", 1);
		
		KARMA_PLAYER_CAN_SHOP = players.getProperty("KarmaPlayerCanShop", false);
		KARMA_PLAYER_CAN_USE_GK = players.getProperty("KarmaPlayerCanUseGK", false);
		KARMA_PLAYER_CAN_TELEPORT = players.getProperty("KarmaPlayerCanTeleport", true);
		KARMA_PLAYER_CAN_TRADE = players.getProperty("KarmaPlayerCanTrade", true);
		KARMA_PLAYER_CAN_USE_WH = players.getProperty("KarmaPlayerCanUseWareHouse", true);
		KARMA_DROP_GM = players.getProperty("CanGMDropEquipment", false);
		KARMA_AWARD_PK_KILL = players.getProperty("AwardPKKillPVPPoint", true);
		KARMA_PK_LIMIT = players.getProperty("MinimumPKRequiredToDrop", 5);
		KARMA_NONDROPPABLE_PET_ITEMS = players.getProperty("ListOfPetItems", new int[]
		{
			2375,
			3500,
			3501,
			3502,
			4422,
			4423,
			4424,
			4425,
			6648,
			6649,
			6650
		});
		KARMA_NONDROPPABLE_ITEMS = players.getProperty("ListOfNonDroppableItemsForPK", new int[]
		{
			1147,
			425,
			1146,
			461,
			10,
			2368,
			7,
			6,
			2370,
			2369
		});
		
		PVP_NORMAL_TIME = players.getProperty("PvPVsNormalTime", 40000);
		PVP_PVP_TIME = players.getProperty("PvPVsPvPTime", 20000);
		
		PARTY_XP_CUTOFF_METHOD = players.getProperty("PartyXpCutoffMethod", "level");
		PARTY_XP_CUTOFF_PERCENT = players.getProperty("PartyXpCutoffPercent", 3.);
		PARTY_XP_CUTOFF_LEVEL = players.getProperty("PartyXpCutoffLevel", 20);
		PARTY_RANGE = players.getProperty("PartyRange", 1500);
		
		DEFAULT_ACCESS_LEVEL = players.getProperty("DefaultAccessLevel", 0);
		GM_HERO_AURA = players.getProperty("GMHeroAura", false);
		GM_STARTUP_INVULNERABLE = players.getProperty("GMStartupInvulnerable", false);
		GM_STARTUP_INVISIBLE = players.getProperty("GMStartupInvisible", false);
		GM_STARTUP_BLOCK_ALL = players.getProperty("GMStartupBlockAll", false);
		GM_STARTUP_AUTO_LIST = players.getProperty("GMStartupAutoList", true);
		
		PETITIONING_ALLOWED = players.getProperty("PetitioningAllowed", true);
		MAX_PETITIONS_PER_PLAYER = players.getProperty("MaxPetitionsPerPlayer", 5);
		MAX_PETITIONS_PENDING = players.getProperty("MaxPetitionsPending", 25);
		
		IS_CRAFTING_ENABLED = players.getProperty("CraftingEnabled", true);
		DWARF_RECIPE_LIMIT = players.getProperty("DwarfRecipeLimit", 50);
		COMMON_RECIPE_LIMIT = players.getProperty("CommonRecipeLimit", 50);
		BLACKSMITH_USE_RECIPES = players.getProperty("BlacksmithUseRecipes", true);
		
		AUTO_LEARN_SKILLS = players.getProperty("AutoLearnSkills", false);
		MAGIC_FAILURES = players.getProperty("MagicFailures", true);
		PERFECT_SHIELD_BLOCK_RATE = players.getProperty("PerfectShieldBlockRate", 5);
		LIFE_CRYSTAL_NEEDED = players.getProperty("LifeCrystalNeeded", true);
		SP_BOOK_NEEDED = players.getProperty("SpBookNeeded", true);
		ES_SP_BOOK_NEEDED = players.getProperty("EnchantSkillSpBookNeeded", true);
		DIVINE_SP_BOOK_NEEDED = players.getProperty("DivineInspirationSpBookNeeded", true);
		SUBCLASS_WITHOUT_QUESTS = players.getProperty("SubClassWithoutQuests", false);
		
		MAX_BUFFS_AMOUNT = players.getProperty("MaxBuffsAmount", 20);
		STORE_SKILL_COOLTIME = players.getProperty("StoreSkillCooltime", true);
		LEAVE_BUFFS_ON_DIE = players.getProperty("LoseBuffsOnDeath", false);
		CHAOTIC_LEAVE_BUFFS_ON_DIE = players.getProperty("ChaoticLoseBuffsOnDeath", false);
		BUFFER_FIGHTER_SET = players.getProperty("BufferFighterSet", "");
		BUFFER_MAGE_SET = players.getProperty("BufferMageSet", "");
		INFINITY_MANAPOT =  players.getProperty("InfinityManaPot", false);
		MAX_MP = Integer.parseInt(players.getProperty("MaxMpRecovery", "400"));
		
		
		EMOTION_CHAT_SYSTEM = players.getProperty("EmotionChatSystem", false);
		EMOTION_CHAT_LIST = new HashMap<>();
		for (String words : players.getProperty("EmotionChat").split(";"))
		{
			final String[] infos = words.split(",");
			EMOTION_CHAT_LIST.put(infos[0], Integer.valueOf(infos[1]));
		}
		
		ITEM_CLASS_ID = players.getProperty("ItemCard", 0);
		
		CLAN_ITEMID = players.getProperty("CoinClanId", 5);
		CLAN_REWARD_REPUTATION = players.getProperty("ClandReputation", 500);
		CLAN_LEVEL = players.getProperty("ClandSetLevel", 8);
		CLAN_FULL_SKILL = players.getProperty("RewardSkillsClan", false);
		
		NOBLES_ITEMID = players.getProperty("CoinNoblesId", 5);
		NOBLES_REWARD_NOBLESS_TIARA = players.getProperty("RewardNoblesTiaraId", 7694);
		
		VIP_COIN_ID1 = Integer.parseInt(players.getProperty("VipItemId7Days", "6392"));
		VIP_DAYS_ID1 = Integer.parseInt(players.getProperty("TimeVip7Days", "1"));
		VIP_COIN_ID2 = Integer.parseInt(players.getProperty("VipItemId30Days", "6393"));
		VIP_DAYS_ID2 = Integer.parseInt(players.getProperty("TimeVip30Days", "2"));
		VIP_COIN_ID3 = Integer.parseInt(players.getProperty("VipItemId90Days", "5557"));
		VIP_DAYS_ID3 = Integer.parseInt(players.getProperty("TimeVip90Days", "3"));
		
		HERO_ITEM_ID_7DAYS = players.getProperty("HeroItemId7Days", 5);
		HERO_ITEM_ID_30DAYS = players.getProperty("HeroItemId30Days", 5);
		HERO_ITEM_ID_90DAYS = players.getProperty("HeroItemId90Days", 5);
		
		HERO_7DAYS = players.getProperty("TimeHero7Days", 7);
		HERO_30DAYS = players.getProperty("TimeHero30Days", 30);
		HERO_90DAYS = players.getProperty("TimeHero90Days", 800);
	}
	public static int MaxClanMemeber, MaxAllyMember;
	
	/**
	 * Loads siege settings.
	 */
	private static final void loadSieges()
	{
		final ExProperties sieges = initProperties(Config.SIEGE_FILE);
		
		SIEGE_LENGTH = sieges.getProperty("SiegeLength", 120);
		MINIMUM_CLAN_LEVEL = sieges.getProperty("SiegeClanMinLevel", 4);
		MAX_ATTACKERS_NUMBER = sieges.getProperty("AttackerMaxClans", 10);
		MAX_DEFENDERS_NUMBER = sieges.getProperty("DefenderMaxClans", 10);
		ATTACKERS_RESPAWN_DELAY = sieges.getProperty("AttackerRespawn", 10000);
		MaxClanMemeber = sieges.getProperty("MaxClanMemeberSiegeZone", 5);
		MaxAllyMember = sieges.getProperty("MaxAllyMemeberSiegeZone", 5);
		
		CH_MINIMUM_CLAN_LEVEL = sieges.getProperty("ChSiegeClanMinLevel", 4);
		CH_MAX_ATTACKERS_NUMBER = sieges.getProperty("ChAttackerMaxClans", 10);
	}
	/** Time Skills Buffer */
	public static boolean ENABLE_ALTERNATIVE_SKILL_DURATION;
	public static HashMap<Integer, Integer> SKILL_DURATION_LIST;

	public static boolean INFINITY_MANAPOT;
	public static int MAX_MP;
	/**
	 * Loads gameserver settings.<br>
	 * IP addresses, database, rates, feature enabled/disabled, misc.
	 */
	private static final void loadServer()
	{
		final ExProperties server = initProperties(SERVER_FILE);
		
		HOSTNAME = server.getProperty("Hostname", "*");
		GAMESERVER_HOSTNAME = server.getProperty("GameserverHostname");
		GAMESERVER_PORT = server.getProperty("GameserverPort", 7777);
		GAMESERVER_LOGIN_HOSTNAME = server.getProperty("LoginHost", "127.0.0.1");
		GAMESERVER_LOGIN_PORT = server.getProperty("LoginPort", 9014);
		REQUEST_ID = server.getProperty("RequestServerID", 0);
		ACCEPT_ALTERNATE_ID = server.getProperty("AcceptAlternateID", true);
		USE_BLOWFISH_CIPHER = server.getProperty("UseBlowfishCipher", true);
		
		ENABLE_IP_BOX = server.getProperty("IPDualBoxEnable", false);
		IP_MAX_DUALBOX = server.getProperty("LimitDualBox", 2);
		IP_TIME_LOGOUT = server.getProperty("TimeLogoutDualBox", 30);
		MIN_PROTOCOL_REVISION = server.getProperty("MinProtocolRevision", 737);
		MAX_PROTOCOL_REVISION = server.getProperty("MaxProtocolRevision", 746);
		
		DATABASE_URL = server.getProperty("URL", "jdbc:mariadb://localhost/acis");
		DATABASE_LOGIN = server.getProperty("Login", "root");
		DATABASE_PASSWORD = server.getProperty("Password", "");
		DATABASE_MAX_CONNECTIONS = server.getProperty("MaximumDbConnections", 10);
		SERVER_NAME = server.getProperty("ServerName", "JDev Interlude");
		
		SERVER_LIST_BRACKET = server.getProperty("ServerListBrackets", false);
		SERVER_LIST_CLOCK = server.getProperty("ServerListClock", false);
		SERVER_GMONLY = server.getProperty("ServerGMOnly", false);
		SERVER_LIST_AGE = server.getProperty("ServerListAgeLimit", 0);
		SERVER_LIST_TESTSERVER = server.getProperty("TestServer", false);
		SERVER_LIST_PVPSERVER = server.getProperty("PvpServer", true);
		
		DELETE_DAYS = server.getProperty("DeleteCharAfterDays", 7);
		MAXIMUM_ONLINE_USERS = server.getProperty("MaximumOnlineUsers", 100);
		
		AUTO_LOOT = server.getProperty("AutoLoot", false);
		AUTO_LOOT_HERBS = server.getProperty("AutoLootHerbs", false);
		AUTO_LOOT_RAID = server.getProperty("AutoLootRaid", false);
		
		ALLOW_DISCARDITEM = server.getProperty("AllowDiscardItem", true);
		MULTIPLE_ITEM_DROP = server.getProperty("MultipleItemDrop", true);
		HERB_AUTO_DESTROY_TIME = server.getProperty("AutoDestroyHerbTime", 15) * 1000;
		ITEM_AUTO_DESTROY_TIME = server.getProperty("AutoDestroyItemTime", 600) * 1000;
		EQUIPABLE_ITEM_AUTO_DESTROY_TIME = server.getProperty("AutoDestroyEquipableItemTime", 0) * 1000;
		SPECIAL_ITEM_DESTROY_TIME = new HashMap<>();
		String[] data = server.getProperty("AutoDestroySpecialItemTime", (String[]) null, ",");
		if (data != null)
		{
			for (String itemData : data)
			{
				String[] item = itemData.split("-");
				SPECIAL_ITEM_DESTROY_TIME.put(Integer.parseInt(item[0]), Integer.parseInt(item[1]) * 1000);
			}
		}
		PLAYER_DROPPED_ITEM_MULTIPLIER = server.getProperty("PlayerDroppedItemMultiplier", 1);
		
		RATE_XP = server.getProperty("RateXp", 1.);
		RATE_SP = server.getProperty("RateSp", 1.);
		RATE_PARTY_XP = server.getProperty("RatePartyXp", 1.);
		RATE_PARTY_SP = server.getProperty("RatePartySp", 1.);
		RATE_DROP_CURRENCY = server.getProperty("RateDropCurrency", 1.);
		RATE_DROP_ITEMS = server.getProperty("RateDropItems", 1.);
		RATE_DROP_ITEMS_BY_RAID = server.getProperty("RateRaidDropItems", 1.);
		RATE_DROP_SPOIL = server.getProperty("RateDropSpoil", 1.);
		RATE_DROP_HERBS = server.getProperty("RateDropHerbs", 1.);
		RATE_DROP_MANOR = server.getProperty("RateDropManor", 1);
		RATE_QUEST_DROP = server.getProperty("RateQuestDrop", 1.);
		RATE_QUEST_REWARD = server.getProperty("RateQuestReward", 1.);
		RATE_QUEST_REWARD_XP = server.getProperty("RateQuestRewardXP", 1.);
		RATE_QUEST_REWARD_SP = server.getProperty("RateQuestRewardSP", 1.);
		RATE_QUEST_REWARD_ADENA = server.getProperty("RateQuestRewardAdena", 1.);
		RATE_KARMA_EXP_LOST = server.getProperty("RateKarmaExpLost", 1.);
		RATE_SIEGE_GUARDS_PRICE = server.getProperty("RateSiegeGuardsPrice", 1.);
		PLAYER_DROP_LIMIT = server.getProperty("PlayerDropLimit", 3);
		PLAYER_RATE_DROP = server.getProperty("PlayerRateDrop", 5);
		PLAYER_RATE_DROP_ITEM = server.getProperty("PlayerRateDropItem", 70);
		PLAYER_RATE_DROP_EQUIP = server.getProperty("PlayerRateDropEquip", 25);
		PLAYER_RATE_DROP_EQUIP_WEAPON = server.getProperty("PlayerRateDropEquipWeapon", 5);
		PET_XP_RATE = server.getProperty("PetXpRate", 1.);
		PET_FOOD_RATE = server.getProperty("PetFoodRate", 1);
		SINEATER_XP_RATE = server.getProperty("SinEaterXpRate", 1.);
		KARMA_DROP_LIMIT = server.getProperty("KarmaDropLimit", 10);
		KARMA_RATE_DROP = server.getProperty("KarmaRateDrop", 70);
		KARMA_RATE_DROP_ITEM = server.getProperty("KarmaRateDropItem", 50);
		KARMA_RATE_DROP_EQUIP = server.getProperty("KarmaRateDropEquip", 40);
		KARMA_RATE_DROP_EQUIP_WEAPON = server.getProperty("KarmaRateDropEquipWeapon", 10);
		
		ALLOW_FREIGHT = server.getProperty("AllowFreight", true);
		ALLOW_WAREHOUSE = server.getProperty("AllowWarehouse", true);
		ALLOW_WEAR = server.getProperty("AllowWear", true);
		WEAR_DELAY = server.getProperty("WearDelay", 5);
		WEAR_PRICE = server.getProperty("WearPrice", 10);
		ALLOW_LOTTERY = server.getProperty("AllowLottery", false);
		ALLOW_WATER = server.getProperty("AllowWater", true);
		ALLOW_MANOR = server.getProperty("AllowManor", true);
		ALLOW_BOAT = server.getProperty("AllowBoat", true);
		ALLOW_CURSED_WEAPONS = server.getProperty("AllowCursedWeapons", true);
		
		ENABLE_FALLING_DAMAGE = server.getProperty("EnableFallingDamage", true);
		
		NO_SPAWNS = server.getProperty("NoSpawns", false);
		DEVELOPER = server.getProperty("Developer", false);
		PACKET_HANDLER_DEBUG = server.getProperty("PacketHandlerDebug", false);
		
		DEADLOCK_DETECTOR = server.getProperty("DeadLockDetector", false);
		DEADLOCK_CHECK_INTERVAL = server.getProperty("DeadLockCheckInterval", 20);
		RESTART_ON_DEADLOCK = server.getProperty("RestartOnDeadlock", false);
		
		LOG_CHAT = server.getProperty("LogChat", false);
		LOG_ITEMS = server.getProperty("LogItems", false);
		GMAUDIT = server.getProperty("GMAudit", false);

		ROLL_DICE_TIME = server.getProperty("RollDiceTime", 4200);
		HERO_VOICE_TIME = server.getProperty("HeroVoiceTime", 10000);
		SUBCLASS_TIME = server.getProperty("SubclassTime", 2000);
		DROP_ITEM_TIME = server.getProperty("DropItemTime", 1000);
		SERVER_BYPASS_TIME = server.getProperty("ServerBypassTime", 100);
		MULTISELL_TIME = server.getProperty("MultisellTime", 100);
		MANUFACTURE_TIME = server.getProperty("ManufactureTime", 300);
		MANOR_TIME = server.getProperty("ManorTime", 3000);
		SENDMAIL_TIME = server.getProperty("SendMailTime", 10000);
		CHARACTER_SELECT_TIME = server.getProperty("CharacterSelectTime", 3000);
		GLOBAL_CHAT_TIME = server.getProperty("GlobalChatTime", 0);
		TRADE_CHAT_TIME = server.getProperty("TradeChatTime", 0);
		SOCIAL_TIME = server.getProperty("SocialTime", 2000);
		SKIN_TIME = server.getProperty("DressMeTime", 4200);
		SCHEDULED_THREAD_POOL_COUNT = server.getProperty("ScheduledThreadPoolCount", -1);
		THREADS_PER_SCHEDULED_THREAD_POOL = server.getProperty("ThreadsPerScheduledThreadPool", 4);
		INSTANT_THREAD_POOL_COUNT = server.getProperty("InstantThreadPoolCount", -1);
		THREADS_PER_INSTANT_THREAD_POOL = server.getProperty("ThreadsPerInstantThreadPool", 2);
		
		L2WALKER_PROTECTION = server.getProperty("L2WalkerProtection", false);
		ZONE_TOWN = server.getProperty("ZoneTown", 0);
		SERVER_NEWS = server.getProperty("ShowServerNews", false);
		
		
		ALLOW_PVP_CHAT = server.getProperty("AllowPvPChat", false);
		PVPS_TO_TALK_ON_SHOUT = server.getProperty("PvPsToTalkOnShout", 40);
		PVPS_TO_TALK_ON_TRADE = server.getProperty("PvPsToTalkOnTrade", 40);
	
		ENABLE_ALTERNATIVE_SKILL_DURATION = server.getProperty("EnableAlternativeSkillDuration", false);
		if (ENABLE_ALTERNATIVE_SKILL_DURATION)
		{
			SKILL_DURATION_LIST = new HashMap<>();
			
			String[] propertySplit;
			propertySplit = server.getProperty("SkillDurationList", "").split(";");
			
			for (String skill : propertySplit)
			{
				String[] skillSplit = skill.split(",");
				if (skillSplit.length != 2)
				{
					System.out.println("[SkillDurationList]: invalid config property -> SkillDurationList \"" + skill + "\"");
				}
				else
				{
					try
					{
						SKILL_DURATION_LIST.put(Integer.parseInt(skillSplit[0]), Integer.parseInt(skillSplit[1]));
					}
					catch (NumberFormatException nfe)
					{
						nfe.printStackTrace();
						
						if (!skill.equals(""))
						{
							System.out.println("[SkillDurationList]: invalid config property -> SkillList \"" + skillSplit[0] + "\"" + skillSplit[1]);
						}
					}
				}
			}
		}
		
		RAID_BOSS_IDS = server.getProperty("RaidInfoBossIds", "0,0");
		LIST_RAID_BOSS_IDS = new ArrayList<>();
		for (String val : RAID_BOSS_IDS.split(","))
		{
			int npcId = Integer.parseInt(val);
			LIST_RAID_BOSS_IDS.add(npcId);
		}
	}
	
	/**
	 * Loads loginserver settings.<br>
	 * IP addresses, database, account, misc.
	 */
	private static final void loadLogin()
	{
		final ExProperties server = initProperties(LOGINSERVER_FILE);
		
		HOSTNAME = server.getProperty("Hostname", "localhost");
		LOGINSERVER_HOSTNAME = server.getProperty("LoginserverHostname", "*");
		LOGINSERVER_PORT = server.getProperty("LoginserverPort", 2106);
		GAMESERVER_LOGIN_HOSTNAME = server.getProperty("LoginHostname", "*");
		GAMESERVER_LOGIN_PORT = server.getProperty("LoginPort", 9014);
		LOGIN_TRY_BEFORE_BAN = server.getProperty("LoginTryBeforeBan", 3);
		LOGIN_BLOCK_AFTER_BAN = server.getProperty("LoginBlockAfterBan", 600);
		ACCEPT_NEW_GAMESERVER = server.getProperty("AcceptNewGameServer", false);
		SHOW_LICENCE = server.getProperty("ShowLicence", true);
		
		DATABASE_URL = server.getProperty("URL", "jdbc:mariadb://localhost/acis");
		DATABASE_LOGIN = server.getProperty("Login", "root");
		DATABASE_PASSWORD = server.getProperty("Password", "");
		DATABASE_MAX_CONNECTIONS = server.getProperty("MaximumDbConnections", 5);
		
		AUTO_CREATE_ACCOUNTS = server.getProperty("AutoCreateAccounts", true);
		
		FLOOD_PROTECTION = server.getProperty("EnableFloodProtection", true);
		FAST_CONNECTION_LIMIT = server.getProperty("FastConnectionLimit", 15);
		NORMAL_CONNECTION_TIME = server.getProperty("NormalConnectionTime", 700);
		FAST_CONNECTION_TIME = server.getProperty("FastConnectionTime", 350);
		MAX_CONNECTION_PER_IP = server.getProperty("MaxConnectionPerIP", 50);
	}
	
	public static final void loadGameServer()
	{
		LOGGER.info("Loading gameserver configuration files.");
		
		// clans settings
		loadClans();
		
		// events settings
		loadEvents();
		
		// geoengine settings
		loadGeoengine();
		
		// hexID
		loadHexID();
		
		// NPCs/monsters settings
		loadNpcs();
		
		// players settings
		loadPlayers();
		
		// siege settings
		loadSieges();
		
		// server settings
		loadServer();
		
		// Party Farm
		loadPartyFarm();
		
		// special tutorial
		loadSpecialutorial();
		
		AntharasConfig.init();
		BaiumConfig.init();
		BenomConfig.init();
		CoreConfig.init();
		DrChaosConfig.init();
		FrintezzaConfig.init();
		LindviorConfig.ini();
		OrfenConfig.ini();
		QAConfig.ini();
		SailrenConfig.ini();
		ValakasConfig.ini();
		ZakenConfig.init();
		FafurionConfig.init();
		VanHalterConfig.init();
		EventDropConfig.init();
		loadCommunity();
		// offline settings
		loadOffline();

	}
	
	public static final void loadLoginServer()
	{
		LOGGER.info("Loading loginserver configuration files.");
		
		// login settings
		loadLogin();
	}
	
	public static final void loadAccountManager()
	{
		LOGGER.info("Loading account manager configuration files.");
		
		// login settings
		loadLogin();
	}
	
	public static final void loadGameServerRegistration()
	{
		LOGGER.info("Loading gameserver registration configuration files.");
		
		// login settings
		loadLogin();
	}
	
	public static final class ClassMasterSettings
	{
		private final Map<Integer, Boolean> _allowedClassChange;
		private final Map<Integer, List<IntIntHolder>> _claimItems;
		private final Map<Integer, List<IntIntHolder>> _rewardItems;
		
		public ClassMasterSettings(String configLine)
		{
			_allowedClassChange = new HashMap<>(3);
			_claimItems = new HashMap<>(3);
			_rewardItems = new HashMap<>(3);
			
			if (configLine != null)
				parseConfigLine(configLine.trim());
		}
		
		private void parseConfigLine(String configLine)
		{
			StringTokenizer st = new StringTokenizer(configLine, ";");
			while (st.hasMoreTokens())
			{
				// Get allowed class change.
				int job = Integer.parseInt(st.nextToken());
				
				_allowedClassChange.put(job, true);
				
				List<IntIntHolder> items = new ArrayList<>();
				
				// Parse items needed for class change.
				if (st.hasMoreTokens())
				{
					StringTokenizer st2 = new StringTokenizer(st.nextToken(), "[],");
					while (st2.hasMoreTokens())
					{
						StringTokenizer st3 = new StringTokenizer(st2.nextToken(), "()");
						items.add(new IntIntHolder(Integer.parseInt(st3.nextToken()), Integer.parseInt(st3.nextToken())));
					}
				}
				
				// Feed the map, and clean the list.
				_claimItems.put(job, items);
				items = new ArrayList<>();
				
				// Parse gifts after class change.
				if (st.hasMoreTokens())
				{
					StringTokenizer st2 = new StringTokenizer(st.nextToken(), "[],");
					while (st2.hasMoreTokens())
					{
						StringTokenizer st3 = new StringTokenizer(st2.nextToken(), "()");
						items.add(new IntIntHolder(Integer.parseInt(st3.nextToken()), Integer.parseInt(st3.nextToken())));
					}
				}
				
				_rewardItems.put(job, items);
			}
		}
		
		public boolean isAllowed(int job)
		{
			if (_allowedClassChange == null)
				return false;
			
			if (_allowedClassChange.containsKey(job))
				return _allowedClassChange.get(job);
			
			return false;
		}
		
		public List<IntIntHolder> getRewardItems(int job)
		{
			return _rewardItems.get(job);
		}
		
		public List<IntIntHolder> getRequiredItems(int job)
		{
			return _claimItems.get(job);
		}
	}
	
	public static List<RewardHolder> parseReward(ExProperties propertie, String configName)
	{
		List<RewardHolder> auxReturn = new ArrayList<>();
		
		String aux = propertie.getProperty(configName).trim();
		for (String randomReward : aux.split(";"))
		{
			final String[] infos = randomReward.split(",");
			if (infos.length > 2)
				auxReturn.add(new RewardHolder(Integer.valueOf(infos[0]), Integer.valueOf(infos[1])));
			else
				auxReturn.add(new RewardHolder(Integer.valueOf(infos[0]), Integer.valueOf(infos[1])));
		}
		return auxReturn;
	}
}