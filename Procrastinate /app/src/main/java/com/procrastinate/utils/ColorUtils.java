package com.procrastinate.utils;

import java.util.ArrayList;
import java.util.List;

public class ColorUtils {

    private static int[] color = {0xFFF48FB1, 0xFF90CAF9, 0xFFA5D6A7, 0xFFF44336, 0xFFE91E63,
            0xFF9C27B0, 0xFF673AB7, 0xFF3F51B5, 0xFF2196F3, 0xFF03A9F4, 0xFF00BCD4, 0xFF009688};

    public static List<Integer> getColorList(int count) {
        List<Integer> colors = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            colors.add(color[i % color.length]);
        }
        return colors;
    }
}
