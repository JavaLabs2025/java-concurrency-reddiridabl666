package dws.labs.lunch;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.params.Parameter;
import org.junit.jupiter.params.ParameterizedClass;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.FieldSource;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@ParameterizedClass(name = "Programmers: {0}, waiters: {1}, soup portions: {2}")
@FieldSource(value = "args")
class LunchTest {
    @Parameter(0)
    int programmersNum;

    @Parameter(1)
    int soupPortions;

    @Parameter(2)
    int waitersNum;

    @RepeatedTest(value = 5)
    void test() throws InterruptedException {
        var lunch = new Lunch(programmersNum, soupPortions, waitersNum);

        assertTrue(lunch.run(5, TimeUnit.MINUTES));
    }

    static List<Arguments> args = Arrays.asList(
            Arguments.of(3, 2, 15),
            Arguments.of(5, 2, 25),
            Arguments.of(10, 2, 1000),
            Arguments.of(100, 2, 1000),
            Arguments.of(7, 2, 1_000_000));
}
