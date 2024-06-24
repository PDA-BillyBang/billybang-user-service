package com.billybang.userservice.model.dto.request;

import com.billybang.userservice.model.type.CompanySize;
import com.billybang.userservice.model.type.Occupation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoRequestDto {

    @Schema(name = "occupation", example = "FINANCE")
    private Occupation occupation;

    @Schema(name = "companySize", example = "INTERMEDIATE")
    private CompanySize companySize;

    @Schema(name = "employmentDuration", example = "24")
    private Integer employmentDuration;

    @Schema(name = "individualIncome", example = "40")
    private Integer individualIncome;

    @Schema(name = "individualAssets", example = "100")
    private Integer individualAssets;

    @Schema(name = "totalMarriedIncome", example = "90")
    private Integer totalMarriedIncome;

    @Schema(name = "totalMarriedAssets", example = "300")
    private Integer totalMarriedAssets;

    @Schema(name = "childrenCount", example = "2")
    private Integer childrenCount;

    @Schema(name = "isForeign", example = "false")
    private Boolean isForeign;

    @Schema(name = "isFirstHouseBuyer", example = "true")
    private Boolean isFirstHouseBuyer;

    @Schema(name = "isMarried", example = "false")
    private Boolean isMarried;

    @Schema(name = "yearOfMarriage", example = "2015")
    private Integer yearOfMarriage;

    @Schema(name = "hasOtherLoans", example = "false")
    private Boolean hasOtherLoans;
}
