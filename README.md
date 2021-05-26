# Invest API

## Introduction

* 사용자(member)는 원하는 부동산/신용 투자 상품(product)을 투자할 수 있습니다.
* 투자상품이 오픈될 때, 다수의 고객이 동시에 투자를 합니다.
* 투자 후 투자상품의 누적 투자모집 금액, 투자자 수가 증가됩니다.
* 총 투자모집금액 달성 시 투자는 마감되고 상품은 Sold out 됩니다.

<br/>

## 개발 스펙
 * Java 1.8
 * Spring Boot
 * Maven
 * Embedded Redis
   * 접속정보 : (host) 127.0.0.1 (port) 163771
 * Embedded H2
   * 접속정보 : (url) http://localhost:8080/h2-console  (JDBC URL)jdbc:h2:mem:test
 * Lombok
 
 
<br/>
<br/>

## 투자가능한 상품 Redis적재 API (다른 API TEST 전 호출 필수)  
어드민을 통해 상품을 등록/수정하거나 스케줄러를 통해 상품의 투자가능 상태가 될 때 Redis에 적재된다고 가정한다.

현재는 위의 과정이 없기 때문에 Api를 통해 임의로 현재 투자 가능한 모든 투자가능 상품을 Redis에 적재한다.

```bash
POST http://localhost:8080/api/product

> curl --location --request POST 'localhost:8080/api/product'
```

<br/>

## 전체 투자 상품 조회 API

현재 모집기간인 상품 데이터를 조회한다.

```bash
GET http://localhost:8080/api/product

> curl --location --request GET 'localhost:8080/api/product'
```

<br/>

Success Response (List)
|  Name  | Type | Description | Required |  |
| ---------- | ---------- | ---------- | ------- |  ------- | 
| productId    | long    | 상품ID  | O       |  |
| productTitle    | long    | 상품제목  | O       |  |
| productTotalInvestingAmount    | long    | 총 모집금액  | O       |  |
| investedAmount    | long    | 현재 모집금액  | O       |  |
| investorCount    | int    | 투자자 수  | O       |  |
| status    | string    | 투자 모집상태  | O       |  |
| startedAt    | string (DateTime)   | 상품모집 시작  | O       |  |
| finishedAt    | string (DateTime)    | 상품모집 종료  | O       |  |

Fail Response
|  Name  | Type | Description | Required |  |
| ---------- | ---------- | ---------- | ------- |  ------- | 
| errorMsg    | String    | 에러메시지  | O       |  |



<br/>


## 투자하기 API

현재 모집 가능한 상품에 대해 투자 금액만큼 투자한다.

투자 상세 조건)<br/>
회원은 한가지 상품에 대해서는 한번만 투자 가능합니다.<br/>
회원당 최소/최대 투자금액은 정해져있지 않습니다.  <br/>
회원의 상태(회원여부,잔액)와 상품의 상태(투자가능기간, 투자모집상태, 투자가능금액)에 대해 확인 후 투자가 가능합니다.

```bash
POST http://localhost:8080/api/invest

> curl --location --request POST 'localhost:8080/api/invest' \
--header 'Content-Type: application/json' \
--header 'X-USER-ID: 1' \
--data-raw '{
    "productId":3,
    "investingAmount":5000
}'
```
<br/>


Request Header
|  Name  | Type | Description | Required |
| ---------- | ---------- | ---------- | ------- |
| X-USER-ID  | long       | 사용자 아이디 | O       |


Request Body
|  Name  | Type | Description | Required |
| ---------- | ---------- | ---------- | ------- |
| productId  | long       | 상품 ID | O       |
| investingAmount  | long       | 투자 금액  | O       |


Success Response : OK

Fail Response
|  Name  | Type | Description | Required |  |
| ---------- | ---------- | ---------- | ------- |  ------- | 
| errorMsg    | String    | 에러메시지  | O       |  |


<br/>


## 나의 투자상품 조회 API

회원이 투자한 모든 상품을 반환한다. 

```bash
GET http://localhost:8080/api/member/myList

> curl --location --request GET 'localhost:8080/api/member/myList' \
--header 'X-USER-ID: 1'
```

<br/>

Request Header
|  Name  | Type | Description | Required |
| ---------- | ---------- | ---------- | ------- |
| X-USER-ID  | long       | 사용자 아이디 | O       |


Success Response (List)
|  Name  | Type | Description | Required |  |
| ---------- | ---------- | ---------- | ------- |  ------- | 
| productId    | long    | 상품ID  | O       |  |
| productTitle    | string    | 상품 제목  | O       |  |
| productTotalInvestingAmount    | long    | 총 모집금액  | O       |  |
| myInvestingAmount    | long    | 나의 투자금액  | O       |  |
| createdAt    | string(DateTime)    | 투자일시  | O       |  |


Fail Response
|  Name  | Type | Description | Required |  |
| ---------- | ---------- | ---------- | ------- |  ------- | 
| errorMsg    | String    | 에러메시지  | O       |  |



<br/>


## 참고) TEST를 위한 Init Data 

<img width="1285" alt="image" src="https://user-images.githubusercontent.com/67402035/119659189-e6069780-be68-11eb-9f6f-c474d0c3bd57.png">

