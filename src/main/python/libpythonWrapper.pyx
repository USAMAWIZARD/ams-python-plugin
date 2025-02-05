import numpy as np
import cv2
import cython
import time
from cypes import *
import time
from libc.stdint cimport (uint8_t, uint16_t, uint32_t, uint64_t,
                          int8_t, int16_t, int32_t, int64_t)


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

cdef public void streamStarted(const char* streamid):
    try:
        py_streamid = streamid.decode('utf-8') 
        print("-------------on stream started in python plugin",py_streamid)
    except Exception as e:
        print("Exception occurred in streamStarted:", e)
        traceback.print_exc() 
    return

cdef public void streamFinished(const char* streamid):
    print("-------------on stream finished in python plugin")
    return

cdef public void onVideoFrame(const char* streamid, AVFrame *avframe):
    width = avframe.width
    height = avframe.height
    try:
        py_streamid = streamid.decode('utf-8') 
        print("new test 3")
        y_data = np.frombuffer(avframe.data[0], dtype=np.uint8, count=width * height).reshape((height, width))
        u_data = np.frombuffer(avframe.data[1], dtype=np.uint8, count=(width // 2) * (height // 2)).reshape((height // 2, width // 2))
        v_data = np.frombuffer(avframe.data[2], dtype=np.uint8, count=(width // 2) * (height // 2)).reshape((height // 2, width // 2))

        print("testing1 ")

        u_resized = cv2.resize(u_data, (width, height), interpolation=cv2.INTER_LINEAR)
        v_resized = cv2.resize(v_data, (width, height), interpolation=cv2.INTER_LINEAR)

        print("testing ")

        yuv_image = cv2.merge((y_data, u_resized, v_resized))

        rgb_image = cv2.cvtColor(yuv_image, cv2.COLOR_YUV2RGB)

        cv2.imwrite("output.jpg", rgb_image)

    except Exception as e:
        print("Exception occurred in onVideoFrame:", e)
        print("on video frame recieved in python : ", streamid,width,height,avframe.format)
    return

cdef public void onAudioFrame(const char* streamid, avfame):
    py_streamid = streamid.decode('utf-8') 
    # print("on audio frame recieved in python : ", streamid)
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
