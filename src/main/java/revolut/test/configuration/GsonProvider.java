package revolut.test.configuration;

import com.google.gson.Gson;
import com.google.inject.Provider;


public class GsonProvider implements Provider<Gson> {

    @Override
    public Gson get() {
        return new Gson();
    }
}
