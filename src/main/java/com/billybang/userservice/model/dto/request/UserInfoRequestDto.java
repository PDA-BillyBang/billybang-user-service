package com.billybang.userservice.model.dto.request;

import com.billybang.userservice.model.type.CompanySize;
import com.billybang.userservice.model.type.Occupation;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoRequestDto {

    private Occupation occupation;
    private CompanySize companySize;
    private Integer employmentDuration;
    private Integer individualIncome;
    private Integer individualAssets;
    private Integer totalMarriedIncome;
    private Integer totalMarriedAssets;
    private Integer childrenCount;
    private Boolean isForeign;
    private Boolean isFirstHouseBuyer;
    private Boolean isMarried;
    private Integer yearOfMarriage;
    private Boolean hasOtherLoans;
}
