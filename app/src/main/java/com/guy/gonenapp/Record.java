package com.guy.gonenapp;

public class Record {

    int type;
    long unixTime;
    int msTime;
    int lengthOfRecord;
    int packetIndex;
    int channelMapping;
    int samplingRate;
    int downSamplingFactor;
    int[] data;

    public Record() {}
}
