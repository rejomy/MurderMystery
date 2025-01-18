package me.rejomy.murder.util;

import lombok.experimental.UtilityClass;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class TimeUtil {

    public int toMillis(String format) {
        Pattern pattern = Pattern.compile("(\\d+)([A-z]+)");
        Matcher matcher = pattern.matcher(format);

        if (matcher.find()) {
            int time = Integer.parseInt(matcher.group(1));
            String unit = matcher.group(2).toLowerCase(Locale.ENGLISH);

            if ("ticks".startsWith(unit)) {
                return time * 50;
            }

            if ("seconds".startsWith(unit)) {
                return time * 1000;
            }

            if ("minutes".startsWith(unit)) {
                return time * 1000 * 60;
            }

            if ("hours".startsWith(unit)) {
                return time * 1000 * 60 * 60;
            }

            if ("days".startsWith(unit)) {
                return time * 1000 * 60 * 60 * 24;
            }
        }

        throw new IllegalArgumentException("Time " + format + " is incorrect! Use 10m or 10s as example.");
    }

    public int toTicks(String format) {
        return toMillis(format) / 50;
    }

    public int toSeconds(String format) {
        return toMillis(format) / 1000;
    }
}
