package com.waither.domain.user.entity;

import com.waither.domain.user.entity.enums.Season;
import com.waither.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "survey")
@Entity
public class Survey extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 체감 온도
    private Double temp;

    // 답변 (1~5 정도로 저장)
    private Integer ans; // 답변을 숫자로 저장

    // 답변 시각
    private LocalDateTime time;

    // 계절
    @Enumerated(EnumType.STRING)
    private Season season;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // 연관관계 설정
    public void setUser(User setUser) {
        user = setUser;
    }

}
