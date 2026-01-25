package io.github.kittheuh.voicecmd.client.mixin;

import io.github.kittheuh.voicecmd.client.VoiceCommandsClient;
import io.github.kittheuh.voicecmd.client.config.VoiceConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Minecraft.class)
public class HotbarMixin {
    @Redirect(method = "handleKeybinds", at = @At(value = "INVOKE", target = "net/minecraft/world/entity/player/Inventory.setSelectedSlot(I)V"))
    private void interceptSlotSelect(Inventory instance, int i) {
        boolean result = VoiceCommandsClient.instance().manager().dispatchCommandSelect(i);
        if (result && VoiceConfig.blockSlotChange()) return;

        instance.setSelectedSlot(i);
    }
}
