package com.aps.Visitors_form.utils;

import org.springframework.stereotype.Component;
import java.util.Base64;

@Component
public class Base64ImageEncoder {
    public String encodeImage(byte[] imageBytes) {
        return Base64.getEncoder().encodeToString(imageBytes);
    }
}

