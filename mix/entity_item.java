package net.polyacovyury.hoppersfix.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.world.World;
import net.polyacovyury.hoppersfix.interfaces.HopperPusher;
import net.polyacovyury.hoppersfix.interfaces.IPaperHopper;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityItem.class)
public abstract class MixinEntityItem extends Entity implements HopperPusher {
    public MixinEntityItem(World worldIn) {
        super(worldIn);
    }

    @Override
    public boolean acceptItem(IPaperHopper hopper) {
        return !((EntityItem) (Object) this).isDead && TileEntityHopper.putDropInInventoryAllSlots(null, hopper, (EntityItem) (Object) this);
    }

    @Inject(method = "onUpdate()V",
            at = @At(value = "FIELD", target = "Lnet/minecraft/entity/item/EntityItem;pickupDelay:I", opcode = Opcodes.GETFIELD, ordinal = 0),
            cancellable = true)
    private void onUpdate(CallbackInfo info) {
        if (tryPutInHopper()) info.cancel();
    }
}