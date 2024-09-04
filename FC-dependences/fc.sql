CREATE TABLE tb_user
(
    id          BIGINT PRIMARY KEY,
    name        TEXT   NOT NULL,
    password    TEXT   NOT NULL,
    phone       TEXT   NOT NULL,
    signature   TEXT   NOT NULL DEFAULT '',
    avatar      TEXT   NOT NULL DEFAULT '',
    create_time BIGINT NOT NULL,
    update_time BIGINT NOT NULL
);

CREATE TABLE tb_video
(
    id          BIGINT PRIMARY KEY,
    uid         BIGINT NOT NULL,
    title       TEXT   NOT NULL,
    description TEXT   NOT NULL DEFAULT '',
    video_url   TEXT   NOT NULL,
    cover_url   TEXT   NOT NULL,
    create_time BIGINT NOT NULL,
    update_time BIGINT NOT NULL,
    FOREIGN KEY (uid) REFERENCES tb_user (id)
);

CREATE TABLE tb_video_comment
(
    id          BIGINT PRIMARY KEY,
    uid         BIGINT NOT NULL,
    vid         BIGINT NOT NULL,
    fav_cnt     INT    NOT NULL DEFAULT 0,
    content     TEXT   NOT NULL,
    create_time BIGINT NOT NULL,
    update_time BIGINT NOT NULL,
    FOREIGN KEY (uid) REFERENCES tb_user (id),
    FOREIGN KEY (vid) REFERENCES tb_video (id)
);

CREATE TABLE tb_user_video_favorite
(
    id          BIGINT PRIMARY KEY,
    uid         BIGINT NOT NULL,
    vid         BIGINT NOT NULL,
    create_time BIGINT NOT NULL,
    FOREIGN KEY (uid) REFERENCES tb_user (id),
    FOREIGN KEY (vid) REFERENCES tb_video (id)
);

CREATE TABLE tb_user_video_collection
(
    id          BIGINT PRIMARY KEY,
    uid         BIGINT NOT NULL,
    vid         BIGINT NOT NULL,
    create_time BIGINT NOT NULL,
    FOREIGN KEY (uid) REFERENCES tb_user (id),
    FOREIGN KEY (vid) REFERENCES tb_video (id)
);

CREATE TABLE tb_user_favorite
(
    id          BIGINT PRIMARY KEY,
    uid         BIGINT NOT NULL,
    fav_uid     BIGINT NOT NULL,
    create_time BIGINT NOT NULL,
    FOREIGN KEY (uid) REFERENCES tb_user (id),
    FOREIGN KEY (fav_uid) REFERENCES tb_user (id)
);


CREATE TABLE tb_user_video_history
(
    id          BIGINT PRIMARY KEY,
    uid         BIGINT NOT NULL,
    vid         BIGINT NOT NULL,
    create_time BIGINT NOT NULL,
    FOREIGN KEY (uid) REFERENCES tb_user (id),
    FOREIGN KEY (vid) REFERENCES tb_video (id)
);
