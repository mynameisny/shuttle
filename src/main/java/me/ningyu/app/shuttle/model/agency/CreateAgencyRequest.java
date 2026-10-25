package me.ningyu.app.shuttle.model.agency;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAgencyRequest
{
    @NotBlank(message = "agency_id不能为空")
    private String agencyId;

    @NotBlank(message = "agency_name不能为空")
    private String agencyName;

    @NotBlank(message = "agency_url不能为空")
    private String agencyUrl;

    @Builder.Default
    private String agencyTimezone = "Asia/Shanghai";

    @Builder.Default
    private String agencyLang = "zh";

    private String agencyPhone;

    private String agencyEmail;

    private String agencyFareUrl;
}