package com.kakaopay.investment.exception;

public class InvestmentException extends Exception {
    private final ErrorCodeInfo errorCodeInfo;

    public InvestmentException(ErrorCodeInfo errorCodeInfo) {
        super(errorCodeInfo.getErrorMessage());
        this.errorCodeInfo = errorCodeInfo;
    }

    public ErrorCodeInfo getErrorCodeInfo() {
        return errorCodeInfo;
    }
}
