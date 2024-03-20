package com.guy.gonenapp;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class DataManager {

    public static void readData(Context context, String fileName) {

        try {
            InputStream inputStream = context.getAssets().open(fileName);
            byte[] buffer = new byte[1024];
            int bytesRead;

            ArrayList<Integer> stream = new ArrayList<>();
            boolean in = false;
            boolean out = false;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                for (int i = 0; i < bytesRead; i++) {
                    if (in) {
                        if (buffer[i] == 0) {
                            out = true;
                            continue;
                        } else if (buffer[i] == 10  &&  out) {
                            in = false;
                            processRecord(stream);
                            break; // change to continue
                        } else if (out && buffer[i] != 0) {
                            stream.add(buffer[i] & 0x00); // prev 0
                        }

                        stream.add(buffer[i] & 0xFF);
                    } else if (buffer[i] == 13) {
                        in = true;
                    }

                    // Convert each byte to a two-digit hexadecimal value
                    String hexString = String.format("%02X", buffer[i]);
//                    if (hexString.equals(""))
                    // & 0xFF for unsigned integer.
                    Log.d("pttt", hexString + ": " + (buffer[i] & 0xFF));
//                    Log.d("pttt", hexString);
//                    System.out.print(hexString + " ");
                }
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
