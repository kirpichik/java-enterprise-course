DROP TABLE IF EXISTS nodes;
DROP TABLE IF EXISTS nodes_tags;

DROP TABLE IF EXISTS ways;
DROP TABLE IF EXISTS ways_nodes;
DROP TABLE IF EXISTS ways_tags;

DROP TABLE IF EXISTS relations;
DROP TABLE IF EXISTS relations_nodes_members;
DROP TABLE IF EXISTS relations_ways_members;
DROP TABLE IF EXISTS relations_tags;

CREATE TABLE IF NOT EXISTS nodes (
    "id" INTEGER PRIMARY KEY,
    "version" INTEGER,
    "timestamp" DATE,
    "uid" INTEGER,
    "user" VARCHAR(255),
    "changeset" INTEGER,
    "lat" DOUBLE PRECISION,
    "lon" DOUBLE PRECISION
);

CREATE TABLE IF NOT EXISTS nodes_tags (
    "id" SERIAL PRIMARY KEY,
    "key" VARCHAR(255),
    "value" VARCHAR(255),
    "node_id" INTEGER REFERENCES nodes (id)
);

CREATE TABLE IF NOT EXISTS ways (
    "id" INTEGER PRIMARY KEY,
    "user" VARCHAR(255),
    "uid" INTEGER,
    "visible" INTEGER,
    "version" INTEGER,
    "changeset" INTEGER,
    "timestamp" DATE
);

CREATE TABLE IF NOT EXISTS ways_nodes (
    "id" SERIAL PRIMARY KEY,
    "way_id" INTEGER REFERENCES ways (id),
    "node_id" INTEGER REFERENCES ways (id)
);

CREATE TABLE IF NOT EXISTS ways_tags (
    "id" SERIAL PRIMARY KEY,
    "key" VARCHAR(255),
    "value" VARCHAR(255),
    "way_id" INTEGER REFERENCES ways (id)
);

CREATE TABLE IF NOT EXISTS relations (
    "id" INTEGER PRIMARY KEY,
    "user" VARCHAR(255),
    "uid" INTEGER,
    "visible" INTEGER,
    "version" INTEGER,
    "changeset" INTEGER,
    "timestamp" DATE
);

CREATE TABLE IF NOT EXISTS relations_nodes_members (
    "id" SERIAL PRIMARY KEY,
    "relation_id" INTEGER REFERENCES relations (id),
    "node_id" INTEGER REFERENCES nodes (id),
);

CREATE TABLE IF NOT EXISTS relations_ways_members (
    "id" SERIAL PRIMARY KEY,
    "relation_id" INTEGER REFERENCES relations (id),
    "way_id" INTEGER REFERENCES ways (id)
);

CREATE TABLE IF NOT EXISTS relations_tags (
    "id" SERIAL PRIMARY KEY,
    "key" VARCHAR(255),
    "value" VARCHAR(255),
    "relation_id" INTEGER REFERENCES ways (id)
);
