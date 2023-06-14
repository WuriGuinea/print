-- -------------------------------------------------------------------------------------------------
-- Database Name: mosip_print
-- Table Name 	: print.print_transaction
-- Purpose    	: Print Transaction: Print Transaction list table, Store all the transaction which are sent to ActiveMQ for print.
--
-- Create By   	: Thamaraikannan
-- Created Date	: 07-Oct-2021
--
-- Modified Date        Modified By         Comments / Remarks
-- ------------------------------------------------------------------------------------------

-- ------------------------------------------------------------------------------------------

-- object: regprc.print_transaction | type: TABLE --
-- DROP TABLE IF EXISTS regprc.print_transaction CASCADE;

CREATE TABLE IF NOT EXISTS print.print_transaction (
    print_id character varying(36) NOT NULL,
    credential_transaction_id character varying(36) NOT NULL,
    reg_id character varying(39) NOT NULL,
    status_code character varying(36) NOT NULL,
    status_comment character varying(256),
    lang_code character varying(3) NOT NULL,
    read_dtimes timestamp,
    print_dtimes timestamp,
    cr_by character varying(256) NOT NULL,
    cr_dtimes timestamp NOT NULL,
    upd_by character varying(256),
    upd_dtimes timestamp,
    is_deleted boolean DEFAULT false,
    del_dtimes timestamp,
    CONSTRAINT pk_printtrn_id PRIMARY KEY (print_id, lang_code)
);
-- ddl-end --
COMMENT ON TABLE print.print_transaction IS 'Print Transaction: Print Transaction list table, Store all the transaction which are sent to ActiveMQ for print';
-- ddl-end --
COMMENT ON COLUMN print.print_transaction.print_id IS 'ID: Primary key of print transaction, with lang_cd for multi language ';
-- ddl-end --
COMMENT ON COLUMN print.print_transaction.credential_transaction_id IS 'Description : Credential Transaction Unique Id mosip_credential(credential_transaction)';
-- ddl-end --
COMMENT ON COLUMN print.print_transaction.reg_id IS 'Description : Registration Id (RID)';
-- ddl-end --
COMMENT ON COLUMN print.print_transaction.status_code IS 'Description : Status Code for Printing';
-- ddl-end --
COMMENT ON COLUMN print.print_transaction.status_comment IS 'Description : Detailed Status Comment';
-- ddl-end --
COMMENT ON COLUMN print.print_transaction.lang_code IS 'Language Code : For multilanguage implementation this attribute Refers master.language.code. The value of some of the attributes in current record is stored in this respective language.';
-- ddl-end --
COMMENT ON COLUMN print.print_transaction.read_dtimes IS 'Read DateTimestamp : Date and Timestamp when listener read message from ActiveMQ.';
-- ddl-end --
COMMENT ON COLUMN print.print_transaction.print_dtimes IS 'Print DateTimestamp : Date and Timestamp when listener print the pdf.';
-- ddl-end --
COMMENT ON COLUMN print.print_transaction.cr_by IS 'Created By : ID or name of the user who create / insert record.';
-- ddl-end --
COMMENT ON COLUMN print.print_transaction.cr_dtimes IS 'Created DateTimestamp : Date and Timestamp when the record is created/inserted';
-- ddl-end --
COMMENT ON COLUMN print.print_transaction.upd_by IS 'Updated By : ID or name of the user who update the record with new values';
-- ddl-end --
COMMENT ON COLUMN print.print_transaction.upd_dtimes IS 'Updated DateTimestamp : Date and Timestamp when any of the fields in the record is updated with new values.';
-- ddl-end --
COMMENT ON COLUMN print.print_transaction.is_deleted IS 'IS_Deleted : Flag to mark whether the record is Soft deleted.';
-- ddl-end --
COMMENT ON COLUMN print.print_transaction.del_dtimes IS 'Deleted DateTimestamp : Date and Timestamp when the record is soft deleted with is_deleted=TRUE';
-- ddl-end --
