package kutepov.ru.smarttool.common;

import java.util.UUID;

/**
 * Created by alexey on 15.03.17.
 */

public class Constants {
    private static final Constants ourInstance = new Constants();

    public static Constants getInstance() {
        return ourInstance;
    }

    private Constants() {
    }

    public static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
}
