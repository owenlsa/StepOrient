package life;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class FallDataMap {
    private static final FallDataMap fallDatamap = new FallDataMap();

    // 用来存放加速度和陀螺仪数据的HashMap
    public static HashMap accDataMap = new HashMap();
    public static HashMap gyroDataMap = new HashMap();

    // 模型输入数组[][1200]，加速度和陀螺仪三轴数据各200组
    private static int sampleAmount = 200;

    private FallDataMap() {

    }

    public static FallDataMap getInstance() {
        return fallDatamap;
    }

    /*
     * 获取陀螺仪最大值
     * */
    public float getGyroValueMax(List dataList) {
        float gyroValueMax = (float) Collections.max(dataList.subList(600,1200));
        return gyroValueMax;
    }

    /*
     * 获取陀螺仪最小值
     * */
    public float getGyroValueMin(List dataList) {
        float gyroValueMin = (float) Collections.min(dataList.subList(600,1200));
        return gyroValueMin;
    }

    /*
     * 获取加速度最大值
     * */
    public float getAccValueMax(List dataList) {
        float accValueMax = (float) Collections.max(dataList.subList(0,600));
        return accValueMax;
    }

    /*
     * 获取加速度最小值
     * */
    public float getAccValueMin(List dataList) {
        float accValueMin = (float) Collections.min(dataList.subList(0,600));
        return accValueMin;
    }

    /**
     * 获取传感器数据sample
     * @return 包含加速度和陀螺仪的ArrayList
     */
    public List getDataList() {
        int accID = accDataMap.size();  // 加速度Map的大小
        int gyroID = gyroDataMap.size();  // 陀螺仪Map的大小
        List dataList = new ArrayList();
        if (accID < 200 || gyroID < 200) {  // 数据量不足时返回一位0的dataList
            dataList.add(0);
            return dataList;
        }
        for (int i = accID - sampleAmount; i < accID; i++) { // 保存加速度数据
            float[] accValues = (float[]) accDataMap.get(i);
            dataList.add(accValues[0]);
            dataList.add(accValues[1]);
            dataList.add(accValues[2]);
        }
        for (int i = gyroID - sampleAmount; i < gyroID; i++) { // 保存陀螺仪数据
            float[] gyroValues = (float[]) gyroDataMap.get(i);
            dataList.add(gyroValues[0]);
            dataList.add(gyroValues[1]);
            dataList.add(gyroValues[2]);
        }
        return dataList;
    }
}
