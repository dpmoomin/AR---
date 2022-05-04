-- kummap.account definition

CREATE TABLE `account` (
  `acc_uid` bigint(20) NOT NULL AUTO_INCREMENT,
  `acc_name` varchar(20) NOT NULL,
  `acc_pwd` varchar(30) NOT NULL,
  PRIMARY KEY (`acc_uid`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;


-- kummap.buildings definition

CREATE TABLE `buildings` (
  `bldn_uid` bigint(20) NOT NULL AUTO_INCREMENT,
  `bldn_name` varchar(20) NOT NULL,
  `bldn_idnt` varchar(2) DEFAULT NULL,
  `bldn_x` double NOT NULL,
  `bldn_y` double NOT NULL,
  PRIMARY KEY (`bldn_uid`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8;


-- kummap.facility definition

CREATE TABLE `facility` (
  `fac_uid` bigint(20) NOT NULL AUTO_INCREMENT,
  `fac_name` varchar(255) NOT NULL,
  `fac_bldnid` bigint(20) DEFAULT NULL,
  `fac_x` double DEFAULT NULL,
  `fac_y` double DEFAULT NULL,
  `fac_floor` int(11) NOT NULL,
  `fac_dept` varchar(30) DEFAULT NULL,
  `fac_idname` varchar(6) DEFAULT NULL,
  PRIMARY KEY (`fac_uid`),
  KEY `facility_bldn_FK` (`fac_bldnid`),
  CONSTRAINT `facility_bldn_FK` FOREIGN KEY (`fac_bldnid`) REFERENCES `buildings` (`bldn_uid`)
) ENGINE=InnoDB AUTO_INCREMENT=200 DEFAULT CHARSET=utf8;


-- kummap.phone definition

CREATE TABLE `phone` (
  `phone_uid` bigint(20) NOT NULL AUTO_INCREMENT,
  `phone_num` varchar(12) NOT NULL,
  `phone_facid` bigint(20) NOT NULL,
  PRIMARY KEY (`phone_uid`),
  KEY `phone_FK` (`phone_facid`),
  CONSTRAINT `phone_FK` FOREIGN KEY (`phone_facid`) REFERENCES `facility` (`fac_uid`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8;


-- kummap.accesspoint definition

CREATE TABLE `accesspoint` (
  `ap_uid` bigint(20) NOT NULL AUTO_INCREMENT,
  `ap_name` varchar(255) NOT NULL,
  `ap_bssid` varchar(20) NOT NULL,
  `ap_facid` bigint(20) NOT NULL,
  `ap_floor` int(11) NOT NULL,
  `ap_x` double NOT NULL,
  `ap_y` double NOT NULL,
  PRIMARY KEY (`ap_uid`),
  KEY `accesspoint_fac_FK` (`ap_facid`),
  CONSTRAINT `accesspoint_fac_FK` FOREIGN KEY (`ap_facid`) REFERENCES `facility` (`fac_uid`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;