package io.antmedia.app;

import org.bytedeco.ffmpeg.avcodec.AVPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.antmedia.plugin.api.IPacketListener;
import io.antmedia.plugin.api.StreamParametersInfo;

public class SamplePacketListener implements IPacketListener {

  protected static Logger logger = LoggerFactory.getLogger(SamplePacketListener.class);

  @Override
  public void writeTrailer(String streamId) {
    System.out.println("SamplePacketListener.writeTrailer()");
  }

  @Override
  public AVPacket onVideoPacket(String streamId, AVPacket packet) {
    NativeInterface.JNA_RTSP_SERVER.INSTANCE.aquirejil();
    NativeInterface.JNA_RTSP_SERVER.INSTANCE.onVideoPacket(streamId, packet.address());
    NativeInterface.JNA_RTSP_SERVER.INSTANCE.releasejil();
    return packet;
  }

  @Override
  public AVPacket onAudioPacket(String streamId, AVPacket packet) {
    NativeInterface.JNA_RTSP_SERVER.INSTANCE.aquirejil();
    NativeInterface.JNA_RTSP_SERVER.INSTANCE.onAudioPacket(streamId, packet.address());
    NativeInterface.JNA_RTSP_SERVER.INSTANCE.releasejil();

    return packet;
  }

  @Override
  public void setVideoStreamInfo(String streamId, StreamParametersInfo videoStreamInfo) {
  }

  @Override
  public void setAudioStreamInfo(String streamId, StreamParametersInfo audioStreamInfo) {
  }

}
