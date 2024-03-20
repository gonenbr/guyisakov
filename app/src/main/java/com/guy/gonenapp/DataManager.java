package com.guy.gonenapp;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.time.chrono.MinguoDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class DataManager {

    public static ArrayList<Record> readData2(Context context, String fileName) {
        ArrayList<Record> records = new ArrayList<>();
        try {
            InputStream inputStream = context.getAssets().open(fileName);
            byte[] buffer = new byte[1024];
            int bytesRead;

            ArrayList<String> temp = new ArrayList<>();
            ArrayList<Integer> stream = new ArrayList<>();
            boolean in = false;
            boolean out = false;

            int index = 0;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                for (int i = 0; i < bytesRead; i++) {
                    temp.add(String.format("%02X", buffer[i]));
                    if (in) {
                        if (buffer[i] == 0) {
                            out = true;
                            continue;
                        } else if (buffer[i] == 10  &&  out) {
                            in = false;
                            out = false;
                            Record record = processRecord(stream);
                            if (record != null  &&  record.type == 160) {
                                records.add(record);
                            }
//                            processStr(temp);
                            stream.clear();
                            Log.d("pttt", "index: " + index++);
                            continue;
                        } else if (out && buffer[i] != 0) {
                            out = false;
                            stream.add(0x00); // prev 0
                        }

                        stream.add(buffer[i] & 0xFF);
                    } else if (buffer[i] == 13) {
                        in = true;
                        temp.clear();
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

        return records;
    }

    public static ArrayList<Record> readData(Context context, String fileName) {
        ArrayList<Record> records = new ArrayList<>();
        try {
            InputStream inputStream = context.getAssets().open(fileName);
            byte[] buffer = new byte[1024];
            int bytesRead;

            ArrayList<String> temp = new ArrayList<>();
            ArrayList<Integer> stream = new ArrayList<>();
            boolean in = false;
            int out = 0;


            int index = 0;
            int currentLengthOfRecord = -1;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                for (int i = 0; i < bytesRead; i++) {
                    if (out > 0) {
                        out--;
                        continue;
                    }

                    if (!in) {
                        in = true;
                        continue;
                    }

                    temp.add(String.format("%02X", buffer[i]));
                    stream.add(buffer[i] & 0xFF);

                    if (stream.size() == 16) {
                        currentLengthOfRecord = stream.get(7) * 1 + stream.get(8) * 16*16;
                    } else if (currentLengthOfRecord >= 0) {
                        if (stream.size() >= (9 + currentLengthOfRecord)) {
                            Record record = processRecord(stream);
                            Log.d("pttt", "" + index++);
                            if (record != null  &&  record.type == 160) {
                                records.add(record);
                            }
                            temp.clear();
                            stream.clear();
                            in = false;
                            out = 3;
                        }
                    }
                }
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return records;
    }

    private static Record processRecord(ArrayList<Integer> stream) {
        if (stream.get(0) != 160) {
            // TODO: 20/03/2024 implement type 161 (sensors) 6byte+6byte
            return null;
        }

//        Log.d("pttt", "" + stream.size());
        Record record = new Record();
        record.type = stream.get(0);
        record.unixTime = stream.get(1) * 1 + stream.get(2) * 16*16 + stream.get(3) * 16*16*16*16 + stream.get(4) * 16*16*16*16*16*16;
        record.msTime = stream.get(5) * 1 + stream.get(6) * 16*16;
        record.lengthOfRecord = stream.get(7) * 1 + stream.get(8) * 16*16;
        record.packetIndex = stream.get(9) * 1 + stream.get(10) * 16*16;
        int channelMappingValue = stream.get(11) * 1 + stream.get(12) * 16*16;
        record.samplingRate = stream.get(13) * 1 + stream.get(14) * 16*16;
        record.downSamplingFactor = stream.get(15);

//        Log.d("pttt", "type: " + record.type);
//        Log.d("pttt", "unixTime: " + record.unixTime);
//        Log.d("pttt", "msTime: " + record.msTime);
//        Log.d("pttt", "lengthOfRecord: " + record.lengthOfRecord);
//        Log.d("pttt", "packetIndex: " + record.packetIndex);
//        Log.d("pttt", "channelMappingValue: " + channelMappingValue);
//        Log.d("pttt", "samplingRate: " + record.samplingRate);
//        Log.d("pttt", "downSamplingFactor: " + record.downSamplingFactor);

        for (int i = 0; i < 16; i++) {
            if ((channelMappingValue & (1 << i)) > 0) {
                record.channelMapping[i] = 1;
            }
        }
//        Log.d("pttt", "channelMapping: " + Arrays.toString(record.channelMapping));

        for (int i = 0; i < record.data.length; i++) {
            record.data[i] = new LinkedList();
        }

        if (channelMappingValue > 0) {
            int currentChannel = 0;

            for (int i = 16; i < stream.size(); i += 2) {
                while (true) {
                    if (currentChannel >= 16) {
                        currentChannel = 0;
                    }
                    if (record.channelMapping[currentChannel] == 1) {
                        break;
                    }
                    currentChannel++;
                }

                int val = stream.get(i) * 1 + stream.get(i+1) * 16*16;
                //Log.d("pttt", "val: " + val);
                record.data[currentChannel].add(val);

                currentChannel++;
            }
        }

        return record;
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