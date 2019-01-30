package id.co.collega.v7.seed;

import id.co.collega.v7.AbstractApplicationInitializer;
import id.co.collega.v7.seed.config.ApplicationConfiguration;
import id.co.collega.v7.seed.config.WebConfiguration;

public class WebAppInitializer extends AbstractApplicationInitializer {
    @Override
    public Class getConfigurationClass() {
        return ApplicationConfiguration.class;
    }

    @Override
    public Class getMVCConfigurationClass() {
        return WebConfiguration.class;
    }
}
