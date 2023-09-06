CREATE TABLE `grandboss_data` (
  `boss_id` int(11) NOT NULL DEFAULT 0,
  `loc_x` int(11) NOT NULL DEFAULT 0,
  `loc_y` int(11) NOT NULL DEFAULT 0,
  `loc_z` int(11) NOT NULL DEFAULT 0,
  `heading` int(11) NOT NULL DEFAULT 0,
  `respawn_time` bigint(20) NOT NULL DEFAULT 0,
  `currentHP` decimal(8,0) DEFAULT NULL,
  `currentMP` decimal(8,0) DEFAULT NULL,
  `status` tinyint(4) NOT NULL DEFAULT 0,
  PRIMARY KEY (`boss_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

INSERT INTO `grandboss_data` VALUES ('25512', '96080', '-110822', '-3343', '0', '0', '0', '0', '0');
INSERT INTO `grandboss_data` VALUES ('29001', '-21584', '181900', '-5720', '2555', '0', '229898', '667', '0');
INSERT INTO `grandboss_data` VALUES ('29006', '17726', '108915', '-6472', '64412', '0', '622493', '3793', '0');
INSERT INTO `grandboss_data` VALUES ('29014', '54717', '17145', '-5528', '24175', '0', '622493', '3793', '0');
INSERT INTO `grandboss_data` VALUES ('29019', '178595', '115146', '-7704', '4931', '0', '17850000', '39960', '0');
INSERT INTO `grandboss_data` VALUES ('29020', '115249', '16482', '10080', '44119', '0', '4068372', '39960', '0');
INSERT INTO `grandboss_data` VALUES ('29022', '55293', '219132', '-3224', '59641', '0', '858518', '399600', '0');
INSERT INTO `grandboss_data` VALUES ('29028', '214708', '-113302', '-1632', '9544', '0', '17850000', '3996000', '0');
INSERT INTO `grandboss_data` VALUES ('29045', '174240', '-89805', '-5016', '16384', '0', '1018821', '52001', '0');
INSERT INTO `grandboss_data` VALUES ('29046', '174231', '-88006', '-5115', '0', '0', '0', '0', '0');
INSERT INTO `grandboss_data` VALUES ('29047', '174231', '-88006', '-5115', '0', '0', '0', '0', '0');
INSERT INTO `grandboss_data` VALUES ('29062', '-16550', '-53562', '-10448', '0', '0', '275385', '4553', '0');
INSERT INTO `grandboss_data` VALUES ('29065', '27549', '-6638', '-2008', '0', '0', '0', '0', '0');
INSERT INTO `grandboss_data` VALUES ('37001', '0', '0', '0', '0', '0', '0', '0', '0');
INSERT INTO `grandboss_data` VALUES ('37004', '0', '0', '0', '0', '0', '0', '0', '0');
