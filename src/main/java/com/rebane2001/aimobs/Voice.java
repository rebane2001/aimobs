package com.rebane2001.aimobs;

import java.util.List;

public class Voice {
    private List<String> languageCodes;
    private String name;
    private String ssmlGender;
    private int naturalSampleRateHertz;

    public Voice(List<String> languageCodes, String name, String ssmlGender, int naturalSampleRateHertz) {
        this.languageCodes = languageCodes;
        this.name = name;
        this.ssmlGender = ssmlGender;
        this.naturalSampleRateHertz = naturalSampleRateHertz;
    }

    public List<String> getLanguageCodes() {
        return languageCodes;
    }

    public void setLanguageCodes(List<String> languageCodes) {
        this.languageCodes = languageCodes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSsmlGender() {
        return ssmlGender;
    }

    public void setSsmlGender(String ssmlGender) {
        this.ssmlGender = ssmlGender;
    }

    public int getNaturalSampleRateHertz() {
        return naturalSampleRateHertz;
    }

    public void setNaturalSampleRateHertz(int naturalSampleRateHertz) {
        this.naturalSampleRateHertz = naturalSampleRateHertz;
    }
}
