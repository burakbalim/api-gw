/*
IMPORTANT:
    If using PostgreSQL, update ALL columns defined with 'text' to 'text',
    as PostgreSQL does not support the 'text' data type.
*/
CREATE TABLE oauth2_authorization (
                                      id varchar(100) NOT NULL,
                                      registered_client_id varchar(100) NOT NULL,
                                      principal_name varchar(200) NOT NULL,
                                      authorization_grant_type varchar(100) NOT NULL,
                                      authorized_scopes varchar(1000) DEFAULT NULL,
                                      attributes text DEFAULT NULL,
                                      state varchar(500) DEFAULT NULL,
                                      authorization_code_value text DEFAULT NULL,
                                      authorization_code_issued_at timestamp DEFAULT NULL,
                                      authorization_code_expires_at timestamp DEFAULT NULL,
                                      authorization_code_metadata text DEFAULT NULL,
                                      access_token_value text DEFAULT NULL,
                                      access_token_issued_at timestamp DEFAULT NULL,
                                      access_token_expires_at timestamp DEFAULT NULL,
                                      access_token_metadata text DEFAULT NULL,
                                      access_token_type varchar(100) DEFAULT NULL,
                                      access_token_scopes varchar(1000) DEFAULT NULL,
                                      oidc_id_token_value text DEFAULT NULL,
                                      oidc_id_token_issued_at timestamp DEFAULT NULL,
                                      oidc_id_token_expires_at timestamp DEFAULT NULL,
                                      oidc_id_token_metadata text DEFAULT NULL,
                                      refresh_token_value text DEFAULT NULL,
                                      refresh_token_issued_at timestamp DEFAULT NULL,
                                      refresh_token_expires_at timestamp DEFAULT NULL,
                                      refresh_token_metadata text DEFAULT NULL,
                                      user_code_value text DEFAULT NULL,
                                      user_code_issued_at timestamp DEFAULT NULL,
                                      user_code_expires_at timestamp DEFAULT NULL,
                                      user_code_metadata text DEFAULT NULL,
                                      device_code_value text DEFAULT NULL,
                                      device_code_issued_at timestamp DEFAULT NULL,
                                      device_code_expires_at timestamp DEFAULT NULL,
                                      device_code_metadata text DEFAULT NULL,
                                      PRIMARY KEY (id)
);

-- TODO add index for refresh token

SET GLOBAL event_scheduler = ON;
CREATE EVENT oauth2_authorization_delete_expired_data
    ON SCHEDULE
        EVERY 1 HOUR
            STARTS CURRENT_TIMESTAMP
    DO
    DELETE FROM oauth2_authorization WHERE refresh_token_expires_at <= CONVERT_TZ(NOW(), 'UTC', 'Asia/Dubai');
DELETE FROM oauth2_authorization WHERE oauth2_authorization.access_token_expires_at <= CONVERT_TZ(NOW(), 'UTC', 'Asia/Dubai');


-- For postgres configuration watcher

CREATE OR REPLACE FUNCTION notify_table_change() RETURNS TRIGGER AS $$
BEGIN
    PERFORM pg_notify('table_changes', row_to_json(NEW)::text);
RETURN NEW;
END;
$$ LANGUAGE plpgsql;
