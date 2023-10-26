package me.ningyu.app.nuoche.config;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Tess4jConfiguration
{
    @Bean
    public ITesseract tesseract()
    {
        ITesseract instance = new Tesseract();

        // 设置语言库位置
        instance.setDatapath("src/main/resources/tessdata");

        // 设置语言：chi_sim/eng
        String language = "chi_sim";
        instance.setLanguage(language);

        return instance;
    }

}
