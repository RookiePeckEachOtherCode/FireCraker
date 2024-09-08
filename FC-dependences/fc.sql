create table tb_user
(
    id              bigint                   not null
        primary key,
    name            text                     not null,
    password        text                     not null,
    phone           text unique              not null,
    signature       text    default ''::text not null,
    avatar          text    default ''::text not null,
    create_time     bigint                   not null,
    update_time     bigint                   not null,
    email           text unique              not null,
    show_collection boolean default true     not null
);

alter table tb_user
    owner to fc_user;

create table tb_video
(
    id          bigint                not null
        primary key,
    uid         bigint                not null
        references tb_user,
    title       text                  not null,
    description text default ''::text not null,
    video_url   text                  not null,
    cover_url   text                  not null,
    create_time bigint                not null,
    update_time bigint                not null,
    play_cnt    integer,
    fav_cnt     integer,
    col_cnt     integer,
    tags        text default ''       not null
);

alter table tb_video
    owner to fc_user;

create table tb_video_comment
(
    id          bigint            not null
        primary key,
    uid         bigint            not null
        references tb_user,
    vid         bigint            not null
        references tb_video,
    fav_cnt     integer default 0 not null,
    content     text              not null,
    create_time bigint            not null,
    update_time bigint            not null
);

alter table tb_video_comment
    owner to fc_user;

create table tb_user_video_favorite
(
    id          bigint not null
        primary key,
    uid         bigint not null
        references tb_user,
    vid         bigint not null
        references tb_video,
    create_time bigint not null
);

alter table tb_user_video_favorite
    owner to fc_user;

create table tb_user_video_collection
(
    id          bigint not null
        primary key,
    uid         bigint not null
        references tb_user,
    vid         bigint not null
        references tb_video,
    create_time bigint not null
);

alter table tb_user_video_collection
    owner to fc_user;

create table tb_user_favorite
(
    id          bigint not null
        primary key,
    uid         bigint not null
        references tb_user,
    fav_uid     bigint not null
        references tb_user,
    create_time bigint not null
);

alter table tb_user_favorite
    owner to fc_user;

create table tb_user_video_history
(
    id          bigint not null
        primary key,
    uid         bigint not null
        references tb_user,
    vid         bigint not null
        references tb_video,
    create_time bigint not null
);

alter table tb_user_video_history
    owner to fc_user;

