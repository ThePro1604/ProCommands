package io.github.kittheuh.voicecmd.client.config;

import io.github.kittheuh.voicecmd.VoiceCommands;
import io.github.kittheuh.voicecmd.client.VoiceCommandsClient;
import io.github.kittheuh.voicecmd.client.VoiceMenu;
import io.github.kittheuh.voicecmd.client.VoiceMenuManager;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.api.Requirement;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import me.shedaniel.clothconfig2.gui.entries.StringListEntry;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class VoiceConfig {
    public static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("voicecommands.properties");

    private static final MenuValues[] menuValues = new MenuValues[3];

    private static int backgroundColor = ARGB.black(128), textColor = ARGB.white(192);
    private static boolean blockSlotChange = true, textShadow= true;
    private static String globalChatPrefix = "(Voice)";

    static {
        // default values
        menuValues[0] = new MenuValues(
                new String[]{"MEDIC!", "Thanks!", "Go Go Go!", "Move Up!", "Go Left", "Go Right", "Yes", "No", "Pass to me!"},
                new String[]{"MEDIC!", "Thanks!", "Go Go Go!", "Move Up!", "Go Left", "Go Right", "Yes", "No", "Pass to me!"}
        );

        menuValues[1] = new MenuValues(
                new String[]{"Incoming", "Spy!", "Sentry Ahead!", "Teleporter Here", "Dispenser Here", "Sentry Here", "Activate ÜberCharge", "MEDIC: ÜberCharge Ready", "Pass to me!"},
                new String[]{"Incoming!", "Spy around here!", "Sentry Ahead!", "Place a Teleporter Here", "Place a Dispenser Here", "Place a Sentry Here", "Activate ÜberCharge", "MEDIC: ÜberCharge Ready", "Pass to me!"}
        );

        menuValues[2] = new MenuValues(
                new String[]{"Help!", "Battle Cry", "Cheers", "Jeers", "Positive", "Negative", "Nice Shot", "Good Job"},
                new String[]{"Help!", "Battle Cry", "Cheers", "Jeers", "Positive", "Negative", "Nice Shot", "Good Job"}
        );

        loadConfig();
    }

    public static void loadConfig() {
        if (Files.notExists(CONFIG_PATH)) return;

        Properties properties = new Properties();
        try (InputStream stream = Files.newInputStream(CONFIG_PATH)) {
            properties.load(stream);

            blockSlotChange = Boolean.parseBoolean(properties.getProperty("global.block-slot-change", "true"));
            globalChatPrefix = properties.getProperty("global.chat-prefix", "(Voice)");
            textShadow = Boolean.parseBoolean(properties.getProperty("global.text-shadow", "true"));

            try {
                backgroundColor = Integer.parseInt(properties.getProperty("global.background-color", String.valueOf(ARGB.black(128))));
            } catch (NumberFormatException e) {
                VoiceCommands.LOGGER.warn("Invalid value set for menu background color");
            }

            try {
                textColor = Integer.parseInt(properties.getProperty("global.text-color", String.valueOf(ARGB.white(192))));
            } catch (NumberFormatException e) {
                VoiceCommands.LOGGER.warn("Invalid value set for menu text color");
            }

            for (int i = 0; i < menuValues.length; i++) {
                menuValues[i].load(properties, i);
            }
        } catch (IOException e) {
            VoiceCommands.LOGGER.error("Failed to load config file", e);
        }
    }

    public static void saveConfig() {
        Properties properties = new Properties();
        try (OutputStream stream = Files.newOutputStream(CONFIG_PATH)) {
            properties.setProperty("global.block-slot-change", Boolean.toString(blockSlotChange));
            properties.setProperty("global.chat-prefix", globalChatPrefix);
            properties.setProperty("global.text-shadow", Boolean.toString(textShadow));

            properties.setProperty("global.background-color", String.valueOf(backgroundColor));
            properties.setProperty("global.text-color", String.valueOf(textColor));

            for (int i = 0; i < menuValues.length; i++) {
                menuValues[i].save(properties, i);
            }

            properties.store(stream, "VoiceCommand mod configuration file");
        } catch (IOException e) {
            VoiceCommands.LOGGER.error("Failed to save config file", e);
        }
    }

    public static MenuValues configMenuValues(int i) {
        return menuValues[i];
    }

    public static String globalChatPrefix() {
        return globalChatPrefix;
    }

    public static boolean blockSlotChange() {
        return blockSlotChange;
    }

    public static int backgroundColor() {
        return backgroundColor;
    }

    public static int textColor() {
        return textColor;
    }

    public static boolean textShadow() {
        return textShadow;
    }

    public static Screen createConfigScreen() {
        VoiceMenuManager manager = VoiceCommandsClient.instance().manager();

        ConfigBuilder builder = ConfigBuilder.create()
                .setTitle(Component.translatable("svoicecommands.config.title"))
                .setTransparentBackground(true)
                .setSavingRunnable(VoiceConfig::saveConfig);

        ConfigCategory mainCategory = builder.getOrCreateCategory(Component.translatable("svoicecommands.config.tab.global"));
        mainCategory.addEntry(ConfigEntryBuilder.create()
                .fillKeybindingField(Component.translatable("key.%s.open_settings".formatted(VoiceCommands.MOD_ID)), VoiceCommandsClient.instance().settingsKeyMapping())
                .build()
        );

        mainCategory.addEntry(ConfigEntryBuilder.create()
                .startBooleanToggle(Component.translatable("svoicecommands.config.option.block_hotbar_slot_change.label"), blockSlotChange)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("svoicecommands.config.option.block_hotbar_slot_change.tooltip"))
                .setSaveConsumer(b -> blockSlotChange = b)
                .build()
        );

        mainCategory.addEntry(ConfigEntryBuilder.create()
                .startStrField(Component.translatable("svoicecommands.config.option.global_prefix.label"), globalChatPrefix)
                .setTooltip(Component.translatable("svoicecommands.config.option.global_prefix.tooltip"))
                .setDefaultValue("(Voice)")
                .setSaveConsumer(string -> globalChatPrefix = string)
                .build()
        );

        mainCategory.addEntry(ConfigEntryBuilder.create()
                .startAlphaColorField(Component.translatable("svoicecommands.config.option.menu_background_color.label"), backgroundColor)
                .setDefaultValue(ARGB.black(128))
                .setSaveConsumer(i -> backgroundColor = i)
                .build()
        );

        mainCategory.addEntry(ConfigEntryBuilder.create()
                .startAlphaColorField(Component.translatable("svoicecommands.config.option.menu_text_color.label"), textColor)
                .setDefaultValue(ARGB.white(192))
                .setSaveConsumer(i -> textColor = i)
                .build()
        );

        mainCategory.addEntry(ConfigEntryBuilder.create()
                .startBooleanToggle(Component.translatable("svoicecommands.config.option.menu_text_shadow.label"), textShadow)
                .setDefaultValue(true)
                .setSaveConsumer(b -> textShadow = b)
                .build()
        );

        VoiceMenu[] voiceMenus = manager.menuArray();
        for (int i = 0; i < voiceMenus.length; i++) {
            VoiceMenu voiceMenu = voiceMenus[i];
            MenuValues values = menuValues[i];

            ConfigCategory category = builder.getOrCreateCategory(Component.translatable("svoicecommands.config.tab.voicemenu", i + 1));

            category.addEntry(ConfigEntryBuilder.create()
                    .fillKeybindingField(Component.translatable("key.%s.menu%d".formatted(VoiceCommands.MOD_ID, i+1)), voiceMenu.keyMapping())
                    .build()
            );

            BooleanListEntry autoAppendSpacerToggle = ConfigEntryBuilder.create()
                    .startBooleanToggle(Component.translatable("svoicecommands.config.option.voicemenu.auto_append_spacer.label"), values.autoAppendSpacer())
                    .setTooltip(Component.translatable("svoicecommands.config.option.voicemenu.auto_append_spacer.tooltip"))
                    .setSaveConsumer(values::autoAppendSpacer)
                    .build();
            category.addEntry(autoAppendSpacerToggle);

            BooleanListEntry useCustomPrefixToggle = ConfigEntryBuilder.create()
                    .startBooleanToggle(Component.translatable("svoicecommands.config.option.voicemenu.use_custom_prefix.label"), values.usePrefix())
                    .setTooltip(Component.translatable("svoicecommands.config.option.voicemenu.use_custom_prefix.tooltip"))
                    .setSaveConsumer(values::usePrefix)
                    .build();
            category.addEntry(useCustomPrefixToggle);

            StringListEntry customPrefixField = ConfigEntryBuilder.create()
                    .startStrField(Component.translatable("svoicecommands.config.option.voicemenu.custom_prefix.label"), values.prefix())
                    .setTooltip(Component.translatable("svoicecommands.config.option.voicemenu.custom_prefix.tooltip"))
                    .setDefaultValue("")
                    .setSaveConsumer(values::prefix)
                    .setDisplayRequirement(Requirement.isTrue(useCustomPrefixToggle))
                    .build();
            category.addEntry(customPrefixField);

            for (int j = 0; j < MenuValues.MAX_VALUES; j++) {
                SubCategoryBuilder subCategory = ConfigEntryBuilder.create()
                        .startSubCategory(Component.translatable("svoicecommands.config.option.voicemenu.sub_entry.label", j + 1))
                        .setTooltip(Component.translatable("svoicecommands.config.option.voicemenu.sub_entry.tooltip"));

                final int index = j;
                subCategory.add(ConfigEntryBuilder.create()
                        .startStrField(Component.translatable("svoicecommands.config.option.voicemenu.shorthand.label"), values.shorthandValue(j))
                        .setDefaultValue(values.shorthandDef()[j])
                        .setSaveConsumer(s -> values.updateShorthand(index, s))
                        .setTooltip(Component.translatable("svoicecommands.config.option.voicemenu.shorthand.tooltip"))
                        .build()
                );

                subCategory.add(ConfigEntryBuilder.create()
                        .startStrField(Component.translatable("svoicecommands.config.option.voicemenu.message.label"), values.messageValue(j))
                        .setDefaultValue(values.messageDef()[j])
                        .setSaveConsumer(s -> values.updateMessage(index, s))
                        .setTooltip(Component.translatable("svoicecommands.config.option.voicemenu.message.tooltip"))

                        .build()
                );
                category.addEntry(subCategory.build());
            }
        }

        return builder.build();
    }
}
