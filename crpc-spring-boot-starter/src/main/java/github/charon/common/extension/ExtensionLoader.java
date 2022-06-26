package github.charon.common.extension;

import github.charon.common.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ExtensionLoader<T> {

    private static final String SERVICE_DIRECTORY = "META-INF/extensions/";
    private static final Map<Class<?>, ExtensionLoader<?>> EXTENSION_LOADERS = new ConcurrentHashMap<>();
    private static final Map<Class<?>, Object> EXTENSION_INSTANCES = new ConcurrentHashMap<>();

    private final Class<?> type;
    private final Map<String, Holder<Object>> cachedInstances = new ConcurrentHashMap<>();
    private final Holder<Map<String, Class<?>>> cachedClasses = new Holder<>();

    private ExtensionLoader(Class<?> type) {
        this.type = type;
    }

    @SuppressWarnings("unchecked")
    public static <S> ExtensionLoader<S> getExtensionLoader(Class<S> type) {
        if (null == type) throw new IllegalArgumentException("extension type should not be null");

        if (!type.isInterface()) throw new IllegalArgumentException("extension type must be an interface");

        if (null == type.getAnnotation(SPI.class)) throw new IllegalArgumentException("extension type must be annotated by @SPI");

        // 先从缓存获取，没命中就创建一个
        ExtensionLoader<S> extensionLoader = (ExtensionLoader<S>) EXTENSION_LOADERS.get(type);
        if (null == extensionLoader) {
            EXTENSION_LOADERS.putIfAbsent(type, new ExtensionLoader<S>(type));
            extensionLoader = (ExtensionLoader<S>) EXTENSION_LOADERS.get(type);
        }
        return extensionLoader;
    }

    public T getExtension(String name) {
        if (StringUtil.isBlank(name)) throw new IllegalArgumentException("extension name should not be null or empty");

        Holder<Object> holder = cachedInstances.get(name);
        if (null == holder) {
            cachedInstances.putIfAbsent(name, new Holder<>());
            holder = cachedInstances.get(name);
        }

        Object instance = holder.get();
        if (null == instance) {
            synchronized (holder) {
                instance = holder.get();
                if (null == instance) {
                    instance = createExtension(name);
                    holder.set(instance);
                }
            }
        }
        return (T) instance;
    }

    @SuppressWarnings("unchecked")
    private T createExtension(String name) {
        Class<?> klass = getExtensionClasses().get(name);
        if (null == klass) throw new RuntimeException("no such extension of name " + name);
        T instance = (T) EXTENSION_INSTANCES.get(klass);
        if (null == instance) {
            try {
                EXTENSION_INSTANCES.putIfAbsent(klass, klass.getDeclaredConstructor().newInstance());
                instance = (T) EXTENSION_INSTANCES.get(klass);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return instance;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Class<?>> getExtensionClasses() {
        Map<String, Class<?>> classes = cachedClasses.get();
        if (null == classes) {
            synchronized (cachedClasses) {
                classes = cachedClasses.get();
                if (null == classes) {
                    classes = new HashMap<>();
                    loadDirectory(classes);
                    cachedClasses.set(classes);
                }
            }
        }
        return classes;
    }

    private void loadDirectory(Map<String, Class<?>> extensionClasses) {
        String fileName = ExtensionLoader.SERVICE_DIRECTORY + type.getName();
//        System.out.println(fileName);
        try {
            Enumeration<URL> urls;
            ClassLoader classLoader = ExtensionLoader.class.getClassLoader();
            urls = classLoader.getResources(fileName);
            if (null != urls) {
                while (urls.hasMoreElements()) {
                    URL resourceUrl = urls.nextElement();
                    loadResource(extensionClasses, classLoader, resourceUrl);
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void loadResource(Map<String, Class<?>> extensionClasses, ClassLoader classLoader, URL resourceUrl) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resourceUrl.openStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                final int ci = line.indexOf('#');
                if (ci >= 0)
                    line = line.substring(0, ci);

                line = line.trim();
                if (line.length() > 0) {
                    try {
                        final int ei = line.indexOf('=');
                        String name = line.substring(0, ei).trim();
                        String klassName = line.substring(ei+1).trim();
                        if (name.length() > 0 && klassName.length() > 0) {
                            Class<?> klass = classLoader.loadClass(klassName);
                            extensionClasses.put(name, klass);
                        }
//                        System.out.println(name + " == " + klassName);
                    } catch (ClassNotFoundException e) {
                        log.error(e.getMessage());
                    }
                }
            }

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

}
