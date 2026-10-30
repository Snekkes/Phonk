package de.skyqol;

public final class HypixelServerTest {
    public static void main(String[] args) {
        assert HypixelServer.matches("mc.hypixel.net");
        assert HypixelServer.matches("HYPIXEL.NET:25565");
        assert HypixelServer.matches("proxy.example.hypixel.net.");
        assert !HypixelServer.matches("evilhypixel.net");
        assert !HypixelServer.matches("hypixel.net.evil.org");
        assert !HypixelServer.matches("hypixel.net:0");
        assert !HypixelServer.matches("hypixel.net:99999");
        assert !HypixelServer.matches("http://hypixel.net");
        assert !HypixelServer.matches("play.other-server.net");
        assert !HypixelServer.matches(null);
        System.out.println("HypixelServerTest OK");
    }
}
