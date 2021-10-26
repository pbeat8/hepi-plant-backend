-- USERS SCHEMA --
CREATE SCHEMA IF NOT EXISTS users;

CREATE SEQUENCE IF NOT EXISTS users.users_seq AS BIGINT;
CREATE SEQUENCE IF NOT EXISTS users.roles_seq AS BIGINT;

CREATE TABLE IF NOT EXISTS users.roles
(
    id bigint NOT NULL,
    name character varying(50) COLLATE pg_catalog."default",
    CONSTRAINT roles_pkey PRIMARY KEY (id)
)
TABLESPACE pg_default;
ALTER TABLE users.roles OWNER to postgres;

CREATE TABLE IF NOT EXISTS users.users
(
    id bigint NOT NULL,
    email character varying(255) UNIQUE NOT NULL COLLATE pg_catalog."default",
    username character varying(50) COLLATE pg_catalog."default",
    uid character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT users_pkey PRIMARY KEY (id),
    CONSTRAINT uk_efqukogbk7i0poucwoy2qie74 UNIQUE (uid)
)
TABLESPACE pg_default;
ALTER TABLE users.users OWNER to postgres;

CREATE TABLE IF NOT EXISTS users.user_role
(
    user_id bigint NOT NULL,
    role_id bigint NOT NULL,
    CONSTRAINT user_role_pkey PRIMARY KEY (user_id, role_id),
    CONSTRAINT fkaaftlgxa90nmuunajoxgbl316 FOREIGN KEY (user_id)
        REFERENCES users.users (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fkbbftlgxa90nmuunajoxgbl316 FOREIGN KEY (role_id)
            REFERENCES users.roles (id) MATCH SIMPLE
            ON UPDATE NO ACTION
            ON DELETE NO ACTION
)
TABLESPACE pg_default;
ALTER TABLE users.user_role OWNER to postgres;

-- PLANTS SCHEMA --
CREATE SCHEMA IF NOT EXISTS plants;

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
    photo character varying(255) COLLATE pg_catalog."default",
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
CREATE SCHEMA IF NOT EXISTS forum;

CREATE SEQUENCE IF NOT EXISTS forum.posts_seq AS BIGINT;
CREATE SEQUENCE IF NOT EXISTS forum.sales_offers_seq AS BIGINT;
CREATE SEQUENCE IF NOT EXISTS forum.post_comments_seq AS BIGINT;
CREATE SEQUENCE IF NOT EXISTS forum.sales_offer_comments_seq AS BIGINT;

CREATE TABLE IF NOT EXISTS forum.tags
(
    id bigint NOT NULL,
    name character varying(255) UNIQUE NOT NULL COLLATE pg_catalog."default",
    CONSTRAINT tags_pkey PRIMARY KEY (id),
    CONSTRAINT tg_efqukogbk7i0poucwoy2qie74 UNIQUE (name)
)
TABLESPACE pg_default;
ALTER TABLE forum.tags OWNER to postgres;

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
    photo character varying(255) COLLATE pg_catalog."default",
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
    photo character varying(255) COLLATE pg_catalog."default",
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

CREATE TABLE IF NOT EXISTS forum.post_comments
(
    id bigint NOT NULL,
    body character varying(255) COLLATE pg_catalog."default",
    created_date timestamp without time zone,
    updated_date timestamp without time zone,
    post_id bigint NOT NULL,
    user_id bigint NOT NULL,
    CONSTRAINT post_comments_pkey PRIMARY KEY (id),
    CONSTRAINT fkijnwr3brs8vaosl80jg9rp8uc FOREIGN KEY (post_id)
       REFERENCES forum.posts (id) MATCH SIMPLE
       ON UPDATE NO ACTION
       ON DELETE NO ACTION,
    CONSTRAINT fk5lidm6cqbc7u4xhqpxm899qme FOREIGN KEY (user_id)
        REFERENCES users.users (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
TABLESPACE pg_default;
ALTER TABLE forum.post_comments OWNER to postgres;

CREATE TABLE IF NOT EXISTS forum.sales_offer_comments
(
    id bigint NOT NULL,
    body character varying(255) COLLATE pg_catalog."default",
    created_date timestamp without time zone,
    updated_date timestamp without time zone,
    sales_offer_id bigint NOT NULL,
    user_id bigint NOT NULL,
    CONSTRAINT sales_offer_comments_pkey PRIMARY KEY (id),
    CONSTRAINT fkijnwr3brs2vaosl80jg9rp8uc FOREIGN KEY (sales_offer_id)
       REFERENCES forum.sales_offers (id) MATCH SIMPLE
       ON UPDATE NO ACTION
       ON DELETE NO ACTION,
    CONSTRAINT fk5lidm6cqbc1u4xhqpxm899qme FOREIGN KEY (user_id)
        REFERENCES users.users (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
TABLESPACE pg_default;
ALTER TABLE forum.sales_offer_comments OWNER to postgres;


CREATE TABLE IF NOT EXISTS forum.post_tag
(
    post_id bigint NOT NULL,
    tag_id bigint NOT NULL,
    CONSTRAINT post_tag_pkey PRIMARY KEY (post_id, tag_id),
    CONSTRAINT fkpsftlgxa90nmuunajoxgbl316 FOREIGN KEY (post_id)
        REFERENCES forum.posts (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fktgtlgxa90nmuunajoxgbl316 FOREIGN KEY (tag_id)
            REFERENCES forum.tags (id) MATCH SIMPLE
            ON UPDATE NO ACTION
            ON DELETE NO ACTION
)
TABLESPACE pg_default;
ALTER TABLE forum.post_tag OWNER to postgres;

CREATE TABLE IF NOT EXISTS forum.sales_offer_tag
(
    sales_offer_id bigint NOT NULL,
    tag_id bigint NOT NULL,
    CONSTRAINT salesOffer_tag_pkey PRIMARY KEY (sales_offer_id, tag_id),
    CONSTRAINT fksoftlgxa10nmuunajoxgbl316 FOREIGN KEY (sales_offer_id)
        REFERENCES forum.sales_offers (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fktgtlgxa10nmuunajoxgbl316 FOREIGN KEY (tag_id)
            REFERENCES forum.tags (id) MATCH SIMPLE
            ON UPDATE NO ACTION
            ON DELETE NO ACTION
)
TABLESPACE pg_default;
ALTER TABLE forum.sales_offer_tag OWNER to postgres;

CREATE SEQUENCE IF NOT EXISTS forum.tags_seq AS BIGINT;
