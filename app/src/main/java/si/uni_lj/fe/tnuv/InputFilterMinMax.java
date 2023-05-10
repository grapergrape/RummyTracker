package si.uni_lj.fe.tnuv;

import android.text.InputFilter;
import android.text.Spanned;

public class InputFilterMinMax implements InputFilter {

    private int min;
    private int max;

    public InputFilterMinMax(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public InputFilterMinMax(String min, String max) {
        this.min = Integer.parseInt(min);
        this.max = Integer.parseInt(max);
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        try {
            int input = Integer.parseInt(dest.toString() + source.toString());
            if (isInRange(input))
                return null;
        } catch (NumberFormatException nfe) {
            // do nothing
        }
        return "";
    }

    private boolean isInRange(int c) {
        return c >= min && c <= max;
    }
}
