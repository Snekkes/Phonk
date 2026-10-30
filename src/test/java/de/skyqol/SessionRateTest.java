package de.skyqol;
public final class SessionRateTest {
    public static void main(String[] args) {
        assert SessionRate.perHour(5, 1_000, 60_999) == -1;
        assert SessionRate.perHour(5, 1_000, 61_000) == 300;
        assert SessionRate.perHour(2, 1_000, 7_201_000) == 1;
        assert SessionRate.perHour(42, 0, 99_000) == -1;
        assert SessionRate.perHour(1, 5_000, 1_000) == -1;
        assert SessionRate.perHour(Long.MAX_VALUE, 1, 100_001) > 0;
        System.out.println("SessionRateTest OK");
    }
}
