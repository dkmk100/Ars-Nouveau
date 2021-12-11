package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.mana.IManaTile;
import com.hollingsworth.arsnouveau.api.util.SourceUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class RelayDepositTile extends ArcaneRelayTile{

    public RelayDepositTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.RELAY_DEPOSIT_TILE, pos, state);
    }

    @Override
    public void tick() {
        super.tick();
        if(!level.isClientSide && level.getGameTime() % 20 == 0 && getCurrentMana() > 0){
            List<BlockPos> posList = SourceUtil.canGiveManaAny(worldPosition, level, 5);
            for(BlockPos jarPos : posList) {
                if(this.getCurrentMana() == 0)
                    break;

                if (jarPos != null && !jarPos.equals(this.getToPos()) && !jarPos.equals(this.getFromPos()) && level.getBlockEntity(jarPos) instanceof SourceJarTile) {
                    transferMana(this, (IManaTile) level.getBlockEntity(jarPos));
                    ParticleUtil.spawnFollowProjectile(level, this.worldPosition, jarPos);
                }
            }
        }
    }

    @Override
    public boolean setTakeFrom(BlockPos pos) {
        return super.setTakeFrom(pos);
    }

    @Override
    public boolean setSendTo(BlockPos pos) {
        return super.setSendTo(pos);
    }
}
