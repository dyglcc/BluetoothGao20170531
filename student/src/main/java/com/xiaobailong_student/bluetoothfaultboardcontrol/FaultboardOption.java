package com.xiaobailong_student.bluetoothfaultboardcontrol;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.xiaobailong_student.bluetooth.BluetoothDevicesListDialog;
import com.xiaobailong_student.bluetooth.BluetoothListener;
import com.xiaobailong_student.bluetooth.BluetoothManager;
import com.xiaobailong_student.bluetooth.BluetoothMessage;

import java.util.ArrayList;

@SuppressLint("HandlerLeak")
public class FaultboardOption implements BluetoothListener {

    private final String DefaultPSW = "1234";
    private final int DataReadWrong = 0;
    private final int BluetoothError = 1;
    private ReadStateListenter readStateListenter;

    private Handler mHandler = null;

    private Context context = null;

    private BluetoothManager blueToothManager = null;

    private ProgressDialog waitingDialog = null;

    private CommandCreater commandCreater = null;

    private DataParser dataParser = null;

    private ArrayList<Relay> relayList = null;

    public FaultboardOption(Context context, Handler mHandler,
                            ArrayList<Relay> relayList, ReadStateListenter listener) {
        this.context = context;
        this.mHandler = mHandler;
        blueToothManager = new BluetoothManager(context, this);
        commandCreater = new CommandCreater();
        dataParser = new DataParser();
        this.relayList = relayList;
        openBluetooth();
        readStateListenter = listener;
    }

    @Override
    public void optionCallBack(int optionId, BluetoothMessage msg) {
        switch (optionId) {
            case SearchFinished:
                if (waitingDialog != null && waitingDialog.isShowing()) {
                    waitingDialog.dismiss();
                }
                if (activity != null && !activity.isFinishing()) {
                    showBlueToothDevice(activity);
                }
                break;
            case BluetoothDevicesSelected:
                int position = msg.getArg1();
                blueToothManager.connect(position, DefaultPSW);
                break;
            case BluetoothSearch:
                blueToothManager.search();
                showWaitingDialog(context.getString(R.string.bluetooth_searching));
                break;
            case BluetoothConnected:
                if (waitingDialog != null && waitingDialog.isShowing()) {
                    waitingDialog.dismiss();
                }
                Toast.makeText(context, "Bluetooth connect Success !",
                        Toast.LENGTH_SHORT).show();
                if (readStateListenter != null) {

                    readStateListenter.onConnected();
                }
                break;
        }
    }

    private void showWaitingDialog(String msg) {
        if (context == null) {
            return;
        }
        if (waitingDialog == null) {
            waitingDialog = new ProgressDialog(context);
        }
        waitingDialog.setMessage(msg);
        if (!waitingDialog.isShowing()) {
            waitingDialog.show();
        }
    }

    public void showBlueToothDevice(Activity activity) {
        BluetoothDevicesListDialog bluetoothDevicesListDialog = new BluetoothDevicesListDialog(
                activity, blueToothManager.getAllBluetoothName(), this);
        bluetoothDevicesListDialog.show();
    }

    public void openBluetooth() {
        if (!blueToothManager.isBluetoothOpened()) {
            blueToothManager.openBluetooth();
        }
    }

    public boolean isConneted() {

        return blueToothManager.isBluetoothCononected();
    }

    private Activity activity;

    public void bluetoothConnect(Activity activity) {
        this.activity = activity;
        blueToothManager.getBondedDevices();
        showBlueToothDevice(activity);
    }

    public void setArray(ArrayList<Relay> relays) {
        relayList = relays;
    }

    /**
     * 点火
     */
    protected void ignition(int optionId) {
        byte[] command = commandCreater.create(CommandCreater.FireUp,
                (byte) 0x65);
        blueToothManager.sendData(command, optionId);
    }

