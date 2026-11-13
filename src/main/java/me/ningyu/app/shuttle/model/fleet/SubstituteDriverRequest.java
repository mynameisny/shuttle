package me.ningyu.app.shuttle.model.fleet;

import lombok.Getter;
import lombok.Setter;
import me.ningyu.app.shuttle.enums.SubstituteReason;

import java.time.LocalDate;

/**
 * 新增代班请求（某天某班次由替补司机代替）。
 */
@Getter
@Setter
public class SubstituteDriverRequest
{
    private LocalDate substituteDate;
    private Long substituteDriverId;
    private SubstituteReason reason;
}
