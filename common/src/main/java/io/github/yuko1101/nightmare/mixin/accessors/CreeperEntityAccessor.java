package io.github.yuko1101.nightmare.mixin.accessors;

import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.mob.CreeperEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CreeperEntity.class)
public interface CreeperEntityAccessor {
    @Accessor("IGNITED")
    static TrackedData<Boolean> getIgnited() {
        throw new AssertionError();
    }
}
