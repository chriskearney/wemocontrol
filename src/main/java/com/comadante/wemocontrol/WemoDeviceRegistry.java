package com.comadante.wemocontrol;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import com.google.common.collect.Maps;
import org.cybergarage.upnp.*;

import java.util.Collection;
import java.util.Map;

public class WemoDeviceRegistry {

    private final Map<String, Device> devices = Maps.newConcurrentMap();
    private final Interner<String> deviceInterner = Interners.newStrongInterner();

    public void addDevice(Device device) {
        synchronized (deviceInterner.intern(device.getFriendlyName())) {
            this.devices.put(device.getFriendlyName(), device);
        }
    }

    public void removeDevice(Device device) {
        synchronized (deviceInterner.intern(device.getFriendlyName())) {
            this.devices.remove(device.getFriendlyName());
        }
    }

    public void turnOn(String friendlyName) {
        synchronized (deviceInterner.intern(friendlyName)) {
            Action action = devices.get(friendlyName).getAction("SetBinaryState");
            action.setArgumentValue("BinaryState", 1);
            performAction(action);
        }
    }

    public void turnOff(String friendlyName) {
        synchronized (deviceInterner.intern(friendlyName)) {
            Action action = devices.get(friendlyName).getAction("SetBinaryState");
            action.setArgumentValue("BinaryState", 0);
            performAction(action);
        }

    }

    private void performAction(Action action) {
        if (!action.postControlAction()) {
            throw new RuntimeException(action.getStatus().toString());
        }
    }

    public void toggle(String friendlyName) {
        synchronized (deviceInterner.intern(friendlyName)) {
            Action getBinaryStateAction = devices.get(friendlyName).getAction("GetBinaryState");
            performAction(getBinaryStateAction);
            int binaryState = Integer.valueOf(getBinaryStateAction.getArgumentValue("BinaryState"));
            if (binaryState == 1) {
                turnOff(friendlyName);
            } else {
                turnOn(friendlyName);
            }
        }
    }

    public Collection<Device> getDevices() {
        return devices.values();
    }


}
