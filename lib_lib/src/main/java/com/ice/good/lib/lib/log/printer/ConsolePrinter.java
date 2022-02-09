package com.ice.good.lib.lib.log.printer;

import android.util.Log;

import androidx.annotation.NonNull;

import com.ice.good.lib.lib.log.base.ILogPrinter;
import com.ice.good.lib.lib.log.base.LogConfig;

public class ConsolePrinter implements ILogPrinter {
    @Override
    public void print(@NonNull LogConfig config, int level, String tag, @NonNull String printString) {
        int len = printString.length();
        int countOfSub = len/LogConfig.MAX_LEN;
        if(countOfSub>0){
            StringBuilder sb = new StringBuilder();
            int index = 0;
            for(int i=0; i<countOfSub; i++){
                sb.append(printString.substring(index, index+LogConfig.MAX_LEN));
                index+=LogConfig.MAX_LEN;
            }
            if (index != len) {
                sb.append(printString.substring(index, len));
            }
            Log.println(level, tag, sb.toString());
        }else{
            Log.println(level, tag, printString);
        }
    }
}
