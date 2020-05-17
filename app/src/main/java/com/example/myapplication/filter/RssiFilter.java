package com.example.myapplication.filter;

import com.example.myapplication.Beacons;

public interface RssiFilter {
    float filter(Beacons beacon);
}
