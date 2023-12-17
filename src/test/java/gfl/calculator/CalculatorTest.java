package gfl.calculator;

import gfl.exceptions.IncorrectFormulaException;
import gfl.exceptions.LexicalException;
import org.junit.jupiter.api.*;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class CalculatorTest {

    Calculator calculator = new Calculator();
    String formula;
    HashMap<String, Double> variables;
    double expectedResult;


    @Nested
    class AlgorithmWithoutExceptionsTests {
        @BeforeEach
        public void beforeEach() {
            formula = "";
            variables = null;
            expectedResult = 0;
        }

        @AfterEach
        public void afterEach() throws LexicalException, IncorrectFormulaException {
            Assertions.assertEquals(expectedResult, calculator.calculate(formula, variables));
        }

        @Test
        @DisplayName("2+2")
        void calculateFormula1() {

            formula = "2+2";
            variables = new HashMap<>();
            expectedResult = 2 + 2;

        }

        @Test
        @DisplayName("-3+4")
        void calculateFormula2() {
            formula = "-3+4.";

            variables = new HashMap<>();

            expectedResult = -3 + 4;
        }

        @Test
        @DisplayName("x+2, x=-4")
        void calculateFormula3() {
            formula = "x+2";

            variables = new HashMap<>();
            variables.put("x", -4.);


            expectedResult = -4. + 2.;
        }

        @Test
        @DisplayName("x-2, x=-4")
        void calculateFormula4() {
            formula = "x-2";

            variables = new HashMap<>();
            variables.put("x", -4.);

            expectedResult = -4. - 2.;
        }

        @Test
        @DisplayName("x*3, x=56")
        void calculateFormula5() {
            formula = "x*3";

            variables = new HashMap<>();
            variables.put("x", 56.);

            expectedResult = 56. * 3;
        }

        @Test
        @DisplayName("x/9, x=5")
        void calculateFormula6() {
            formula = "x/9";

            variables = new HashMap<>();
            variables.put("x", 5.);


            expectedResult = 5 / 9.;
        }

        @Test
        @DisplayName("5/9/4")
        void calculateFormula7() {
            formula = "5/9/4";

            expectedResult = 5. / 9. / 4.;
        }

        @Test
        @DisplayName("-3-(-3)")
        void calculateFormula8() {
            formula = "-3-(-3)";
            variables = new HashMap<>();
            expectedResult = -3 - (-3);
        }

        @Test
        @DisplayName("-x+(x-(-2*x+3)) x=50")
        void calculateFormula9() {
            formula = "-x+(x-(-2*x+3))";
            variables = new HashMap<>();
            variables.put("x", 50.);
            expectedResult = -50 + (50 - (-2 * 50 + 3));
        }
    }


    @Nested
    class AlgorithmExceptionsTests {

        @Test
        @DisplayName("2+2*a+3+v")
        void calculateFormula1() {
            formula = "2+2*a+3+v";
            variables = new HashMap<>();
            assertThrowsExactly(IncorrectFormulaException.class, () -> calculator.calculate(formula, variables));
        }


        @Test
        @DisplayName("2+2/0")
        void calculateFormula2() {
            formula = "2+2/0";
            variables = new HashMap<>();
            assertThrowsExactly(ArithmeticException.class, () -> calculator.calculate(formula, variables));
        }

        @Test
        @DisplayName("2+2((")
        void calculateFormula3() {
            formula = "2+2/0";
            variables = new HashMap<>();
            assertThrowsExactly(ArithmeticException.class, () -> calculator.calculate(formula, variables));
        }


    }

}