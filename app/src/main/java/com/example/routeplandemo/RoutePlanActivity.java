package com.example.routeplandemo;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.model.AMapGestureListener;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Poi;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.DriveStep;
import com.amap.api.services.route.RidePath;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.TMC;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.example.routeplandemo.R;
import com.example.routeplandemo.othermap.OtherMapManager;
import com.example.routeplandemo.othermap.bean.MapLocation;
import com.example.routeplandemo.othermap.bean.StartAndEndInfo;
import com.example.routeplandemo.utils.AMapUtil;
import com.amap.api.navi.view.PoiInputItemWidget;
import java.util.List;

/**
 * 类说明
 *
 * @author renjialiang
 * @version [版本]
 * @see [相关类]
 * @since [模块]
 */
public class RoutePlanActivity extends AppCompatActivity implements RouteSearch.OnRouteSearchListener, GeocodeSearch.OnGeocodeSearchListener {

    private TextureMapView mapView;
    private Button btn_drive;
    private Button btn_walk;
    private Button btn_bus;
    private Button rideMode;
    private ImageView mImageViewBtn;
    private final int TYPE_DRIVE = 100;
    private final int TYPE_BUS = 101;
    private final int TYPE_WALK = 102;
    private final int TYPE_RIDE = 103;
    private int mSelectedType = TYPE_DRIVE;
    private AMap aMap;

    //    private LatLonPoint startPoint = new LatLonPoint(39.742295, 116.235891);
//    private LatLonPoint endPoint = new LatLonPoint(39.995576, 116.481288);
    private LatLonPoint startPoint = new LatLonPoint(39.890868, 116.467627);
    private LatLonPoint endPoint = new LatLonPoint(39.837478, 116.471268);
    private String endName;
    private String startName;
    private RouteSearch routeSearch;
    private boolean FirstLocate = true;
    private SensorManager mSensorManager;
    private GeocodeSearch geocodeSearch;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_search);

        mapView = (TextureMapView) findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState); //此方法必须重写
        btn_drive = (Button) findViewById(R.id.btn_drive);
        btn_walk = (Button) findViewById(R.id.btn_walk);
        btn_bus = (Button) findViewById(R.id.btn_bus);
        rideMode = (Button) findViewById(R.id.btn_ride);
        mImageViewBtn = (ImageView) findViewById(R.id.route_plan_loca_btn);
        mImageViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocaBtnOnclick();
            }
        });
        init();
        initSensor();
        addMarkers();
    }

    private void initSensor() {
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorManager.registerListener(mSensorListner,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_UI);
    }

    private MyLocationStyle mLocationStyle;
    private SensorEventListener mSensorListner = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float degree = event.values[0];
            mDegree = degree;
//            xLog.D("degree:"+mDegree);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    private void getAddressByLatlng(LatLng latLng) {
        //逆地理编码查询条件：逆地理编码查询的地理坐标点、查询范围、坐标类型。
        LatLonPoint latLonPoint = new LatLonPoint(latLng.latitude, latLng.longitude);
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 500f, GeocodeSearch.AMAP);
        //异步查询
        geocodeSearch.getFromLocationAsyn(query);
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
//        RegeocodeAddress regeocodeAddress = regeocodeResult.getRegeocodeAddress();
//        String formatAddress = regeocodeAddress.getFormatAddress();
//        startName = formatAddress.substring(9);

        if (i == 1000) {
            if (regeocodeResult != null && regeocodeResult.getRegeocodeAddress() != null
                    && regeocodeResult.getRegeocodeAddress().getFormatAddress() != null) {
                String addressName = regeocodeResult.getRegeocodeAddress().getFormatAddress(); // 逆转地里编码不是每次都可以得到对应地图上的opi
                startName = addressName;
                //search();
                Toast.makeText(RoutePlanActivity.this, addressName,
                        Toast.LENGTH_SHORT).show();
                // L.d("逆地理编码回调  得到的地址：" + addressName);
                // mAddressEntityFirst = new AddressSearchTextEntity(addressName, addressName, true, convertToLatLonPoint(mFinalChoosePosition));

            } else {
                Toast.makeText(RoutePlanActivity.this, "没有结果",
                        Toast.LENGTH_SHORT).show();
            }
        } else if (i == 27) {
            Toast.makeText(RoutePlanActivity.this, "网络错误",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(RoutePlanActivity.this, "未知错误",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
        GeocodeAddress regeocodeAddress = geocodeResult.getGeocodeAddressList().get(0);
        String formatAddress = regeocodeAddress.getFormatAddress();
    }

    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        /**   定位模式   **/
        mLocationStyle = new MyLocationStyle();
        mLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
        mLocationStyle.interval(2000);
        aMap.setMyLocationStyle(mLocationStyle);
        aMap.setMyLocationEnabled(true);
        aMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        /**   监听   **/
        aMap.setOnMyLocationChangeListener(new AMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                if (FirstLocate) {
                    LocaBtnOnclick();
                    FirstLocate = false;
                    startPoint = new LatLonPoint(location.getLatitude(), location.getLongitude());
                    getAddressByLatlng(new LatLng(location.getLatitude(), location.getLongitude()));
                }
            }
        });
        aMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
