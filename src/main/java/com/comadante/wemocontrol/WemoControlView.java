package com.comadante.wemocontrol;

import io.dropwizard.views.View;
import org.cybergarage.upnp.Device;

import java.util.List;
import java.util.stream.Collectors;

public class WemoControlView extends View {

    private final WemoControlService wemoControlService;

    public WemoControlView(WemoControlService wemoControlService) {
        super("wemocontrol.ftl");
        this.wemoControlService = wemoControlService;
    }

    public WemoControlService getWemoControlService() {
        return wemoControlService;
    }

    public List<String> getSwitches() {
        return wemoControlService.getDevices()
                .stream()
                .map(Device::getFriendlyName)
                .collect(Collectors.toList());
    }

}