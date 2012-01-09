package media;

/**
 *
 * @author elegios
 */
public class AnimateFloat {

    public float current;
    public float target;

    boolean atTarget;

    public AnimateFloat() {
        current  = 0;
        target   = 0;

        atTarget = true;
    }

    public void update(int diff) {
        if (!atTarget) {
            if (Math.abs(current - target) <= 0.001f)
                current = target;
            current += (target - current) / (1 + (float)diff/10);
            if (current == target)
                atTarget = true;
        }
    }

    public void set(float target) {
        this.target = target;

        atTarget = false;
    }

    public void force(float target) {
        this.target = target;
        this.current = target;

        atTarget = true;
    }

    public float get() {
        return current;
    }
}