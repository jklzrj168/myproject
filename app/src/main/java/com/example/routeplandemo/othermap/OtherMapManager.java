package com.example.routeplandemo.othermap;

import android.app.Activity;
import android.support.design.widget.BottomSheetDialog;
import android.view.View;
import android.widget.Toast;

import com.example.routeplandemo.othermap.bean.StartAndEndInfo;
import com.example.routeplandemo.othermap.operator.BaiduMapOperator;
import com.example.routeplandemo.othermap.operator.BaseMapOperator;
import com.example.routeplandemo.othermap.operator.GaodeMapOperator;
import com.example.routeplandemo.othermap.operator.GoogleMapOperator;
import com.example.routeplandemo.othermap.utils.BottomSheetUtil;
import com.example.routeplandemo.othermap.utils.OtherMapUtil;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * 调用第三方地图导航
 *
 * @author zpan
 */
public class OtherMapManager {

    private Activity mActivity;
    private static List<String> sMapList = new ArrayList<>();
    private List<String> sheetItemList;
    private boolean needShow = true;

    static {
        sMapList.add(BaiduMapOperator.PACKAGE_NAME);
        sMapList.add(GaodeMapOperator.PACKAGE_NAME);
        sMapList.add(GoogleMapOperator.PACKAGE_NAME);
    }

    private StartAndEndInfo mEntity;
    private BottomSheetDialog bottomSheetDialog;

    /**
     * @param entity 传入火星坐标（GCJ-02）坐标系
     */
    public OtherMapManager(Activity activity, StartAndEndInfo entity) {
        mActivity = activity;
        mEntity = entity;
        init();
    }

    private void init() {
        sheetItemList = new ArrayList<>();
        Iterator<String> iterator = sMapList.iterator();
        while (iterator.hasNext()) {
            String packageName = iterator.next();
            if (OtherMapUtil.checkInstalled(mActivity, packageName)) {
                sheetItemList.add(OtherMapUtil.getMapName(packageName));
            } else {
                iterator.remove();
            }
        }
        if (sheetItemList.size() == 0) {
            needShow = false;
        } else {
            HashMap<String, View.OnClickListener> map = new HashMap<>();

            for (int i = 0; i < sheetItemList.size(); i++) {
                final Class mapClass = OtherMapUtil.getMapClass(sMapList.get(i));
                map.put("使用" + sheetItemList.get(i).toString() + "导航", new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        MapOperatorFactory.getOperator(mapClass).navigation(mActivity, mEntity);
                    }
                });
            }
            bottomSheetDialog = BottomSheetUtil.createTextBottomSheetDialog(mActivity, map);
        }
    }

    public void navigation() {
        if (needShow) {
            bottomSheetDialog.show();
        } else {
            if (sheetItemList.size() == 1) {
                String p = sMapList.get(0);
                Class mapClass = OtherMapUtil.getMapClass(p);
                BaseMapOperator operator = MapOperatorFactory.getOperator(mapClass);
                operator.navigation(mActivity, mEntity);
            } else {
                Toast.makeText(mActivity, "您尚未安装任何地图应用", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
