package io.github.yuko1101.nightmare.fabric;

import io.github.yuko1101.nightmare.Nightmare;
import net.fabricmc.api.ModInitializer;

public final class NightmareFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Nightmare.init();
    }
}
