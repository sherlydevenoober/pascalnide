/*
 * Copyright (C) 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.googlecode.sl4a.facade;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.duy.pascal.interperter.builtin_libraries.PascalLibrary;
import com.duy.pascal.interperter.builtin_libraries.android.AndroidLibraryManager;
import com.duy.pascal.interperter.builtin_libraries.android.activity.PascalFutureResult;
import com.duy.pascal.interperter.builtin_libraries.annotations.PascalMethod;
import com.duy.pascal.interperter.builtin_libraries.annotations.PascalParameter;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.googlecode.sl4a.event.Event;
import com.googlecode.sl4a.jsonrpc.JsonBuilder;
import com.googlecode.sl4a.rpc.RpcDefault;
import com.googlecode.sl4a.rpc.RpcDeprecated;
import com.googlecode.sl4a.rpc.RpcOptional;
import com.duy.pascal.interperter.ast.expressioncontext.ExpressionContextMixin;

import org.json.JSONException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Manage the event queue. <br>
 * <br>
 * <b>Usage Notes:</b><br>
 * EventFacade APIs interact with the Event Queue (a data buffer containing up to 1024 event
 * entries).<br>
 * Events are automatically entered into the Event Queue following API calls such as startAllSensor()
 * and startLocating().<br>
 * The Event Facade provides control over how events are entered into (and removed from) the Event
 * Queue.<br>
 * The Event Queue provides a useful means of recording background events (such as sensor data) when
 * the phone is busy with foreground activities.
 *
 * @author Felix Arends (felix.arends@gmail.com)
 */
public class AndroidEvent implements PascalLibrary {
    /**
     * The maximum length of the event queue. Old events will be discarded when this limit is
     * exceeded.
     */
    private static final int MAX_QUEUE_SIZE = 1024;
    private final Queue<Event> mEventQueue = new ConcurrentLinkedQueue<>();
    private final CopyOnWriteArrayList<EventObserver> mGlobalEventObservers =
            new CopyOnWriteArrayList<>();
    private final Multimap<String, EventObserver> mNamedEventObservers = Multimaps
            .synchronizedListMultimap(ArrayListMultimap.<String, EventObserver>create());
    private final HashMap<String, BroadcastListener> mBroadcastListeners =
            new HashMap<>();
    private final Context mContext;
    private EventServer mEventServer = null;

    public AndroidEvent(AndroidLibraryManager manager) {
        mContext = manager.getContext();
    }

    /**
     * Example (python): droid.eventClearBuffer()
     */
    @SuppressWarnings("unused")
    @PascalMethod(description = "Clears all events from the event buffer.")
    public void eventClearBuffer() {
        mEventQueue.clear();
    }

    /**
     * Registers a listener for a new broadcast signal
     */
    @SuppressWarnings("unused")
    @PascalMethod(description = "Registers a listener for a new broadcast signal")
    public boolean eventRegisterForBroadcast(
            @PascalParameter(name = "category") String category,
            @PascalParameter(name = "enqueue", description = "Should this events be added to the event queue or only dispatched") @RpcDefault(value = "true") Boolean enqueue) {
        if (mBroadcastListeners.containsKey(category)) {
            return false;
        }

        BroadcastListener b = new BroadcastListener(this, enqueue);
        IntentFilter c = new IntentFilter(category);
        mContext.registerReceiver(b, c);
        mBroadcastListeners.put(category, b);

        return true;
    }

    @SuppressWarnings("unused")
    @PascalMethod(description = "Stop listening for a broadcast signal")
    public void eventUnregisterForBroadcast(@PascalParameter(name = "category") String category) {
        if (!mBroadcastListeners.containsKey(category)) {
            return;
        }

        mContext.unregisterReceiver(mBroadcastListeners.get(category));
        mBroadcastListeners.remove(category);
    }

    @SuppressWarnings("unused")
    @PascalMethod(description = "Lists all the broadcast signals we are listening for")
    public Set<String> eventGetBrodcastCategories() {
        return mBroadcastListeners.keySet();
    }

