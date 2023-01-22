package de.mherrmann.famkidmem.backend.utils;

public class ConversionUtil {

    public static String base64urlToBase64(String base64url) {
        return base64url.replace('_', '/').replace('-', '+');
    }

    public static String base64ToBase64url(String base64) {
        return base64.replace('/', '_').replace('+', '-');
    }

}
