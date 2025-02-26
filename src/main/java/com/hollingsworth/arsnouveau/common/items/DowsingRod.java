package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.ritual.SingleBlockScryer;
import com.hollingsworth.arsnouveau.common.potions.ModPotions;
import com.hollingsworth.arsnouveau.common.ritual.ScryingRitual;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class DowsingRod extends ModItem{
    public DowsingRod(Properties properties) {
        super(properties);
    }

    public DowsingRod(Properties properties, String registryName) {
        super(properties, registryName);
    }

    public DowsingRod(String registryName) {
        super(ItemsRegistry.defaultItemProperties().durability(4), registryName);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack heldStack = pPlayer.getItemInHand(pUsedHand);
        heldStack.setDamageValue(pPlayer.getItemInHand(pUsedHand).getDamageValue() + 1);
        if(heldStack.getDamageValue() >= getMaxDamage(heldStack))
            heldStack.shrink(1);
        if(!pLevel.isClientSide){
            pPlayer.addEffect(new MobEffectInstance(ModPotions.MAGIC_FIND_EFFECT, 60 * 20));
            SingleBlockScryer singleBlockScryer = new SingleBlockScryer(Blocks.BUDDING_AMETHYST);
            ScryingRitual.grantScrying((ServerPlayer) pPlayer, 60*20, singleBlockScryer);

        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }
}