    /**
     * Actual data returned in the map will depend on the type of event.
     * <p>
     * <pre>
     * Example (python):
     *     import android, time
     *     droid = android.Android()
     *     droid.startAllSensor()
     *     time.sleep(1)
     *     droid.eventClearBuffer()
     *     time.sleep(1)
     *     e = eventPoll(1).result
     *     event_entry_number = 0
     *     x = e[event_entry_ number]['data']['xforce']
     * </pre>
     * <p>
     * e has the format:<br>
     * [{u'data': {u'accuracy': 0, u'pitch': -0.48766891956329345, u'xmag': -5.6875, u'azimuth':
     * 0.3312483489513397, u'zforce': 8.3492730000000002, u'yforce': 4.5628165999999997, u'time':
     * 1297072704.813, u'ymag': -11.125, u'zmag': -42.375, u'roll': -0.059393649548292161, u'xforce':
     * 0.42223078000000003}, u'name': u'sensors', u'time': 1297072704813000L}]<br>
     * x has the string value of the x force data (0.42223078000000003) at the time of the event
     * entry. </pre>
     */

    @SuppressWarnings("unused")
    @PascalMethod(description = "Returns and removes the oldest n events (i.e. location or sensor update, etc.) from the event buffer.", returns = "A List of Maps of event properties.")
    public List<Event> eventPoll(
            @PascalParameter(name = "number_of_events") @RpcDefault("1") Integer number_of_events) {
        List<Event> events = Lists.newArrayList();
        for (int i = 0; i < number_of_events; i++) {
            Event event = mEventQueue.poll();
            if (event == null) {
                break;
            }
            events.add(event);
        }
        return events;
    }

    @PascalMethod(description = "Blocks until an event with the supplied name occurs. The returned event is not removed from the buffer.", returns = "Map of event properties.")
    public Event eventWaitFor(
            @PascalParameter(name = "eventName") final String eventName,
            @PascalParameter(name = "timeout", description = "the maximum time to wait (in ms)") @RpcOptional Integer timeout)
            throws InterruptedException {
        synchronized (mEventQueue) { // First check to make sure it isn't already there
            for (Event event : mEventQueue) {
                if (event.getName().equals(eventName)) {
                    return event;
                }
            }
        }
        final PascalFutureResult<Event> futureEvent = new PascalFutureResult<>();
        addNamedEventObserver(eventName, new EventObserver() {
            @Override
            public void onEventReceived(Event event) {
                if (event.getName().equals(eventName)) {
                    synchronized (futureEvent) {
                        if (!futureEvent.isDone()) {
                            futureEvent.set(event);
                            removeEventObserver(this);
                        }
                    }
                }
            }
        });
        if (timeout != null) {
            return futureEvent.get(timeout, TimeUnit.MILLISECONDS);
        } else {
            return futureEvent.get();
        }
    }

    @SuppressWarnings("unused")
    @PascalMethod(description = "Blocks until an event occurs. The returned event is removed from the buffer.", returns = "Map of event properties.")
    public Event eventWait(
            @PascalParameter(name = "timeout", description = "the maximum time to wait") @RpcOptional Integer timeout)
            throws InterruptedException {
        Event result;
        final PascalFutureResult<Event> futureEvent = new PascalFutureResult<>();
        synchronized (mEventQueue) { // Anything in queue?
            if (mEventQueue.size() > 0) {
                return mEventQueue.poll(); // return it.
            }
        }
        EventObserver observer = new EventObserver() {
            @Override
            public void onEventReceived(Event event) { // set up observer for any events.
                synchronized (futureEvent) {
                    if (!futureEvent.isDone()) {
                        futureEvent.set(event);
                        removeEventObserver(this);
                    }
                    mEventQueue.remove(event);
                }
            }
        };
        addGlobalEventObserver(observer);
        if (timeout != null) {
            result = futureEvent.get(timeout, TimeUnit.MILLISECONDS);
        } else {
            result = futureEvent.get();
        }
        removeEventObserver(observer); // Make quite sure this goes away.
        return result;
    }

    /**
     * <pre>
     * Example:
     *   import android
     *   from datetime import datetime
     *   droid = android.Android()
     *   t = datetime.now()
     *   droid.eventPost('Some Event', t)
     * </pre>
     */
    @PascalMethod(description = "Post an event to the event queue.")
    public void eventPost(
            @PascalParameter(name = "name", description = "Name of event") String name,
            @PascalParameter(name = "data", description = "Data contained in event.") String data,
            @PascalParameter(name = "enqueue", description = "Set to False if you don't want your events to be added to the event queue, just dispatched.")
                    boolean enqueue) {
        postEvent(name, data, enqueue);
    }

    /**
     * Post an event and queue it
     */
    public void postEvent(String name, Object data) {
        postEvent(name, data, true);
    }

