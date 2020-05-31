package life;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.support.annotation.WorkerThread;
import android.util.Log;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class FallDetection {
    private final Context context;
    private static final String TAG = "FallDetection";

    // tflite模型文件名
    private static final String MODEL_PATH = "fallDetection.tflite";
    // tflite解释器
    private Interpreter tfliteInterpreter;

    public FallDetection(Context context) {
        this.context = context;
    }

    /**
     * 摔倒检测模型主函数
     * @return 是否摔倒
     */
    public synchronized boolean fallModel(float[] dataList) {
        load();
        boolean FALL_RESULT = fallDetect(dataList);
        return FALL_RESULT;
    }

    /*
     * 获取数组中最大值索引
     * */
    private int getMaxID(float[] data) {
        float max = -1000000;
        int maxID = -1;
        for (int i=0; i<data.length; i++) {
            if ((data[i]) > max) {
                max = data[i];
                maxID = i;
            }
        }
        return maxID;
    }


    /**
     * 读取模型
     */
    @WorkerThread
    public void load() {
        loadModel();
    }

    /*
     * 读取tflite模型
     * */
    @WorkerThread
    private synchronized void loadModel() {
        try {
            ByteBuffer buffer = loadModelFile(this.context.getAssets());
            tfliteInterpreter = new Interpreter(buffer);
            Log.v(TAG, "TFLite model loaded.");
        } catch (IOException ex) {
            Log.e(TAG, ex.getMessage());
        }
    }

    /**
     * 释放tflite解释器
     */
    @WorkerThread
    public synchronized void unload() {
        tfliteInterpreter.close();
    }

    /**
     * 摔倒检测函数
     */
    @WorkerThread
    public synchronized boolean fallDetect(float[] dataList) {
        float[] input = dataList;
        boolean FALL_RESULT = false;
        float[][] output = new float[1][8]; // 模型输出格式 float[1][8]
        // tflite解释器run
        tfliteInterpreter.run(input, output);
        float[] resultProb = output[0]; // 模型输出变形为 float[8]
        int maxID = getMaxID(resultProb); // 获取输出最大概率的索引
        if (maxID == 0) { // 索引为0时，判定为摔倒
            FALL_RESULT = true;
        }
        unload(); // 释放解释器
        return FALL_RESULT;
    }

    /**
     * 从assets文件夹中获取tflite文件
     */
    private static MappedByteBuffer loadModelFile(AssetManager assetManager) throws IOException {
        try (AssetFileDescriptor fileDescriptor = assetManager.openFd(MODEL_PATH);
             FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor())) {
            FileChannel fileChannel = inputStream.getChannel();
            long startOffset = fileDescriptor.getStartOffset();
            long declaredLength = fileDescriptor.getDeclaredLength();
            return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
        }
    }
}
