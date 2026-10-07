package io.github.thepro1604.procommands.client;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.thepro1604.procommands.ProCommands;
import io.github.thepro1604.procommands.client.config.VoiceConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;

public class ProCommandsClient implements ClientModInitializer {
    private static ProCommandsClient instance;
    public static ProCommandsClient instance() {
        return instance;
    }

    private KeyMapping settingsKeyMapping;
    private VoiceMenuManager manager;

    @Override
    public void onInitializeClient() {
        instance = this;

        manager = new VoiceMenuManager();
        ClientTickEvents.END_CLIENT_TICK.register(manager::processInput);

        settingsKeyMapping = KeyMappingHelper.registerKeyMapping(
                new KeyMapping("key.%s.open_settings".formatted(ProCommands.MOD_ID),
                        InputConstants.Type.KEYBOARD, InputConstants.KEY_F8, manager.menuCategory()
                )
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
