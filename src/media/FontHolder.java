package media;

import java.awt.Color;
import java.nio.file.Path;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;

public class FontHolder {
    public static final int INV_SIZE = 20;

    private UnicodeFont inv;
    private UnicodeFont invSelected;
    private UnicodeFont name;
    private UnicodeFont chat;

    @SuppressWarnings("unchecked")
    public FontHolder(Path artDir) throws SlickException {
        inv = new UnicodeFont(artDir.toString() + "/inv.ttf", 20, false, false);
        inv.addAsciiGlyphs();
        inv.getEffects().add(new ColorEffect(Color.BLACK));
        inv.loadGlyphs();

        invSelected = new UnicodeFont(artDir.toString() + "/inv.ttf", 20, false, false);
        invSelected.addAsciiGlyphs();
        invSelected.getEffects().add(new ColorEffect(Color.lightGray));
        invSelected.loadGlyphs();

        name = new UnicodeFont(artDir.toString() + "/name.ttf", 12, false, false);
        name.addAsciiGlyphs();
        name.getEffects().add(new ColorEffect(Color.PINK));
        name.loadGlyphs();

        chat = new UnicodeFont(artDir.toString() + "/chat.ttf", 14, false, false);
        chat.addAsciiGlyphs();
        chat.getEffects().add(new ColorEffect(Color.BLACK));
        chat.loadGlyphs();
    }

    public UnicodeFont inv         () { return inv;          }
    public UnicodeFont invSelected () { return invSelected;  }
    public UnicodeFont name        () { return name;         }
    public UnicodeFont chat        () { return chat;         }

}
