# DevLab 프로젝트

DevLab은 개발과 관련된 정보들을 서로 공유하고 질문할 수 있는 커뮤니티 사이트입니다.

## Usecase Diagram

![usecase](document/usecase.svg)

## DB 테이블 설계

![ER Diagram](document/erd.svg)

### 공통

**PK 자료형 결정**
- Using _UUID_ as PK
  - 상대적으로 큰 크기 -> Secondary Index 및 FK 크기 증가
  - 분산 환경에서 데이터 중복 문제 해결
  - PK 값을 예측하기 어렵기 때문에 보안상의 이점
- Using _INT_ as PK
  - 상대적으로 작은 용량
  - 직관적으로 이해하기 쉬움 (이로인해 동시에 보안상의 문제 발생)
  - 분산 환경에서 데이터 중복 문제 발생

이 프로젝트에서는 분산 환경을 고려할 만큼 규모가 크지 않으며, 외부에 공개하는 OPEN API가 아니기 때문에 INT 자료형을 테이블의 PK로 결정하였습니다.

_Reference_
- https://stackoverflow.com/questions/52414414/best-practices-on-primary-key-auto-increment-and-uuid-in-sql-databases
- https://medium.com/daangn/varchar-vs-text-230a718a22a1

**공통 필드**
- created_by : API URL을 넣는 필드로 어떤 API를 통해서 데이터가 생성 되었는지 식별할 수 있도록 하기 위함
- modified_by : API URL을 넣는 필드로 어떤 API를 통해서 데이터가 수정 되었는지 식별할 수 있도록 하기 위함
- created_at : 테이블 생성 시간
- modified_at : 테이블 수정 시간
- deleted : 해당 테이블의 논리적인 삭제 여부

### Member (사용자)

- name : 사용자의 닉네임이며 유일한 값을 가집니다.
- email : 사용자의 이메일 (소셜 회원가입을 하지 않은 사용자의 경우에는 NULL 값 허용)
- password : 암호화 된 비밀번호
- role : 유저 권한
- oauth2_type : 어떤 소셜 로그인을 통해 회원가입을 했는지에 대한 필드
- refresh_token : 액세스 토큰 만료 시, 토큰 재발급을 위한 갱신 토큰
- refresh_token_expired_at : 리프레시 토큰 만료 시간

### Posts (게시글)

- title : 게시글 제목
- contents : 게시글 내용

### Posts_Likes (게시글-좋아요)

- post_id : 어떤 게시글에 대한 '좋아요'인지 식별하는 참조키
- member_id : 누가 '좋아요'를 했는지에 대해 식별하는 참조키
- value : '좋아요' 여부를 나타내는 필드

### Comments (댓글)

- post_id : 어떤 게시글에 대한 댓글인지 식별하는 참조키
- member_id : 누가 댓글을 작성했는지에 대해 식별하는 참조키
- contents : 댓글 내용

### Comments_Likes (댓글-좋아요)

- post_id : 어떤 댓글에 대한 '좋아요'인지 식별하는 참조키
- member_id : 누가 '좋아요'를 했는지에 대해 식별하는 참조키
- value : '좋아요' 여부를 나타내는 필드

## SQL

```sql
drop database if exists DevLab;
create database DevLab;
use DevLab;

create table members
(
    id                       bigint auto_increment,
    name                     varchar(30)  not null,
    email                    varchar(255) not null unique,
    password                 varchar(255),
    role                     varchar(10)  not null,
    oauth2_type              varchar(10)  not null,
    profile_img_url          varchar(255),
    refresh_token            varchar(255),
    refresh_token_expired_at datetime,
    created_by               varchar(255) not null,
    modified_by              varchar(255) not null,
    created_at               timestamp    not null,
    modified_at              timestamp    not null,
    deleted                  tinyint(1)   not null default 0,
    primary key (id)
) default character set utf8mb4 collate utf8mb4_general_ci;

create index members_name_idx on members (name);
create index members_email_idx on members (email);
create index members_created_at_idx on members (created_at);

create table posts
(
    id          bigint auto_increment,
    member_id   bigint       not null,
    title       varchar(255) not null,
    contents    text         not null,
    created_by  varchar(255) not null,
    modified_by varchar(255) not null,
    created_at  timestamp    not null,
    modified_at timestamp    not null,
    deleted     tinyint(1)   not null default 0,
    primary key (id),
    foreign key (member_id) references members (id)
) default character set utf8mb4 collate utf8mb4_general_ci;

create index posts_title_idx on posts (title);
create index posts_created_at_idx on posts (created_at);
create index posts_member_id_idx on posts (member_id);

create table comments
(
    id          bigint auto_increment,
    member_id     bigint       not null,
    post_id     bigint       not null,
    contents    text         not null,
    created_by  varchar(255) not null,
    modified_by varchar(255) not null,
    created_at  timestamp    not null,
    modified_at timestamp    not null,
    deleted     tinyint(1)   not null default 0,
    primary key (id),
    foreign key (member_id) references members (id),
    foreign key (post_id) references posts (id)
) default character set utf8mb4 collate utf8mb4_general_ci;

create index comments_created_at_idx on comments (created_at);
create index comments_member_id_idx on comments (member_id);
create index comments_post_id_idx on comments (post_id);

create table posts_likes
(
    id          bigint auto_increment,
    member_id   bigint       not null,
    post_id     bigint       null,
    value       tinyint      not null,
    created_by  varchar(255) not null,
    modified_by varchar(255) not null,
    created_at  timestamp    not null,
    modified_at timestamp    not null,
    primary key (id),
    foreign key (member_id) references members (id),
    foreign key (post_id)   references posts (id)
) default character set utf8mb4 collate utf8mb4_general_ci;

create index posts_likes_member_id_idx on posts_likes (member_id);
create index posts_likes_post_id_idx on posts_likes (post_id);

create table comments_likes
(
    id          bigint auto_increment,
    member_id   bigint       not null,
    comment_id  bigint       null,
    value       tinyint      not null,
    created_by  varchar(255) not null,
    modified_by varchar(255) not null,
    created_at  timestamp    not null,
    modified_at timestamp    not null,
    primary key (id),
    foreign key (member_id) references members (id),
    foreign key (comment_id) references comments (id)
) default character set utf8mb4 collate utf8mb4_general_ci;

create index comments_likes_member_id on comments_likes (member_id);
create index comments_likes_comment_id on comments_likes (comment_id);
```

## API Endpoints

공유 링크 : https://docs.google.com/spreadsheets/d/1qybDebGINrPRCwvj_EIV89gqKfhXwwa4-aGY2DBxk1I/edit?usp=sharing

## Tech Stacks

- Language : Java 17
- Build : Gradle
- Framework : Spring Boot 3.1.3
- Testing Tools : Junit5
- Database : MySQL 8.0.33 (Prod), H2 (Test)
- Database Access : Spring Data JPA 3.1.3
