#add your imports here <

import numpy as np
import subprocess
import cv2
import time
import time
from cypes import *
import cython
from libc.stdint cimport (uint8_t, uint16_t, uint32_t, uint64_t,
                          int8_t, int16_t, int32_t, int64_t)

cdef streamidProcessDict

cdef extern from "libavutil/frame.h":
    cdef struct AVFrame:
        uint8_t *data[8]
        int width;
        int height;
        int format;
        int quality;

cdef extern from "libavcodec/avcodec.h":
    struct AVPacket:
        pass

cdef public void init_python_plugin_state():
    # initialize all global variables and state of the program in this functions it will be called once when program is initialized
    global streamidProcessDict
    try:
        streamidProcessDict = {}
    except Exception as e:
        print("Exception occurred in init_python_plugin_state:", e)
    return


cdef public void init_restream(streamid):
    try:
        global streamidProcessDict
        print("initializing writer for stream"+streamid)
        rtmpUrl = 'rtmp://127.0.0.1/WebRTCAppEE/' + streamid + "frompython"
        send_gst = " appsrc !  videoconvert ! video/x-raw,format=I420 ! videoconvert ! x264enc tune=zerolatency speed-preset=veryfast  ! video/x-h264,stream-format=byte-stream,alignment=au ! h264parse ! queue !  flvmux ! rtmpsink location=" + rtmpUrl

        out_send = cv2.VideoWriter(send_gst, 0, 30, (640, 480))    

        print("done writer for stream "+streamid)
        streamidProcessDict[streamid] = out_send
    except Exception as e:
        print("Exception occurred in init_restream :", e)
    return

cdef public void uninit_restream(streamid):
    try:
        global streamidProcessDict

        if streamid in streamidProcessDict:
            print("releasing re streaming resources for "+ streamid)
            streamidProcessDict[streamid].release()
        else:
            print("failed to release video stream no such stream exist "+streamid)
    except Exception as e:
        print("Exception occurred in uninit_restream :", e)

    return;


cdef public void streamStarted(const char* utfstreamid):
    try:
        streamid = utfstreamid.decode('utf-8') 
        init_restream(streamid)
        print("-------------on stream started in python plugin---------------",streamid)
    except Exception as e:
        print("Exception occurred in streamStarted:", e)
    return

cdef public void streamFinished(const char* utfstreamid):
    try:
        streamid = utfstreamid.decode('utf-8') 
        uninit_restream(streamid)
        print("-------------on stream finished in python plugin------------------",streamid)
    except Exception as e:
        print("Exception occurred in streamFinished:", e)
    return

cdef public void onVideoFrame(const char* streamid, AVFrame *avframe):
    global streamidProcessDict

    width = avframe.width
    height = avframe.height

    try:
        global streamidProcessDict
        py_streamid = streamid.decode('UTF-8')

        # print("on video frame recieved in python : ", py_streamid,width,height,avframe.format)

        y_data = np.frombuffer(avframe.data[0], dtype=np.uint8, count=width * height).reshape((height, width)).copy()  # Make a writable copy
        u_data = np.frombuffer(avframe.data[1], dtype=np.uint8, count=(width // 2) * (height // 2)).reshape((height // 2, width // 2))
        v_data = np.frombuffer(avframe.data[2], dtype=np.uint8, count=(width // 2) * (height // 2)).reshape((height // 2, width // 2))

        u_resized = cv2.resize(u_data, (width, height), interpolation=cv2.INTER_LINEAR)
        v_resized = cv2.resize(v_data, (width, height), interpolation=cv2.INTER_LINEAR)

        yuv_image = cv2.merge((y_data, u_resized, v_resized))

        # rgb_image = cv2.cvtColor(yuv_image, cv2.COLOR_YUV2RGB)

        cv2.rectangle(y_data, (100, 100), (400, 400), (255), -1)

        new_yuv_data = np.concatenate([y_data.flatten(), u_resized.flatten(), v_resized.flatten()])
        new_yuv_image = cv2.merge((y_data, u_resized, v_resized))

        # cv2.imwrite("output.jpg", new_yuv_image)

        streamidProcessDict[py_streamid].write(new_yuv_image)

    except Exception as e:
        print("Exception occurred in onVideoFrame:", e)
    return

cdef public void onAudioFrame(const char* streamid, avfame):
    py_streamid = streamid.decode('utf-8') 
    print("on audio frame recieved in python : ", streamid)
    return

cdef public void onVideoPacket(const char* streamid, AVPacket *avpacket):
    py_streamid = streamid.decode('utf-8') 
    print("on video packet recieved in python : ", streamid)
    return

cdef public void onAudioPacket(const char *streamid, AVPacket *avpacket):
    py_streamid = streamid.decode('utf-8') 
    print("on audio packet recieved in python : ", streamid)
    return

cdef public void setVideoStreamInfo(const char* streamid,const void *audioStreamInfo):
    py_streamid = streamid.decode('utf-8') 
    print("on video stream info")
    return

cdef public void setAudioStreamInfo(const char* streamId, const void *audioStreamInfo):
    print("on audio stream info")
    return
