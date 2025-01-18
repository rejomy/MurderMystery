package me.rejomy.murder.util.value;

import lombok.Getter;
import me.rejomy.murder.util.RandomUtil;

@Getter
public class BothIntValue {

    final int min;
    final int max;

    public BothIntValue(int first, int second) {
        max = Math.max(first, second);
        min = Math.min(first, second);
    }

    public int getDiapasonValue() {
        return min + RandomUtil.RANDOM.nextInt((max - min) + 1);
    }
}
