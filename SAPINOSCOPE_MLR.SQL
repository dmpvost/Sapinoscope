DROP DATABASE IF EXISTS Sapinoscope_MLR;

CREATE DATABASE IF NOT EXISTS Sapinoscope_MLR;
USE Sapinoscope_MLR;
# -----------------------------------------------------------------------------
#       TABLE : INFO_SECTEUR
# -----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS INFO_SECTEUR
 (
   INF_SEC_ID BIGINT(4) NOT NULL  ,
   SEC_ID BIGINT(4) NOT NULL  ,
   ANN_ID DATE NOT NULL  ,
   INF_SEC_COEF_GEL DECIMAL(10,2) NULL  
   , PRIMARY KEY (INF_SEC_ID) 
 ) 
 comment = "";

# -----------------------------------------------------------------------------
#       INDEX DE LA TABLE INFO_SECTEUR
# -----------------------------------------------------------------------------


CREATE  INDEX I_FK_INFO_SECTEUR_SECTEUR
     ON INFO_SECTEUR (SEC_ID ASC);

CREATE  INDEX I_FK_INFO_SECTEUR_ANNEE
     ON INFO_SECTEUR (ANN_ID ASC);

# -----------------------------------------------------------------------------
#       TABLE : SECTEUR
# -----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS SECTEUR
 (
   SEC_ID BIGINT(4) NOT NULL  ,
   PARC_ID BIGINT(4) NOT NULL  ,
   SEC_N CHAR(32) NOT NULL  ,
   SEC_ANGLE DECIMAL(10,2) NULL  ,
   SEC_CROIS DECIMAL(10,2) NULL  
   , PRIMARY KEY (SEC_ID) 
 ) 
 comment = "";

# -----------------------------------------------------------------------------
#       INDEX DE LA TABLE SECTEUR
# -----------------------------------------------------------------------------


CREATE  INDEX I_FK_SECTEUR_PARCELLE
     ON SECTEUR (PARC_ID ASC);

# -----------------------------------------------------------------------------
#       TABLE : VARIETE
# -----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS VARIETE
 (
   VAR_ID BIGINT(2) NOT NULL  ,
   VAR_NOM CHAR(32) NOT NULL  ,
   VAR_POUSSE DECIMAL(10,2) NOT NULL  
   , PRIMARY KEY (VAR_ID) 
 ) 
 comment = "";

# -----------------------------------------------------------------------------
#       TABLE : EQUA
# -----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS EQUA
 (
   EQUA_ID BIGINT(4) NOT NULL  ,
   EQUA_X DECIMAL(10,2) NOT NULL  ,
   EQUA_Y DECIMAL(10,2) NULL  ,
   EQUA_Z DECIMAL(10,2) NULL  ,
   EQUA_L DECIMAL(10,2) NULL  ,
   EQUA_M DECIMAL(10,2) NULL  
   , PRIMARY KEY (EQUA_ID) 
 ) 
 comment = "";

# -----------------------------------------------------------------------------
#       TABLE : INFO_SAPIN
# -----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS INFO_SAPIN
 (
   INF_SAP_ID BIGINT(4) NOT NULL  ,
   ANN_ID DATE NOT NULL  ,
   SAP_ID BIGINT(5) NOT NULL  ,
   INF_SAP_TAIL DECIMAL(10,2) NOT NULL  
   , PRIMARY KEY (INF_SAP_ID) 
 ) 
 comment = "";

# -----------------------------------------------------------------------------
#       INDEX DE LA TABLE INFO_SAPIN
# -----------------------------------------------------------------------------


CREATE  INDEX I_FK_INFO_SAPIN_ANNEE
     ON INFO_SAPIN (ANN_ID ASC);

CREATE  INDEX I_FK_INFO_SAPIN_SAPIN
     ON INFO_SAPIN (SAP_ID ASC);

# -----------------------------------------------------------------------------
#       TABLE : PARCELLE
# -----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS PARCELLE
 (
   PARC_ID BIGINT(4) NOT NULL  ,
   PARC_N CHAR(32) NOT NULL  ,
   PAR_DESC CHAR(32) NULL  ,
   PARC_COEF DECIMAL(10,2) NOT NULL  
   , PRIMARY KEY (PARC_ID) 
 ) 
 comment = "";

# -----------------------------------------------------------------------------
#       TABLE : CROISSANCE
# -----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS CROISSANCE
 (
   CROIS_ID BIGINT(4) NOT NULL  ,
   CROIS_B_INF DECIMAL(10,2) NOT NULL  ,
   CROIS_B_SUP DECIMAL(10,2) NOT NULL  
   , PRIMARY KEY (CROIS_ID) 
 ) 
 comment = "";

