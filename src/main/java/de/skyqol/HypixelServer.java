package de.skyqol;

import java.util.Locale;

/** Strict hostname gate: avoid collecting progress on unrelated servers or singleplayer. */
public final class HypixelServer {
    private HypixelServer() {}

    public static boolean matches(String serverAddress) {
        if (serverAddress == null) return false;
        String host = serverAddress.trim().toLowerCase(Locale.ROOT);
        // Minecraft server list addresses may contain a port, but reject URL, path, or credentials.
        if (host.startsWith("http:") || host.startsWith("https:") || host.contains("/") || host.contains("@")) return false;
        int portSeparator = host.lastIndexOf(':');
        if (portSeparator >= 0) {
            String port = host.substring(portSeparator + 1);
            if (port.isEmpty() || port.length() > 5 || !port.chars().allMatch(Character::isDigit)) return false;
            try { int value = Integer.parseInt(port); if (value < 1 || value > 65535) return false; }
            catch (NumberFormatException ex) { return false; }
            host = host.substring(0, portSeparator);
        }
        if (host.endsWith(".")) host = host.substring(0, host.length()-1);
        return host.equals("hypixel.net") || host.endsWith(".hypixel.net");
    }
}
