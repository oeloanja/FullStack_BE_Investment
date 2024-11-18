package com.billit.investment.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class InvestStatus {
    @Id
    private Integer investmentId;

    @Enumerated(EnumType.STRING)
    private InvestStatusType investStatusType;

    public enum InvestStatusType {
        WATING("투자 희망", "투자 신청이 완료되어 대출군과의 매칭을 대기하는 상태"),
        EXECUTING("투자 중", "상환-정산이 진행되고 있는 상태"),
        COMPLETED("정산 완료", "마지막 정산이 완료된 상태"),
        CANCELED("투자 취소", "투자자가 투자 희망 상태에서 투자 신청을 취소한 상태");

        private final String description;
        private final String detail;

        InvestStatusType(String description, String detail) {
            this.description = description;
            this.detail = detail;
        }
    }
}


