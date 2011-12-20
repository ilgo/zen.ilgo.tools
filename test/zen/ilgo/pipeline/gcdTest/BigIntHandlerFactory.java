package zen.ilgo.pipeline.gcdTest;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import zen.ilgo.pipeline.PipelineException;
import zen.ilgo.pipeline.ifaces.IHandler;
import zen.ilgo.pipeline.ifaces.IHandlerFactory;

public class BigIntHandlerFactory implements IHandlerFactory<BigIntPair> {

    private static IHandlerFactory<BigIntPair> factory;

    private BigIntHandlerFactory() {
    }

    public static IHandlerFactory<BigIntPair> getFactory() {
        if (factory == null) {
            factory = new BigIntHandlerFactory();
        }
        return factory;
    }

    @Override
    public List<IHandler<BigIntPair>> getHandlerOrder() {

        List<IHandler<BigIntPair>> handlers = new ArrayList<IHandler<BigIntPair>>();
        handlers.add(new GcdBigIntHandler());
        return handlers;
    }

    class GcdBigIntHandler implements IHandler<BigIntPair> {

        @Override
        public String getHandlerName() {
            return "GCD BigInt Handler";
        }

        @Override
        public void handle(BigIntPair t) throws PipelineException {

            BigInteger bigA = t.getA();
            BigInteger bigB = t.getB();
            BigInteger gcd = bigA.gcd(bigB);

            System.out.println("GCD " + t.getIdx() + " calculated: " + gcd);
        }
    }
}