//                xLog.D("onCameraChange");
            }

            @Override
            public void onCameraChangeFinish(CameraPosition cameraPosition) {
//                mBtnState=BTN_STATE_NOR;
//                LocateBtnUIChagen();
            }
        });
        aMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return false;
            }
        });
        aMap.setAMapGestureListener(new AMapGestureListener() {
            @Override
            public void onDoubleTap(float v, float v1) {
            }

            @Override
            public void onSingleTap(float v, float v1) {
            }

            @Override
            public void onFling(float v, float v1) {
            }

            @Override
            public void onScroll(float v, float v1) {
            }

            @Override
            public void onLongPress(float v, float v1) {
            }

            @Override
            public void onDown(float v, float v1) {
            }

            @Override
            public void onUp(float v, float v1) {
                mBtnState = BTN_STATE_NOR;
                LocateBtnUIChagen();
            }

            @Override
            public void onMapStable() {
            }
        });
        routeSearch = new RouteSearch(this);
        routeSearch.setRouteSearchListener(this);
        geocodeSearch = new GeocodeSearch(this);
        geocodeSearch.setOnGeocodeSearchListener(this);

    }

    private final int BTN_STATE_NOR = 100;
    private final int BTN_STATE_LOCATE = 101;
    private final int BTN_STATE_DIRE = 102;
    private int mBtnState = BTN_STATE_NOR;

    public void LocaBtnOnclick() {
        FirstLocate = true;
        if (mBtnState == BTN_STATE_NOR) {
            aMap.setMapType(AMap.MAP_TYPE_NORMAL);
            changeMapLevelAndAngle(16, 0);
            mBtnState = BTN_STATE_LOCATE;
            LocateBtnUIChagen();
        } else if (mBtnState == BTN_STATE_LOCATE) {
            aMap.setMapType(AMap.MAP_TYPE_NORMAL);
            changeMapLevelAndAngle(18, 40);
            mBtnState = BTN_STATE_DIRE;
            LocateBtnUIChagen();
        } else if (mBtnState == BTN_STATE_DIRE) {
            aMap.setMapType(AMap.MAP_TYPE_NORMAL);
            changeMapLevelAndAngle(16, 0);
            mBtnState = BTN_STATE_NOR;
            LocateBtnUIChagen();
        }
    }

    private void LocateBtnUIChagen() {
        if (mBtnState == BTN_STATE_NOR) {
            mImageViewBtn.setImageResource(R.drawable.icon_c34);
        } else if (mBtnState == BTN_STATE_LOCATE) {
            mImageViewBtn.setImageResource(R.drawable.icon_c34_b);
        } else if (mBtnState == BTN_STATE_DIRE) {
            mImageViewBtn.setImageResource(R.drawable.icon_c34_a);
        }
    }

    private float mDegree = 0f;

    private void changeMapLevelAndAngle(final int lv, final int angle) {
        CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLngZoom(
                new LatLng(aMap.getMyLocation().getLatitude(), aMap.getMyLocation().getLongitude())
                , lv);
        startPoint = new LatLonPoint(aMap.getMyLocation().getLatitude(), aMap.getMyLocation().getLongitude());
        aMap.animateCamera(mCameraUpdate, new AMap.CancelableCallback() {
            @Override
            public void onFinish() {
                aMap.animateCamera(CameraUpdateFactory.changeTilt(angle), new AMap.CancelableCallback() {
                    @Override
                    public void onFinish() {
                        if (lv > 17) {
                            CameraUpdate cameraUpdate = CameraUpdateFactory.changeBearing(mDegree);
                            aMap.animateCamera(cameraUpdate);
                        } else {
                            CameraUpdate cameraUpdate = CameraUpdateFactory.changeBearing(0);
                            aMap.animateCamera(cameraUpdate);
                        }

                    }

                    @Override
                    public void onCancel() {
                    }
                });
            }

            @Override
            public void onCancel() {
            }
        });
    }

    Marker start;
    Marker end;

    /**
     * 添加标记.
     */
    private void addMarkers() {
        aMap.clear();
        LatLng start = new LatLng(startPoint.getLatitude(), startPoint.getLongitude());
        MarkerOptions startos = new MarkerOptions()
                .position(start)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.start));
        aMap.addMarker(startos);
        LatLng end = new LatLng(endPoint.getLatitude(), endPoint.getLongitude());
