package lottery;

import java.math.BigDecimal;

import org.junit.Test;
import org.springframework.util.Assert;

public class BigDecimalTest {

	@Test
	public void test() {
		BigDecimal val1 = new BigDecimal(1001);
		BigDecimal val2 = new BigDecimal(100);
		BigDecimal val3 = new BigDecimal(10.01);
		BigDecimal val4 = new BigDecimal(1);
		int ret = val1.divide(val2, 2, BigDecimal.ROUND_HALF_DOWN).compareTo(val3.divide(val4, 2, BigDecimal.ROUND_HALF_DOWN));
		Assert.isTrue(ret == 0);
	}

}
