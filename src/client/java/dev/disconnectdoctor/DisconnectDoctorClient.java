package dev.disconnectdoctor;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.List;

public class DisconnectDoctorClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        DisconnectHints.load();

        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (!(screen instanceof DisconnectedScreen)) return;

            // Title + reason joined together, no private fields or mixins needed.
            String message = screen.getNarrationMessage().getString();
            DisconnectHints.Hint hint = DisconnectHints.match(message);

            ScreenEvents.afterRender(screen).register((s, graphics, mouseX, mouseY, delta) ->
                    drawPanel(graphics, client.font, s.width, s.height, hint));
        });
    }

    private static void drawPanel(GuiGraphics g, Font font, int width, int height, DisconnectHints.Hint hint) {
        int panelWidth = Math.min(width - 20, 320);
        int x = (width - panelWidth) / 2;
        int inner = panelWidth - 12;

        List<FormattedCharSequence> lines = new ArrayList<>();
        lines.addAll(font.split(Component.literal(hint.title), inner));
        lines.add(FormattedCharSequence.EMPTY);
        lines.addAll(font.split(Component.literal(hint.explanation), inner));
        lines.add(FormattedCharSequence.EMPTY);
        lines.addAll(font.split(Component.literal("Try this: " + hint.fix), inner));

        int lineHeight = 10;
        int panelHeight = lines.size() * lineHeight + 12;
        int y = height - panelHeight - 8;

        g.fill(x, y, x + panelWidth, y + panelHeight, 0xB0000000);
        int ty = y + 6;
        for (int i = 0; i < lines.size(); i++) {
            int color = (i == 0) ? 0xFFFFD75E : 0xFFFFFFFF; // title in yellow
            g.drawString(font, lines.get(i), x + 6, ty, color);
            ty += lineHeight;
        }
    }
}
