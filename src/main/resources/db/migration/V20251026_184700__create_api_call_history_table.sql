create table if not exists api_call_history (
    id bigserial primary key,
    endpoint varchar not null,
    http_method varchar not null,
    response_status int not null,
    created_at timestamptz not null default current_timestamp
);