package zen.ilgo.pipeline;

import java.util.Random;

import zen.ilgo.pipeline.ifaces.ICollector;

public class TestCollector implements ICollector<Data> {

	private int count;
	private int idx;
	private Random rand;

	public TestCollector(int n) {
		count = n;
		idx = 0;
		rand = new Random();
	}

	@Override
	public boolean hasNext() {
		return idx < count ? true : false;
	}

	@Override
	public Data next() {
		idx++;
		return new Data(rand.nextInt(100));
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
