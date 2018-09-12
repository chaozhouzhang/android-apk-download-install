package me.chaozhouzhang.apkdownloadinstall.download;

import java.math.BigDecimal;

/**
 * Created on 18/9/12 15:31
 *
 * @author zhangchaozhou
 */
public class MathUtils {
    /**
     * 除法
     *
     * @param v1
     * @param v2
     * @param scale
     * @return
     */
    public static double div(double v1, double v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(" the scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }


    /**
     * 计算百分比
     *
     * @param v1
     * @param v2
     * @return
     */
    public static int getPercent(double v1, double v2) {
        return (int) (div(v1, v2, 2) * 100);
    }
}
