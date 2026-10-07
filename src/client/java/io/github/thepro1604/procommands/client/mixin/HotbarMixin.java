package io.github.thepro1604.procommands.client.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.thepro1604.procommands.client.ProCommandsClient;
import io.github.thepro1604.procommands.client.config.VoiceConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Minecraft.class)
public class HotbarMixin {
    // WrapOperation instead of Redirect so it can chain with other mods (e.g. Axiom) that redirect the same call
    @WrapOperation(method = "handleKeybinds", at = @At(value = "INVOKE", target = "net/minecraft/world/entity/player/Inventory.setSelectedSlot(I)V"))
    private void interceptSlotSelect(Inventory instance, int i, Operation<Void> original) {
        boolean result = ProCommandsClient.instance().manager().dispatchCommandSelect(i);
        if (result && VoiceConfig.blockSlotChange()) return;

        original.call(instance, i);
    }
}