    /**
     * Posts an event with to the event queue.
     */
    private void postEvent(String name, Object data, boolean enqueue) {
        Event event = new Event(name, data);
        if (enqueue) {
            mEventQueue.add(event);
            if (mEventQueue.size() > MAX_QUEUE_SIZE) {
                mEventQueue.remove();
            }
        }
        synchronized (mNamedEventObservers) {
            for (EventObserver observer : mNamedEventObservers.get(name)) {
                observer.onEventReceived(event);
            }
        }
        synchronized (mGlobalEventObservers) {
            for (EventObserver observer : mGlobalEventObservers) {
                observer.onEventReceived(event);
            }
        }
    }

    @SuppressWarnings("unused")
    @RpcDeprecated(value = "eventPost")
    @PascalMethod(description = "Post an event to the event queue.")
    public void rpcPostEvent(@PascalParameter(name = "name") String name,
                             @PascalParameter(name = "data") String data) {
        postEvent(name, data);
    }

    @SuppressWarnings("unused")
    @RpcDeprecated(value = "eventPoll")
    @PascalMethod(description = "Returns and removes the oldest event (i.e. location or sensor update, etc.) from the event buffer.", returns = "Map of event properties.")
    public Event receiveEvent() {
        return mEventQueue.poll();
    }

    @SuppressWarnings("unused")
    @RpcDeprecated(value = "eventWaitFor")
    @PascalMethod(description = "Blocks until an event with the supplied name occurs. The returned event is not removed from the buffer.", returns = "Map of event properties.")
    public Event waitForEvent(
            @PascalParameter(name = "eventName") final String eventName,
            @PascalParameter(name = "timeout", description = "the maximum time to wait") @RpcOptional Integer timeout)
            throws InterruptedException {
        return eventWaitFor(eventName, timeout);
    }

    @SuppressWarnings("unused")
    @PascalMethod(description = "Opens up a socket where you can read for events posted")
    public int startEventDispatcher(
            @PascalParameter(name = "port", description = "Port to use") @RpcDefault("0") @RpcOptional() Integer port) {
        if (mEventServer == null) {
            if (port == null) {
                port = 0;
            }
            mEventServer = new EventServer(port);
            addGlobalEventObserver(mEventServer);
        }
        return mEventServer.getAddress().getPort();
    }

    @PascalMethod(description = "Stops the event server, you can't read in the port anymore")
    public void stopEventDispatcher() throws RuntimeException {
        if (mEventServer == null) {
            throw new RuntimeException("Not RUNNING");
        }
        mEventServer.shutdown();
        removeEventObserver(mEventServer);
        mEventServer = null;
    }

    @Override
    public boolean instantiate(Map<String, Object> pluginargs) {
        return false;
    }

    @Override
    public void shutdown() {
        try {
            stopEventDispatcher();
        } catch (Exception ignored) {
        }
        // let others (like webviews) know we're going down
        postEvent("sl4a", "{\"shutdown\": \"event-facade\"}");
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void declareConstants(ExpressionContextMixin parentContext) {

    }

    @Override
    public void declareTypes(ExpressionContextMixin parentContext) {

    }

    @Override
    public void declareVariables(ExpressionContextMixin parentContext) {

    }

    @Override
    public void declareFunctions(ExpressionContextMixin parentContext) {

    }

    public void addNamedEventObserver(String eventName, EventObserver observer) {
        mNamedEventObservers.put(eventName, observer);
    }

    public void addGlobalEventObserver(EventObserver observer) {
        mGlobalEventObservers.add(observer);
    }

    public void removeEventObserver(EventObserver observer) {
        mNamedEventObservers.removeAll(observer);
        mGlobalEventObservers.remove(observer);
    }

    public interface EventObserver {
        void onEventReceived(Event event);
    }

    public class BroadcastListener extends android.content.BroadcastReceiver {
        private AndroidEvent mParent;
        private boolean mEnQueue;

        public BroadcastListener(AndroidEvent parent, boolean enqueue) {
            mParent = parent;
            mEnQueue = enqueue;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle data;
            if (intent.getExtras() != null) {
                data = (Bundle) intent.getExtras().clone();
            } else {
                data = new Bundle();
            }
            data.putString("action", intent.getAction());
            try {
                mParent.eventPost("sl4a", JsonBuilder.build(data).toString(), mEnQueue);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
}