//        RouteSearch routeSearch = new RouteSearch(this);
//        routeSearch.setRouteSearchListener(this);
//        RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(AMapServicesUtil.convertToLatLonPoint(start), AMapServicesUtil.convertToLatLonPoint(end));
//        RouteSearch.DriveRouteQuery dquery = new RouteSearch.DriveRouteQuery(fromAndTo, RouteSearch.DRIVING_MULTI_STRATEGY_FASTEST_SHORTEST, null, null, "");
//        routeSearch.calculateDriveRouteAsyn(dquery);
        MarkerOptions endos = new MarkerOptions()
                .position(end)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.end));
        aMap.addMarker(endos);
    }

    public void addMarkers(View view) {
        addMarkers();
    }

    public void daohang(View view) {
        StartAndEndInfo entity = new StartAndEndInfo();
        MapLocation end = new MapLocation(endPoint.getLongitude(), endPoint.getLatitude());
        MapLocation start = new MapLocation(startPoint.getLongitude(), startPoint.getLatitude());
        entity.setStartName(startName);
        entity.setStartLocation(start);
        entity.setEndName(endName);
        entity.setEndLocation(end);
        OtherMapManager otherMapManager = new OtherMapManager(RoutePlanActivity.this, entity);
        otherMapManager.navigation();
    }

    /**
     * 驾车模式.
     *
     * @param view view
     */
    public void driveMode(View view) {
        mSelectedType = TYPE_DRIVE;
        RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
                startPoint, endPoint);
        RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(
                fromAndTo, //路径规划的起点和终点
                RouteSearch.DrivingDefault, //驾车模式
                null, //途经点
                null, //示避让区域
                "" //避让道路
        );
        routeSearch.calculateDriveRouteAsyn(query);
    }

    /**
     * 步行模式.
     *
     * @param view view
     */
    public void walkMode(View view) {
        mSelectedType = TYPE_WALK;
        RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
                startPoint, endPoint);
        RouteSearch.WalkRouteQuery query = new RouteSearch.WalkRouteQuery(
                fromAndTo, RouteSearch.WalkDefault);
        routeSearch.calculateWalkRouteAsyn(query);
    }

    public void xqidiang(View view) {
        Intent sintent = new Intent(RoutePlanActivity.this, SearchPoiActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("pointType", PoiInputItemWidget.TYPE_START);
        sintent.putExtras(bundle);
        startActivityForResult(sintent, 100);
    }

    public void xzdiang(View view) {
        Intent eintent = new Intent(RoutePlanActivity.this, SearchPoiActivity.class);
        Bundle ebundle = new Bundle();
        ebundle.putInt("pointType", PoiInputItemWidget.TYPE_DEST);
        eintent.putExtras(ebundle);
        startActivityForResult(eintent, 200);
    }

    /**
     * 公交模式.
     *
     * @param view view
     */
    public void busMode(View view) {
        mSelectedType = TYPE_BUS;
        RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
                startPoint, endPoint);
        RouteSearch.BusRouteQuery query = new RouteSearch.BusRouteQuery(
                fromAndTo, //路径规划的起点和终点
                RouteSearch.BusDefault, //公交查询模式
                "010", //公交查询城市区号
                0 //是否计算夜班车，0表示不计算
        );
        routeSearch.calculateBusRouteAsyn(query);
    }

    /**
     * 骑行模式.
     *
     * @param view view
     */
    public void rideMode(View view) {
        mSelectedType = TYPE_RIDE;
        RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
                startPoint, endPoint);
        RouteSearch.RideRouteQuery query = new RouteSearch.RideRouteQuery(
                fromAndTo, RouteSearch.RidingDefault);
        routeSearch.calculateRideRouteAsyn(query);
    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult result, int errorCode) {
        aMap.clear();// 清理地图上的所有覆盖物
        if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
                    DrivePath drivePath = result.getPaths().get(0);
                    DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(
                            RoutePlanActivity.this, aMap, drivePath,
                            result.getStartPos(),
                            result.getTargetPos(), null);
//                    for (int i = 0; i < mDriveRouteResult.getPaths().size(); i++) {
//                        updatePathGeneral(mDriveRouteResult.getPaths().get(i), i);
//                    }
                    String dur = "", dis = "";
                    dur = AMapUtil.getFriendlyTime((int) drivePath.getDuration());
                    dis = AMapUtil.getFriendlyLength((int) drivePath.getDistance());
                    switch (mSelectedType) {
                        case TYPE_DRIVE:
                            btn_drive.setText("驾车时间" + dur + "\n距离" + dis);
                            break;
                        case TYPE_BUS:
                            btn_bus.setText("驾车时间" + dur + "\n距离" + dis);
                            break;
                        case TYPE_WALK:
                            btn_walk.setText("驾车时间" + dur + "\n距离" + dis);
                            break;
                        case TYPE_RIDE:
                            rideMode.setText("驾车时间" + dur + "\n距离" + dis);
                            break;
                    }
                    drivingRouteOverlay.setNodeIconVisibility(false);//设置节点marker是否显示
                    drivingRouteOverlay.removeFromMap();
                    drivingRouteOverlay.addToMap();
                    drivingRouteOverlay.zoomToSpan();
                } else {
                    Toast.makeText(RoutePlanActivity.this, "对不起，没有搜索到相关数据",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(RoutePlanActivity.this, "对不起，没有搜索到相关数据",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(RoutePlanActivity.this, "onDriveRouteSearched error.[" + errorCode + "]",
                    Toast.LENGTH_SHORT).show();
        }

        List<DrivePath> drivePathList = result.getPaths();
        DrivePath drivePath = drivePathList.get(0);
        List<DriveStep> steps = drivePath.getSteps();
        for (DriveStep step : steps) {
            List<LatLonPoint> polyline = step.getPolyline();
            List<TMC> tmcList = step.getTMCs();
            for (TMC tmc : tmcList) {
                String status = tmc.getStatus();
                List<LatLonPoint> polyline1 = tmc.getPolyline();
            }
        }

    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult result, int errorCode) {
        aMap.clear();// 清理地图上的所有覆盖物
        if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
                    WalkPath walkPath = result.getPaths()
                            .get(0);
                    WalkRouteOverlay walkRouteOverlay = new WalkRouteOverlay(
                            RoutePlanActivity.this, aMap, walkPath,
                            result.getStartPos(),
                            result.getTargetPos());
                    walkRouteOverlay.setNodeIconVisibility(true);//设置节点marker是否显示
                    walkRouteOverlay.removeFromMap();
                    walkRouteOverlay.addToMap();
                    walkRouteOverlay.zoomToSpan();
                    String dur = "", dis = "";
                    dur = AMapUtil.getFriendlyTime((int) walkPath.getDuration());
                    dis = AMapUtil.getFriendlyLength((int) walkPath.getDistance());
                    btn_walk.setText("步行时间" + dur + "\n距离" + dis);
                } else {
                    Toast.makeText(RoutePlanActivity.this, "对不起，没有搜索到相关数据",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(RoutePlanActivity.this, "对不起，没有搜索到相关数据",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(RoutePlanActivity.this, "onWalkRouteSearched error.[" + errorCode + "]",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBusRouteSearched(BusRouteResult result, int errorCode) {
        aMap.clear();// 清理地图上的所有覆盖物
        if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
                    List<BusPath> busPath = result.getPaths();
                    BusRouteOverlay busRouteOverlay = new BusRouteOverlay(
                            RoutePlanActivity.this, aMap, busPath.get(0),
                            result.getStartPos(),
                            result.getTargetPos());
                    busRouteOverlay.setNodeIconVisibility(true);//设置节点marker是否显示
                    busRouteOverlay.removeFromMap();
                    busRouteOverlay.addToMap();
                    busRouteOverlay.zoomToSpan();
//                    String dur = "", dis = "";
//                    dur = AMapUtil.getFriendlyTime((int) busPath.getDuration());
//                    dis = AMapUtil.getFriendlyLength((int) busPath.getDistance());
                    btn_bus.setText("公交" + "\n暂无");
                } else {
                    Toast.makeText(RoutePlanActivity.this, "对不起，没有搜索到相关数据",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(RoutePlanActivity.this, "对不起，没有搜索到相关数据",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(RoutePlanActivity.this, "onWalkRouteSearched error.[" + errorCode + "]",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRideRouteSearched(RideRouteResult result, int errorCode) {
        aMap.clear();// 清理地图上的所有覆盖物
        if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
                    RidePath ridePath = result.getPaths()
                            .get(0);
                    RideRouteOverlay rideRouteOverlay = new RideRouteOverlay(
                            this, aMap, ridePath,
                            result.getStartPos(),
                            result.getTargetPos());
                    rideRouteOverlay.setNodeIconVisibility(true);//设置节点marker是否显示
                    rideRouteOverlay.removeFromMap();
                    rideRouteOverlay.addToMap();
                    rideRouteOverlay.zoomToSpan();
                    String dur = "", dis = "";
                    dur = AMapUtil.getFriendlyTime((int) ridePath.getDuration());
                    dis = AMapUtil.getFriendlyLength((int) ridePath.getDistance());
                    rideMode.setText("骑行时间" + dur + "\n距离" + dis);
                } else {
                    Toast.makeText(RoutePlanActivity.this, "对不起，没有搜索到相关数据",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(RoutePlanActivity.this, "对不起，没有搜索到相关数据",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(RoutePlanActivity.this, "onWalkRouteSearched error.[" + errorCode + "]",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && data.getParcelableExtra("poi") != null) {
            Poi poi = data.getParcelableExtra("poi");
            if (requestCode == 100) {//起点选择完成
                //Toast.makeText(this, "100", Toast.LENGTH_SHORT).show();
//                startLatlng = new NaviLatLng(poi.getCoordinate().latitude, poi.getCoordinate().longitude);
//                mStartMarker.setPosition(new LatLng(poi.getCoordinate().latitude, poi.getCoordinate().longitude));
                startPoint = new LatLonPoint(poi.getCoordinate().latitude, poi.getCoordinate().longitude);
                startName = poi.getName();
            }
            if (requestCode == 200) {//终点选择完成
                //Toast.makeText(this, "200", Toast.LENGTH_SHORT).show();
//                endLatlng = new NaviLatLng(poi.getCoordinate().latitude, poi.getCoordinate().longitude);
//                mEndMarker.setPosition(new LatLng(poi.getCoordinate().latitude, poi.getCoordinate().longitude));
                endPoint = new LatLonPoint(poi.getCoordinate().latitude, poi.getCoordinate().longitude);
                endName = poi.getName();

            }
        }
    }
}
