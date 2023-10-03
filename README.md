# DevLab 프로젝트

DevLab은 개발과 관련된 정보들을 서로 공유하고 질문할 수 있는 커뮤니티 사이트입니다.

## Usecase Diagram

![usecase](document/usecase.svg)

## DB 테이블 설계

![ER Diagram](document/erd.svg)

### 공통

**공통 필드**
- created_by : API URL을 넣는 필드로 어떤 API를 통해서 데이터가 생성 되었는지 식별할 수 있도록 하기 위함
- modified_by : API URL을 넣는 필드로 어떤 API를 통해서 데이터가 수정 되었는지 식별할 수 있도록 하기 위함
- created_at : 테이블 생성 시간
- modified_at : 테이블 수정 시간

### Member (유저)

- login_id : 사용자의 로그인 아이디이며 유일한 값
- name : 사용자의 닉네임이며 유일한 값
- email : 사용자의 이메일 (이메일 인증을 하지 않으면 NULL)
- password : 비밀번호
- oauth_type : 소설 로그인 종류
- oauth_id : 소셜 로그인을 통해 회원가입을 했을 경우, 소셜 계정의 고유 아이디
- role : 유저 권한
- refresh_token : 액세스 토큰 갱신을 위한 리프레시 토큰이며, 분실 시 해당 토큰을 강제 만료처리 하기 위해 DB에 저장
- refresh_token_expired_at : 리프레시 토큰이 만료되는 시간

### Posts (게시글)

- title : 게시글 제목
- contents : 게시글 내용
- category : 게시글의 범주

### Comments (댓글)

- contents : 댓글 내용

### Likes (좋아요)

- value : '좋아요' 여부로 1 또는 0으로 표현
- post_id, comment_id : 어떤 게시글 또는 댓글에 대한 '좋아요'인지 식별하는 참조 키로 NULL 허용

### Tags (태그)

- name : 태그 이름

### Hashtags (해시태그)

- 게시글:태그의 N:M 관계를 풀어내기 위한 테이블로, 독립적인 Tags, Posts 두 테이블이 서로 관계를 맺어 새로운 의미를 가진 Hashtags 테이블이 생성된다. 

## SQL

```sql
drop database if exists DevLab;
create database DevLab;
use DevLab;

create table members
(
    id                       bigint auto_increment,
    login_id                 varchar(20) unique,
    nickname                 varchar(10) not null unique,
    email                    varchar(255) unique,
    password                 varchar(255),
    oauth_type               varchar(20),
    oauth_id                 varchar(255) unique,
    role                     varchar(10)  not null,
    refresh_token            varchar(255),
    refresh_token_expired_at bigint,
    created_by               varchar(255) not null,
    modified_by              varchar(255) not null,
    created_at               bigint       not null,
    modified_at              bigint       not null,
    primary key (id)
) default character set utf8mb4 collate utf8mb4_general_ci;

create index members_name_idx on members (nickname);
create index members_created_at_idx on members (created_at);

create table posts
(
    id          bigint auto_increment,
    member_id   bigint       not null,
    title       varchar(255) not null,
    contents    text         not null,
    category    varchar(20)  not null,
    created_by  varchar(255) not null,
    modified_by varchar(255) not null,
    created_at  bigint       not null,
    modified_at bigint       not null,
    primary key (id),
    foreign key (member_id) references members (id)
) default character set utf8mb4 collate utf8mb4_general_ci;

create fulltext index posts_title_idx on posts (title);
create fulltext index posts_contents_idx on posts (contents);
create index posts_type_idx on posts (category);
create index posts_member_id_idx on posts (member_id);
create index posts_created_at_idx on posts (created_at);

create table comments
(
    id          bigint auto_increment,
    post_id     bigint       null,
    member_id   bigint       not null,
    contents    text         not null,
    created_by  varchar(255) not null,
    modified_by varchar(255) not null,
    created_at  bigint       not null,
    modified_at bigint       not null,
    primary key (id),
    foreign key (member_id) references members (id),
    foreign key (post_id) references posts (id) on delete set null
) default character set utf8mb4 collate utf8mb4_general_ci;

create index comments_member_id_idx on comments (member_id);
create index comments_post_id_idx on comments (post_id);
create fulltext index comments_contents_idx on comments (contents);
create index comments_created_at_idx on comments (created_at);

create table likes
(
    id          bigint auto_increment,
    member_id   bigint       not null,
    post_id     bigint       null,
    comment_id  bigint       null,
    value       tinyint(1)   not null,
    created_by  varchar(255) not null,
    modified_by varchar(255) not null,
    created_at  bigint       not null,
    modified_at bigint       not null,
    primary key (id),
    foreign key (member_id) references members (id),
    foreign key (post_id) references posts (id),
    foreign key (comment_id) references comments (id)
) default character set utf8mb4 collate utf8mb4_general_ci;

create index likes_member_id_idx on likes (member_id);

create table tags
(
    id          bigint auto_increment,
    name        varchar(30)  unique not null,
    created_by  varchar(255) not null,
    modified_by varchar(255) not null,
    created_at  bigint       not null,
    modified_at bigint       not null,
    primary key (id)
) default character set utf8mb4 collate utf8mb4_general_ci;

create index tags_name_idx on tags (name);

create table hashtags
(
    id          bigint auto_increment,
    post_id     bigint       not null,
    tag_id      bigint       not null,
    primary key (id),
    foreign key (post_id) references posts (id),
    foreign key (tag_id) references tags (id)
) default character set utf8mb4 collate utf8mb4_general_ci;

create index hashtags_post_id_idx on hashtags (post_id);
create index hashtags_tag_id_idx on hashtags (tag_id);
```

## API Endpoints

공유 링크 : https://docs.google.com/spreadsheets/d/1qybDebGINrPRCwvj_EIV89gqKfhXwwa4-aGY2DBxk1I/edit?usp=sharing

## Tech Stacks

- Language : Java 17
- Build : Gradle
- Framework : Spring Boot
- Testing Tools : Junit5
- Database : MySQL 8 (Prod), H2 (Test)
- Database Access : MyBatis, Querydsl
- ORM : Spring Data JPA (Hibernate)

