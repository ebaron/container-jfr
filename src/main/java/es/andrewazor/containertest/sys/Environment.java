package es.andrewazor.containertest.sys;

public class Environment {

    public String getEnv(String key) {
        return System.getenv(key);
    }

    public String getEnv(String key, String def) {
        String res = getEnv(key);
        if (res == null || res.isBlank()) {
            return def;
        }
        return res;
    }

    public String getProperty(String key) {
        return System.getProperty(key);
    }

    public String getProperty(String key, String def) {
        return System.getProperty(key, def);
    }

}