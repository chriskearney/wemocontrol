package com.comadante.wemocontrol;

import io.dropwizard.Application;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;
import org.cybergarage.upnp.Device;

public class WemoControlApplication extends Application<WemoControlConfiguration> {
    public static void main(String[] args) throws Exception {
        new WemoControlApplication().run(args);
    }

    @Override
    public String getName() {
        return "wemocontrol";
    }

    @Override
    public void initialize(Bootstrap<WemoControlConfiguration> bootstrap) {
        bootstrap.addBundle(new ViewBundle<WemoControlConfiguration>());
    }

    @Override
    public void run(WemoControlConfiguration configuration,
                    Environment environment) {

        WemoControlService wemoControlService = new WemoControlService(new WemoControlService.DevicePresenceConsumer() {
            @Override
            public void run(WemoControlDevice device) {

            }
        }, new WemoControlService.DeviceStateCheckConsumer() {
            @Override
            public void run(WemoControlDevice device) {
                //
            }
        });
        environment.lifecycle().manage(new Managed() {
            @Override
            public void start() throws Exception {
                wemoControlService.startAsync().awaitRunning();
            }

            @Override
            public void stop() throws Exception {
                wemoControlService.stopAsync().awaitTerminated();
            }
        });

        environment.jersey().register(new WemoControlResource(wemoControlService));
        environment.jersey().register(new WemoControlViewResource(wemoControlService));
    }

}
