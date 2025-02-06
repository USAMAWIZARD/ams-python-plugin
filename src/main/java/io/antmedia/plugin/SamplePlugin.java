package io.antmedia.plugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.access.method.P;
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

    if (!onlyCallforThisApps())
      return;

    IAntMediaStreamHandler app = getApplication();
    app.addStreamListener(this);
  }

  public ApplicationContext getApplicationContext() {
    return this.applicationContext;
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
    if (!NativeInterface.PY_WRAPPER.INSTANCE.Py_IsInitialized()) {
      logger.info("python context not running intializing new context");
      NativeInterface.PY_WRAPPER.INSTANCE.init_py_and_wrapperlib();

      NativeInterface.PY_WRAPPER.INSTANCE.aquirejil();
      NativeInterface.PY_WRAPPER.INSTANCE.init_python_plugin_state();
      NativeInterface.PY_WRAPPER.INSTANCE.releasejil();

      return;
    }
    logger.info("already running python context");
    return;
  }

  public void register(String streamId) {
    IAntMediaStreamHandler app = getApplication();
    app.addFrameListener(streamId, frameListener);
    // app.addPacketListener(streamId, packetListener);
  }

  public IAntMediaStreamHandler getApplication() {
    return (IAntMediaStreamHandler) applicationContext.getBean(AntMediaApplicationAdapter.BEAN_NAME);
  }

  public boolean onlyCallforThisApps() {
    return this.applicationContext.getApplicationName().startsWith("/LiveApp");
  }

  @Override
  public void streamStarted(String streamId) {
    isPythonRunning();
    NativeInterface.PY_WRAPPER.INSTANCE.aquirejil();
    NativeInterface.PY_WRAPPER.INSTANCE.streamStarted(streamId);
    NativeInterface.PY_WRAPPER.INSTANCE.releasejil();
    register(streamId);
  }

  @Override
  public void streamFinished(String streamId) {
    isPythonRunning();
    NativeInterface.PY_WRAPPER.INSTANCE.aquirejil();
    NativeInterface.PY_WRAPPER.INSTANCE.streamFinished(streamId);
    NativeInterface.PY_WRAPPER.INSTANCE.releasejil();
  }

  @Override
  public void joinedTheRoom(String roomId, String streamId) {
    isPythonRunning();
    NativeInterface.PY_WRAPPER.INSTANCE.aquirejil();
    NativeInterface.PY_WRAPPER.INSTANCE.joinedTheRoom(roomId, streamId);
    NativeInterface.PY_WRAPPER.INSTANCE.releasejil();
  }

  @Override
  public void leftTheRoom(String roomId, String streamId) {
    isPythonRunning();
    NativeInterface.PY_WRAPPER.INSTANCE.aquirejil();
    NativeInterface.PY_WRAPPER.INSTANCE.leftTheRoom(roomId, streamId);
    NativeInterface.PY_WRAPPER.INSTANCE.releasejil();
  }

}
