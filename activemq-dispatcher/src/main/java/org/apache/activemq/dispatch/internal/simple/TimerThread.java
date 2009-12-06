package org.apache.activemq.dispatch.internal.simple;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.apache.activemq.dispatch.DispatchQueue;
import org.apache.activemq.util.TimerHeap;

import static org.apache.activemq.dispatch.internal.simple.TimerThread.Type.*;

final public class TimerThread extends Thread {
    enum Type {
        RELATIVE,
        ABSOLUTE,
        SHUTDOWN
    }
    final private static class TimerRequest {
        Type type;
        long time;
        TimeUnit unit;
        Runnable runnable;
        DispatchQueue target;
    }

    private final Object mutex = new Object();
    private ArrayList<TimerRequest> requests = new ArrayList<TimerRequest>();
    
    public TimerThread(SimpleDispatcher dispatcher) {
        setName(dispatcher.getLabel()+" timer");
        setDaemon(true);
    }

    public final void addAbsolute(Runnable runnable, DispatchQueue target, long time, TimeUnit unit) {
        TimerRequest request = new TimerRequest();
        request.type = ABSOLUTE;
        request.time = time;
        request.unit = unit;
        request.runnable = runnable;
        request.target = target;
        add(request);
    }

    public final void addRelative(Runnable runnable, DispatchQueue target, long delay, TimeUnit unit) {
        TimerRequest request = new TimerRequest();
        request.type = RELATIVE;
        request.time = delay;
        request.unit = unit;
        request.runnable = runnable;
        request.target = target;
        add(request);
    }

    public final void shutdown(Runnable onShutdown) {
        TimerRequest request = new TimerRequest();
        request.type = SHUTDOWN;
        request.runnable = onShutdown;
        add(request);
    }

    private void add(TimerRequest request) {
        synchronized(mutex) {
            requests.add(request);
            mutex.notify();
        }
    }

    public void run() {
        
        final TimerHeap<TimerRequest> timerHeap = new TimerHeap<TimerRequest>() {
            @Override
            protected final void execute(TimerRequest request) {
                request.target.dispatchAsync(request.runnable);
            }
        };
        
        ArrayList<TimerRequest> swaped = new ArrayList<TimerRequest>();
        
        try {
            for(;;) {

                synchronized(mutex) {
                    // Swap the arrays.
                    ArrayList<TimerRequest> t = requests;
                    requests = swaped;
                    swaped = t;
                }
                
                if( !swaped.isEmpty() ) {
                    for (TimerRequest request : swaped) {
                        switch( request.type ) {
                        case RELATIVE:
                            timerHeap.addRelative(request, request.time, request.unit);
                            break;
                        case ABSOLUTE:
                            timerHeap.addAbsolute(request, request.time, request.unit);
                            break;
                        case SHUTDOWN:
                            if( request.runnable!=null ) {
                                request.runnable.run();
                            }
                            return;
                        }
                    }
                    swaped.clear();
                }
                
                timerHeap.executeReadyTimers();

                long start = System.nanoTime();
                long next = timerHeap.timeToNext(TimeUnit.NANOSECONDS);
                
                if( next==0 ) {
                    continue;
                }
                
                // if it's coming up soon.. just spin..
                if( next>0 && next < 1000 ) {
                    while( System.nanoTime()-start < next ) {
                    }
                    continue;
                }
                
                long waitms = next / 1000000;
                int waitns = (int) (next % 1000000);
                synchronized(mutex) {
                    if( requests.isEmpty() ) {
                        if(next==-1) {
                            mutex.wait();
                        }  else {
                            mutex.wait(waitms, waitns);
                        }
                    }
                }                
            }
        } catch (InterruptedException e) {
        }
    }
}
