package com.billybang.userservice.model.entity;

import com.billybang.userservice.model.type.CompanySize;
import com.billybang.userservice.model.type.Occupation;
import com.billybang.userservice.model.vo.BaseTime;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "user_infos")
public class UserInfo extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_info_id")
    private Long id;

    @Setter
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column
    @Enumerated(EnumType.STRING)
    private Occupation occupation;

    @Column(name = "company_size")
    @Enumerated(EnumType.STRING)
    private CompanySize companySize;

    @Column(name = "employment_duration")
    private Integer employmentDuration;

    @Column(name = "individual_income")
    private Integer individualIncome;

    @Column(name = "total_married_income")
    private Integer totalMarriedIncome;

    @Column(name = "children_count")
    private Integer childrenCount;

    @Column(name = "is_foreign")
    private Boolean isForeign;

    @Column(name = "is_first_house_buyer")
    private Boolean isFirstHouseBuyer;

    @Column(name = "is_married")
    private Boolean isMarried;

    @Column(name = "is_newly_married")
    private Boolean isNewlyMarried;

    @Column(name = "has_other_loans")
    private Boolean hasOtherLoans;

}
