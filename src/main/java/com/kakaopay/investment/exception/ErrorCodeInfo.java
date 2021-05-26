package com.kakaopay.investment.exception;

public enum ErrorCodeInfo {
    USER_VAILDATION("정상 유저가 아닙니다.", 400),
    USER_BALANCE("잔액이 부족합니다.", 400),

    PRODUCT_VALIDATION("신청 불가능한 투자상품입니다.", 400),
    PRODUCT_VALIDATION_PERIOD("신청 기간이 아닌 투자상품입니다.", 400),
    PRODUCT_VALIDATION_STATUS("모집이 완료된 투자상품입니다.", 400),
    PRODUCT_VALIDATION_AMOUNT("모집 금액이 초과되었습니다. 모집 가능한 금액을 확인해주세요.", 400),
    PRODUCT_VALIDATION_REDIS("신청 불가능한 투자상품입니다.", 400),

    INVESTMENT_SYNC("사용자가 많아요. 기다려주세요.", 400),
    INVESTMENT_REDIS_REAMINAMOUNT("모집금액 초과로 신청이불가능합니다.", 400),

    ALREADY_INVESTED("이미 투자중입니다.", 200);

    ErrorCodeInfo(String errorMessage, int statusCode) {
        this.errorMessage = errorMessage;
        this.statusCode = statusCode;
    }

    private String errorMessage;

    private int statusCode;

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
