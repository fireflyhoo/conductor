package cn.yayatao.middleware.conductor.thread;


import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

public class NamedThreadFactory implements ThreadFactory {

    public final String id;
    protected final AtomicLong n = new AtomicLong(1);
    private final int priority;
    private final ClassLoader contextClassLoader;
    private final boolean daemon;

    public NamedThreadFactory(String id) {
        this(id, false);
    }

    public NamedThreadFactory(String id, boolean daemon) {
        this(id, Thread.NORM_PRIORITY, daemon);
    }

    public NamedThreadFactory(String id, int priority, boolean daemon) {
        this(id, priority, null, daemon);
    }

    public NamedThreadFactory(String id, int priority, ClassLoader contextClassLoader, boolean daemon) {
        this.id = id;
        this.priority = priority;
        this.contextClassLoader = contextClassLoader;
        this.daemon = daemon;
    }

    @Override
    public Thread newThread(Runnable r) {
        String name = String.join("-", id, String.valueOf(n.getAndIncrement()));
        Thread thread = new Thread(r, name);
        thread.setPriority(priority);
        if(this.daemon){
            thread.setDaemon(true);
        }
        if (contextClassLoader != null) {
            thread.setContextClassLoader(contextClassLoader);
        }
        return thread;

    }

    public String getId() {
        return id;
    }

}