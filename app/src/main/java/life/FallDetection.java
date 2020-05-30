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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FallDetection {
    private static final String TAG = "FallDetection";
    private static final String MODEL_PATH = "fallDetection.tflite";
    private final Context context;
    private Interpreter tfliteInterpreter;

    public FallDetection(Context context) {
        this.context = context;
    }

    public synchronized boolean fallModel(float[] dataList) {
        load();
        boolean FALL_RESULT = fallDetect(dataList);
        return FALL_RESULT;
    }

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


    /** Load the TF Lite model and dictionary so that the client can start classifying text. */
    @WorkerThread
    public void load() {
        loadModel();
    }

    /** Load TF Lite model. */
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

    /** Free up resources as the client is no longer needed. */
    @WorkerThread
    public synchronized void unload() {
        tfliteInterpreter.close();
    }

    /** Classify an input string and returns the classification results. */
    @WorkerThread
    public synchronized boolean fallDetect(float[] dataList) {
        float[] input = dataList;
        Log.v(TAG, "Classifying text with TF Lite...");
        float[][] output = new float[1][8];
        tfliteInterpreter.run(input, output);
        float[] resultProb = output[0];
        int maxID = getMaxID(resultProb);
        boolean FALL_RESULT = false;
        if (maxID == 0) {
            FALL_RESULT = true;
        }
        unload();

        return FALL_RESULT;
    }

    /** Load TF Lite model from assets. */
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
