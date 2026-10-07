package io.github.thepro1604.procommands.client;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.thepro1604.procommands.ProCommands;
import io.github.thepro1604.procommands.client.config.MenuValues;
import io.github.thepro1604.procommands.client.config.VoiceConfig;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.StringUtil;
import net.minecraft.util.Util;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class VoiceMenu implements HudElement {
    private final KeyMapping keyMapping;
    private final MenuValues values;

    private boolean displaying;
    private long pressedOn;

    public VoiceMenu(String suffix, int keycode, KeyMapping.Category category, MenuValues values) {
        this.values = values;

        keyMapping = KeyMappingHelper.registerKeyMapping(
                new KeyMapping("key.%s.%s".formatted(ProCommands.MOD_ID, suffix), InputConstants.Type.KEYBOARD, keycode, category)
        );
        HudElementRegistry.addFirst(Identifier.fromNamespaceAndPath(ProCommands.MOD_ID, suffix), this);
    }

    public KeyMapping keyMapping() {
        return keyMapping;
    }

    public void checkPress() {
        if (!keyMapping.isDown()) {
            pressedOn = 0;
            return;
        }

        if (pressedOn == 0) { // process key press
            pressedOn = Util.getMillis();

            if (!values.allInvalid())
                displaying = !displaying;
        }
    }

    public long pressedOn() {
        return pressedOn;
    }

    public void displaying(boolean displaying) {
        this.displaying = displaying;
    }

    public boolean displaying() {
        return displaying;
    }

    @Override
    public void extractRenderState(@NotNull GuiGraphicsExtractor g, @NotNull DeltaTracker tracker) {
        if (!displaying) return;

        List<Component> components = new ArrayList<>();
        for (int i = 0; i < MenuValues.MAX_VALUES; i++) {
            String shorthandStr = values.shorthandValue(i);
            if (StringUtil.isNullOrEmpty(shorthandStr)) continue;
            String messageStr = values.messageValue(i);
            if (StringUtil.isNullOrEmpty(messageStr)) continue;

            Component c = Component.keybind("key.hotbar." + (i+1)).append(". " + shorthandStr);
            components.add(c);
        }

        Component closeComp = Component.translatable("procommands.voicemenu.close", keyMapping.getTranslatedKeyMessage());
        components.add(closeComp);

        Minecraft minecraft = Minecraft.getInstance();
        Font mcFont = minecraft.font;
        int lineHeight = mcFont.lineHeight;

        int spacer = mcFont.lineHeight;
        int menuX = spacer;

        int menuWidth = 0;
        for (Component c : components) {
            menuWidth = Math.max(menuWidth, mcFont.width(c.getString()));
        }

        menuWidth += (spacer*2); // spacing

        int menuHeight = (lineHeight*components.size()) + (spacer*2);
        int menuY = g.guiHeight()/2 - (menuHeight/2);

        g.fill(menuX, menuY, menuX+menuWidth, menuY+menuHeight, VoiceConfig.backgroundColor());

        int ypos = menuY + spacer;
        for (int i = 0; i < components.size(); i++) {
            Component c = components.get(i);

            g.text(mcFont, c, menuX + spacer, ypos + (lineHeight*i), VoiceConfig.textColor(), VoiceConfig.textShadow());
        }
    }

    public String line(int i) {
        if (i < 0 || i >= MenuValues.MAX_VALUES) return null;

        String shorthandValue = values.shorthandValue(i);
        if (shorthandValue == null || shorthandValue.isEmpty()) return null; // does not appear on menu

        String messageValue = values.messageValue(i);
        if (messageValue == null || messageValue.isEmpty()) return null;
        return messageValue;
    }
}
