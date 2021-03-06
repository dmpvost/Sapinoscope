CREATE TABLE IF NOT EXISTS EQUA
 (
   EQUA_ID BIGINT(4) NOT NULL  ,
   EQUA_X DECIMAL(10,2) NOT NULL  ,
   EQUA_Y DECIMAL(10,2) NULL  ,
   EQUA_Z DECIMAL(10,2) NULL  ,
   EQUA_L DECIMAL(10,2) NULL  ,
   EQUA_M DECIMAL(10,2) NULL  
   , PRIMARY KEY (EQUA_ID) 
 );

CREATE TABLE IF NOT EXISTS ANNEE
 (
   ANN_ID DATE NOT NULL  
   , PRIMARY KEY (ANN_ID) 
 );
 
 CREATE TABLE IF NOT EXISTS PARCELLE
 (
   PARC_ID BIGINT(4) NOT NULL  ,
   PARC_N CHAR(32) NOT NULL  ,
   PARC_DESC CHAR(32) NULL  ,
   PARC_COEF DECIMAL(10,2) NOT NULL  
   , PRIMARY KEY (PARC_ID) 
 );

CREATE TABLE IF NOT EXISTS SECTEUR
 (
   SEC_ID BIGINT(4) NOT NULL  ,
   PARC_ID BIGINT(4) NOT NULL  ,
   SEC_N CHAR(32) NOT NULL  ,
   SEC_ANGLE DECIMAL(10,2) NULL  ,
   SEC_CROIS DECIMAL(10,2) NULL  , 
   PRIMARY KEY (SEC_ID) ,
   FOREIGN KEY (PARC_ID)
      REFERENCES PARCELLE (PARC_ID)
 );
 
 CREATE TABLE IF NOT EXISTS VARIETE
 (
   VAR_ID BIGINT(2) NOT NULL  ,
   VAR_NOM CHAR(32) NOT NULL  ,
   VAR_POUSSE DECIMAL(10,2) NOT NULL  
   , PRIMARY KEY (VAR_ID) 
 );
 
 CREATE TABLE IF NOT EXISTS COORDONNEE
 (
   COORD_ID CHAR(32) NOT NULL  ,
   COORD_LAT DECIMAL(20,10) NOT NULL  ,
   COORD_LON DECIMAL(20,10) NOT NULL  
   , PRIMARY KEY (COORD_ID) 
 );

CREATE TABLE IF NOT EXISTS INFO_SECTEUR
 (
   INF_SEC_ID BIGINT(4) NOT NULL  ,
   SEC_ID BIGINT(4) NOT NULL  ,
   ANN_ID DATE NOT NULL  ,
   INF_SEC_COEF_GEL DECIMAL(10,2) NULL, 
   PRIMARY KEY (INF_SEC_ID),
   FOREIGN KEY (SEC_ID)
      REFERENCES SECTEUR (SEC_ID),
   FOREIGN KEY (ANN_ID)
      REFERENCES ANNEE (ANN_ID)	  
 ); 


 CREATE TABLE IF NOT EXISTS SAPIN
 (
   SAP_ID BIGINT(5) NOT NULL  ,
   VAR_ID BIGINT(2) NOT NULL  ,
   SEC_ID BIGINT(4) NOT NULL  ,
   SAP_LIG BIGINT(4) NOT NULL  ,
   SAP_COL BIGINT(4) NOT NULL  ,
   SAP_PHO LONGBLOB NULL  ,
   SAP_STATUS BIGINT(1) NOT NULL  , 
   PRIMARY KEY (SAP_ID) ,
   FOREIGN KEY (VAR_ID)
      REFERENCES VARIETE (VAR_ID),
   FOREIGN KEY (SEC_ID)
      REFERENCES SECTEUR (SEC_ID)
 );

CREATE TABLE IF NOT EXISTS INFO_SAPIN
 (
   INF_SAP_ID BIGINT(4) NOT NULL  ,
   ANN_ID DATE NOT NULL  ,
   SAP_ID BIGINT(5) NOT NULL  ,
   INF_SAP_TAIL DECIMAL(10,2) NOT NULL  , 
   PRIMARY KEY (INF_SAP_ID) ,
   FOREIGN KEY (ANN_ID)
      REFERENCES ANNEE (ANN_ID) ,
   FOREIGN KEY (SAP_ID)
      REFERENCES SAPIN (SAP_ID) 
 );


CREATE TABLE IF NOT EXISTS CROISSANCE
 (
   CROIS_ID BIGINT(4) NOT NULL  ,
   CROIS_B_INF DECIMAL(10,2) NOT NULL  ,
   CROIS_B_SUP DECIMAL(10,2) NOT NULL  
   , PRIMARY KEY (CROIS_ID) 
 );


CREATE TABLE IF NOT EXISTS SEC_COORD
 (
   COORD_ID CHAR(32) NOT NULL  ,
   SEC_ID BIGINT(4) NOT NULL   , 
   PRIMARY KEY (COORD_ID,SEC_ID) ,
   FOREIGN KEY (COORD_ID)
      REFERENCES COORDONNEE (COORD_ID) ,
   FOREIGN KEY (SEC_ID)
      REFERENCES SECTEUR (SEC_ID) 
 );

