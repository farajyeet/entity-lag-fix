package net.polyacovyury.hoppersfix.mixin;

import net.minecraft.block.BlockHopper;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.IHopper;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.world.World;
import net.polyacovyury.hoppersfix.HoppersFix;
import net.polyacovyury.hoppersfix.interfaces.IPaperHopper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TileEntityHopper.class)
public abstract class MixinTileEntityHopper extends TileEntityLockableLoot implements IHopper, ITickable, IPaperHopper {

    private static final String World = "Lnet/minecraft/world/World;";
    private static final String IInventory = "Lnet/minecraft/inventory/IInventory;";
    private static final String IHopper = "Lnet/minecraft/tileentity/IHopper;";
    private static final String TEHopper = "Lnet/minecraft/tileentity/TileEntityHopper;";
    private boolean mayAcceptItems = false;

    @Inject(method = "getSourceInventory(" + IHopper + ")" + IInventory, at = @At(value = "HEAD"), cancellable = true)
    private static void getSourceInventory(IHopper hopper, CallbackInfoReturnable<IInventory> cir) {
        cir.setReturnValue(getInventory(hopper, true));
    }

    private static IInventory getInventory(IHopper ihopper, boolean searchForEntities) {
        return IPaperHopper.getInventory(ihopper.getWorld(), ihopper.getXPos(), ihopper.getYPos() + 1.0D, ihopper.getZPos(), searchForEntities);
    }

    @Inject(method = "getInventoryAtPosition(" + World + "DDD)" + IInventory, at = @At(value = "HEAD"), cancellable = true)
    private static void getInventoryAtPosition(World world, double d0, double d1, double d2, CallbackInfoReturnable<IInventory> info) {
        info.setReturnValue(IPaperHopper.getInventory(world, d0, d1, d2, true));
    }

    @Inject(method = "pullItems",
            at = @At(value = "INVOKE", target = TEHopper + "getSourceInventory(" + IHopper + ")" + IInventory, shift = At.Shift.BEFORE), cancellable = true)
    private static void pullItems(IHopper hopper, CallbackInfoReturnable<Boolean> cir) {
        IInventory iinventory = getInventory(hopper, !(hopper instanceof TileEntityHopper));
        cir.setReturnValue(IPaperHopper.acceptItem(hopper, iinventory));
    }

    public boolean canAcceptItems() {
        return mayAcceptItems;
    }

    @Inject(method = "update()V", at = @At("HEAD"), cancellable = true)
    private void update(CallbackInfo info) {
        mayAcceptItems = false;
        if (this.world != null && !this.world.isRemote) {
            if (HoppersFix.IGNORE_TILE_UPDATES) info.cancel();
        }
    }


    @Redirect(method = "updateHopper()Z",
            at = @At(value = "INVOKE", target = TEHopper + "pullItems(" + IHopper + ")Z"))
    private boolean redirectPullItems(IHopper hopper) {
        mayAcceptItems = true;
        return TileEntityHopper.pullItems(hopper);
    }

 

    @Inject(method = "transferItemsOut()Z", at = @At(value = "INVOKE", target = TEHopper + "getInventoryForHopperTransfer()" + IInventory, shift = At.Shift.BEFORE), cancellable = true)
    private void transferItemsOut(CallbackInfoReturnable<Boolean> cir) {
        EnumFacing enumfacing = BlockHopper.getFacing(this.getBlockMetadata());
        cir.setReturnValue(IPaperHopper.hopperPush(
                this,
                IPaperHopper.getInventory(this.getWorld(),
                        this.getXPos() + (double) enumfacing.getXOffset(),
                        this.getYPos() + (double) enumfacing.getYOffset(),
                        this.getZPos() + (double) enumfacing.getZOffset(),
                        true),
                enumfacing));
    }
}   