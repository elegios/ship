package media;

/**
 *
 * @author elegios
 */
public class AnimateInt {

    public int current;
    public int target;

    boolean atTarget;

    public AnimateInt() {
        current  = 0;
        target   = 0;

        atTarget = true;
    }

    public void update(int i) {
        if (!atTarget) {
            if (Math.abs(current - target) <= 2)
                current = target;
            current += (target - current) / (1 + (float)i/10);
            if (current == target)
                atTarget = true;
        }
    }

    public void set(int target) {
        this.target = target;

        atTarget = false;
    }

    public void force(int target) {
        this.target = target;
        this.current = target;

        atTarget = true;
    }

    public int get() {
        return current;
    }
}