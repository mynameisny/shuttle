package me.ningyu.app.easymonger.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OcrCoordinate
{
    private String field;

    private String position;

    private List<Character> deleteChars;

    private List<String> deleteRegex;
}
