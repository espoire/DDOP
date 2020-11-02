package util;

public class Vector2<A, B> {
	public final A first;
	public final B second;
	
	public Vector2(A first, B second) {
		this.first = first;
		this.second = second;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if(obj == null) return false;
		if(this == obj) return true;
		if(this.getClass() != obj.getClass()) return false;
		
		Vector2<A, B> other = (Vector2<A, B>) obj;
		
		if(this.first == null) {
			if(other.first != null) return false;
		} else {
			if(!this.first.equals(other.first)) return false;
		}
		
		if(this.second == null) {
			if(other.second != null) return false;
		} else {
			if(!this.second.equals(other.second)) return false;
		}
		
		return true;
	}
	
	@Override
	public int hashCode() {
		int firstHash = 0,
			secondHash = 0;
		if(this.first  != null) firstHash  = this.first.hashCode();
		if(this.second != null) secondHash = this.second.hashCode();
		return firstHash ^ secondHash;
	}
}
