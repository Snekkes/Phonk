package de.skyqol;

public final class MessageDeduplicatorTest {
    public static void main(String[] args) {
        MessageDeduplicator d = new MessageDeduplicator();
        assert d.accept("+12 log", 1000);
        assert !d.accept("+12 log", 1100);
        assert d.accept("+12 log", 1250);
        assert d.accept("+12 wood", 1251);
        for (int i=0;i<200;i++) d.accept("message "+i, 2000+i);
        assert d.rememberedKeys() <= 64;
        d.clear();
        assert d.rememberedKeys() == 0;
        System.out.println("MessageDeduplicatorTest OK");
    }
}
