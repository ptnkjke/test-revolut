package revolut.test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import revolut.test.configuration.HttpServer;
import revolut.test.configuration.Module;

public class Context {
    private Injector injector = Guice.createInjector(new Module());
    private HttpServer httpServer;

    public void start() {
        httpServer = injector.getInstance(HttpServer.class);
        httpServer.start();
    }

    public void end() {
        httpServer.shutDown();
    }

    public <T> T get(Class<T> key) {
        return injector.getInstance(key);
    }
}
