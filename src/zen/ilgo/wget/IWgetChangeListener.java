package zen.ilgo.wget;

import java.util.EventListener;

/**
 * Interface to be implemented by an Observer.
 *
 * @author ilgo (ilgo711@gmail.com)
 * @since Apr 4, 2009
 */
public interface IWgetChangeListener extends EventListener {

    /**
     *
     * @param e the Event
     */
    void wgetStateChanged(WgetChangeEvent e);
}
