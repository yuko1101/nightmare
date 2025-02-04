package io.github.yuko1101.nightmare.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.yuko1101.nightmare.entity.goals.BlockBreakGoal;
import io.github.yuko1101.nightmare.mixin.accessors.MobEntityAccessor;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.mob.MobEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TrackTargetGoal.class)
public class TrackTargetGoalMixin {
    @Final
    @Shadow
    protected MobEntity mob;

    @ModifyExpressionValue(method = "shouldContinue", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/ai/goal/TrackTargetGoal;checkVisibility:Z", opcode = Opcodes.GETFIELD))
    private boolean modifyVisibilityChecks(boolean checkVisibility) {
        return checkVisibility && ((MobEntityAccessor) this.mob).getGoalSelector().getGoals().stream().noneMatch((goal) -> goal.getGoal() instanceof BlockBreakGoal);
    }

    @ModifyExpressionValue(method = "canTrack", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/ai/goal/TrackTargetGoal;checkCanNavigate:Z", opcode = Opcodes.GETFIELD))
    private boolean modifyNavigationChecks(boolean checkCanNavigate) {
        return checkCanNavigate && ((MobEntityAccessor) this.mob).getGoalSelector().getGoals().stream().noneMatch((goal) -> goal.getGoal() instanceof BlockBreakGoal);
    }
}
