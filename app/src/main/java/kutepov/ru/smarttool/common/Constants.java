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

    public static final UUID MY_UUID = UUID.fromString("4f2bd256-2373-410f-bfcd-41909be5475f");
}
