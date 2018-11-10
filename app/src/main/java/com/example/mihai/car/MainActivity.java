package com.example.mihai.car;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static BluetoothLeScanner mBluetoothLeScanner;
    private static BluetoothGatt mBluetoothGatt;
    private final String DEVICE_MAC = "00:15:84:32:52:28";
    private BluetoothAdapter mBluetoothAdapter;
    private BLEScanCallback mScanCallback;
    private BluetoothGattCallback mBTGattCallback;
    private BluetoothDevice mBluetoothDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prepareBluetoothAdapter();
    }

    @Override
    protected void onStart() {
        super.onStart();
        startScan();
    }

    private void prepareBluetoothAdapter() {

        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager != null) {
            mBluetoothAdapter = bluetoothManager.getAdapter();
            mBluetoothAdapter.enable();
        }
    }

    private void connect() {
        mBTGattCallback = new BTGattCallback();
        mBluetoothGatt = mBluetoothDevice.connectGatt(this, true, mBTGattCallback);

        if (mBluetoothGatt.connect()) {
            CarControl mCarControl = new CarControl(findViewById(R.id.forward_button),
                    findViewById(R.id.backward_button),
                    findViewById(R.id.left_button),
                    findViewById(R.id.right_button),
                    findViewById(R.id.emergecy_brake_button),
                    findViewById(R.id.enable_high_speed_switch),
                    mBluetoothGatt);
        }
    }

    private void startScan() {
        //GET BLE SCANNER
        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();

        // PREPARE FOR SCANNING
        ScanFilter.Builder scanFilterBuilder = new ScanFilter.Builder();
        scanFilterBuilder.setDeviceAddress(DEVICE_MAC);

        ScanFilter scanFilter = scanFilterBuilder.build();
        List<ScanFilter> scanFilterList = new ArrayList<>();
        scanFilterList.add(scanFilter);

        ScanSettings.Builder scanSettingsBuilder = new ScanSettings.Builder();
        scanSettingsBuilder.setNumOfMatches(1);

        ScanSettings scanSettings = scanSettingsBuilder.build();
        mScanCallback = new BLEScanCallback();
        mBluetoothLeScanner.startScan(scanFilterList, scanSettings, mScanCallback);
    }

    class BTGattCallback extends BluetoothGattCallback {

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);

            mBluetoothGatt.discoverServices();
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
        }
    }

    public class BLEScanCallback extends ScanCallback {

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);

            mBluetoothLeScanner.stopScan(this);
            mBluetoothDevice = result.getDevice();
            connect();
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);

            Log.i("Scan result : ", "device not found");
        }
    }
}
