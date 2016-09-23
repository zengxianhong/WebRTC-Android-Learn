/*
 *  Copyright 2016 The WebRTC project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a BSD-style license
 *  that can be found in the LICENSE file in the root of the source
 *  tree. An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */

package org.webrtc;

import java.util.HashMap;
import java.util.Map;

// Java-side of androidmetrics_jni.cc.
//
// Rtc histograms can be queried through the API, getAndReset().
// The returned map holds the name of a histogram and its samples.
//
// Example of |map| with one histogram:
// |name|: "WebRTC.Video.InputFramesPerSecond"
//     |min|: 1
//     |max|: 100
//     |bucketCount|: 50
//     |samples|: [30]:1
//
// Most histograms are not updated frequently (e.g. most video metrics are an
// average over the call and recorded when a stream is removed).
// The metrics can for example be retrieved when a peer connection is closed.
// 度量
public class Metrics {
    static {
        System.loadLibrary("jingle_peerconnection_so");
    }

    public final Map<String, HistogramInfo> map =
            new HashMap<>();  // <name, HistogramInfo>

    /**
     * Class holding histogram information.
     */
    public static class HistogramInfo {
        public final int min;
        public final int max;
        public final int bucketCount;
        public final Map<Integer, Integer> samples =
                new HashMap<>();  // <value, # of events>

        public HistogramInfo(int min, int max, int bucketCount) {
            this.min = min;
            this.max = max;
            this.bucketCount = bucketCount;
        }

        public void addSample(int value, int numEvents) {
            samples.put(value, numEvents);
        }
    }

    private void add(String name, HistogramInfo info) {
        map.put(name, info);
    }

    // Enables gathering of metrics (which can be fetched with getAndReset()).
    // Must be called before PeerConnectionFactory is created.
    public static void enable() {
        nativeEnable();
    }

    // Gets and clears native histograms.
    public static Metrics getAndReset() {
        return nativeGetAndReset();
    }

    private static native void nativeEnable();

    private static native Metrics nativeGetAndReset();
}