package zen.ilgo.pipeline;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

/**
 * A singleton class. A pool for everybody.
 * How can i constrain the shutdown to a single caller?
 *
 * @author roger holenweger (ilgo711@gmail.com)
 * @since Sep 18, 2009
 */
public final class Pool {
    
    public static Logger log = Logger.getLogger("zen.ilgo.tools.pipeline");
    
    private final ExecutorService executor;
    private static Pool pool;
    
    /**
     * pool shutdown timeout in Seconds
     */
    private final int TIMEOUT = 10;
    
    private Pool() {
        executor = Executors.newCachedThreadPool();
    }
    
    public static Pool getInstance() {
        if (pool == null) {
            pool = new Pool();
        }
        return pool;
    }
    
    public <T> Future<T> submit(Callable<T> callable) {
        return executor.submit(callable);
    }
    
    public void execute(Runnable runnable) {
        executor.execute(runnable);
    }
    
    public boolean shutPool() {
        
        log.debug("Pool shutdown");

        executor.shutdown();
        try {
            // Wait a while for existing tasks to terminate
            if (!executor.awaitTermination(TIMEOUT, TimeUnit.SECONDS)) {
                executor.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!executor.awaitTermination(TIMEOUT, TimeUnit.SECONDS)) {             
                    return false;
                }                
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            executor.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
        return true;
    }
}
