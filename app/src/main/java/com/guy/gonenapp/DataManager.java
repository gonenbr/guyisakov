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

            ArrayList<String> temp = new ArrayList<>();
            ArrayList<Integer> stream = new ArrayList<>();
            boolean in = false;
            boolean out = false;

            boolean working = true;
            while (working && (bytesRead = inputStream.read(buffer)) != -1) {
                for (int i = 0; i < bytesRead; i++) {
                    if (in) {
                        if (buffer[i] == 0) {
                            out = true;
                            continue;
                        } else if (buffer[i] == 10  &&  out) {
                            in = false;
                            processRecord(stream);
//                            processStr(temp);
                            stream.clear();
                            working = false;
                            break; // change to continue
                        } else if (out && buffer[i] != 0) {
                            out = false;
                            stream.add(0x00); // prev 0
                            temp.add(String.format("%02X", 0x00));
                        }

                        stream.add(buffer[i] & 0xFF);
                        temp.add(String.format("%02X", buffer[i]));
                    } else if (buffer[i] == 13) {
                        in = true;
                    }

                    // Convert each byte to a two-digit hexadecimal value
//                    String hexString = String.format("%02X", buffer[i]);
//                    if (hexString.equals(""))
                    // & 0xFF for unsigned integer.
//                    Log.d("pttt", hexString + ": " + (buffer[i] & 0xFF));
//                    Log.d("pttt", hexString);
//                    System.out.print(hexString + " ");
                }
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void processRecord(ArrayList<Integer> stream) {
        Record record = new Record();
        record.type = stream.get(0);
        record.unixTime = stream.get(1) * 1 + stream.get(2) * 16*16 + stream.get(3) * 16*16*16*16 + stream.get(4) * 16*16*16*16*16*16;
        record.msTime = stream.get(5) * 1 + stream.get(6) * 16*16;
        record.lengthOfRecord = stream.get(7) * 1 + stream.get(8) * 16*16;
        record.packetIndex = stream.get(9) * 1 + stream.get(10) * 16*16;
        record.channelMapping = stream.get(11) * 1 + stream.get(12) * 16*16;
        record.samplingRate = stream.get(13) * 1 + stream.get(14) * 16*16;
        record.downSamplingFactor = stream.get(15);
        Log.d("pttt", "" + stream.size());
        Log.d("pttt", "unixTime: " + record.unixTime);
        Log.d("pttt", "msTime: " + record.msTime);
        Log.d("pttt", "lengthOfRecord: " + record.lengthOfRecord);
        Log.d("pttt", "packetIndex: " + record.packetIndex);
        Log.d("pttt", "channelMapping: " + record.channelMapping);
        Log.d("pttt", "samplingRate: " + record.samplingRate);
        Log.d("pttt", "downSamplingFactor: " + record.downSamplingFactor);
        for (Integer i : stream) {
            //Log.d("pttt", "" + i);
        }
    }

    private static void processStr(ArrayList<String> temp) {
        Log.d("pttt", "" + temp.size());
        for (int i = 0; i < temp.size(); i++) {
            Log.d("pttt", i + ": " + temp.get(i));
        }
    }
}
/*
144
  0: A0 type
  1: 99 unixTime
  2: 72
  3: 7B
  4: 65
  5: 1B msTime
  6: 01
  7: 87 lengthOfRecord
  8: 00
  9: 28 packetIndex
 10: 16
 11: FF channelMapping
 12: FF
 13: A0 samplingRate
 14: 0F
 15: 10 downSamplingFactor
 16: 15 data
 17: 40
 18: 01
 19: 40
 20: FD
 21: 3F
 22: 2A
 23: 40
 24: EE
 25: 3F
 26: 07
 27: 40
 28: F3
 29: 3F
 30: DD
 31: 3F
 32: D7
 33: 3F
 34: F7
 35: 3F
 36: 1B
 37: 40
 38: F4
 39: 3F
 40: 2A
 41: 40
 42: FA
 43: 3F
 44: E0
 45: 3F
 46: CE
 47: 3F
 48: 1D
 49: 40
 50: EE
 51: 3F
 52: F0
 53: 3F
 54: 29
 55: 40
 56: DF
 57: 3F
 58: 07
 59: 40
 60: F3
 61: 3F
 62: E5
 63: 3F
 64: E3
 65: 3F
 66: FB
 67: 3F
 68: 1A
 69: 40
 70: F1
 71: 3F
 72: 2A
 73: 40
 74: F7
 75: 3F
 76: CF
 77: 3F
 78: D2
 79: 3F
 80: 50
 81: 40
 82: 27
 83: 40
 84: 28
 85: 40
 86: 65
 87: 40
 88: 14
 89: 40
 90: 33
 91: 40
 92: 16
 93: 40
 94: 19
 95: 40
 96: 11
 97: 40
 98: 29
 99: 40
100: 58
101: 40
102: 1E
103: 40
104: 6B
105: 40
106: 40
107: 40
108: 2E
109: 40
110: 2D
111: 40
112: 4D
113: 40
114: 26
115: 40
116: 2A
117: 40
118: 66
119: 40
120: 1F
121: 40
122: 33
123: 40
124: 2A
125: 40
126: 16
127: 40
128: 15
129: 40
130: 38
131: 40
132: 67
133: 40
134: 38
135: 40
136: 71
137: 40
138: 45
139: 40
140: 47
141: 40
142: 36
143: 40
 */