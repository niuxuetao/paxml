create sequence if not exists session_id_seq;
create sequence if not exists execution_id_seq;
create tabel if not exists paxml_execution (
    id bigint primary key,
    session_id bigint,
    process_id int,
    plan_id bigint,
    paxml_name varchar(255),
    paxml_path varchar(1000),
    paxml_params clob,
    status tinyint,
    started_at timestamp,
    ended_at timestamp
);
