package com.example.routeplandemo.utils;

import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.LatLonPoint;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 类说明
 *
 * @author renjialiang
 * @version [版本]
 * @see [相关类]
 * @since [模块]
 */
public class AMapUtil {

    public static LatLng convertToLatLng(LatLonPoint point) {
        return new LatLng(point.getLatitude(), point.getLongitude());
    }

    public static List<LatLng> convertToLatLngList(List<LatLonPoint> pointList) {
        List<LatLng> resultList = new ArrayList<>();
        if (pointList == null) {
            return resultList;
        }
        for (LatLonPoint point : pointList) {
            resultList.add(new LatLng(point.getLatitude(), point.getLongitude()));
        }
        return resultList;
    }
    public static String getFriendlyTime(int second) {
        if (second > 3600) {
            int hour = second / 3600;
            int miniate = (second % 3600) / 60;
            return hour + "小时" + miniate + "分钟";
        }
        if (second >= 60) {
            int miniate = second / 60;
            return miniate + "分钟";
        }
        return second + "秒";
    }
    public static String getFriendlyLength(int lenMeter) {
        if (lenMeter > 10000) // 10 km
        {
            int dis = lenMeter / 1000;
            return dis + ChString.Kilometer;
        }

        if (lenMeter > 1000) {
            float dis = (float) lenMeter / 1000;
            DecimalFormat fnum = new DecimalFormat("##0.0");
            String dstr = fnum.format(dis);
            return dstr + ChString.Kilometer;
        }

        if (lenMeter > 100) {
            int dis = lenMeter / 50 * 50;
            return dis + ChString.Meter;
        }

        int dis = lenMeter / 10 * 10;
        if (dis == 0) {
            dis = 10;
        }

        return dis + ChString.Meter;
    }
}
