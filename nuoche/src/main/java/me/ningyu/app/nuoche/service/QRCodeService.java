package me.ningyu.app.nuoche.service;

import lombok.RequiredArgsConstructor;
import me.ningyu.app.nuoche.common.QRCodeUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.OutputStream;

@Service
@RequiredArgsConstructor
public class QRCodeService
{
    public OutputStream generateBase64String(String content) throws Exception
    {

        QRCodeUtils.encode(content, null);
        return null;
    }
}
