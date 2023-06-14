\c mosip_print

-- object: grant_18180691b7 | type: PERMISSION --
GRANT CONNECT
   ON DATABASE mosip_print
   TO printuser;
-- ddl-end --

-- object: grant_3543fb6cf7 | type: PERMISSION --
--GRANT USAGE
--   ON SCHEMA print
--   TO printuser;
-- ddl-end --

-- object: grant_8e1a2559ed | type: PERMISSION --
--GRANT SELECT,UPDATE
--   ON ALL TABLES IN SCHEMA print
--   TO printuser;
-- ddl-end --

--ALTER DEFAULT PRIVILEGES IN SCHEMA print
--	GRANT SELECT,UPDATE ON TABLES TO printuser;