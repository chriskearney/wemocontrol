package com.comadante.wemocontrol;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import com.google.common.collect.Maps;
import org.cybergarage.upnp.*;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public class WemoDeviceRegistry {

    private final Map<String, WemoControlDevice> devices = Maps.newConcurrentMap();
    private final Interner<String> deviceInterner = Interners.newStrongInterner();

    public void addDevice(WemoControlDevice wemoControlDevice) {
        synchronized (deviceInterner.intern(wemoControlDevice.getDevice().getFriendlyName())) {
            this.devices.put(wemoControlDevice.getDevice().getFriendlyName(), wemoControlDevice);
            boolean on = on(wemoControlDevice.getDevice().getFriendlyName());
            wemoControlDevice.setState(on);
        }
    }

    public void removeDevice(String friendlyName) {
        synchronized (deviceInterner.intern(friendlyName)) {
            this.devices.remove(friendlyName);
        }
    }

    public void turnOn(String friendlyName) {
        synchronized (deviceInterner.intern(friendlyName)) {
            Action action = devices.get(friendlyName).getDevice().getAction("SetBinaryState");
            action.setArgumentValue("BinaryState", 1);
            performAction(action);
        }
    }

    public void turnOff(String friendlyName) {
        synchronized (deviceInterner.intern(friendlyName)) {
            Action action = devices.get(friendlyName).getDevice().getAction("SetBinaryState");
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
            Action getBinaryStateAction = devices.get(friendlyName).getDevice().getAction("GetBinaryState");
            performAction(getBinaryStateAction);
            int binaryState = Integer.valueOf(getBinaryStateAction.getArgumentValue("BinaryState"));
            if (binaryState == 1) {
                turnOff(friendlyName);
            } else {
                turnOn(friendlyName);
            }
        }
    }

    public Collection<WemoControlDevice> getDevices() {
        return devices.values();
    }

    public Optional<WemoControlDevice> getDevice(String friendlyName) {
        return Optional.of(devices.get(friendlyName));
    }


    public boolean on(String friendlyName) {
        Action getBinaryStateAction = devices.get(friendlyName).getDevice().getAction("GetBinaryState");
        performAction(getBinaryStateAction);
        int binaryState = Integer.valueOf(getBinaryStateAction.getArgumentValue("BinaryState"));
        if (binaryState == 1) {
            return true;
        } else {
            return false;
        }
    }
}
