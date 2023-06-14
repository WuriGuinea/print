-- object: regprcuser | type: ROLE --
-- DROP ROLE IF EXISTS regprcuser;
CREATE ROLE printuser WITH
	INHERIT
	LOGIN
	PASSWORD :dbuserpwd;
-- ddl-end --