    /**
     * 打开单个继电器
     *
     * @param id
     */
    protected void start(byte id, int optionId) {
        byte[] command = commandCreater.create(CommandCreater.Start, id);
        blueToothManager.sendData(command, optionId);
    }

    /**
     * 打开单个继电器
     *
     * @param id
     */
    protected void shutDown(byte id, int optionId) {
        byte[] command = commandCreater.create(CommandCreater.ShutDown, id);
        blueToothManager.sendData(command, optionId);
    }

    /**
     * 启动打开
     *
     * @param id
     */
    protected void startUp(byte id, int optionId) {
        byte[] command = commandCreater.create(CommandCreater.StartUp, id);
        blueToothManager.sendData(command, optionId);
    }

    /**
     * 启动关闭
     *
     * @param id
     */
    protected void startDown(byte id, int optionId) {
        byte[] command = commandCreater.create(CommandCreater.StartDown, id);
        blueToothManager.sendData(command, optionId);
    }

    /**
     * 熄火
     *
     * @param id
     */
    protected void fireDown(byte id, int optionId) {
        byte[] command = commandCreater.create(CommandCreater.FireDown, id);
        blueToothManager.sendData(command, optionId);
    }

    /**
     * 状态读取
     */
    protected void stateIsRead(int optionId) {
        byte[] command = commandCreater.create(CommandCreater.StateIsRead,
                (byte) 0);
        blueToothManager.sendData(command, optionId);
    }

    /**
     * 全部设置
     */
    protected void setAll(int optionId) {
        byte[] command = commandCreater.create(CommandCreater.SetAll, (byte) 0);
        blueToothManager.sendData(command, optionId);
    }

    /**
     * 全部清除
     */
    protected void clearAll(int optionId) {
        // curCommand =CommandCreater.ClearAll;
        byte[] command = commandCreater.create(CommandCreater.ClearAll,
                (byte) 0);
        blueToothManager.sendData(command, optionId);
    }

    public void closeBluetoothSocket() {
        if (blueToothManager.isBluetoothCononected()) {
            blueToothManager.close();
        }

    }

    @Override
    public void log(String errorMsg) {
        Message msg = bluetoothListenerHandler.obtainMessage();
        msg.arg1 = BluetoothError;
        msg.obj = errorMsg;
        bluetoothListenerHandler.sendMessage(msg);
    }

    /**
     * 解析蓝牙板反馈数据
     */
    @Override
    public void inputData(byte[] data, int readDataLength, int id) {
        //点火、熄火、启动相关返回数据不需要更新到界面
        if (data[2] == 0xffffffE0 || data[2] == 0xffffffE1 || data[2] == 0xffffffF0 || data[2] == 0xffffffF1) {
            return;
        }
        byte[] state = dataParser.parse(data);
        if (readDataLength < 19) {
            Message msg = bluetoothListenerHandler.obtainMessage();
            msg.arg1 = DataReadWrong;
            msg.arg2 = readDataLength;
            bluetoothListenerHandler.sendMessage(msg);
            return;
        }
        int size = relayList.size();
        for (int i = 0; i < size; i++) {
            Relay relay = relayList.get(i);
            if (state[i] == 0) {
                relay.setState(Relay.Green);
                relay.setExamination(false);
            } else {
                relay.setState(Relay.Red);
                relay.setExamination(true);
            }
        }
        Message msg = this.mHandler.obtainMessage();
        msg.arg1 = id;
        this.mHandler.sendMessage(msg);
    }

    private Handler bluetoothListenerHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.arg1) {
                case DataReadWrong:
                    Toast.makeText(
                            context,
                            "The length of read data is wrong ! The length should is 17,but now it is "
                                    + msg.arg2 + ".", Toast.LENGTH_LONG).show();
                    break;
                case BluetoothError:
                    Toast.makeText(context, (String) msg.obj, Toast.LENGTH_SHORT)
                            .show();
                    break;
            }
        }

        ;
    };

}
