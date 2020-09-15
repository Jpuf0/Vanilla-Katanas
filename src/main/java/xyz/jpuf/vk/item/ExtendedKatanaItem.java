package xyz.jpuf.vk.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import xyz.jpuf.vk.data.KatanaData;
import xyz.jpuf.katanalib.item.KatanaItem;

public class ExtendedKatanaItem extends KatanaItem {

    private final KatanaData data;

    public ExtendedKatanaItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings, int launchDistance, KatanaData data) {
        super(toolMaterial, attackDamage, attackSpeed, settings, launchDistance);
        this.data = data;
    }

    public KatanaData getData() {
        return data;
    }

    @Override
    public boolean isIn(ItemGroup group) {
        return super.isIn(group) || group.equals(data.getGroup());
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
        playerEntity.playSound(SoundEvents.BLOCK_ANCIENT_DEBRIS_PLACE, 1.0f, 1.0f);
        return new TypedActionResult<>(ActionResult.SUCCESS, playerEntity.getStackInHand(hand));
    }
}
