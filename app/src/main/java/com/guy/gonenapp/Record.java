package com.guy.gonenapp;

import java.util.LinkedList;

public class Record {

    int type;
    long unixTime;
    int msTime;
    int lengthOfRecord;
    int packetIndex;
    int[] channelMapping = new int[16];
    int samplingRate;
    int downSamplingFactor;
    LinkedList<Integer>[] data = new LinkedList[16];

    public Record() {}
}
