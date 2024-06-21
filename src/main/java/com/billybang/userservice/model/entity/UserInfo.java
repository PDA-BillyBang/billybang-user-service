package com.billybang.userservice.model.entity;

import com.billybang.userservice.model.dto.request.UserInfoRequestDto;
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

    @Column(name = "individual_assets")
    private Integer individualAssets;

    @Column(name = "total_married_income")
    private Integer totalMarriedIncome;

    @Column(name = "total_married_assets")
    private Integer totalMarriedAssets;

    @Column(name = "children_count")
    private Integer childrenCount;

    @Column(name = "is_foreign")
    private Boolean isForeign;

    @Column(name = "is_first_house_buyer")
    private Boolean isFirstHouseBuyer;

    @Column(name = "is_married")
    private Boolean isMarried;

    @Column(name = "year_of_marriage")
    private Integer yearOfMarriage;

    @Column(name = "has_other_loans")
    private Boolean hasOtherLoans;

    public void update(UserInfoRequestDto dto) {
        this.occupation = dto.getOccupation();
        this.companySize = dto.getCompanySize();
        this.employmentDuration = dto.getEmploymentDuration();
        this.individualIncome = dto.getIndividualIncome();
        this.individualAssets = dto.getIndividualAssets();
        this.totalMarriedIncome = dto.getTotalMarriedIncome();
        this.totalMarriedAssets = dto.getTotalMarriedAssets();
        this.childrenCount = dto.getChildrenCount();
        this.isForeign = dto.getIsForeign();
        this.isFirstHouseBuyer = dto.getIsFirstHouseBuyer();
        this.isMarried = dto.getIsMarried();
        this.yearOfMarriage = dto.getYearOfMarriage();
        this.hasOtherLoans = dto.getHasOtherLoans();
    }

}
