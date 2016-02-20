package org.yli.web.rbm.proxy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yli.web.rbm.memcached.MemcachedUtil;
import org.yli.web.rbm.services.Analyzer;
import org.yli.web.rbm.services.IAnalyzer;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by yli on 2/20/2016.
 */
public class CachedAnalyzerProxy implements InvocationHandler {

  private static final Logger LOGGER = LogManager.getLogger(CachedAnalyzerProxy.class);

  private IAnalyzer analyzer;

  private final MemcachedUtil cached = new MemcachedUtil("localhost", "11211");

  public CachedAnalyzerProxy(IAnalyzer analyzer) {
    this.analyzer = analyzer;
  }

  public static IAnalyzer bind(IAnalyzer analyzer) {
    return (IAnalyzer) Proxy.newProxyInstance(
        analyzer.getClass().getClassLoader(), new Class[] { IAnalyzer.class }, new CachedAnalyzerProxy(analyzer));
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    LOGGER.debug("invoking " + method.getName());
    String result = cached.get(method.getName());
    if (result == null) {
      LOGGER.debug("cache is not hit.");
      result = (String) method.invoke(analyzer, args);
      cached.set(method.getName(), result);
    } else {
      LOGGER.debug("cache is hit");
    }
    return result;
  }
}
