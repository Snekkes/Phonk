package de.skyqol;
public class FusionNotesTest {
    public static void main(String[] args) {
        var f = new FusionNotes();
        assert f.display() == null;
        assert !f.record(0);
        assert f.record(1000);
        assert !f.record(1100) : "Ignore repeated key presses";
        assert f.record(1500);
        assert f.manualNotes() == 2;
        assert "Manual fusion notes: 2".equals(f.display());
        f.reset();
        assert f.manualNotes() == 0;
        System.out.println("FusionNotesTest OK");
    }
}
