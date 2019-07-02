package net.silentchaos512.mechanisms.block.crusher;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.silentchaos512.lib.inventory.SlotOutputOnly;
import net.silentchaos512.lib.util.InventoryUtils;
import net.silentchaos512.mechanisms.init.ModContainers;

public class CrusherContainer extends Container {
    final CrusherTileEntity tileEntity;
    private final World world;

    public CrusherContainer(int id, PlayerInventory playerInventory) {
        this(id, playerInventory, new CrusherTileEntity());
    }

    public CrusherContainer(int id, PlayerInventory playerInventory, CrusherTileEntity tileEntity) {
        super(ModContainers.crusher, id);
        this.tileEntity = tileEntity;
        this.world = playerInventory.player.world;

        this.addSlot(new Slot(this.tileEntity, 0, 26, 35));
        this.addSlot(new SlotOutputOnly(this.tileEntity, 1, 80, 35));
        this.addSlot(new SlotOutputOnly(this.tileEntity, 2, 98, 35));
        this.addSlot(new SlotOutputOnly(this.tileEntity, 3, 116, 35));
        this.addSlot(new SlotOutputOnly(this.tileEntity, 4, 134, 35));

        InventoryUtils.createPlayerSlots(playerInventory, 8, 84).forEach(this::addSlot);

        func_216961_a(this.tileEntity.fields);
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        // FIXME: world is null?
//        return this.tileEntity.isUsableByPlayer(playerIn);
        return true;
    }

    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (index >= 1 && index < 5) {
                if (!this.mergeItemStack(itemstack1, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onSlotChange(itemstack1, itemstack);
            } else if (index != 0) {
                if (this.isCrushingIngredient(itemstack1)) {
                    if (!this.mergeItemStack(itemstack1, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 5 && index < 32) {
                    if (!this.mergeItemStack(itemstack1, 30, 39, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 32 && index < 41 && !this.mergeItemStack(itemstack1, 5, 32, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, 5, 41, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, itemstack1);
        }

        return itemstack;
    }

    private boolean isCrushingIngredient(ItemStack stack) {
        // TODO
        return true;
    }
}
