package com.comadante.wemocontrol;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
@Produces(MediaType.TEXT_HTML)
public class WemoControlViewResource {
    private final WemoControlService wemoControlService;

    public WemoControlViewResource(WemoControlService wemoControlService) {
        this.wemoControlService = wemoControlService;
    }

    @GET
    public WemoControlView get() {
        return new WemoControlView(wemoControlService);
    }
}