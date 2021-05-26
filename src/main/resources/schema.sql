CREATE TABLE MEMBER
(
  id bigint auto_increment,
  balance integer not null comment '잔액',
  created_by varchar(10) not null comment '생성자',
  created_at datetime not null DEFAULT CURRENT_TIMESTAMP comment '생성일시',
  updated_by varchar(10)  comment '수정자',
  updated_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP  ON UPDATE CURRENT_TIMESTAMP comment '수정일시',

  PRIMARY key(id)
);

INSERT INTO MEMBER (balance, created_by, created_at) VALUES
(10000, 'SYSTEM', CURRENT_TIMESTAMP())
,(10000, 'SYSTEM', CURRENT_TIMESTAMP())
,(10000, 'SYSTEM', CURRENT_TIMESTAMP())
,(10000, 'SYSTEM', CURRENT_TIMESTAMP())
,(10000, 'SYSTEM', CURRENT_TIMESTAMP())
;


CREATE TABLE PRODUCT
(
  id bigint auto_increment,
  title varchar(20) not null comment '투자명',
  total_investing_amount integer not null comment '총 투자 모집금액',
  started_at datetime not null DEFAULT CURRENT_TIMESTAMP comment '투자 시작일시',
  finished_at datetime not null DEFAULT CURRENT_TIMESTAMP comment '투자 종료일시',
  status varchar(10) not null DEFAULT 'AVAILABLE' comment '상품 상태',
  created_by varchar(10) not null comment '생성자',
  created_at datetime not null DEFAULT CURRENT_TIMESTAMP comment '생성일시',
  updated_by varchar(10)  comment '수정자',
  updated_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP  ON UPDATE CURRENT_TIMESTAMP comment '수정일시',

  PRIMARY key(id),
  UNIQUE (title)

);

INSERT INTO PRODUCT (title, total_investing_amount, started_at, finished_at, status, created_by) VALUES
('개인신용 포트폴리오', 30000, '2021-03-01 00:00:00', '2021-06-30 00:00:00', 'AVAILABLE','SYSTEM')
,('부동산 포트폴리오', 20000, '2021-01-01 00:00:00', '2021-02-01 00:00:00', 'AVAILABLE', 'SYSTEM')
,('주식 포트폴리오', 10000, '2021-01-01 00:00:00', '2021-06-01 00:00:00', 'AVAILABLE', 'SYSTEM')
;


CREATE TABLE INVESTMENT
(
  member_id integer not null comment '사용자 id ',
  product_id integer not null comment '투자상품 id',
  investing_amount varchar(20) not null comment '투자금액',
  created_by varchar(10) not null comment '생성자',
  created_at datetime not null DEFAULT CURRENT_TIMESTAMP comment '생성일시',
  updated_by varchar(10)  comment '수정자',
  updated_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP  ON UPDATE CURRENT_TIMESTAMP comment '수정일시',

  PRIMARY key(member_id, product_id)
);

INSERT INTO INVESTMENT (member_id, product_id, investing_amount, created_by, created_at) VALUES
(1, 1,10000,'1',CURRENT_TIMESTAMP())
, (2, 1,10000,'1',CURRENT_TIMESTAMP())
;