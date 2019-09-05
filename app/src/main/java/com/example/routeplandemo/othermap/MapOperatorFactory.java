package com.example.routeplandemo.othermap;


import com.example.routeplandemo.othermap.operator.BaseMapOperator;

/**
 * 地图操作类工厂，使用此类获取指定地图操作类
 *
 * @author zpan
 */
class MapOperatorFactory {

    static <T extends BaseMapOperator> T getOperator(Class<T> clazz) {
        BaseMapOperator operator = null;
        try {
            operator = (BaseMapOperator) Class.forName(clazz.getName()).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (T) operator;
    }
}
