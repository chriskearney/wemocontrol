package com.comadante.wemocontrol;

import io.dropwizard.views.View;
import org.cybergarage.upnp.Device;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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

    public String getUrlSafe(String friendlyName) throws UnsupportedEncodingException {
        return URLEncoder.encode(friendlyName, "ISO-8859-1").replace("+", "%20");
    }

}