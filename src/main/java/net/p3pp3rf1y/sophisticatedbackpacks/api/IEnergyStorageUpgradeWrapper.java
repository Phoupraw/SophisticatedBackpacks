package net.p3pp3rf1y.sophisticatedbackpacks.api;

import net.neoforged.neoforge.energy.IEnergyStorage;

import javax.annotation.Nullable;

public interface IEnergyStorageUpgradeWrapper {
	@Nullable
	IEnergyStorage wrapStorage(@Nullable IEnergyStorage energyStorage);
}
