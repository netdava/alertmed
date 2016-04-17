CREATE SEQUENCE alertmed_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


CREATE TABLE accounts (
    id bigint NOT NULL,
    uuid character varying(255),
    brandcolor character varying(255),
    brandlogo character varying(255),
    credit bigint NOT NULL,
    displayname character varying(255),
    invitetemplatename character varying(255),
    name character varying(255) NOT NULL,
    thankyoutemplatename character varying(255),
    owner_id bigint,
    parent_id bigint,
    address1 character varying,
    address2 character varying,
    city character varying,
    country character varying,
    phonenumber character varying,
    postalcode character varying,
    preferredlanguage character varying,
    vatnumber character varying,
    vatpercent real
);