# -----------------------------------------------------------------------------
#       TABLE : COORDONNEE
# -----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS COORDONNEE
 (
   COORD_ID CHAR(32) NOT NULL  ,
   COORD_LAT DECIMAL(20,10) NOT NULL  ,
   COORD_LON DECIMAL(20,10) NOT NULL  
   , PRIMARY KEY (COORD_ID) 
 ) 
 comment = "";

# -----------------------------------------------------------------------------
#       TABLE : SAPIN
# -----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS SAPIN
 (
   SAP_ID BIGINT(5) NOT NULL  ,
   VAR_ID BIGINT(2) NOT NULL  ,
   SEC_ID BIGINT(4) NOT NULL  ,
   SAP_LIG BIGINT(4) NOT NULL  ,
   SAP_COL BIGINT(4) NOT NULL  ,
   SAP_PHO LONGBLOB NULL  ,
   SAP_STATUS BIGINT(1) NOT NULL  
   , PRIMARY KEY (SAP_ID) 
 ) 
 comment = "";

# -----------------------------------------------------------------------------
#       INDEX DE LA TABLE SAPIN
# -----------------------------------------------------------------------------


CREATE  INDEX I_FK_SAPIN_VARIETE
     ON SAPIN (VAR_ID ASC);

CREATE  INDEX I_FK_SAPIN_SECTEUR
     ON SAPIN (SEC_ID ASC);

# -----------------------------------------------------------------------------
#       TABLE : ANNEE
# -----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS ANNEE
 (
   ANN_ID DATE NOT NULL  
   , PRIMARY KEY (ANN_ID) 
 ) 
 comment = "";

# -----------------------------------------------------------------------------
#       TABLE : SEC_COORD
# -----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS SEC_COORD
 (
   COORD_ID CHAR(32) NOT NULL  ,
   SEC_ID BIGINT(4) NOT NULL  
   , PRIMARY KEY (COORD_ID,SEC_ID) 
 ) 
 comment = "";

# -----------------------------------------------------------------------------
#       INDEX DE LA TABLE SEC_COORD
# -----------------------------------------------------------------------------


CREATE  INDEX I_FK_SEC_COORD_COORDONNEE
     ON SEC_COORD (COORD_ID ASC);

CREATE  INDEX I_FK_SEC_COORD_SECTEUR
     ON SEC_COORD (SEC_ID ASC);


# -----------------------------------------------------------------------------
#       CREATION DES REFERENCES DE TABLE
# -----------------------------------------------------------------------------


ALTER TABLE INFO_SECTEUR 
  ADD FOREIGN KEY FK_INFO_SECTEUR_SECTEUR (SEC_ID)
      REFERENCES SECTEUR (SEC_ID) ;


ALTER TABLE INFO_SECTEUR 
  ADD FOREIGN KEY FK_INFO_SECTEUR_ANNEE (ANN_ID)
      REFERENCES ANNEE (ANN_ID) ;


ALTER TABLE SECTEUR 
  ADD FOREIGN KEY FK_SECTEUR_PARCELLE (PARC_ID)
      REFERENCES PARCELLE (PARC_ID) ;


ALTER TABLE INFO_SAPIN 
  ADD FOREIGN KEY FK_INFO_SAPIN_ANNEE (ANN_ID)
      REFERENCES ANNEE (ANN_ID) ;


ALTER TABLE INFO_SAPIN 
  ADD FOREIGN KEY FK_INFO_SAPIN_SAPIN (SAP_ID)
      REFERENCES SAPIN (SAP_ID) ;


ALTER TABLE SAPIN 
  ADD FOREIGN KEY FK_SAPIN_VARIETE (VAR_ID)
      REFERENCES VARIETE (VAR_ID) ;


ALTER TABLE SAPIN 
  ADD FOREIGN KEY FK_SAPIN_SECTEUR (SEC_ID)
      REFERENCES SECTEUR (SEC_ID) ;


ALTER TABLE SEC_COORD 
  ADD FOREIGN KEY FK_SEC_COORD_COORDONNEE (COORD_ID)
      REFERENCES COORDONNEE (COORD_ID) ;


ALTER TABLE SEC_COORD 
  ADD FOREIGN KEY FK_SEC_COORD_SECTEUR (SEC_ID)
      REFERENCES SECTEUR (SEC_ID) ;

