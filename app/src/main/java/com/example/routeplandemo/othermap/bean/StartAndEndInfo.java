package com.example.routeplandemo.othermap.bean;

/**
 * 起始点信息
 *
 * @author zpan
 */
public class StartAndEndInfo {

    /** 起点名称 */
    private String startName;
    /** 起点坐标 */
    private MapLocation startLocation;
    /** 终点名称 */
    private String endName;
    /** 终点坐标 */
    private MapLocation endLocation;

    public StartAndEndInfo() {
    }

    public String getStartName() {
        return startName;
    }

    public void setStartName(String startName) {
        this.startName = startName;
    }

    public MapLocation getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(MapLocation startLocation) {
        this.startLocation = startLocation;
    }

    public String getEndName() {
        return endName;
    }

    public void setEndName(String endName) {
        this.endName = endName;
    }

    public MapLocation getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(MapLocation endLocation) {
        this.endLocation = endLocation;
    }

    @Override
    public String toString() {
        return "StartAndEndInfo{"
            + "startName='"
            + startName
            + '\''
            + ", fromLocation="
            + startLocation
            + ", endName='"
            + endName
            + '\''
            + ", to="
            + endLocation
            + '}';
    }
}
