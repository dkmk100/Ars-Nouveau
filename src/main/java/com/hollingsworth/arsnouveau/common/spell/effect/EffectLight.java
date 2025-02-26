package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.common.block.tile.TempLightTile;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.block.SconceBlock;
import com.hollingsworth.arsnouveau.common.block.tile.LightTile;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDampen;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDurationDown;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtendTime;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

public class EffectLight extends AbstractEffect {
    public static EffectLight INSTANCE = new EffectLight();

    private EffectLight() {
        super(GlyphLib.EffectLightID, "Conjure Magelight");
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if (rayTraceResult.getEntity() instanceof ILightable entity) {
            entity.onLight(rayTraceResult, world, shooter, spellStats, spellContext);
        }

        if (!(rayTraceResult.getEntity() instanceof LivingEntity living))
            return;
        if (shooter == null || !shooter.equals(living)) {
            applyConfigPotion(living, MobEffects.GLOWING, spellStats);
        }
        applyConfigPotion(living, MobEffects.NIGHT_VISION, spellStats);
    }

    public ForgeConfigSpec.BooleanValue ONLY_TEMPORARY;

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        BlockPos pos = rayTraceResult.getBlockPos().relative(rayTraceResult.getDirection());
        if (!BlockUtil.destroyRespectsClaim(getPlayer(shooter, (ServerLevel) world), world, pos))
            return;

        if (world.getBlockEntity(rayTraceResult.getBlockPos()) instanceof ILightable lightable) {
            lightable.onLight(rayTraceResult, world, shooter, spellStats, spellContext);
            return;
        }

        if (world.getBlockState(pos).getMaterial().isReplaceable() && world.isUnobstructed(BlockRegistry.LIGHT_BLOCK.defaultBlockState(), pos, CollisionContext.of(ANFakePlayer.getPlayer((ServerLevel) world)))) {
            BlockState lightBlockState = (spellStats.getDurationMultiplier() != 0 || ONLY_TEMPORARY.get() ? BlockRegistry.T_LIGHT_BLOCK : BlockRegistry.LIGHT_BLOCK).defaultBlockState().setValue(WATERLOGGED, world.getFluidState(pos).getType() == Fluids.WATER);
            world.setBlockAndUpdate(pos, lightBlockState.setValue(SconceBlock.LIGHT_LEVEL, Math.max(0, Math.min(15, 14 + (int) spellStats.getAmpMultiplier()))));
            if (world.getBlockEntity(pos) instanceof LightTile tile) {
                tile.red = spellContext.colors.r;
                tile.green = spellContext.colors.g;
                tile.blue = spellContext.colors.b;
                if (tile instanceof TempLightTile)
                    ((TempLightTile) tile).lengthModifier = spellStats.getDurationMultiplier();
            }
            world.sendBlockUpdated(pos, world.getBlockState(pos), world.getBlockState(pos), 2);

        }
    }

    @Override
    public void buildConfig(ForgeConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addPotionConfig(builder, 30);
        ONLY_TEMPORARY = builder.comment("Make lights created expire after some time.").define("temporary_lights_only", false);
    }

    @Override
    protected Map<String, Integer> getDefaultAugmentLimits() {
        Map<String, Integer> map = super.getDefaultAugmentLimits();
        map.put(GlyphLib.AugmentAmplifyID, 1);
        return map;
    }

    @Override
    public int getDefaultManaCost() {
        return 25;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAmplify.INSTANCE, AugmentDurationDown.INSTANCE, AugmentDampen.INSTANCE, AugmentExtendTime.INSTANCE);
    }

    @Override
    public String getBookDescription() {
        return "If cast on a block, a permanent light source is created. May be amplified up to Glowstone brightness, or Dampened for a lower light level. When cast on yourself, you will receive night vision. When cast on other entities, they will receive Night Vision and Glowing.";
    }

    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.CONJURATION);
    }
}
