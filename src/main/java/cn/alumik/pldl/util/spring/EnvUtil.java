package cn.alumik.pldl.util.spring;

import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class EnvUtil implements EnvironmentAware {

    private static Environment env;

    @Override
    public void setEnvironment(@NonNull Environment env) {
        EnvUtil.env = env;
    }

    public static String getProperty(String name) {
        return env.getProperty(name);
    }
}
