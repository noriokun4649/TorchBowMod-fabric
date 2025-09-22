package mod.torchbowmod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.block.Block;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

import static com.mojang.text2speech.Narrator.LOGGER;
import static mod.torchbowmod.TorchBow.TORCH_ITEMS;

public class TorchBowMod implements ModInitializer {

    public static final String MODID = "torchbowmod";
    public static final Identifier TORCH_BOW_ID = Identifier.of(MODID, "torchbow");
    public static final Identifier MULCH_TORCH_ID = Identifier.of(MODID, "multitorch");
    public static final Identifier TORCH_ARROW_ID = Identifier.of(MODID, "torcharrow");
    public static final RegistryKey<Item> TORCH_BOW_KEY = RegistryKey.of(RegistryKeys.ITEM, TORCH_BOW_ID);
    public static final RegistryKey<Item> MULCH_TORCH_KEY = RegistryKey.of(RegistryKeys.ITEM, MULCH_TORCH_ID);
    public static final RegistryKey<Item> TORCH_ARROW_KEY = RegistryKey.of(RegistryKeys.ITEM, TORCH_ARROW_ID);
    public static final Item TORCH_BOW_ITEM = new TorchBow(new Item.Settings().registryKey(TORCH_BOW_KEY).maxDamage(384));
    public static final Item MULCH_TORCH_ITEM = new Item(new Item.Settings().registryKey(MULCH_TORCH_KEY).maxCount(64));
    public static final Item TORCH_ARROW_ITEM = new TorchArrow(new Item.Settings().registryKey(TORCH_ARROW_KEY).maxCount(64));
    public static final Identifier TORCH_ENTITY = Identifier.of(MODID, "entitytorch");
    public static final EntityType<TorchEntity> TORCH;
    public static final RegistryKey<EntityType<?>> TORCH_ENTITY_ID = RegistryKey.of(RegistryKeys.ENTITY_TYPE, TORCH_ENTITY);
    public static final ItemGroup TORCH_BOW_TAB = FabricItemGroup.builder()
            .displayName(Text.translatable("itemGroup.torchbowmod.torchbowmod_tab"))
            .icon(() -> new ItemStack(TorchBowMod.TORCH_BOW_ITEM))
            .entries((enabledFeatures, entries) -> {
                entries.add(TorchBowMod.TORCH_BOW_ITEM);
                entries.add(TorchBowMod.MULCH_TORCH_ITEM);
                entries.add(TorchBowMod.TORCH_ARROW_ITEM);
            })
            .build();
    public static final Map<BlockItem, WallTorchBlock> ITEM_TO_WALL_BLOCK = new HashMap<>();

    static {
        TORCH = Registry.register(Registries.ENTITY_TYPE,
                TORCH_ENTITY,
                EntityType.Builder.<TorchEntity>create(TorchEntity::new, SpawnGroup.MISC).dropsNothing().dimensions(0.5F, 0.5F).eyeHeight(0.13F).maxTrackingRange(4).trackingTickInterval(20).build(TORCH_ENTITY_ID));
    }

    @Override
    public void onInitialize() {
        Registry.register(Registries.ITEM_GROUP, Identifier.of(MODID, "torchbowmod_tab"), TORCH_BOW_TAB);
        Registry.register(Registries.ITEM, TORCH_BOW_KEY, TORCH_BOW_ITEM);
        Registry.register(Registries.ITEM, MULCH_TORCH_KEY, MULCH_TORCH_ITEM);
        Registry.register(Registries.ITEM, TORCH_ARROW_KEY, TORCH_ARROW_ITEM);
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            Map<String, Integer> modCountMap = new HashMap<>();

            for (Block block : Registries.BLOCK) {
                if (block instanceof WallTorchBlock wallTorch) {
                    Item asItem = block.asItem();
                    if (asItem instanceof BlockItem blockItem) {
                        ITEM_TO_WALL_BLOCK.put(blockItem, wallTorch);
                        TORCH_ITEMS.add(blockItem);

                        String namespace = Registries.ITEM.getId(asItem).getNamespace();
                        modCountMap.merge(namespace, 1, Integer::sum);
                    }
                }
            }

            LOGGER.info("==== TorchBowMod Torch Item Auto-Registration Stats ====");
            LOGGER.info("Total registered pairs: {}", ITEM_TO_WALL_BLOCK.size());
            modCountMap.forEach((ns, count) -> LOGGER.info("Namespace '{}' has {} torch items", ns, count));
            LOGGER.info("========================================================");
        });

    }

}
