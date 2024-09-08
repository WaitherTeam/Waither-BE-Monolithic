package com.waither.domain.user.entity;

import com.waither.domain.user.entity.enums.Season;
import com.waither.global.entity.BaseEntity;
import com.waither.global.exception.CustomException;
import com.waither.domain.user.exception.UserErrorCode;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "user_data")
@Entity
public class UserData extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 각 답변의 평균 값 (level 1 쪽이 추움 ~ level 5 쪽이 더움)
    private Double level1;
    private Double level2;
    private Double level3;
    private Double level4;
    private Double level5;

    // 계절
    @Enumerated(EnumType.STRING)
    private Season season;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public void setUser(User user) {
        this.user = user;
    }
    public Double getLevel(int level) {
        return switch (level) {
            case 1 -> level1;
            case 2 -> level2;
            case 3 -> level3;
            case 4 -> level4;
            case 5 -> level5;
            default -> throw new CustomException(UserErrorCode.INVALID_LEVEL_VALUE);
        };
    }

    public void updateLevelValue(int level, double newValue) {
        switch (level) {
            case 1:
                this.level1 = newValue;
                break;
            case 2:
                this.level2 = newValue;
                break;
            case 3:
                this.level3 = newValue;
                break;
            case 4:
                this.level4 = newValue;
                break;
            case 5:
                this.level5 = newValue;
                break;
            default:
                throw new CustomException(UserErrorCode.INVALID_LEVEL_VALUE);
        }
    }

}