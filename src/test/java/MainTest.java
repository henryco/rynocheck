import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Henry on 15/01/18.
 */

public class MainTest {


	@Test
	public void mapTest() {

		Map<Long, String> map = new HashMap<>();

		for (long i = 0; i < 100; i++) {
			map.put(i, "some: " + i);
		}

		for (long i = 0; i < 100; i++) {
			@SuppressWarnings("UnnecessaryBoxing")
			Long l = new Long(i);
			if (map.containsKey(l))
				System.out.println("CONTAINS: " + l);
		}
	}
}