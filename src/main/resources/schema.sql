-- USERS SCHEMA --
CREATE SCHEMA IF NOT EXISTS users AUTHORIZATION postgres;

CREATE SEQUENCE IF NOT EXISTS users.users_seq AS BIGINT;

CREATE TABLE IF NOT EXISTS users.users
(
    id bigint NOT NULL,
    email character varying(255) COLLATE pg_catalog."default",
    permission character varying(255) COLLATE pg_catalog."default",
    u_id character varying(50) COLLATE pg_catalog."default",
    username character varying(50) COLLATE pg_catalog."default",
    uid character varying(50) COLLATE pg_catalog."default",
    CONSTRAINT users_pkey PRIMARY KEY (id),
    CONSTRAINT uk_efqukogbk7i0poucwoy2qie74 UNIQUE (uid)
)
TABLESPACE pg_default;
ALTER TABLE users.users OWNER to postgres;


-- PLANTS SCHEMA --
CREATE SCHEMA IF NOT EXISTS plants AUTHORIZATION postgres;

CREATE SEQUENCE IF NOT EXISTS plants.categories_seq AS BIGINT START 7;
CREATE SEQUENCE IF NOT EXISTS plants.species_seq AS BIGINT START 23;
CREATE SEQUENCE IF NOT EXISTS plants.plants_seq AS BIGINT;
CREATE SEQUENCE IF NOT EXISTS plants.events_seq AS BIGINT;
CREATE SEQUENCE IF NOT EXISTS plants.schedules_seq AS BIGINT;

CREATE TABLE IF NOT EXISTS plants.categories
(
    id bigint NOT NULL,
    name character varying(255) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT categories_pkey PRIMARY KEY (id),
    CONSTRAINT uk_t8o6pivur7nn124jehx7cygw5 UNIQUE (name)
)
TABLESPACE pg_default;
ALTER TABLE plants.categories OWNER to postgres;

CREATE TABLE IF NOT EXISTS plants.species
(
    id bigint NOT NULL,
    fertilizing_frequency integer NOT NULL,
    misting_frequency integer NOT NULL,
    name character varying(255) COLLATE pg_catalog."default" NOT NULL,
    placement character varying(255) COLLATE pg_catalog."default",
    soil character varying(255) COLLATE pg_catalog."default",
    watering_frequency integer NOT NULL,
    category_id bigint,
    CONSTRAINT species_pkey PRIMARY KEY (id),
    CONSTRAINT uk_29ixq8ot8e88rk6v7jpkisgr3 UNIQUE (name),
    CONSTRAINT fkcnftlgxa90nmuunajoxgbl316 FOREIGN KEY (category_id)
        REFERENCES plants.categories (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
TABLESPACE pg_default;
ALTER TABLE plants.species OWNER to postgres;

CREATE TABLE IF NOT EXISTS plants.plants
(
    id bigint NOT NULL,
    location character varying(255) COLLATE pg_catalog."default",
    name character varying(255) COLLATE pg_catalog."default" NOT NULL,
    purchase_date timestamp without time zone,
    category_id bigint,
    species_id bigint,
    user_id bigint,
    CONSTRAINT plants_pkey PRIMARY KEY (id),
    CONSTRAINT fk3o3bo53lvmnms6v5l84nnkk7q FOREIGN KEY (species_id)
        REFERENCES plants.species (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fkgk96wou3hd3xfgqlisa1e0lyc FOREIGN KEY (category_id)
        REFERENCES plants.categories (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fkobr4pknd4t8mj5brx0du97c3j FOREIGN KEY (user_id)
        REFERENCES users.users (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
TABLESPACE pg_default;
ALTER TABLE plants.plants OWNER to postgres;

CREATE TABLE IF NOT EXISTS plants.events
(
    id bigint NOT NULL,
    event_date timestamp without time zone,
    event_description character varying(255) COLLATE pg_catalog."default",
    event_name character varying(255) COLLATE pg_catalog."default",
    is_done boolean NOT NULL,
    plant_id bigint,
    CONSTRAINT events_pkey PRIMARY KEY (id),
    CONSTRAINT fkkos9p3x0qhc3qcy32ri5yjof3 FOREIGN KEY (plant_id)
        REFERENCES plants.plants (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
TABLESPACE pg_default;
ALTER TABLE plants.events OWNER to postgres;

CREATE TABLE IF NOT EXISTS plants.schedules
(
    id bigint NOT NULL,
    fertilizing_frequency integer NOT NULL,
    misting_frequency integer NOT NULL,
    watering_frequency integer NOT NULL,
    plant_id bigint,
    CONSTRAINT schedules_pkey PRIMARY KEY (id),
    CONSTRAINT fk3vrbosn4k8dtgj6w5761f12v FOREIGN KEY (plant_id)
        REFERENCES plants.plants (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
TABLESPACE pg_default;
ALTER TABLE plants.schedules OWNER to postgres;


-- FORUM SCHEMA --
CREATE SCHEMA IF NOT EXISTS forum AUTHORIZATION postgres;

CREATE SEQUENCE IF NOT EXISTS forum.posts_seq AS BIGINT;
CREATE SEQUENCE IF NOT EXISTS forum.sales_offers AS BIGINT;

CREATE TABLE IF NOT EXISTS forum.posts
(
    id bigint NOT NULL,
    body character varying(255) COLLATE pg_catalog."default",
    created_date timestamp without time zone,
    tag1 character varying(255) COLLATE pg_catalog."default",
    tag2 character varying(255) COLLATE pg_catalog."default",
    tag3 character varying(255) COLLATE pg_catalog."default",
    tag4 character varying(255) COLLATE pg_catalog."default",
    tag5 character varying(255) COLLATE pg_catalog."default",
    title character varying(255) COLLATE pg_catalog."default",
    updated_date timestamp without time zone,
    category_id bigint,
    user_id bigint NOT NULL,
    CONSTRAINT posts_pkey PRIMARY KEY (id),
    CONSTRAINT fk5lidm6cqbc7u4xhqpxm898qme FOREIGN KEY (user_id)
        REFERENCES users.users (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fkijnwr3brs8vaosl80jg9rp7uc FOREIGN KEY (category_id)
        REFERENCES plants.categories (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
TABLESPACE pg_default;
ALTER TABLE forum.posts OWNER to postgres;

CREATE TABLE IF NOT EXISTS forum.sales_offers
(
    id bigint NOT NULL,
    body character varying(255) COLLATE pg_catalog."default",
    created_date timestamp without time zone,
    location character varying(255) COLLATE pg_catalog."default",
    price numeric(19,2),
    tag1 character varying(255) COLLATE pg_catalog."default",
    tag2 character varying(255) COLLATE pg_catalog."default",
    tag3 character varying(255) COLLATE pg_catalog."default",
    title character varying(255) COLLATE pg_catalog."default",
    updated_date timestamp without time zone,
    category_id bigint,
    user_id bigint NOT NULL,
    CONSTRAINT sales_offers_pkey PRIMARY KEY (id),
    CONSTRAINT fk3ssa02wxqfynma6bxuj449joo FOREIGN KEY (category_id)
        REFERENCES plants.categories (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fkob0uasweeaogr2j0ilv2900f7 FOREIGN KEY (user_id)
        REFERENCES users.users (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
TABLESPACE pg_default;
ALTER TABLE forum.sales_offers OWNER to postgres;
