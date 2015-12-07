
CREATE TABLE IF NOT EXISTS app_conf(
pkey TEXT NOT NULL,
pvalue TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS QUESTION(
	question_id integer PRIMARY KEY ,
	is_a boolean,
	is_b boolean,
	is_c boolean,
	q_type integer,
	description TEXT,
	picture TEXT,
	answer TEXT,
	answerDetail TEXT,
	options TEXT,
	stared boolean,
	errored boolean	
);

CREATE TABLE IF NOT EXISTS QUESTION_CHAPTER_MAPING(
	question_id integer,
	CHAPTER_ID integer
);

CREATE TABLE IF NOT EXISTS CHAPTER(
	_ID INTEGER PRIMARY KEY AUTOINCREMENT,
	CHAPTER_ID INTEGER,
	PARENT_ID INTEGER,
	ch_type INTEGER,
	CHAPTER TEXT
);

CREATE TABLE IF NOT EXISTS STARED_SCHOOL(
	SCHOOL_ID INTEGER PRIMARY KEY,
	NAME TEXT,
	DISTANCE TEXT,
	FPHONE TEXT,
	MPHONE TEXT,
	ADDRESS TEXT,
	PRICE DOUBLE,
	PICTURE TEXT,
	LATITUDE DOUBLE,
	LONGTITUDE DOUBLE,
	DESCRIPTION TEXT,
	AREAID INTEGER
	
);
CREATE TABLE IF NOT EXISTS county_code (
  id INTEGER PRIMARY KEY ,
  parentid INTEGER NOT NULL,
  name string NOT NULL,
  is_municipality boolean
);
CREATE TABLE IF NOT EXISTS sign_category (
  sign_category_id int NOT NULL,
  parent_id int,
  name string NOT NULL,
  PRIMARY KEY (sign_category_id)
);
CREATE TABLE IF NOT EXISTS sign_content (
  sign_content_id int NOT NULL,
  category_id int NOT NULL,
  name string NOT NULL,
  picture string NOT NULL,
  PRIMARY KEY (sign_content_id)
);
CREATE TABLE IF NOT EXISTS skills (
  id int NOT NULL,
  name String NOT null,
  file_name string NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS laws (
  id int NOT NULL,
  name String NOT null,
  file_name string NOT NULL,
  PRIMARY KEY (id)
);