/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app;

import com.almasb.fxgl.service.ServiceType;
import com.almasb.fxgl.settings.ReadOnlyGameSettings;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.name.Names;
import javafx.stage.Stage;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Module that binds services with their providers.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class ApplicationModule extends AbstractModule {

    private GameApplication app;
    private Stage mainStage;
    private ReadOnlyGameSettings settings;

    ApplicationModule(GameApplication app) {
        this.app = app;
        mainStage = app.getPrimaryStage();
        settings = app.getSettings();
    }

    public GameApplication getApp() {
        return app;
    }

    @Override
    protected final void configure() {
        // application is the first thing to get ready
        bindApp();

        // finally bind services, after this it is safe to use them
        bindServices();
    }

    private void bindApp() {
        bind(GameApplication.class).toInstance(app);
        bind(ReadOnlyGameSettings.class).toInstance(settings);
        bind(ApplicationMode.class).toInstance(settings.getApplicationMode());

        bind(Stage.class).toInstance(mainStage);

        // #226: scene must not be a single static instance
        // on resolution change we get rid of old scene and create new
        // while this still points to old instance
        //bind(Scene.class).toInstance(mainStage.getScene());

        bind(Integer.class).annotatedWith(Names.named("appWidth")).toInstance(app.getWidth());
        bind(Integer.class).annotatedWith(Names.named("appHeight")).toInstance(app.getHeight());
    }

    List<ServiceType> allServices = new ArrayList<>();

    /**
     * Can be overridden to provide own services or mock.
     */
    @SuppressWarnings("unchecked")
    protected void bindServices() {
        allServices = mergeServices();

        for (ServiceType type : allServices) {
            try {
                if (type.service().equals(type.serviceProvider()))
                    bind(type.serviceProvider()).in(type.scope());
                else
                    bind(type.service()).to(type.serviceProvider()).in(type.scope());

                // this is necessary because even if Service.class is in Singleton scope
                // ServiceProvider.class may be instantiated multiple times
                // @see https://github.com/google/guice/wiki/Scopes#applying-scopes
                if (type.scope() == Scopes.SINGLETON)
                    bind(type.serviceProvider()).in(Scopes.SINGLETON);
            } catch (Exception e) {
                throw new IllegalArgumentException("Failed to configure service: "
                        + type.service() + " with provider: " + type.serviceProvider()
                        + " Scope: " + type.scope()
                        + " Error: " + e);
            }
        }
    }

    private List<ServiceType> mergeServices() {
        List<Class> userServices = app.getSettings()
                .getServices()
                .stream()
                .map(ServiceType::service)
                .collect(Collectors.toList());

        List<ServiceType> services = Arrays.stream(ServiceType.class.getDeclaredFields())
                .map(this::mapFieldToType)
                // filter types that were not overridden by user
                .filter(serviceType -> !userServices.contains(serviceType.service()))
                .collect(Collectors.toList());

        // add user overridden services
        services.addAll(app.getSettings().getServices());
        return services;
    }

    private ServiceType mapFieldToType(Field field) {
        try {
            return (ServiceType) field.get(null);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Failed to map field to service type: " + e);
        }
    }
}
