CREATE TABLE `knol`(
  `id` integer NOT NULL AUTO_INCREMENT,
  `name` character varying(254) NOT NULL,
  `email` character varying(254) NOT NULL,
  `dob` date NOT NULL,
  CONSTRAINT knol_pkey PRIMARY KEY (id)
);

CREATE TABLE `knolx`( 
  `id` int NOT NULL AUTO_INCREMENT,
  `topic` character varying(254) NOT NULL,
  `date` date NOT NULL,
  `knol_id` integer NOT NULL,
  CONSTRAINT knolx_pkey PRIMARY KEY (id),
  CONSTRAINT knol_session_fk FOREIGN KEY (knol_id) REFERENCES knol (id) 
);
