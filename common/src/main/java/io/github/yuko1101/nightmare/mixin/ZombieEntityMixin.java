package io.github.yuko1101.nightmare.mixin;

import io.github.yuko1101.nightmare.entity.goals.BlockBreakGoal;
import io.github.yuko1101.nightmare.utils.EntityUtils;
import io.github.yuko1101.nightmare.utils.Vec3dUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ZombieEntity.class)
public abstract class ZombieEntityMixin extends HostileEntity {

    protected ZombieEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "initCustomGoals", at = @At("TAIL"))
    private void initCustomGoals(CallbackInfo ci) {
        this.goalSelector.add(1, new BlockBreakGoal((ZombieEntity) (Object) this, difficulty -> difficulty == Difficulty.HARD, pos -> {
            if (!pos.isWithinDistance(this.getPos(), 4.0)) return false;
            if (this.getTarget() == null) return false;
            if (!this.getTarget().getBlockPos().isWithinDistance(this.getPos(), 8.0)) return false;
            if (Vec3dUtils.getAngle(this.getPos(), this.getTarget().getPos(), pos.toCenterPos()) > Math.PI / 2) return false;
            BlockState blockState = this.getWorld().getBlockState(pos);
            return !blockState.isAir() && blockState.getHardness(this.getWorld(), pos) < 100F; // TODO: better filtering
        }, mobEntity -> mobEntity.getTarget() != null && mobEntity.getTarget().isAlive() && !EntityUtils.isRangedAttack(mobEntity)));
    }

    @ModifyArg(method = "initCustomGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/goal/ActiveTargetGoal;<init>(Lnet/minecraft/entity/mob/MobEntity;Ljava/lang/Class;Z)V"), index = 2)
    private boolean modifyChecks(boolean checkVisibility) {
        return false;
    }
}
