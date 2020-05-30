package life;

import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class FallDataMap {
    private static final FallDataMap fallDatamap = new FallDataMap();
    public static HashMap accDataMap = new HashMap();
    public static HashMap gyroDataMap = new HashMap();

    private static int sampleAmount = 200; //输出数组?*1200,3轴，400组数据，两个传感器每个各200

    private FallDataMap() {

    }

    public static FallDataMap getInstance() {
        return fallDatamap;
    }

    public float getGyroValueMax(List dataList) {
        float gyroValueMax = (float) Collections.max(dataList.subList(600,1200));
        return gyroValueMax;
    }

    public float getGyroValueMin(List dataList) {
        float gyroValueMin = (float) Collections.min(dataList.subList(600,1200));
        return gyroValueMin;
    }

    public List getDataList() {
        int accID = accDataMap.size();  //加速度Map的大小
        int gyroID = gyroDataMap.size();  //陀螺仪Map的大小
        List dataList = new ArrayList();
        if (accID < 200 || gyroID < 200) {  //数据量不足时返回一位0的dataLiat
            dataList.add(0);
            return dataList;
        }
        for (int i = accID - sampleAmount; i < accID; i++) { //保存加速度数据
            float[] accValues = (float[]) accDataMap.get(i);
            dataList.add(accValues[0]);
            dataList.add(accValues[1]);
            dataList.add(accValues[2]);
        }
        for (int i = gyroID - sampleAmount; i < gyroID; i++) { //保存陀螺仪数据
            float[] gyroValues = (float[]) gyroDataMap.get(i);
            dataList.add(gyroValues[0]);
            dataList.add(gyroValues[1]);
            dataList.add(gyroValues[2]);
        }
        return dataList;
    }
}
