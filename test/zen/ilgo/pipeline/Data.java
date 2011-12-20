package zen.ilgo.pipeline;

public class Data {

	private int num;
	private final int id;
	
	public Data(int n) {
		num = n;
		id = n;
	}
	
	public void setNum(int n) {
		num = n;
	}
	
	public int getNum() {
		return num;
	}
	
	public String toString() {
		return "D:" + id;
	}
}
