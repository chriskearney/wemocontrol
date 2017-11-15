package com.comadante.wemocontrol;
import com.codahale.metrics.annotation.Timed;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/")
public class WemoControlResource {

    private final WemoControlService wemoControlService;

    public WemoControlResource(WemoControlService wemoControlService) {
        this.wemoControlService = wemoControlService;
    }

    @Path("/wemo/{friendlyName}/on")
    @GET
    public void turnWemoOn(@PathParam("friendlyName") String friendlyName) {
        wemoControlService.turnOn(friendlyName);
    }

    @Path("/wemo/{friendlyName}/off")
    @GET
    public void turnWemoOff(@PathParam("friendlyName") String friendlyName) {
        wemoControlService.turnOff(friendlyName);
    }

    @Path("/wemo/{friendlyName}/toggle")
    @GET
    public void turnWemoToggle(@PathParam("friendlyName") String friendlyName) {
        wemoControlService.toggle(friendlyName);
    }
}