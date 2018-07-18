package lottery;

import java.math.BigDecimal;

import org.junit.Test;
import org.springframework.util.Assert;

public class BigDecimalTest {

	@Test
	public void test() {
		BigDecimal val1 = new BigDecimal(1001);
		BigDecimal val2 = new BigDecimal(100);
		
		Assert.isTrue(val1.divide(val2, 2, BigDecimal.ROUND_HALF_DOWN).floatValue() == 10.01);
	}

}
