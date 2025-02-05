package io.antmedia.plugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import io.antmedia.AntMediaApplicationAdapter;
import io.antmedia.app.NativeInterface;
import io.antmedia.app.SampleFrameListener;
import io.antmedia.app.SamplePacketListener;
import io.antmedia.muxer.IAntMediaStreamHandler;
import io.antmedia.muxer.MuxAdaptor;
import io.antmedia.plugin.api.IFrameListener;
import io.antmedia.plugin.api.IStreamListener;
import io.vertx.core.Vertx;

@Component(value = "plugin.myplugin")
public class SamplePlugin extends NativeInterface implements ApplicationContextAware, IStreamListener {

  public static final String BEAN_NAME = "web.handler";
  protected static Logger logger = LoggerFactory.getLogger(SamplePlugin.class);

  private Vertx vertx;
  private SampleFrameListener frameListener = new SampleFrameListener();
  private SamplePacketListener packetListener = new SamplePacketListener();
  private ApplicationContext applicationContext;

  static int is_init = 0;

  public SamplePlugin() {
    if (is_init == 0) {
      is_init = 1;
    }

  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
    vertx = (Vertx) applicationContext.getBean("vertxCore");

    IAntMediaStreamHandler app = getApplication();
    app.addStreamListener(this);
  }

  public MuxAdaptor getMuxAdaptor(String streamId) {
    IAntMediaStreamHandler application = getApplication();
    MuxAdaptor selectedMuxAdaptor = null;

    if (application != null) {
      selectedMuxAdaptor = application.getMuxAdaptor(streamId);
    }

    return selectedMuxAdaptor;
  }

  public void isPythonRunning() {
    if (!NativeInterface.JNA_RTSP_SERVER.INSTANCE.Py_IsInitialized()) {
      logger.info("python context not running intializing new context");
      NativeInterface.JNA_RTSP_SERVER.INSTANCE.init_py_and_wrapperlib();
      return;
    }
    logger.info("already running python context");
    return;
  }

  public void register(String streamId) {
    IAntMediaStreamHandler app = getApplication();
    app.addFrameListener(streamId, frameListener);
    //app.addPacketListener(streamId, packetListener);
  }

  public IAntMediaStreamHandler getApplication() {
    return (IAntMediaStreamHandler) applicationContext.getBean(AntMediaApplicationAdapter.BEAN_NAME);
  }

  @Override
  public void streamStarted(String streamId) {
    isPythonRunning();
    NativeInterface.JNA_RTSP_SERVER.INSTANCE.aquirejil();
    NativeInterface.JNA_RTSP_SERVER.INSTANCE.streamStarted(streamId);
    NativeInterface.JNA_RTSP_SERVER.INSTANCE.releasejil();
    register(streamId);
  }

  @Override
  public void streamFinished(String streamId) {
    isPythonRunning();
    NativeInterface.JNA_RTSP_SERVER.INSTANCE.aquirejil();
    NativeInterface.JNA_RTSP_SERVER.INSTANCE.streamFinished(streamId);
    NativeInterface.JNA_RTSP_SERVER.INSTANCE.releasejil();
  }

  @Override
  public void joinedTheRoom(String roomId, String streamId) {
    isPythonRunning();
    NativeInterface.JNA_RTSP_SERVER.INSTANCE.aquirejil();
    NativeInterface.JNA_RTSP_SERVER.INSTANCE.joinedTheRoom(roomId, streamId);
    NativeInterface.JNA_RTSP_SERVER.INSTANCE.releasejil();
  }

  @Override
  public void leftTheRoom(String roomId, String streamId) {
    isPythonRunning();
    NativeInterface.JNA_RTSP_SERVER.INSTANCE.aquirejil();
    NativeInterface.JNA_RTSP_SERVER.INSTANCE.leftTheRoom(roomId, streamId);
    NativeInterface.JNA_RTSP_SERVER.INSTANCE.releasejil();
  }

}
