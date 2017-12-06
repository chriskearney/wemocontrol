package com.comadante.wemocontrol;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.AbstractScheduledService;
import org.apache.log4j.Logger;
import org.cybergarage.upnp.ControlPoint;
import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.device.DeviceChangeListener;
import org.cybergarage.upnp.device.SearchResponseListener;
import org.cybergarage.upnp.event.EventListener;
import org.cybergarage.upnp.ssdp.SSDPPacket;
import org.cybergarage.util.Debug;
import org.glassfish.jersey.message.internal.NullOutputStream;

import java.io.PrintStream;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class WemoControlService extends AbstractScheduledService {

    // TODO: I think the "insight" switches have a different device type identifier, but i don't have one to try.
    private static final List<String> BELKIN_INSIGHT_DEVICE_TYPES =
            Lists.newArrayList(
                    "urn:Belkin:device:controllee:1",
                    "urn:Belkin:device:lightswitch:1",
                    "urn:Belkin:device:insight:1",
                    "urn:Belkin:device:dimmer:1"
            );

    private final Logger logger = Logger.getLogger(WemoControlService.class);

    private ControlPoint controlPoint = new ControlPoint();
    private WemoDeviceRegistry wemoDeviceRegistry = new WemoDeviceRegistry();
    private final DevicePresenceConsumer devicePresenceConsumer;
    private final DeviceStateCheckConsumer deviceStateCheckConsumer;

    public WemoControlService(DevicePresenceConsumer devicePresenceConsumer, DeviceStateCheckConsumer deviceStateCheckConsumer) {
        this.devicePresenceConsumer = devicePresenceConsumer;
        this.deviceStateCheckConsumer = deviceStateCheckConsumer;
    }

    public void turnOff(String friendlyName) {
        wemoDeviceRegistry.turnOff(friendlyName);
    }

    public void turnOn(String friendlyName) {
        wemoDeviceRegistry.turnOn(friendlyName);
    }

    public void toggle(String friendlyName) {
        wemoDeviceRegistry.toggle(friendlyName);
    }

    public boolean on(String friendlyName) { return wemoDeviceRegistry.on(friendlyName); }

    public List<WemoControlDevice> getDevices() { return Lists.newArrayList(wemoDeviceRegistry.getDevices()); }

    public Optional<WemoControlDevice> getDevice(String friendlyName) {
        return wemoDeviceRegistry.getDevice(friendlyName);
    }

    @Override
    protected void startUp() throws Exception {
        Debug.getDebug().setOut(new PrintStream(new NullOutputStream()));
        controlPoint.addEventListener(new EventListener() {
            @Override
            public void eventNotifyReceived(String uuid, long seq, String varName, String value) {
                System.out.println("Received an event. seq: " + seq + " varName: " + varName + " value: " + value);
            }
        });
        controlPoint.addSearchResponseListener(new SearchResponseListener() {
            @Override
            public void deviceSearchResponseReceived(SSDPPacket ssdpPacket) {
//                System.out.println("Search Result Received: " + ssdpPacket.getUSN());
            }
        });
        controlPoint.addDeviceChangeListener(new DeviceChangeListener() {
            @Override
            public void deviceAdded(Device device) {
                System.out.println(device.getFriendlyName());
                if (!isBelkin(device)) {
                    return;
                }
                WemoControlDevice wemoControlDevice = new WemoControlDevice(device, new AtomicBoolean(false));
                wemoDeviceRegistry.addDevice(wemoControlDevice);
                devicePresenceConsumer.run(wemoControlDevice);
                System.out.println("Device Added: " + device.getDeviceType() + " - " + device.getFriendlyName());
            }

            @Override
            public void deviceRemoved(Device device) {
                if (!isBelkin(device)) {
                    return;
                }
                wemoDeviceRegistry.removeDevice(device.getFriendlyName());
                devicePresenceConsumer.run(new WemoControlDevice(device, new AtomicBoolean(false)));
                System.out.println("Device Removed: " + device.getDeviceType() + " - " + device.getFriendlyName());
            }
        });
        controlPoint.start();
    }

    @Override
    protected void runOneIteration() throws Exception {
        try {
            List<WemoControlDevice> devices = getDevices();
            devices.parallelStream().forEach(wemoControlDevice -> {
                boolean on = on(wemoControlDevice.getDevice().getFriendlyName());
                wemoControlDevice.setState(on);
                deviceStateCheckConsumer.run(wemoControlDevice);
            });
            controlPoint.search();
        } catch (Exception e) {
            logger.error("Exception when ssearching for devices.", e);
        }
    }

    @Override
    protected Scheduler scheduler() {
        return Scheduler.newFixedDelaySchedule(5, 30, TimeUnit.SECONDS);
    }

    @Override
    protected void shutDown() throws Exception {
        controlPoint.stop();
    }


    private boolean isBelkin(Device device) {
        return BELKIN_INSIGHT_DEVICE_TYPES.contains(device.getDeviceType());
    }

    public interface DevicePresenceConsumer {
        void run(WemoControlDevice device);
    }

    public interface DeviceStateCheckConsumer {
        void run(WemoControlDevice device);
    }
}
