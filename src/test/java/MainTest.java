import org.junit.Test;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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


	@Test
	public void passwordEncodeDecodeTest() {
		String pass1 = "Test123123";
		String pass2 = "SomeInterestingPassword#1";
		String pass3 = "#f67wellPlay";

		String e1 = "3a9c2dd279e55cdbe85d6c486253488";
		String e2 = "64247caec23acd61e5dc9ca4adad2f0";
		String e3 = "3cb5a2de077c2cded4db5d56de3b22";

		String s1 = cryptWithMD5(pass1);
		String s2 = cryptWithMD5(pass2);
		String s3 = cryptWithMD5(pass3);

		System.out.println(s1);
		System.out.println(s2);
		System.out.println(s3);
	}


	@Test
	public void throwTest() {
		try {
			throwSmth();
		} catch (Exception e) {
			System.out.println("MSG: " + e.getMessage());
		}
	}

	private static void throwSmth() {
		throw new RuntimeException("SOME MESSAGE");
	}

	private static String cryptWithMD5(String pass) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.reset();

			StringBuilder sb = new StringBuilder();
			for (byte b : md.digest(pass.getBytes()))
				sb.append(Integer.toHexString(0xff & b));
			return sb.toString();
		} catch (NoSuchAlgorithmException ex) {
			throw new RuntimeException("Cannot encode user password", ex);
		}
	}
}