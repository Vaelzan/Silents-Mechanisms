package net.silentchaos512.mechanisms.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.silentchaos512.mechanisms.SilentMechanisms;
import net.silentchaos512.mechanisms.capability.EnergyStorageItemImpl;
import net.silentchaos512.mechanisms.util.TextUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class BatteryItem extends Item {
    private static final int MAX_ENERGY = 500_000;
    private static final int MAX_TRANSFER = 500;

    public BatteryItem() {
        super(new Properties().group(SilentMechanisms.ITEM_GROUP).maxStackSize(1).rarity(Rarity.UNCOMMON));
        addPropertyOverride(SilentMechanisms.getId("charge"), (stack, world, entity) -> getChargeRatio(stack));
    }

    @SuppressWarnings("TypeMayBeWeakened")
    private static float getChargeRatio(ItemStack stack) {
        LazyOptional<IEnergyStorage> optional = stack.getCapability(CapabilityEnergy.ENERGY);
        if (optional.isPresent()) {
            IEnergyStorage energyStorage = optional.orElseThrow(IllegalStateException::new);
            return (float) energyStorage.getEnergyStored() / energyStorage.getMaxEnergyStored();
        }
        return 0;
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new ICapabilityProvider() {
            @Nonnull
            @Override
            public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
                if (cap == CapabilityEnergy.ENERGY)
                    return LazyOptional.of(() -> new EnergyStorageItemImpl(stack, MAX_ENERGY, MAX_TRANSFER, MAX_TRANSFER)).cast();
                return LazyOptional.empty();
            }
        };
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        // Apparently, addInformation can be called before caps are initialized
        if (CapabilityEnergy.ENERGY == null) return;

        stack.getCapability(CapabilityEnergy.ENERGY).ifPresent(e ->
                tooltip.add(TextUtil.energyWithMax(e.getEnergyStored(), e.getMaxEnergyStored())));
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (this.isInGroup(group)) {
            items.add(new ItemStack(this));

            ItemStack full = new ItemStack(this);
            full.getOrCreateTag().putInt("Energy", MAX_ENERGY);
            items.add(full);
        }
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return true;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        return 1 - getChargeRatio(stack);
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        return MathHelper.hsvToRGB((1 + getChargeRatio(stack)) / 3.0F, 1.0F, 1.0F);
    }
}
