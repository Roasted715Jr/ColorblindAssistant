package com.roasted715jr.colorblindassistant;

import android.graphics.Color;

public class Colors {

    static boolean found = false;
    static float h = 1;        //100
    static float s = 0.75f;    //75
    static float f = 0.50f;     //50
    static float t = 0.25f;    //25

    public String getColorSimple(int r, int g, int b) {
        float[] hsv = new float[3];
        Color.colorToHSV(Color.rgb(r, g, b), hsv);

        int hue = (int) hsv[0];
        float saturation = hsv[1];
        float brightness = hsv[2];

        if (brightness < 0.15) return "Black";
        if (saturation < 0.3) return "White";

        if (hue >= 346 || hue <= 15) return "Red";
        if (hue >= 16 && hue <= 45) return "Orange";
        if (hue >= 46 && hue <= 75) return "Yellow";
        if (hue >= 76 && hue <= 105) return "Yellow-Green";
        if (hue >= 106 && hue <= 135) return "Lime";
        if (hue >= 136 && hue <= 165) return "Teal";
        if (hue >= 166 && hue <= 195) return "Turquoise";
        if (hue >= 196 && hue <= 225) return "Cerulean";
        if (hue >= 226 && hue <= 255) return "Blue";
        if (hue >= 256 && hue <= 285) return "Purple";
        if (hue >= 286 && hue <= 315) return "Magenta";
        if (hue >= 316 && hue <= 345) return "Hot Pink";

        return "";
    }

    public static String getColor(Color col) {
        float[] hsv = new float[3];
        Color.colorToHSV(Color.rgb(col.red(), col.green(), col.blue()), hsv);

        int hue = (int) hsv[0];
        float sat = hsv[1];
        float bri = hsv[2];

        String color = "";

        ColorHue red = new ColorHue("red", "red", "brown", "red", "red", "brown", "pink", "brown", "brown");

        ColorHue orange = new ColorHue(16, 45, "orange", "orange", "brown", "tan", "brown", "brown", "peach", "brown", "brown");
        ColorHue yellow = new ColorHue(46, 75, "yellow", "yellow-green", "yellow-green", "gold", "gold", "dark green", "peach", "tan", "gray");
        ColorHue yellowgreen = new ColorHue(76, 105, "yellow-green", "green", "dark-green", "green", "green", "dark green", "light green", "light gray", "gray");
        ColorHue lightgreen = new ColorHue(106, 135, "light green", "green", "dark green", "green", "green", "dark green", "light gray", "blue-green", "gray");
        ColorHue teal = new ColorHue(136, 165, "teal", "blue-green", "dark blue", "turquoise", "blue-green", "dark blue", "blue-green", "blue-green", "gray");
        ColorHue turquoise = new ColorHue(166, 195, "turquoise", "blue-green", "blue-green", "light blue", "blue-green", "dark blue", "light blue", "blue-green", "gray");
        ColorHue lightblue = new ColorHue(196, 225, "light blue", "blue", "dark blue", "light blue", "blue", "dark blue", "light blue", "blue", "gray");
        ColorHue blue = new ColorHue(226, 255, "blue", "blue", "dark blue", "purple", "indigo", "dark blue", "pink", "purple", "gray");
        ColorHue purple = new ColorHue(256, 285, "purple", "purple", "indigo", "purple", "purple", "indigo", "pink", "purple", "indigo");
        ColorHue magenta = new ColorHue(286, 315, "magenta", "purple", "dark purple", "violet-red", "purple", "dark purple", "pink", "purple", "dark purple");
        ColorHue hotpink = new ColorHue(316, 345, "hot pink", "purple", "dark purple", "purple", "purple", "dark purple", "pink", "purple", "dark purple");

        found = false;

        if (hsv[2] < t) return "black";
        if (hsv[1] < t) {
            if (hsv[2] <= h && hsv[2] > s) return "white";
            if (hsv[2] <= s && hsv[2] > t) return "gray";
        }

        if (hue >= 346 || hue <= 15)
        {
            found = true;

            if (sat <= h && sat > s)      //saturation 100
            {
                if (bri <= h && bri > s) return red.sat100bri100;
                if (bri <= s && bri > f) return red.sat100bri75;
                if (bri <= f && bri > t) return red.sat100bri50;
            }

            if (sat <= s && sat > f)    //saturation 75
            {
                if (bri <= h && bri > s) return red.sat75bri100;
                if (bri <= s && bri > f) return red.sat75bri75;
                if (bri <= f && bri > t) return red.sat75bri50;
            }

            if (sat <= f && sat > t)    //saturation 50
            {
                if (bri <= h && bri > s) return red.sat50bri100;
                if (bri <= s && bri > f) return red.sat50bri75;
                if (bri <= f && bri > t) return red.sat50bri50;
            }
        }

        if (!found) color = processColor(col, orange);
        if (!found) color = processColor(col, yellow);
        if (!found) color = processColor(col, yellowgreen);
        if (!found) color = processColor(col, lightgreen);
        if (!found) color = processColor(col, teal);
        if (!found) color = processColor(col, turquoise);
        if (!found) color = processColor(col, lightblue);
        if (!found) color = processColor(col, blue);
        if (!found) color = processColor(col, purple);
        if (!found) color = processColor(col, magenta);
        if (!found) color = processColor(col, hotpink);

        return color;
    }

    static String processColor(Color col, ColorHue colorHue) {
        float[] hsv = new float[3];
        Color.colorToHSV(Color.rgb(col.red(), col.green(), col.blue()), hsv);

        int hue = (int) hsv[0];
        float sat = hsv[1];
        float bri = hsv[2];

        if(hue >= colorHue.hueMin && hue <= colorHue.hueMax)
        {
            found = true;

            if (sat <= h && sat > s)      //saturation 100
            {
                if (bri <= h && bri > s) return colorHue.sat100bri100;
                if (bri <= s && bri > f) return colorHue.sat100bri75;
                if (bri <= f && bri > t) return colorHue.sat100bri50;
            }

            if (sat <= s && sat > f)    //saturation 75
            {
                if (bri <= h && bri > s) return colorHue.sat75bri100;
                if (bri <= s && bri > f) return colorHue.sat75bri75;
                if (bri <= f && bri > t) return colorHue.sat75bri50;
            }

            if (sat <= f && sat > t)    //saturation 50
            {
                if (bri <= h && bri > s) return colorHue.sat50bri100;
                if (bri <= s && bri > f) return colorHue.sat50bri75;
                if (bri <= f && bri > t) return colorHue.sat50bri50;
            }
        }

        return "";
    }
}
