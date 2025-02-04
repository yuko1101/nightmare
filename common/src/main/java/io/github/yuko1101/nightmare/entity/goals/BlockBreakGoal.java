package io.github.yuko1101.nightmare.entity.goals;

import net.minecraft.block.Block;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;

import java.util.Objects;
import java.util.function.Predicate;

public class BlockBreakGoal extends BlockInteractGoal {
    private static final int MIN_MAX_PROGRESS = 240;

    private final Predicate<Difficulty> difficultySufficientPredicate;

    protected int breakProgress;
    protected int prevBreakProgress = -1;
    protected int maxProgress = -1;

    public BlockBreakGoal(MobEntity mob, Predicate<Difficulty> difficultySufficientPredicate, Predicate<BlockPos> blockPosPredicate) {
        super(mob, blockPosPredicate);
        this.difficultySufficientPredicate = difficultySufficientPredicate;
    }

    public BlockBreakGoal(MobEntity mob, int maxProgress, Predicate<Difficulty> difficultySufficientPredicate, Predicate<BlockPos> blockPosPredicate) {
        this(mob, difficultySufficientPredicate, blockPosPredicate);
        this.maxProgress = maxProgress;
    }

    private int getMaxProgress() {
        return Math.max(this.maxProgress, MIN_MAX_PROGRESS);
    }

    @Override
    public boolean canStart() {
        if (!getServerWorld(this.mob).getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
            return false;
        }
        if (!isDifficultySufficient(this.mob.getWorld().getDifficulty())) {
            return false;
        }
        if (this.mob.getTarget() == null || this.mob.getTarget().isDead()) {
            return false;
        }
        if (!super.canStart()) {
            return false;
        }

        return canNavigateOnlyByBreakingBlocks(this.mob.getTarget());
    }

    @Override
    public void start() {
        super.start();
        this.breakProgress = 0;
    }

    @Override
    public boolean shouldContinue() {
        return this.breakProgress <= this.getMaxProgress()
                && this.mob.hurtTime == this.mob.maxHurtTime
                && blockPosPredicate.test(this.blockPos)
                && this.isDifficultySufficient(this.mob.getWorld().getDifficulty());
    }

    @Override
    public void stop() {
        super.stop();
        this.mob.getWorld().setBlockBreakingInfo(this.mob.getId(), this.blockPos, -1);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.mob.getRandom().nextInt(20) == 0) {
            this.mob.getWorld().syncWorldEvent(1019, this.blockPos, 0);
            if (!this.mob.handSwinging) {
                this.mob.swingHand(this.mob.getActiveHand());
            }
        }

        this.breakProgress++;
        int i = (int)((float)this.breakProgress / (float)this.getMaxProgress() * 10.0F);
        if (i != this.prevBreakProgress) {
            this.mob.getWorld().setBlockBreakingInfo(this.mob.getId(), this.blockPos, i);
            this.prevBreakProgress = i;
        }

        if (this.breakProgress == this.getMaxProgress() && this.isDifficultySufficient(this.mob.getWorld().getDifficulty())) {
            this.mob.getWorld().removeBlock(this.blockPos, false);
            this.mob.getWorld().syncWorldEvent(1021, this.blockPos, 0);
            this.mob.getWorld().syncWorldEvent(2001, this.blockPos, Block.getRawIdFromState(this.mob.getWorld().getBlockState(this.blockPos)));
        }
    }


    private boolean isDifficultySufficient(Difficulty difficulty) {
        return this.difficultySufficientPredicate.test(difficulty);
    }

    private boolean canNavigateOnlyByBreakingBlocks(LivingEntity target) {
        Path path = this.mob.getNavigation().findPathTo(this.mob.getTarget(), 0);
        if (path == null) {
            return false;
        }

        if (path.getLength() == 0) {
            return false;
        }

        return Objects.requireNonNull(path.getEnd()).getPos().distanceTo(target.getPos()) > 2.0;
    }
}
