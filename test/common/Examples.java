package common;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Some short & useful code examples
 *
 * @author M Robertson
 */
public class Examples {
    /// Map declaration (inline)
    // @formatter:off
    final Map<String, String> map = new LinkedHashMap<String, String>() {{
       put("first"          , "primary details"                    );
       put("second"         , "secondary details"                  );
       put("subsequent ..." , "subsequent details continue on ..." );
    }};
    // @formatter:on

    /// List declaration (inline)
    final List<String> list = new ArrayList<String>() {{
        add("first");
        add("second");
        add("continues on ...");
    }};

    /// Convert list to array
    String[] arr = list.toArray(new String[0]);

}
