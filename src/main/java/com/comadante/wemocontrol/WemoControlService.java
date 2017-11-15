package com.comadante.wemocontrol;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.AbstractScheduledService;
import org.cybergarage.upnp.ControlPoint;
import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.device.DeviceChangeListener;
import org.cybergarage.util.Debug;
import org.glassfish.jersey.message.internal.NullOutputStream;

import java.io.PrintStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class WemoControlService extends AbstractScheduledService {

    // TODO: I think the "insight" switches have a different device type identifier, but i don't have one to try.
    private static final List<String> BELKIN_INSIGHT_DEVICE_TYPES =
            Lists.newArrayList(
                    "urn:Belkin:device:controllee:1",
                    "urn:Belkin:device:lightswitch:1"
            );

    private ControlPoint controlPoint = new ControlPoint();
    private WemoDeviceRegistry wemoDeviceRegistry = new WemoDeviceRegistry();

    public void turnOff(String friendlyName) {
        wemoDeviceRegistry.turnOff(friendlyName);
    }

    public void turnOn(String friendlyName) {
        wemoDeviceRegistry.turnOn(friendlyName);
    }

    public void toggle(String friendlyName) {
        wemoDeviceRegistry.toggle(friendlyName);
    }

    public List<Device> getDevices() { return Lists.newArrayList(wemoDeviceRegistry.getDevices()); }

    @Override
    protected void startUp() throws Exception {
        Debug.getDebug().setOut(new PrintStream(new NullOutputStream()));
        controlPoint.start();
        controlPoint.addDeviceChangeListener(new DeviceChangeListener() {
            @Override
            public void deviceAdded(Device device) {
                if (!isBelkin(device)) {
                    return;
                }
                wemoDeviceRegistry.addDevice(device);
                System.out.println("Device Added: " + device.getDeviceType() + " - " + device.getFriendlyName());
            }

            @Override
            public void deviceRemoved(Device device) {
                if (!isBelkin(device)) {
                    return;
                }
                wemoDeviceRegistry.removeDevice(device);
                System.out.println("Device Removed: " + device.getDeviceType() + " - " + device.getFriendlyName());
            }
        });
    }

    @Override
    protected void runOneIteration() throws Exception {
        controlPoint.search();
    }

    @Override
    protected Scheduler scheduler() {
        return Scheduler.newFixedDelaySchedule(0, 30, TimeUnit.SECONDS);
    }

    @Override
    protected void shutDown() throws Exception {
        controlPoint.stop();
    }


    private boolean isBelkin(Device device) {
        return BELKIN_INSIGHT_DEVICE_TYPES.contains(device.getDeviceType());
    }
}
