package net.polyacovyury.hoppersfix.mixin;

import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartContainer;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.ILootContainer;
import net.polyacovyury.hoppersfix.interfaces.HopperPusher;
import net.polyacovyury.hoppersfix.interfaces.IPaperHopper;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EntityMinecartContainer.class)
public abstract class MixinEntityMinecartContainer extends EntityMinecart implements ILockableContainer, ILootContainer, HopperPusher {

    public MixinEntityMinecartContainer(World worldIn) {
        super(worldIn);
    }

    @Override
    public boolean acceptItem(IPaperHopper hopper) {  // needs explicit putting on halt after successfull acceptItem()
        return IPaperHopper.acceptItem(hopper, this) && IPaperHopper.cooldownHopper(hopper);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        tryPutInHopper();
    }
}