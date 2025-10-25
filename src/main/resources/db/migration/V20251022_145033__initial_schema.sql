create table if not exists departments (
    id bigserial primary key,
    code varchar not null,
    name varchar not null,

    created_at timestamptz not null default current_timestamp,
    updated_at timestamptz not null default current_timestamp
);

create unique index if not exists idx_departments_code on departments (code);

create table if not exists locations (
    id bigserial primary key,
    code varchar not null,
    name varchar not null,
    address varchar not null,

    created_at timestamptz not null default current_timestamp,
    updated_at timestamptz not null default current_timestamp
);

create unique index if not exists idx_locations_code on locations (code);

create table if not exists tiers (
    id bigserial primary key,
    code int not null,
    name varchar not null,

    created_at timestamptz not null default current_timestamp,
    updated_at timestamptz not null default current_timestamp
);

create unique index if not exists idx_tiers_code on tiers (code);

create table if not exists employees (
    id bigserial primary key,
    "no" int not null,
    name varchar not null,
    tier_code int default null,
    department_code varchar default null,
    location_code varchar default null,
    supervisor_no int default null,
    salary bigint not null,
    entry_date timestamptz not null,

    created_at timestamptz not null default current_timestamp,
    updated_at timestamptz not null default current_timestamp,

    constraint fk_employees_tier_code foreign key (tier_code) references tiers (code) on update cascade on delete set null,
    constraint fk_employees_department_code foreign key (department_code) references departments (code) on update cascade on delete set null,
    constraint fk_employees_location_code foreign key (location_code) references locations (code) on update cascade on delete set null
);

create unique index if not exists idx_employees_no on employees (no);

alter table employees
add constraint fk_employees_supervisor_no foreign key (supervisor_no) references employees ("no") on update cascade on delete set null;