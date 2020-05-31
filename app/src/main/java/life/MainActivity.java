package life;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import life.orient.OrientSensor;
import life.step.StepSensorAcceleration;
import life.step.StepSensorBase;
import life.util.SensorUtil;

public class MainActivity extends AppCompatActivity implements StepSensorBase.StepCallBack, OrientSensor.OrientCallBack {
    private TextView mStepText;
    private TextView mOrientText;
    private StepView mStepView;
    private StepSensorBase mStepSensor; // 计步传感器
    private OrientSensor mOrientSensor; // 方向传感器
    private int mStepLen = 25; // 步长

    @Override
    public void Step(int stepNum, int fallNum, int CURRENT_FALL) {
        // 计步回调
        mStepText.setText("步数:" + stepNum + "\n摔倒次数:" + fallNum);
        mStepView.autoAddPoint(mStepLen, CURRENT_FALL);
    }

    @Override
    public void Orient(int orient) {
        // 方向回调
        mOrientText.setText("方向:" + orient);
        mStepView.autoDrawArrow(orient);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 保持屏幕不熄屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        SensorUtil.getInstance().printAllSensor(this); // 打印所有可用传感器
        setContentView(R.layout.activity_main);
        mStepText = (TextView) findViewById(R.id.step_text);
        mOrientText = (TextView) findViewById(R.id.orient_text);
        mStepView = (StepView) findViewById(R.id.step_surfaceView);
        mStepSensor = new StepSensorAcceleration(this, this);  //加速度传感器计步
        // 注册计步和摔倒检测监听
        if (!mStepSensor.registerStep()) {
            Toast.makeText(this, "计步和摔倒检测功能不可用！", Toast.LENGTH_SHORT).show();
        }

        // 注册方向监听
        mOrientSensor = new OrientSensor(this, this);
        if (!mOrientSensor.registerOrient()) {
            Toast.makeText(this, "方向功能不可用！", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 注销传感器监听
        mStepSensor.unregisterStep();
        mOrientSensor.unregisterOrient();
    }
}
