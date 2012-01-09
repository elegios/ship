package media;

import java.awt.Color;
import java.nio.file.Path;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;

public class FontHolder {
    public static final int INV_SIZE = 20;

    private UnicodeFont inv;
    private UnicodeFont invHighlight;
    private UnicodeFont invSelected;

    @SuppressWarnings("unchecked")
    public FontHolder(Path artDir) throws SlickException {
        inv = new UnicodeFont(artDir.toString() + "/inv.ttf", 20, false, false);
        inv.addAsciiGlyphs();
        inv.getEffects().add(new ColorEffect(Color.BLACK));
        inv.loadGlyphs();

        invHighlight = new UnicodeFont(artDir.toString() + "/inv.ttf", 20, false, false);
        invHighlight.addAsciiGlyphs();
        invHighlight.getEffects().add(new ColorEffect(Color.gray));
        invHighlight.loadGlyphs();

        invSelected = new UnicodeFont(artDir.toString() + "/inv.ttf", 20, false, false);
        invSelected.addAsciiGlyphs();
        invSelected.getEffects().add(new ColorEffect(Color.lightGray));
        invSelected.loadGlyphs();
    }

    public UnicodeFont inv         () { return inv;          }
    public UnicodeFont invHighlight() { return invHighlight; }
    public UnicodeFont invSelected () { return invSelected;  }

}
