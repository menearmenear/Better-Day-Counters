package net.menear.betterdaycounters;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public final class DayCounterConfigScreen extends Screen {
    private final Screen parent;
    private final List<AbstractWidget> snapWidgets = new ArrayList<>();
    private final List<Integer> snapBaseY = new ArrayList<>();
    private int scrollY = 0;

    public DayCounterConfigScreen(Screen parent) {
        super(Component.literal("Better Day Counters"));
        this.parent = parent;
    }

    @Override
    protected <T extends net.minecraft.client.gui.components.events.GuiEventListener & net.minecraft.client.gui.components.Renderable & net.minecraft.client.gui.narration.NarratableEntry> T addRenderableWidget(T widget) {
        T result = super.addRenderableWidget(widget);
        snapWidgets.add((AbstractWidget) result);
        snapBaseY.add(((AbstractWidget) result).getY());
        return result;
    }

    private void applyScroll() {
        for (int i = 0; i < snapWidgets.size(); i++) {
            snapWidgets.get(i).setY(snapBaseY.get(i) + scrollY);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        scrollY += (int) (verticalAmount * 24);
        int contentHeight = snapBaseY.isEmpty() ? 0 : snapBaseY.get(snapBaseY.size() - 1) + 40;
        int maxScroll = Math.max(0, contentHeight - (this.height - 40));
        scrollY = Math.max(-maxScroll, Math.min(0, scrollY));
        applyScroll();
        return true;
    }

    @Override
    protected void init() {
        snapWidgets.clear();
        snapBaseY.clear();
        scrollY = 0;
        DayCounterConfig cfg = DayCounterConfig.get();
        int cx = this.width / 2;
        int y = 40;
        int w = 200;

        this.addRenderableWidget(Button.builder(toggleLabel("Enabled", cfg.enabled), b -> {
            cfg.enabled = !cfg.enabled;
            b.setMessage(toggleLabel("Enabled", cfg.enabled));
            DayCounterConfig.save();
            VanillaDayCounterBridge.apply(cfg.enabled);
        }).bounds(cx - w / 2, y, w, 20).build());
        y += 24;

        this.addRenderableWidget(Button.builder(cycleLabel("Anchor", cfg.anchor), b -> {
            cfg.anchor = (cfg.anchor + 1) % 9;
            b.setMessage(cycleLabel("Anchor", cfg.anchor));
            DayCounterConfig.save();
        }).bounds(cx - w / 2, y, w, 20).build());
        y += 24;

        this.addRenderableWidget(Button.builder(Component.literal("Scale +"), b -> {
            cfg.scale = Math.min(3.0F, cfg.scale + 0.1F);
            DayCounterConfig.save();
        }).bounds(cx - w / 2, y, w / 2 - 2, 20).build());
        this.addRenderableWidget(Button.builder(Component.literal("Scale -"), b -> {
            cfg.scale = Math.max(0.5F, cfg.scale - 0.1F);
            DayCounterConfig.save();
        }).bounds(cx + 2, y, w / 2 - 2, 20).build());
        // relabel is simpler through note; keep static
        y += 24;

        this.addRenderableWidget(Button.builder(toggleLabel("Shadow", cfg.shadow), b -> {
            cfg.shadow = !cfg.shadow;
            b.setMessage(toggleLabel("Shadow", cfg.shadow));
            DayCounterConfig.save();
        }).bounds(cx - w / 2, y, w, 20).build());
        y += 24;

        this.addRenderableWidget(Button.builder(toggleLabel("Background", cfg.background), b -> {
            cfg.background = !cfg.background;
            b.setMessage(toggleLabel("Background", cfg.background));
            DayCounterConfig.save();
        }).bounds(cx - w / 2, y, w, 20).build());
        y += 24;

        this.addRenderableWidget(Button.builder(toggleLabel("Rainbow Text", cfg.rainbowText), b -> {
            cfg.rainbowText = !cfg.rainbowText;
            b.setMessage(toggleLabel("Rainbow Text", cfg.rainbowText));
            DayCounterConfig.save();
        }).bounds(cx - w / 2, y, w, 20).build());
        y += 24;

        this.addRenderableWidget(Button.builder(toggleLabel("Rainbow Background", cfg.rainbowBackground), b -> {
            cfg.rainbowBackground = !cfg.rainbowBackground;
            b.setMessage(toggleLabel("Rainbow Background", cfg.rainbowBackground));
            DayCounterConfig.save();
        }).bounds(cx - w / 2, y, w, 20).build());
        y += 24;

        EditBox textColor = new EditBox(this.font, cx - w / 2, y, w, 20, Component.literal("Text Color"));
        textColor.setValue(DayCounterConfig.formatColor(cfg.textColor));
        textColor.setResponder(v -> {
            cfg.textColor = DayCounterConfig.parseColor(v, cfg.textColor);
            DayCounterConfig.save();
        });
        this.addRenderableWidget(textColor);
        y += 24;

        EditBox bgColor = new EditBox(this.font, cx - w / 2, y, w, 20, Component.literal("Background Color"));
        bgColor.setValue(DayCounterConfig.formatColor(cfg.backgroundColor));
        bgColor.setResponder(v -> {
            cfg.backgroundColor = DayCounterConfig.parseColor(v, cfg.backgroundColor);
            DayCounterConfig.save();
        });
        this.addRenderableWidget(bgColor);
        y += 24;

        this.addRenderableWidget(Button.builder(toggleLabel("Glow", cfg.glow), b -> {
            cfg.glow = !cfg.glow;
            b.setMessage(toggleLabel("Glow", cfg.glow));
            DayCounterConfig.save();
        }).bounds(cx - w / 2, y, w, 20).build());
        y += 24;

        this.addRenderableWidget(Button.builder(toggleLabel("Rainbow Glow", cfg.rainbowGlow), b -> {
            cfg.rainbowGlow = !cfg.rainbowGlow;
            b.setMessage(toggleLabel("Rainbow Glow", cfg.rainbowGlow));
            DayCounterConfig.save();
        }).bounds(cx - w / 2, y, w, 20).build());
        y += 24;

        EditBox glowColor = new EditBox(this.font, cx - w / 2, y, w, 20, Component.literal("Glow Color"));
        glowColor.setValue(DayCounterConfig.formatColor(cfg.glowColor));
        glowColor.setResponder(v -> {
            cfg.glowColor = DayCounterConfig.parseColor(v, cfg.glowColor);
            DayCounterConfig.save();
        });
        this.addRenderableWidget(glowColor);
        y += 24;

        this.addRenderableWidget(Button.builder(toggleLabel("Roman Numerals", cfg.romanNumerals), b -> {
            cfg.romanNumerals = !cfg.romanNumerals;
            b.setMessage(toggleLabel("Roman Numerals", cfg.romanNumerals));
            DayCounterConfig.save();
        }).bounds(cx - w / 2, y, w, 20).build());
        y += 24;

        this.addRenderableWidget(Button.builder(toggleLabel("Day starts at 1", cfg.oneBasedDay), b -> {
            cfg.oneBasedDay = !cfg.oneBasedDay;
            b.setMessage(toggleLabel("Day starts at 1", cfg.oneBasedDay));
            DayCounterConfig.save();
        }).bounds(cx - w / 2, y, w, 20).build());
        y += 24;

        this.addRenderableWidget(Button.builder(cycleFontLabel(cfg.font), b -> {
            List<String> fonts = List.of("minecraft:default", "minecraft:uniform", "minecraft:alt", "minecraft:illageralt");
            int i = fonts.indexOf(cfg.font);
            i = (i + 1) % fonts.size();
            cfg.font = fonts.get(i);
            b.setMessage(cycleFontLabel(cfg.font));
            DayCounterConfig.save();
        }).bounds(cx - w / 2, y, w, 20).build());
        y += 28;

        EditBox prefix = new EditBox(this.font, cx - w / 2, y, w, 20, Component.literal("Prefix"));
        prefix.setValue(cfg.prefix);
        prefix.setResponder(v -> {
            cfg.prefix = v;
            DayCounterConfig.save();
        });
        this.addRenderableWidget(prefix);
        y += 28;

        this.addRenderableWidget(Button.builder(Component.literal("Done"), b -> {
            Minecraft.getInstance().setScreenAndShow(parent);
        }).bounds(cx - w / 2, y, w, 20).build());
    }

    private static Component toggleLabel(String name, boolean v) {
        return Component.literal(name + ": " + (v ? "On" : "Off"));
    }

    private static Component cycleFontLabel(String font) {
        return Component.literal("Font: " + font);
    }

    private static Component cycleLabel(String name, int v) {
        String[] names = {"Top Left", "Top Center", "Top Right", "Center Left", "Center", "Center Right", "Bottom Left", "Bottom Center", "Bottom Right"};
        return Component.literal(name + ": " + names[v % names.length]);
    }

    private static Component textLabel(String name, String value, boolean plus) {
        return Component.literal((plus ? name + " +" : name + " -"));
    }

    private static String formatFloat(float v) {
        return String.format("%.1f", v);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTick) {
        extractor.fill(0, 0, this.width, this.height, 0x90000000);
        super.extractRenderState(extractor, mouseX, mouseY, partialTick);
        extractor.centeredText(this.font, this.title, this.width / 2, 16, 0xFFFFFFFF);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}