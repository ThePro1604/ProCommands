package io.github.kittheuh.voicecmd.client.config;

import net.minecraft.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.Properties;

public class MenuValues {
    public static final int MAX_VALUES = 9;

    private final String[] shorthandDef, messageDef;
    private final String[] shorthand, message;
    private boolean usePrefix = false, autoAppendSpacer = true;
    private String prefix = "";

    public MenuValues(String[] shorthand, String[] message) {
        if (shorthand.length != MAX_VALUES) this.shorthand = Arrays.copyOf(shorthand, MAX_VALUES);
        else this.shorthand = shorthand;

        if (message.length != MAX_VALUES) this.message = Arrays.copyOf(message, MAX_VALUES);
        else this.message = message;

        // instances are created with default values, save for config menu resetting
        this.shorthandDef = Arrays.copyOf(shorthand, MAX_VALUES);
        this.messageDef = Arrays.copyOf(message, MAX_VALUES);
    }

    public void load(Properties properties, int index) {
        String settingPrefix = "voicemenu.%d.".formatted(index);

        for (int i = 0; i < MAX_VALUES; i++) {
            String shortProp = properties.getProperty(settingPrefix + "shorthand" + i, null);
            if (StringUtils.isBlank(shortProp)) shortProp = null;

            String msgProp = properties.getProperty(settingPrefix + "message" + i, null);
            if (StringUtils.isBlank(msgProp)) msgProp = null;

            shorthand[i] = shortProp;
            message[i] = msgProp;
        }

        usePrefix = Boolean.parseBoolean(properties.getProperty(settingPrefix + "use-prefix", "false"));
        autoAppendSpacer = Boolean.parseBoolean(properties.getProperty(settingPrefix + "auto-append-spacer", "true"));
        prefix = properties.getProperty(settingPrefix + "prefix", "");
    }

    public void save(Properties properties, int index) {
        String settingPrefix = "voicemenu.%d.".formatted(index);

        for (int i = 0; i < MAX_VALUES; i++) {
            String shortProp = shorthand[i];
            properties.setProperty(settingPrefix + "shorthand" + i, shortProp == null ? "" : shortProp);

            String msgProp = message[i];
            properties.setProperty(settingPrefix + "message" + i, msgProp == null ? "" : msgProp);
        }

        properties.setProperty(settingPrefix + "use-prefix", Boolean.toString(usePrefix));
        properties.setProperty(settingPrefix + "auto-append-spacer", Boolean.toString(autoAppendSpacer));
        properties.setProperty(settingPrefix + "prefix", prefix);
    }

    public String[] shorthand() {
        return shorthand;
    }

    public String[] shorthandDef() {
        return shorthandDef;
    }

    public String shorthandValue(int index) {
        String s = shorthand[index];
        return s != null && !s.isEmpty() ? s : "";
    }

    public void updateShorthand(int index, String value) {
        shorthand[index] = value.isEmpty() ? null : value;
    }

    public String[] message() {
        return message;
    }

    public String[] messageDef() {
        return messageDef;
    }

    public String messageValue(int index) {
        String s = message[index];
        return s != null && !s.isEmpty() ? s : "";
    }

    public void updateMessage(int index, String value) {
        message[index] = value.isEmpty() ? null : value;
    }

    public boolean usePrefix() {
        return usePrefix;
    }

    public void usePrefix(boolean usePrefix) {
        this.usePrefix = usePrefix;
    }

    public boolean autoAppendSpacer() {
        return autoAppendSpacer;
    }

    public void autoAppendSpacer(boolean autoAppendSpacer) {
        this.autoAppendSpacer = autoAppendSpacer;
    }

    @NotNull
    public String prefix() {
        if (prefix == null) prefix = "";
        return prefix;
    }

    @Nullable
    public String effectivePrefix() {
        if (usePrefix) return prefix;
        return VoiceConfig.globalChatPrefix();
    }

    public void prefix(String prefix) {
        this.prefix = prefix;
    }

    public boolean allInvalid() {
        for (int i = 0; i < MAX_VALUES; i++) {
            String s = shorthand[i];
            String m = message[i];

            if (!StringUtil.isNullOrEmpty(s) && !StringUtil.isNullOrEmpty(m)) return false;
        }
        return true;
    }
}
