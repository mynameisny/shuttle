package me.ningyu.app.shuttle.model.agency;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAgencyRequest
{
    private String agencyName;

    private String agencyUrl;

    private String agencyTimezone;

    private String agencyLang;

    private String agencyPhone;

    private String agencyEmail;

    private String agencyFareUrl;
}