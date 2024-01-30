package com.persida.pathogenicity_calculator.utils;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class URIEncoder {
    private static final String CHARSET = StandardCharsets.UTF_8.name();

    private static final String[][] CHARACTERS = {
            { "\\+", "%20" },
            { "%21", "!"   },
            { "%27", "'"   },
            { "%28", "("   },
            { "%29", ")"   },
            { "%7E", "~"   }
    };

    public static String encodeURIComponent(String text) {
        String result;
        try {
            result = URLEncoder.encode(text, CHARSET);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        for (String[] entry : CHARACTERS) {
            result = result.replaceAll(entry[0], entry[1]);
        }
        return result;
    }
}
