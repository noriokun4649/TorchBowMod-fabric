package mod.torchbowmod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registry;

public class TorchBowMod implements ModInitializer {

    public static final String MODID = "torchbowmod";
    public static final Item TORCH_BOW_ITEM = new TorchBow(new Item.Settings().maxDamage(384));
    public static final Item MULCH_TORCH_ITEM = new Item(new Item.Settings().maxCount(64));
    public static final Item TORCH_ARROW_ITEM = new TorchArrow(new Item.Settings().maxCount(64));
    public static final EntityType<TorchEntity> TORCH;

    static {
        TORCH = Registry.register(Registries.ENTITY_TYPE,
                new Identifier(MODID, "entitytorch"),
                FabricEntityTypeBuilder.<TorchEntity>create(SpawnGroup.MISC, TorchEntity::new)
                        .trackRangeBlocks(60).trackedUpdateRate(5).forceTrackedVelocityUpdates(true).build());
    }

    @Override
    public void onInitialize() {
        FabricItemGroup.builder(new Identifier(MODID, "torchbowmod_tab"))
                .icon(() -> new ItemStack(TorchBowMod.TORCH_BOW_ITEM))
                .entries((enabledFeatures, entries) -> {
                    entries.add(TorchBowMod.TORCH_BOW_ITEM);
                    entries.add(TorchBowMod.MULCH_TORCH_ITEM);
                    entries.add(TorchBowMod.TORCH_ARROW_ITEM);
                })
                .build();
        Registry.register(Registries.ITEM, new Identifier(MODID, "torchbow"), TORCH_BOW_ITEM);
        Registry.register(Registries.ITEM, new Identifier(MODID, "multitorch"), MULCH_TORCH_ITEM);
        Registry.register(Registries.ITEM, new Identifier(MODID, "torcharrow"), TORCH_ARROW_ITEM);
    }

}
