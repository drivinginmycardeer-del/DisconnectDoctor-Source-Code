package dev.disconnectdoctor;

import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Loads hints.json and matches a disconnect message to a plain-English explanation. No Minecraft code in here. */
public final class DisconnectHints {
    public static final class Hint {
        public String id;
        public List<String> match = new ArrayList<>();
        public String title;
        public String explanation;
        public String fix;
    }

    private static final String PATH = "/assets/disconnectdoctor/hints.json";
    private static List<Hint> hints = new ArrayList<>();

    public static final Hint FALLBACK = new Hint();
    static {
        FALLBACK.id = "unknown";
        FALLBACK.title = "Couldn't work out what went wrong";
        FALLBACK.explanation = "This error isn't in the hint list yet.";
        FALLBACK.fix = "Copy the message above and search it, or send it to the server owner.";
    }

    private DisconnectHints() {}

    public static void load() {
        try (InputStream in = DisconnectHints.class.getResourceAsStream(PATH)) {
            if (in == null) return;
            Hint[] arr = new Gson().fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), Hint[].class);
            hints = new ArrayList<>(List.of(arr));
        } catch (Exception e) {
            System.err.println("[DisconnectDoctor] Failed to load hints: " + e);
        }
    }

    /** First hint whose match strings appear in the message. Order in the JSON = priority. */
    public static Hint match(String message) {
        String m = message == null ? "" : message.toLowerCase(Locale.ROOT);
        for (Hint h : hints) {
            for (String s : h.match) {
                if (m.contains(s.toLowerCase(Locale.ROOT))) return h;
            }
        }
        return FALLBACK;
    }
}
