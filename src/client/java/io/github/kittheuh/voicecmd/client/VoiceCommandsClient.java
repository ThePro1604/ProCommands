package io.github.kittheuh.voicecmd.client;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.kittheuh.voicecmd.VoiceCommands;
import io.github.kittheuh.voicecmd.client.config.VoiceConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class VoiceCommandsClient implements ClientModInitializer {
    private static VoiceCommandsClient instance;
    public static VoiceCommandsClient instance() {
        return instance;
    }

    private KeyMapping settingsKeyMapping;
    private VoiceMenuManager manager;

    @Override
    public void onInitializeClient() {
        instance = this;

        manager = new VoiceMenuManager();
        ClientTickEvents.END_CLIENT_TICK.register(manager::processInput);

        settingsKeyMapping = KeyBindingHelper.registerKeyBinding(
                new KeyMapping("key.%s.open_settings".formatted(VoiceCommands.MOD_ID), InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_F8, manager.menuCategory())
        );
        ClientTickEvents.END_CLIENT_TICK.register(minecraft -> {
            while (settingsKeyMapping.consumeClick()) {
                minecraft.setScreenAndShow(VoiceConfig.createConfigScreen());
            }
        });
    }

    public KeyMapping settingsKeyMapping() {
        return settingsKeyMapping;
    }

    public VoiceMenuManager manager() {
        return manager;
    }
}
