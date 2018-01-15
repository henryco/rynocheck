import net.henryco.rynocheck.RynoCheckPlugin;
import org.junit.Test;

import java.io.File;

/**
 * @author Henry on 15/01/18.
 */
public class MainTest {

	@Test
	public void test() {
		System.out.println(System.getProperty("user.dir") + File.separator + "rynocheck" + File.separator + "mc.db");
	}

}