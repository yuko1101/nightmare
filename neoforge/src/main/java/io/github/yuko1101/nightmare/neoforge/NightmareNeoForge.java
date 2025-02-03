package io.github.yuko1101.nightmare.neoforge;

import io.github.yuko1101.nightmare.Nightmare;
import net.neoforged.fml.common.Mod;

@Mod(Nightmare.MOD_ID)
public final class NightmareNeoForge {
    public NightmareNeoForge() {
        Nightmare.init();
    }
}
