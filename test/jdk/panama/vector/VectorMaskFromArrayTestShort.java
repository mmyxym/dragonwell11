import jdk.panama.vector.*;
import java.util.Arrays;
import java.util.Random;
import java.lang.reflect.Field;
import java.io.IOException;
import jdk.panama.vector.Vector.Mask;
import jdk.panama.vector.Vector.Shape;
/*
 *  @test
 *  @modules jdk.panama.vector
 *   @run testng/othervm --add-opens jdk.panama.vector/jdk.panama.vector=ALL-UNNAMED -XX:+UseVectorAPI VectorMaskFromArrayTestShort
 *
 */
public class VectorMaskFromArrayTestShort
{

    public static int size = 1024 * 16;
    static Random random = new Random();
    static final ShortVector.ShortSpecies Species128 = ShortVector.species(Shape.S_128_BIT);
    static final ShortVector.ShortSpecies Species256 = ShortVector.species(Shape.S_256_BIT);
    static final ShortVector.ShortSpecies Species512 = ShortVector.species(Shape.S_512_BIT);
    static ShortVector.ShortSpecies Species;
    static boolean[] AisNull = new boolean[size];
    static short[] result = new short[size];
    static short[] resultv = new short[size];
    static short[] input = new short[size];
    static short[] resultd = new short[size];
    static boolean print = true;

    static boolean equals(byte a, byte b) {
      return a == b;
    }
    static boolean equals(short a, short b) {
      return a == b;
    }
    static boolean equals(int a, int b) {
      return a == b;
    }
    static boolean equals(long a, long b) {
      return a == b;
    }

    public static void main(String[] args) throws  NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, InstantiationException {
        Species = Species128;
        long start0 = System.currentTimeMillis();
        long startv = System.currentTimeMillis();
        long normalTime = 0;
        long vecTime = 0;
        int i = 0;
        for (i = 0; i < size; i++) {
            input[i] = (short)random.nextInt(50);
            result[i] = 0;
            resultv[i] = 0;
            if (random.nextInt(10) > 4) {
                AisNull[i] = true;
            } else {
                AisNull[i] = false;
            }
        }
        for (i = 0; i < 20000; i++) {
            normalTest();
        }
        for (i = 0; i < 20000; i++) {
            vecTest();
        }
        for (i = 0; i < size; i++) {
            result[i] = 0;
            resultv[i] = 0;
        }
        System.out.println("begin test " + Species.length());
        start0 = System.currentTimeMillis();
        for (i = 0; i < 10000; i++) {
            normalTest();
        }
        normalTime = System.currentTimeMillis() - start0;
        System.out.println("normal  time used:" + normalTime);
        startv = System.currentTimeMillis();
        for (i = 0; i < 10000; i++) {
            vecTest();
        }
        vecTime = System.currentTimeMillis() - startv;
        System.out.println("vector time used:" + vecTime);
        for (i = 0; i < size; i++) {
            result[i] = 0;
            resultv[i] = 0;
        }
        normalTest();
        vecTest();
        for (i = 0; i < size; i++) {
            if (!equals(result[i], resultv[i])) {
                throw new RuntimeException("Wrong result!" + " index " + i + " vec result " + resultv[i] + " result0 " + result[i] + " mask " + AisNull[i]);
            }
        }
    }
    static void normalTest() {
        for (int i = 0; i < size; i++) {
             if (AisNull[i] == true) {
                 result[i] = input[i];
              } else {
                 result[i] = 0;
              }
        }
        return;
    }
    static void vecTest() {
        ShortVector v0;
        int i = 0;
        Mask mask0;
        for (i = 0; i + (Species.length()) <= size; i += Species.length()) {
            mask0 = Species.maskFromArray(AisNull, i);
            v0 = Species.fromArray(input, i, mask0);
            v0.intoArray(resultv, i);
        }
        return;
    }
}
