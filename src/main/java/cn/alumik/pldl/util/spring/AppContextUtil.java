package cn.alumik.pldl.util.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class AppContextUtil implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(@NonNull ApplicationContext context) throws BeansException {
        AppContextUtil.context = context;
    }

    public static <T> T getBean(Class<T> className) throws BeansException {
        return context.getBean(className);
    }
}
