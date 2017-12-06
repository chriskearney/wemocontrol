package com.comadante.wemocontrol;

import org.cybergarage.upnp.Device;

import java.util.concurrent.atomic.AtomicBoolean;

public class WemoControlDevice {

    private final Device device;
    private final AtomicBoolean on;

    public WemoControlDevice(Device device, AtomicBoolean on) {
        this.device = device;
        this.on = on;
    }

    public void setState(boolean state) {
        on.compareAndSet(on.get(), state);
    }

    public void setOn() {
        on.compareAndSet(on.get(), true);
    }

    public void setOff() {
        on.compareAndSet(on.get(), false);
    }

    public Device getDevice() {
        return device;
    }

    public boolean isOn() {
        return on.get();
    }
}
