package io.github.yuko1101.nightmare.entity.goals;

import io.github.yuko1101.nightmare.utils.EntityUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;

import java.util.Objects;
import java.util.function.Predicate;

public class BlockBreakGoal extends BlockInteractGoal implements IPathCreateGoal {
    private final Predicate<Difficulty> difficultySufficientPredicate;
    private final Predicate<MobEntity> mobEntityPredicate;

    protected double breakProgress;
    protected int prevBreakProgress = -1;

    public BlockBreakGoal(MobEntity mob, Predicate<Difficulty> difficultySufficientPredicate, Predicate<BlockPos> blockPosPredicate, Predicate<MobEntity> canStartPredicate) {
        super(mob, blockPosPredicate);
        this.difficultySufficientPredicate = difficultySufficientPredicate;
        this.mobEntityPredicate = canStartPredicate;
    }

    private double getMaxProgress() {
        if (this.blockPos == null) {
            throw new IllegalStateException("BlockPos is null");
        }
        return Objects.requireNonNull(getBlockState()).getHardness(this.mob.getWorld(), this.blockPos) * 30;
    }

    @Override
    public boolean canStart() {
        if (!getServerWorld(this.mob).getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
            return false;
        }
        if (!isDifficultySufficient(this.mob.getWorld().getDifficulty())) {
            return false;
        }
        if (!mobEntityPredicate.test(this.mob)) {
            return false;
        }
        if (!super.canStart()) {
            return false;
        }

        return EntityUtils.canNavigateOnlyByBreakingBlocks(this.mob, this.mob.getTarget());
    }

    @Override
    public void start() {
        super.start();
        this.breakProgress = 0;
        this.prevBreakProgress = -1;
    }

    @Override
    public boolean shouldContinue() {
        return this.breakProgress <= this.getMaxProgress()
                && this.mob.hurtTime == this.mob.maxHurtTime
                && this.isDifficultySufficient(this.mob.getWorld().getDifficulty())
                && mobEntityPredicate.test(this.mob)
                && blockPosPredicate.test(this.blockPos);
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

        this.breakProgress += getProgressSpeed();
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

    // PlayerEntity#getDestroySpeed, but with big modifications for enemies
    private double getProgressSpeed() {
        ItemStack itemStack = this.mob.getMainHandStack();

        double speed = itemStack.getMiningSpeedMultiplier(getBlockState());
        if (speed > 1) speed += this.mob.getAttributes().hasAttribute(EntityAttributes.MINING_EFFICIENCY) ? this.mob.getAttributeValue(EntityAttributes.MINING_EFFICIENCY) : 0;
        speed *= this.mob.getAttributes().hasAttribute(EntityAttributes.BLOCK_BREAK_SPEED) ? this.mob.getAttributeValue(EntityAttributes.BLOCK_BREAK_SPEED) : 1;

        // speed up if the monster has a suitable tool, since a monster usually doesn't have a tool
        if (itemStack.isSuitableFor(getBlockState())) {
            speed *= 3;
        }

        return speed;
    }
}
