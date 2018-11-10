package com.example.mihai.car;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import java.util.UUID;

public class CarControl {

    private final int SLOW_SPEED = 0;
    private final int HIGH_SPEED = 4;
    private final String SERVICE_UUID = "0000ffe0-0000-1000-8000-00805f9b34fb";
    private final String CHARACTERISTIC_UUID = "0000ffe1-0000-1000-8000-00805f9b34fb";
    private final int FORWARD = 1;
    private final int BACKWARD = 2;
    private final int LEFT = 3;
    private final int RIGHT = 4;
    private final int BRAKE = 0;

    private int mSpeed;
    private Button mForwardButton;
    private Button mBackwardButton;
    private Button mLeftButton;
    private Button mRightButton;
    private Button mEmergencyBrake;
    private Switch mHighSpeedSwitch;

    private View.OnClickListener mClickListener;
    private BluetoothGatt mGatt;

    public CarControl(Button fwd, Button bwd, Button left, Button right, Button brk, Switch spd, BluetoothGatt gatt) {
        mGatt = gatt;
        mSpeed = SLOW_SPEED;

        mClickListener = v -> {
            int viewID = v.getId();
            setDrivingDirection(viewID);
        };

        mForwardButton = fwd;
        mBackwardButton = bwd;
        mLeftButton = left;
        mRightButton = right;
        mEmergencyBrake = brk;
        mHighSpeedSwitch = spd;

        setButtonsListener();
    }

    private void setButtonsListener() {
        mForwardButton.setOnClickListener(mClickListener);
        mBackwardButton.setOnClickListener(mClickListener);
        mLeftButton.setOnClickListener(mClickListener);
        mRightButton.setOnClickListener(mClickListener);
        mEmergencyBrake.setOnClickListener(mClickListener);
        mHighSpeedSwitch.setOnClickListener(mClickListener);
    }

    private void setDrivingDirection(int id) {
        switch (id) {
            case R.id.forward_button:
                sendMessage("" + (mSpeed + FORWARD));
                break;
            case R.id.backward_button:
                sendMessage("" + (mSpeed + BACKWARD));
                break;
            case R.id.left_button:
                sendMessage("" + (mSpeed + LEFT));
                break;
            case R.id.right_button:
                sendMessage("" + (mSpeed + RIGHT));
                break;
            case R.id.emergecy_brake_button:
                sendMessage("" + BRAKE);
                break;
            case R.id.enable_high_speed_switch:
                mSpeed = mHighSpeedSwitch.isChecked() ? HIGH_SPEED : SLOW_SPEED;
                Log.i("Switch", "" + mHighSpeedSwitch.isChecked());
                break;
        }
    }

    private void sendMessage(String s) {
        if (mGatt != null) {

            BluetoothGattService service = mGatt.getService(UUID.fromString(SERVICE_UUID));
            BluetoothGattCharacteristic characteristic =
                    service.getCharacteristic(UUID.fromString(CHARACTERISTIC_UUID));

            characteristic.setValue(s);
            mGatt.writeCharacteristic(characteristic);
        }
    }
}
