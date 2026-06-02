package io.github.kittheuh.voicecmd.client;

import io.github.kittheuh.voicecmd.VoiceCommands;
import io.github.kittheuh.voicecmd.client.config.MenuValues;
import io.github.kittheuh.voicecmd.client.config.VoiceConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.StringUtil;
import org.lwjgl.glfw.GLFW;

public class VoiceMenuManager {
    private VoiceMenu activeMenu = null;
    private int activeMenuIndex = -1;

    private final KeyMapping.Category menuCategory;
    private final VoiceMenu menu1;
    private final VoiceMenu menu2;
    private final VoiceMenu menu3;

    public VoiceMenuManager() {
        menuCategory = new KeyMapping.Category(
                Identifier.fromNamespaceAndPath(VoiceCommands.MOD_ID, "menus")
        );

        menu1 = new VoiceMenu("menu1", GLFW.GLFW_KEY_Z, menuCategory, VoiceConfig.configMenuValues(0));
        menu2 = new VoiceMenu("menu2", GLFW.GLFW_KEY_X, menuCategory, VoiceConfig.configMenuValues(1));
        menu3 = new VoiceMenu("menu3", GLFW.GLFW_KEY_C, menuCategory, VoiceConfig.configMenuValues(2));
    }

    public KeyMapping.Category menuCategory() {
        return menuCategory;
    }

    public void processInput(Minecraft minecraft) {
        menu1.checkPress();
        menu2.checkPress();
        menu3.checkPress();

        VoiceMenu mostRecentMenu = null; // the menu most recently opened

        if (menu1.displaying()) mostRecentMenu = menu1;
        if (menu2.displaying()) {
            if (mostRecentMenu == null || mostRecentMenu.pressedOn() < menu2.pressedOn()) {
                mostRecentMenu = menu2;
            }
        }
        if (menu3.displaying()) {
            if (mostRecentMenu == null || mostRecentMenu.pressedOn() < menu3.pressedOn()) {
                mostRecentMenu = menu3;
            }
        }

        if (mostRecentMenu == null) {
            // no menu is open
            activeMenu = null;
            activeMenuIndex = -1;
            return;
        }

        // we know a menu is open, only show most recently opened
        menu1.displaying(mostRecentMenu == menu1);
        menu2.displaying(mostRecentMenu == menu2);
        menu3.displaying(mostRecentMenu == menu3);
        activeMenu = mostRecentMenu;

        if (activeMenu == menu1) activeMenuIndex = 0;
        else if (activeMenu == menu2) activeMenuIndex = 1;
        else activeMenuIndex = 2;
    }

    public boolean dispatchCommandSelect(int index) {
        if (activeMenu == null) return false;

        String message = activeMenu.line(index);
        if (message == null) return false;

        activeMenu.displaying(false);
        activeMenu = null;
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return false;

        MenuValues values = VoiceConfig.configMenuValues(activeMenuIndex);
        String prefix = values.effectivePrefix();

        message = (prefix == null ? "" : prefix) + (values.autoAppendSpacer() ? " " : "") + message;
        if (message.startsWith("/")) player.connection.sendCommand(message.substring(1));
        else {
            int oldLength = message.length();
            message = StringUtil.trimChatMessage(message);

            if (oldLength != message.length()) {
                player.sendSystemMessage(Component.translatable("svoicecommands.error.message_too_long", oldLength, message.length())
                        .withStyle(ChatFormatting.RED));
            }

            player.connection.sendChat(message);
        }
        return true;
    }

    public VoiceMenu[] menuArray() {
        return new VoiceMenu[]{menu1, menu2, menu3};
    }
}
