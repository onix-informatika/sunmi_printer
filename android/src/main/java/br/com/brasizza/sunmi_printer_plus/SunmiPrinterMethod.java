package br.com.brasizza.sunmi_printer_plus;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import woyou.aidlservice.jiuiv5.*;

import java.util.Arrays;


public class SunmiPrinterMethod {

    private final String TAG = SunmiPrinterMethod.class.getSimpleName();
    private ArrayList<Boolean> _printingText = new ArrayList<Boolean>();
    private IWoyouService _woyouService;
    private Context _context;

    public SunmiPrinterMethod(Context context) {
        this._context = context;
    }

    private ServiceConnection connService = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                _woyouService = IWoyouService.Stub.asInterface(service);
                String serviceVersion = _woyouService.getServiceVersion();
                // unnecessary, can be made into a callback to dart if needed. Let the user deal with it
                // Toast
                //         .makeText(
                //                 _context,
                //                 "Sunmi Printer Service Connected. Version :" + serviceVersion,
                //                 Toast.LENGTH_LONG
                //         )
                //         .show();


            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                // unnecessary, can be made into a callback to dart if needed. Let the user deal with it
                // Toast
                //         .makeText(
                //                 _context,
                //                 "Sunmi Printer Service Not Found",
                //                 Toast.LENGTH_LONG
                //         ).show();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // unnecessary, can be made into a callback to dart if needed. Let the user deal with it
            // Toast
            //         .makeText(
            //                 _context,
            //                 "Sunmi Printer Service Disconnected",
            //                 Toast.LENGTH_LONG
            //         )
            //         .show();
        }
    };

    public void bindPrinterService() {
        Intent intent = new Intent();
        intent.setPackage("woyou.aidlservice.jiuiv5");
        intent.setAction("woyou.aidlservice.jiuiv5.IWoyouService");
        _context.bindService(intent, connService, Context.BIND_AUTO_CREATE);
    }

    public void unbindPrinterService() {
        _context.unbindService(connService);
    }

    public void initPrinter() {
        try {
            _woyouService.printerInit(this._callback());
            Log.d(TAG, "initPrinter");
        } catch (RemoteException e) {
            e.printStackTrace();
            Log.e(TAG, "initPrinter RE: " + e.getMessage());
        } catch (NullPointerException e) {
            e.printStackTrace();
            Log.e(TAG, "initPrinter NPE: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "initPrinter E: " + e.getMessage());
        }
    }

    public int updatePrinter() {
        try {
            final int status = _woyouService.updatePrinterState();
            Log.d(TAG, "updatePrinter: " + status);
            return status;
        } catch (RemoteException e) {
            e.printStackTrace();
            Log.e(TAG, "updatePrinter RE: " + e.getMessage());
            return 0; // error
        } catch (NullPointerException e) {
            e.printStackTrace();
            Log.e(TAG, "updatePrinter NPE: " + e.getMessage());
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "updatePrinter E: " + e.getMessage());
            return 0;
        }
    }

    public void printText(String text) {

        this._printingText.add(this._printText(text));
    }

    private Boolean _printText(String text) {
        try {
            _woyouService.printText(text, this._callback());
            return true;
        } catch (RemoteException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
    }

    public Boolean setAlignment(Integer alignment) {
        try {
            _woyouService.setAlignment(alignment, this._callback());
            return true;
        } catch (RemoteException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
    }

    public Boolean setFontSize(int fontSize) {
        try {
            _woyouService.setFontSize(fontSize, this._callback());
            return true;
        } catch (RemoteException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
    }

    public Boolean setFontBold(Boolean bold) {
        if (bold == null) {
            bold = false;
        }

        byte[] command = new byte[]{0x1B, 0x45, 0x1};

        if (bold == false) {
            command = new byte[]{0x1B, 0x45, 0x0};
        }

        try {
            _woyouService.sendRAWData(command, this._callback());
            return true;
        } catch (RemoteException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
    }


    public Boolean printColumn(
            String[] stringColumns,
            int[] columnWidth,
            int[] columnAlignment
    ) {


        try {

            _woyouService.printColumnsText(
                    stringColumns,
                    columnWidth,
                    columnAlignment,
                    this._callback()
            );

            return true;
        } catch (RemoteException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
    }

    public Boolean printImage(Bitmap bitmap) {
        try {
            _woyouService.printBitmap(bitmap, this._callback());
            return true;
        } catch (RemoteException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
    }

    public String getPrinterSerialNo() {

        try {
            final String serial = _woyouService.getPrinterSerialNo();
            return serial;
        } catch (RemoteException e) {
            return ""; // error;
        } catch (NullPointerException e) {
            return "NOT FOUND";
        }
    }

    public String getPrinterVersion() {
        try {

            final String version = _woyouService.getPrinterVersion();
            return version;
        } catch (RemoteException e) {
            return "";// error;
        } catch (NullPointerException e) {
            return "NOT FOUND";
        }
    }

    public void lineWrap(int lines) {
        try {
            _woyouService.lineWrap(lines, this._callback());
        } catch (RemoteException e) {
        } catch (NullPointerException e) {
        }
    }

    public void sendRaw(byte[] bytes) {
        try {
            this._woyouService.sendRAWData(bytes, this._callback());
        } catch (RemoteException e) {
        } catch (NullPointerException e) {
        }
    }

    public void enterPrinterBuffer(Boolean clear) {
        try {
            this._woyouService.enterPrinterBuffer(clear);
        } catch (RemoteException e) {
        } catch (NullPointerException e) {
        }
    }

    public void commitPrinterBuffer() {
        try {
            this._woyouService.commitPrinterBuffer();
        } catch (RemoteException e) {
        } catch (NullPointerException e) {
        }
    }

    public void exitPrinterBuffer(Boolean clear) {
        try {
            this._woyouService.exitPrinterBuffer(clear);
        } catch (RemoteException e) {
        } catch (NullPointerException e) {
        }
    }

    public void setAlignment(int alignment) {
        try {
            _woyouService.setAlignment(alignment, this._callback());
        } catch (RemoteException e) {
        } catch (NullPointerException e) {
        }
    }

    public void printQRCode(String data, int modulesize, int errorlevel) {
        try {
            _woyouService.printQRCode(data, modulesize, errorlevel, this._callback());
        } catch (RemoteException e) {
        } catch (NullPointerException e) {
        }
    }

    public void printBarCode(
            String data,
            int barcodeType,
            int textPosition,
            int width,
            int height
    ) {
        try {
            _woyouService.printBarCode(
                    data,
                    barcodeType,
                    height,
                    width,
                    textPosition,
                    this._callback()
            );
        } catch (RemoteException e) {
        } catch (NullPointerException e) {
        }
    }

    private ICallback _callback() {
        return new ICallback() {
            @Override
            public void onRunResult(boolean isSuccess) throws RemoteException {
            }

            @Override
            public void onReturnString(String result) throws RemoteException {
            }

            @Override
            public void onRaiseException(int code, String msg)
                    throws RemoteException {
            }

            @Override
            public void onPrintResult(int code, String msg) throws RemoteException {
            }

            @Override
            public IBinder asBinder() {
                return null;
            }
        };
    }
}
