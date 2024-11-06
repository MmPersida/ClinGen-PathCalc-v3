package com.persida.pathogenicity_calculator.utils.constants;

import org.springframework.beans.factory.annotation.Value;

public class Constants {

    public static String HTTP_POST = "POST";
    public static String HTTP_GET = "GET";

    public static final String INDEX = "index";
    public static final String ADMIN = "admin";
    public static final String ERROR = "error";
    public static final String LOGIN = "login";

    public static final String UTF8 = "UTF-8";

    public static String CONTENT_TYPE = "Content-Type";
    public static String CONTENT_TYPE_TEXT_PLAIN = "text/plain";
    public static String CONTENT_TYPE_APP_FORM_URLENCODED = "application/x-www-form-urlencoded";
    public static String CONTENT_TYPE_APP_JSON = "application/json";
    public static String CONTENT_TYPE_APP_XML = "application/xml";

    public static String USER_ROLLE_ADMIN = "ADMIN";
    public static String USER_ROLLE_USER = "USER";

    public static String USERNAME_PLACEHOLDER = "USERNAME";

    //evidence types and modifiers
    public static String TYPE_BENIGN = "Benign";
    public static String TYPE_PATHOGENIC = "Pathogenic";
    public static String MODIFIER_SUPPORTING = "Supporting";
    public static String MODIFIER_MODERATE = "Moderate";
    public static String MODIFIER_STRONG = "Strong";
    public static String MODIFIER_VERY_STRONG = "Very Strong";
    public static String MODIFIER_STAND_ALONE = "Stand Alone";


    public static String ENGINES = "engines";
    public static String CONDITIONS = "conditions";

    public static String VAR_IDENTIFIER_RSID_dbSNP = "rsid";
    public static String VAR_IDENTIFIER_HGVS = "hgvs";
    public static String VAR_IDENTIFIER_ClinVar = "clinvar";
    public static String VAR_IDENTIFIER_ClinVarRCV = "clinvarRCV";
    public static String VAR_IDENTIFIER_GnomAD_2 = "gnomad_2";
    public static String VAR_IDENTIFIER_GnomAD_3 = "gnomad_3";
    public static String VAR_IDENTIFIER_GnomAD_4 = "gnomad_4";
    public static String VAR_IDENTIFIER_MyVarInfoHG38 = "myvariantinfohg38";
}
