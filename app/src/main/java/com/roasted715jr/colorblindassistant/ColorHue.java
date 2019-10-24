package com.roasted715jr.colorblindassistant;

public class ColorHue {
    public int hueMin;
    public int hueMax;
    public String sat100bri100;
    public String sat100bri75;
    public String sat100bri50;
    public String sat75bri100;
    public String sat75bri75;
    public String sat75bri50;
    public String sat50bri100;
    public String sat50bri75;
    public String sat50bri50;

    public ColorHue(int min, int max, String hh, String hs, String hf, String sh, String ss, String sf, String fh, String fs, String ff) {
        hueMin = min;
        hueMax = max;
        sat100bri100 = hh;
        sat100bri75 = hs;
        sat100bri50 = hf;
        sat75bri100 = sh;
        sat75bri75 = ss;
        sat75bri50 = sf;
        sat50bri100 = fh;
        sat50bri75 = fs;
        sat50bri50 = ff;
    }

    //for red only
    public ColorHue(String hh, String hs, String hf, String sh, String ss, String sf, String fh, String fs, String ff) {
        sat100bri100 = hh;
        sat100bri75 = hs;
        sat100bri50 = hf;
        sat75bri100 = sh;
        sat75bri75 = ss;
        sat75bri50 = sf;
        sat50bri100 = fh;
        sat50bri75 = fs;
        sat50bri50 = ff;
    }
}
