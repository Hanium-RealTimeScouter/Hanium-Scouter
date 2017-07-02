package hanium.util;

/**
 * ObjectContainer의 HashMap에서 사용할 객체
 * @author occidere
 *
 */
public class Container {
	private String name; //에러 발생 객체의 이름(ex. /jpetstore/main.jsp 등)
	long[] values = new long[5]; //이전부터 쓰던 에러 발생시간 등의 배열
	
	public Container(long[] values){
		this(null, values);
	}
	
	public Container(String name, long[] values){
		this.name = name;
		for(int i=0;i<values.length;i++){
			this.values[i] = values[i];
		}
	}
}