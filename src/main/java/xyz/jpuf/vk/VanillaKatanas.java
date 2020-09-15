package xyz.jpuf.vk;

import xyz.jpuf.vk.config.VanillaKatanasConfig;
import xyz.jpuf.vk.data.KatanaData;
import xyz.jpuf.vk.data.IdentifierDeserializer;
import xyz.jpuf.vk.item.ExtendedKatanaItem;
import xyz.jpuf.vk.material.CustomToolMaterial;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.github.cottonmc.staticdata.StaticData;
import io.github.cottonmc.staticdata.StaticDataItem;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.GsonConfigSerializer;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.registry.FuelRegistry;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Set;


public class VanillaKatanas implements ModInitializer {

    public static String MODID = "vanilla-katanas";
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(Identifier.class, new IdentifierDeserializer()).create();
    public static VanillaKatanasConfig CONFIG = AutoConfig.register(VanillaKatanasConfig.class, GsonConfigSerializer::new).getConfig();
    public static final ItemGroup GROUP = FabricItemGroupBuilder.build(id("group"), () -> new ItemStack(Registry.ITEM.get(id("diamond_katana"))));

    @Override
    public void onInitialize() {
        loadKatanaData();
        registerCallbackHandlers();
    }

    private void registerCallbackHandlers() {
        AttackEntityCallback.EVENT.register((playerEntity, world, hand, entity, entityHitResult) -> {
            ItemStack handStack = playerEntity.getMainHandStack();
            if (handStack.getItem() instanceof ExtendedKatanaItem) {
                ExtendedKatanaItem extendedKatanaItem = (ExtendedKatanaItem) handStack.getItem();

                if(extendedKatanaItem.getData().canSmelt()) {
                    entity.setOnFireFor(4);
                }
            }

            return ActionResult.PASS;
        });
    }

    private void loadKatanaData() {
        Set<StaticDataItem> katanaFile = StaticData.getAllInDirectory("katanas");

        for(StaticDataItem item : katanaFile) {
            try {
                InputStreamReader reader = new InputStreamReader(item.createInputStream(), StandardCharsets.UTF_8);

                KatanaData data = GSON.fromJson(reader, KatanaData.class);
                register(data);

                reader.close();
            } catch (Exception err) {
                err.printStackTrace();
            }
        }
    }

    public static void register(KatanaData data) {
        if (!data.isExtra() || data.isExtra() && CONFIG.enableExtraMaterials) {
            Item.Settings settings = new Item.Settings().group(VanillaKatanas.GROUP);
            if(data.isFireImmune()) {
                settings.fireproof();
            }

            ExtendedKatanaItem katanaItem = new ExtendedKatanaItem(
                    CustomToolMaterial.from(data),
                    0,
                    data.getAttackSpeed() == 0 ? -2.4f : data.getAttackSpeed(),
                    settings,
                    data.getLaunchDistance() == 0 ? 1 : data.getLaunchDistance(),
                    data
            );

            if(data.getBurnTime() > 0) {
                FuelRegistry.INSTANCE.add(katanaItem, data.getBurnTime());
            }

            String path = data.getId() + "_katana";
            Identifier registryID = path.contains(":") ? new Identifier(path) : id(path);
            Registry.register(Registry.ITEM, registryID, katanaItem);
        }
    }

    public static Identifier id(String name){
        return new Identifier(MODID, name);
    }
}
