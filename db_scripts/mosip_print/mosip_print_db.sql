CREATE DATABASE mosip_print
	ENCODING = 'UTF8'
	LC_COLLATE = 'en_US.UTF-8'
	LC_CTYPE = 'en_US.UTF-8'
	TABLESPACE = pg_default
	OWNER = postgres
	TEMPLATE  = template0;
-- ddl-end --
COMMENT ON DATABASE mosip_print IS 'The data related to capture Print Status tracking.';
-- ddl-end --

\c mosip_print postgres

-- object: regprc | type: SCHEMA --
DROP SCHEMA IF EXISTS print CASCADE;
CREATE SCHEMA print;
-- ddl-end --
ALTER SCHEMA print OWNER TO postgres;
-- ddl-end --

ALTER DATABASE mosip_print SET search_path TO print,pg_catalog,public;
-- ddl-end --

-- REVOKECONNECT ON DATABASE mosip_regprc FROM PUBLIC;
-- REVOKEALL ON SCHEMA regprc FROM PUBLIC;
-- REVOKEALL ON ALL TABLES IN SCHEMA regprc FROM PUBLIC ;
