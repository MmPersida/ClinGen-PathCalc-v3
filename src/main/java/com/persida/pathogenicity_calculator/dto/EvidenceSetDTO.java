package com.persida.pathogenicity_calculator.dto;

import lombok.Data;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class EvidenceSetDTO {

    @Nullable
    @Pattern(regexp = "^[01S]$", message = "BP1 must fit the format.")
    private Character bp1;

    @Nullable
    @Pattern(regexp = "^[01S]$", message = "BP2 must fit the format.")
    private Character bp2;

    @Nullable
    @Pattern(regexp = "^[01S]$", message = "BP3 must fit the format.")
    private Character bp3;

    @Nullable
    @Pattern(regexp = "^[01S]$", message = "BP4 must fit the format.")
    private Character bp4;

    @Nullable
    @Pattern(regexp = "^[01S]$", message = "BP5 must fit the format.")
    private Character bp5;

    @Nullable
    @Pattern(regexp = "^[01S]$", message = "BP6 must fit the format.")
    private Character bp6;

    @Nullable
    @Pattern(regexp = "^[01S]$", message = "BP7 must fit the format.")
    private Character bp7;

    @Nullable
    @Pattern(regexp = "^[01P]$", message = "BS1 must fit the format.")
    private Character bs1;

    @Nullable
    @Pattern(regexp = "^[01P]$", message = "BS2 must fit the format.")
    private Character bs2;

    @Nullable
    @Pattern(regexp = "^[01P]$", message = "BS3 must fit the format.")
    private Character bs3;

    @Nullable
    @Pattern(regexp = "^[01P]$", message = "BS4 must fit the format.")
    private Character bs4;

    @Nullable
    @Pattern(regexp = "^[TF]$", message = "BA1 must fit the format.")
    private Character ba1;

    @Nullable
    @Pattern(regexp = "^[01MSV]$", message = "PP1 must fit the format.")
    private Character pp1;

    @Nullable
    @Pattern(regexp = "^[01MSV]$", message = "PP2 must fit the format.")
    private Character pp2;

    @Nullable
    @Pattern(regexp = "^[01MSV]$", message = "PP3 must fit the format.")
    private Character pp3;

    @Nullable
    @Pattern(regexp = "^[01MSV]$", message = "PP4 must fit the format.")
    private Character pp4;

    @Nullable
    @Pattern(regexp = "^[01MSV]$", message = "PP5 must fit the format.")
    private Character pp5;

    @Nullable
    @Pattern(regexp = "^[01PSV]$", message = "PM1 must fit the format.")
    private Character pm1;

    @Nullable
    @Pattern(regexp = "^[01PSV]$", message = "PM2 must fit the format.")
    private Character pm2;

    @Nullable
    @Pattern(regexp = "^[01PSV]$", message = "PM3 must fit the format.")
    private Character pm3;

    @Nullable
    @Pattern(regexp = "^[01PSV]$", message = "PM4 must fit the format.")
    private Character pm4;

    @Nullable
    @Pattern(regexp = "^[01PSV]$", message = "PM5 must fit the format.")
    private Character pm5;

    @Nullable
    @Pattern(regexp = "^[01PSV]$", message = "PM6 must fit the format.")
    private Character pm6;

    @Nullable
    @Pattern(regexp = "^[01PMV]$", message = "PS1 must fit the format.")
    private Character ps1;

    @Nullable
    @Pattern(regexp = "^[01PMV]$", message = "PS2 must fit the format.")
    private Character ps2;

    @Nullable
    @Pattern(regexp = "^[01PMV]$", message = "PS3 must fit the format.")
    private Character ps3;

    @Nullable
    @Pattern(regexp = "^[01PMV]$", message = "PS4 must fit the format.")
    private Character ps4;

    @Nullable
    @Pattern(regexp = "^[01PMS]$", message = "PVS1 must fit the format.")
    private Character pvs1;

}
