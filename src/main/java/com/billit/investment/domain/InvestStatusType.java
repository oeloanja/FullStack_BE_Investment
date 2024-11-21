package com.billit.investment.domain;

import lombok.Getter;

@Getter
public enum InvestStatusType {
    WATING(0, "투자 희망", "투자 신청이 완료되어 대출군과의 매칭을 대기하는 상태"),
    EXECUTING(1, "투자 중", "상환-정산이 진행되고 있는 상태"),
    COMPLETED(2, "정산 완료", "마지막 정산이 완료된 상태"),
    CANCELED(3, "투자 취소", "투자자가 투자 희망 상태에서 투자 신청을 취소한 상태");

    private final Integer code;
    private final String description;
    private final String detail;

    InvestStatusType(Integer code, String description, String detail) {
        this.code = code;
        this.description = description;
        this.detail = detail;
    }

    public static InvestStatusType fromCode(Integer code) {
        for (InvestStatusType status : InvestStatusType.values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown code: " + code);
    }
}
