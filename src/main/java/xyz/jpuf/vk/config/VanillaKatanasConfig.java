package xyz.jpuf.vk.config;

import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;

@Config(name = "vanilla-katanas")
public class VanillaKatanasConfig implements ConfigData {
    public boolean enableExtraMaterials = true;
    public int durabilityModifier = 5;
}
