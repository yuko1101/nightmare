package io.github.yuko1101.nightmare.mixin;

import io.github.yuko1101.nightmare.Nightmare;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IllagerEntity.class)
public abstract class IllagerEntityMixin extends RaiderEntity {
    protected IllagerEntityMixin(EntityType<? extends IllagerEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "initGoals", at = @At("TAIL"))
    private void initCustomGoals(CallbackInfo ci) {
        this.goalSelector.add(1, Nightmare.createBlockBreakGoal(this));
    }
}
