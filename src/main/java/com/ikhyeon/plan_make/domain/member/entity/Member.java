package com.ikhyeon.plan_make.domain.member.entity;

import com.ikhyeon.plan_make.common.entity.BaseEntity;
import com.ikhyeon.plan_make.domain.member.vo.SocialProvider;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SocialProvider socialProvider;

    @Column(nullable = false)
    private String socialId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String name;

    private String profileImageUrl;

    @Column(nullable = false)
    private Integer dailyPlanCreationCount = 0;

    @Column(nullable = false)
    private LocalDate lastPlanCreationDate;

    // 비즈니스 로직

    public void incrementPlanCreationCount() {
        LocalDate today = LocalDate.now();

        // 현재 상태에서 신규 계획 생성 가능한지 확인
        if (today.equals(this.lastPlanCreationDate) && this.dailyPlanCreationCount >= 3) {
            throw new IllegalStateException("일일 계획 생성 한도를 초과했습니다.");
        }

        // 마지막 계획 생성 날짜가 과거라면 카운트 초기화
        if (!today.equals(this.lastPlanCreationDate)) {
            resetDailyPlanCount();
        }

        this.dailyPlanCreationCount++;
        this.lastPlanCreationDate = today;
    }

    private void resetDailyPlanCount() {
        this.dailyPlanCreationCount = 0;
        this.lastPlanCreationDate = LocalDate.now();
    }
}