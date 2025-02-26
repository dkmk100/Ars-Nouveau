package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.world.level.block.state.BlockState;

public class TempLightTile extends LightTile implements ITickable {

    int age;
    public double lengthModifier;

    public TempLightTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.T_LIGHT_TILE, pos, state);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.age = nbt.getInt("age");
        this.lengthModifier = nbt.getDouble("modifier");
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putDouble("modifier", lengthModifier);
        tag.put("age", IntTag.valueOf(age));
    }

    @Override
    public void tick() {
        if (level != null && !level.isClientSide) {
            age++;
            //15 seconds
            if (age > (20 * 15 + 20 * 5 * lengthModifier)) {
                level.destroyBlock(this.getBlockPos(), false);
                level.removeBlockEntity(this.getBlockPos());
            }
        }
    }
}
